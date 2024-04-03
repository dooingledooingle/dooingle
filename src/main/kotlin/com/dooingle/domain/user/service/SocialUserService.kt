package com.dooingle.domain.user.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.*
import com.dooingle.domain.user.model.Profile
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.ProfileRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.global.exception.custom.InvalidParameterException
import com.dooingle.global.exception.custom.ModelNotFoundException
import com.dooingle.global.exception.custom.SocialUserNotFoundByUserLinkException
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.*

@Service
class SocialUserService(
    private val socialUserRepository: SocialUserRepository,
    private val profileRepository: ProfileRepository,
    private val dooingleCountService: DooingleCountService,
    private val amazonS3: AmazonS3,
    @Value("\${cloud.aws.s3.bucketName}") private val bucketName:String
) {

    @Transactional
    fun registerIfAbsent(oAuth2UserInfo: OAuth2UserInfo): SocialUser {
        return if (!socialUserRepository.existsByProviderAndProviderId(oAuth2UserInfo.provider, oAuth2UserInfo.id)) {
            registerUser(oAuth2UserInfo)
        } else {
            socialUserRepository.findByProviderAndProviderId(oAuth2UserInfo.provider, oAuth2UserInfo.id)
        }
    }

    fun registerUser(oAuth2UserInfo: OAuth2UserInfo): SocialUser {
        val socialUser = SocialUser(
            provider = oAuth2UserInfo.provider,
            providerId = oAuth2UserInfo.id,
            nickname = oAuth2UserInfo.nickname,
            userLink = createRandomUserLink(),
        )

        oAuth2UserInfo.profileImage?.let {
            profileRepository.save(
                Profile(user = socialUser, imageUrl = oAuth2UserInfo.profileImage))
        }

        return socialUserRepository.save(socialUser)
    }

    private fun createUniqueRandomUserLink(): String {
        lateinit var randomUserLink: String
        do {
            randomUserLink = createRandomUserLink()
        } while (socialUserRepository.existsByUserLink(randomUserLink)) // 난수 문자열로 생성했는데 존재한다면 다시 생성
        return randomUserLink
    }

    private fun createRandomUserLink() = RandomStringUtils.randomAlphanumeric(10, 20)

    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            HOT_DOOINGLERS_KEYWORD -> dooingleCountService.getHotDooinglerList(HOT_DOOINGLERS_SIZE)
            NEW_DOOINGLERS_KEYWORD -> socialUserRepository.getNewDooinglers(NEW_DOOINGLERS_SIZE)
            else -> throw InvalidParameterException(null)
        }
    }

    fun searchDooinglers(nickname: String): List<SearchDooinglerResponse> {
        return socialUserRepository.searchDooinglers(nickname)
    }

    @Transactional
    fun updateProfile(userId: Long, request: UpdateProfileDto, image: MultipartFile?): UpdateProfileDto {
        var imageUrl:String? = null

        //image 파일이 있다면 S3에 업로드, 파일이 없다면 URL은 있는지 확인
        if(image != null){
            validateImage(image)
            imageUrl = upload2Cloud(image)
        }
        else {
            request.imageUrl?.let {
                imageUrl = request.imageUrl
            }
        }

        //DB에 profile이 존재한다면 수정, 없다면 새로 생성
        val user = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)
        val profile = profileRepository.findByUser(user)

        if(profile != null) {
            profile.imageUrl = imageUrl
            profile.description = request.description
            val newProfile = profileRepository.save(profile)
            return UpdateProfileDto(newProfile.description, newProfile.imageUrl)
        }
        else {
            val newProfile = profileRepository.save(Profile(description = request.description, imageUrl = imageUrl, user = user))
            return UpdateProfileDto(newProfile.description, newProfile.imageUrl)
        }
    }

    fun validateImage(imageFile:MultipartFile){
        val supportedContentType = listOf("image/png", "image/jpg", "image/jpeg")
        val maxSizeBytes = 5 * 1024 * 1024

        val fileContentType = imageFile.contentType

        if(!supportedContentType.contains(fileContentType)){
            throw InvalidParameterException("지원하지 않는 이미지 확장자입니다")
        }
        if(imageFile.size > maxSizeBytes) {
            throw InvalidParameterException("이미지 크기는 5MB를 초과할 수 없습니다")
        }
    }

    fun upload2Cloud(image: MultipartFile) : String {
        // 1. 중복 방지를 위해 이미지에 랜덤 아이디 붙이기
        val originName = image.originalFilename
        val ext = originName!!.substring(originName.lastIndexOf("."))
        val randomId = UUID.randomUUID().toString()
        val newName = randomId + originName

        // 2. 메서드 인자로 들어갈 메타데이터 생성
        val metadata = ObjectMetadata()
        metadata.contentType="image/$ext"

        // 3. S3에 이미지 업로드
        runCatching {
            amazonS3.putObject(bucketName, newName, image.inputStream, metadata)
        }.onFailure {
            throw RuntimeException("S3 이미지 업로드에 실패했습니다") //todo
        }
        return amazonS3.getUrl(bucketName, newName).toString()
    }

    fun getProfile(userId: Long) : ProfileResponse {
        val user = socialUserRepository.findByIdOrNull(userId)
            ?: throw ModelNotFoundException(modelName = "Social User", modelId = userId)
        val profile = profileRepository.findByUser(user)

        if(profile != null){
            return ProfileResponse(nickname = user.nickname, description = profile.description, imageUrl = profile.imageUrl)
        }
        else {
            return ProfileResponse(nickname = user.nickname, description = null, imageUrl = null)
        }
    }

    fun getOtherUserProfile(userLink: String) : ProfileResponse {
        val targetUser = socialUserRepository.findByUserLink(userLink)
            ?: throw SocialUserNotFoundByUserLinkException(userLink)
        val profile = profileRepository.findByUser(targetUser)

        return if (profile != null) {
            ProfileResponse(nickname = targetUser.nickname, description = profile.description, imageUrl = profile.imageUrl)
        } else {
            ProfileResponse(nickname = targetUser.nickname, description = null, imageUrl = null)
        }
    }

    fun getProfileImageUrlByUserLink(userLink: String): ProfileImageUrlResponse {
        val targetUser = socialUserRepository.findByUserLink(userLink)
            ?: throw SocialUserNotFoundByUserLinkException(userLink)

        return ProfileImageUrlResponse(profileRepository.findByUser(targetUser)?.imageUrl)
    }

    fun getCurrentDooingler(userId: Long): DooinglerResponse {
        return socialUserRepository.getDooingler(userId)
    }

    companion object {
        const val HOT_DOOINGLERS_KEYWORD = "hot"
        const val NEW_DOOINGLERS_KEYWORD = "new"
        const val HOT_DOOINGLERS_SIZE: Long = 5
        const val NEW_DOOINGLERS_SIZE: Long = 5
    }

}
