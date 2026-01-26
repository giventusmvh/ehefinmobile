package com.example.ehefin_mobile.feature.profile.domain.usecase

import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.feature.profile.domain.model.UserProfile
import com.example.ehefin_mobile.feature.profile.domain.repository.ProfileRepository
import java.io.File
import javax.inject.Inject

/** Use case to update user profile */
class UpdateProfileUseCase @Inject constructor(private val profileRepository: ProfileRepository) {
    suspend operator fun invoke(
            nik: String,
            phoneNumber: String,
            address: String,
            bankName: String,
            accountNumber: String,
            accountHolderName: String,
            birthdate: String,
            ktpFile: File? = null,
            kkFile: File? = null,
            npwpFile: File? = null
    ): Resource<UserProfile> {
        // Validation
        if (nik.isNotEmpty() && nik.length != 16) {
            return Resource.Error("NIK harus 16 digit")
        }
        if (phoneNumber.isEmpty()) {
            return Resource.Error("Nomor telepon wajib diisi")
        }

        return profileRepository.updateProfile(
                nik = nik,
                phoneNumber = phoneNumber,
                address = address,
                bankName = bankName,
                accountNumber = accountNumber,
                accountHolderName = accountHolderName,
                birthdate = birthdate,
                ktpFile = ktpFile,
                kkFile = kkFile,
                npwpFile = npwpFile
        )
    }
}
