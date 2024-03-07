package com.dooingle.domain.user.service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.dooingle.domain.dooinglecount.service.DooingleCountService
import com.dooingle.domain.user.dto.DooinglerResponse
import com.dooingle.domain.user.dto.UpdateProfileRequest
import com.dooingle.domain.user.dto.UpdateProfileResponse
import com.dooingle.domain.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val dooingleCountService: DooingleCountService,
    private val amazonS3: AmazonS3,
    @Value("cloud.aws.s3.bucketName") private val bucketName:String
) {
    fun getDooinglerList(condition: String?): List<DooinglerResponse> {
        return when (condition) {
            "hot" -> dooingleCountService.getHotDooinglerList()
            "new" -> userRepository.getNewDooinglers()
            else -> throw IllegalArgumentException() // TODO
        }
    }

    fun updateProfile(userId: Long, request: UpdateProfileRequest, img:MultipartFile?): UpdateProfileResponse {
        var s3Url:String? = null

        img?.let {
            // 1. 이미지에 랜덤 아이디 붙이기
            val originName = img.originalFilename
            val ext = originName!!.substring(originName.lastIndexOf("."))
            val randomId = UUID.randomUUID().toString()
            val newName = randomId+originName

            // 2. 메서드 인자로 들어갈 메타데이터 생성
            val metadata = ObjectMetadata()
            metadata.contentType="image/$ext"

            // 3. S3에 이미지 저장
            runCatching {
                amazonS3.putObject(
                    PutObjectRequest(bucketName, newName, img.inputStream, metadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead))
            }.getOrElse {
                TODO("예외처리")
            }
            s3Url = amazonS3.getUrl(bucketName, newName).toString()
        }

        //DB에서 profile 가져와서 수정한 뒤 저장
        TODO()
    }
}
