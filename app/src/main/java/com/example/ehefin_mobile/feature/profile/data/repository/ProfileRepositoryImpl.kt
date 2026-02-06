package com.example.ehefin_mobile.feature.profile.data.repository

import com.example.ehefin_mobile.core.common.DataResult
import com.example.ehefin_mobile.core.common.Resource
import com.example.ehefin_mobile.core.datastore.TokenManager
import com.example.ehefin_mobile.core.di.IoDispatcher
import com.example.ehefin_mobile.core.network.NetworkMonitor
import com.example.ehefin_mobile.feature.profile.data.mapper.toDomain
import com.example.ehefin_mobile.feature.profile.data.mapper.toEntity
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileEntity
import com.example.ehefin_mobile.feature.profile.data.source.local.ProfileLocalDataSource
import com.example.ehefin_mobile.feature.profile.data.source.remote.ProfileRemoteDataSource
import com.example.ehefin_mobile.feature.profile.domain.model.UserProfile
import com.example.ehefin_mobile.feature.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

/**
 * Implementation of ProfileRepository using DataSource interfaces.
 * Handles offline-first caching strategy with proper layer separation.
 */
class ProfileRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProfileRemoteDataSource,
    private val localDataSource: ProfileLocalDataSource,
    private val tokenManager: TokenManager,
    private val networkMonitor: NetworkMonitor,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ProfileRepository {

    override fun getProfile(): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())

        // 1. Get userId from session
        val userId = tokenManager.getUserId().first()

        if (userId != null) {
            // 2. Emit cached data first (offline-first)
            val cachedResult = localDataSource.getProfile(userId)
            if (cachedResult is DataResult.Success && cachedResult.data != null) {
                emit(Resource.Success(cachedResult.data.toDomain()))
            }

            // 3. Fetch from network if online
            if (networkMonitor.isOnline()) {
                when (val remoteResult = remoteDataSource.getProfile()) {
                    is DataResult.Success -> {
                        // Update cache
                        val entity = remoteResult.data.toEntity()
                        localDataSource.saveProfile(entity)
                        emit(Resource.Success(remoteResult.data.toDomain()))
                    }
                    is DataResult.Error -> {
                        // If we have cached data, return error with cached data
                        val cached = (cachedResult as? DataResult.Success)?.data
                        if (cached != null) {
                            emit(Resource.Error(remoteResult.message, cached.toDomain()))
                        } else {
                            emit(Resource.Error(remoteResult.message))
                        }
                    }
                }
            } else {
                // Offline: if no cache, emit error
                if (cachedResult !is DataResult.Success || cachedResult.data == null) {
                    emit(Resource.Error("Tidak ada koneksi internet"))
                }
            }
        } else {
            emit(Resource.Error("User ID tidak ditemukan"))
        }
    }.flowOn(ioDispatcher)

    override suspend fun updateProfile(
        nik: String,
        phoneNumber: String,
        address: String,
        bankName: String,
        accountNumber: String,
        accountHolderName: String,
        birthdate: String,
        job: String,
        companyName: String,
        ktpFile: File?,
        kkFile: File?,
        npwpFile: File?,
        selfieFile: File?,
        salarySlipFile: File?
    ): Resource<UserProfile> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet")
        }

        // Get existing profile for userId, name, email (not returned in update response)
        val userId = tokenManager.getUserId().first()
        val existingProfileResult = if (userId != null) {
            localDataSource.getProfile(userId)
        } else null

        val existingProfile = (existingProfileResult as? DataResult.Success)?.data
        if (existingProfile == null) {
            return Resource.Error("Profil tidak ditemukan")
        }

        return when (val result = remoteDataSource.updateProfile(
            nik = nik,
            phoneNumber = phoneNumber,
            address = address,
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolderName = accountHolderName,
            birthdate = birthdate,
            job = job,
            companyName = companyName,
            ktpFile = ktpFile,
            kkFile = kkFile,
            npwpFile = npwpFile,
            selfieFile = selfieFile,
            salarySlipFile = salarySlipFile
        )) {
            is DataResult.Success -> {
                val profileDto = result.data
                val userProfile = profileDto.toDomain(
                    existingUserId = existingProfile.userId,
                    existingName = existingProfile.name,
                    existingEmail = existingProfile.email
                )
                // Update cache
                localDataSource.saveProfile(
                    ProfileEntity(
                        userId = existingProfile.userId,
                        name = existingProfile.name,
                        email = existingProfile.email,
                        nik = profileDto.nik,
                        birthdate = profileDto.birthdate,
                        phoneNumber = profileDto.phoneNumber,
                        address = profileDto.address,
                        ktpPath = profileDto.ktpUrl,
                        kkPath = profileDto.kkUrl,
                        npwpPath = profileDto.npwpUrl,
                        bankName = profileDto.bankName,
                        accountNumber = profileDto.accountNumber,
                        accountHolderName = profileDto.accountHolderName,
                        job = profileDto.job,
                        companyName = profileDto.companyName,
                        selfiePath = profileDto.selfieUrl,
                        salarySlipPath = profileDto.salarySlipUrl,
                        isComplete = profileDto.isComplete ?: false
                    )
                )
                Resource.Success(userProfile)
            }
            is DataResult.Error -> Resource.Error(result.message)
        }
    }

    override suspend fun registerFcmToken(token: String): Resource<Unit> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Tidak ada koneksi internet")
        }

        return when (val result = remoteDataSource.registerFcmToken(token)) {
            is DataResult.Success -> Resource.Success(Unit)
            is DataResult.Error -> Resource.Error(result.message)
        }
    }

    override suspend fun refreshProfile(): Resource<Unit> {
        if (!networkMonitor.isOnline()) {
            return Resource.Error("Offline")
        }

        return when (val result = remoteDataSource.getProfile()) {
            is DataResult.Success -> {
                localDataSource.saveProfile(result.data.toEntity())
                Resource.Success(Unit)
            }
            is DataResult.Error -> Resource.Error(result.message)
        }
    }
}