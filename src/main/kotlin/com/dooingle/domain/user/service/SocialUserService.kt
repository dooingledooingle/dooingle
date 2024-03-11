package com.dooingle.domain.user.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.model.Profile
import com.dooingle.domain.user.model.SocialUser
import com.dooingle.domain.user.repository.ProfileRepository
import com.dooingle.domain.user.repository.SocialUserRepository
import com.dooingle.domain.user.dto.OAuth2UserInfo
import com.dooingle.domain.user.dto.UpdateProfileDto
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
            val socialUser = SocialUser(
                provider = oAuth2UserInfo.provider,
                providerId = oAuth2UserInfo.id,
                nickname = oAuth2UserInfo.nickname
            )

            oAuth2UserInfo.profileImage?.let {
                profileRepository.save(
                    Profile(user = socialUser, imageUrl = oAuth2UserInfo.profileImage))
            }

            socialUserRepository.save(socialUser)
        } else {
            socialUserRepository.findByProviderAndProviderId(oAuth2UserInfo.provider, oAuth2UserInfo.id)
        }
    }

    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleCountService.getHotDooinglerList()
            "new" -> socialUserRepository.getNewDooinglers()
            else -> throw IllegalArgumentException() // TODO
        }
    }

    @Transactional
    fun updateProfile(userId: Long, request: UpdateProfileDto, img: MultipartFile?): UpdateProfileDto {
        var imageUrl:String? = null

        //기존 프로필사진이 있다면 S3를 거치지 않고 url 넘기기
        if(request.imageUrl != null)
        {
            imageUrl = request.imageUrl
        }
        else
        {
            img?.let {
                imageUrl = upload2Cloud(img)
            }
        }

        //DB에 profile이 존재한다면 수정, 없다면 새로 생성
        val user = socialUserRepository.findByIdOrNull(userId) ?: throw IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다")
        val profile = profileRepository.findByUser(user)

        if(profile != null){
            profile.imageUrl = imageUrl
            profile.description = request.description
            val newProfile = profileRepository.save(profile)
            return UpdateProfileDto(newProfile.description, newProfile.imageUrl)
        }
        else{
            val newProfile = profileRepository.save(Profile(description = request.description, imageUrl = imageUrl, user = user))
            return UpdateProfileDto(newProfile.description, newProfile.imageUrl)
        }
    }

    fun upload2Cloud(img: MultipartFile) : String {
        // 1. 중복 방지를 위해 이미지에 랜덤 아이디 붙이기
        val originName = img.originalFilename
        val ext = originName!!.substring(originName.lastIndexOf("."))
        val randomId = UUID.randomUUID().toString()
        val newName = randomId + originName

        // 2. 메서드 인자로 들어갈 메타데이터 생성
        val metadata = ObjectMetadata()
        metadata.contentType="image/$ext"

        // 3. S3에 이미지 업로드
        runCatching {
            amazonS3.putObject(bucketName, newName, img.inputStream, metadata)
        }.onFailure {
            throw RuntimeException("S3 이미지 업로드에 실패했습니다")
        }
        return amazonS3.getUrl(bucketName, newName).toString()
    }
}
