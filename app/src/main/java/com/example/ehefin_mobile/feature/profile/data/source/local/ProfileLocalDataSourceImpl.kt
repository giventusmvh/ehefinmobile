package com.example.ehefin_mobile.feature.profile.data.source.local

import com.example.ehefin_mobile.core.common.DataResult
import javax.inject.Inject

/**
 * Implementation of ProfileLocalDataSource.
 * Wraps Room DAO calls with proper error handling and returns DataResult.
 */
class ProfileLocalDataSourceImpl @Inject constructor(
    private val profileDao: ProfileDao
) : ProfileLocalDataSource {

    override suspend fun getProfile(userId: Long): DataResult<ProfileEntity?> {
        return try {
            val profile = profileDao.getProfileByUserIdSync(userId)
            DataResult.Success(profile)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal mengambil profil dari cache",
                throwable = e
            )
        }
    }

    override suspend fun saveProfile(profile: ProfileEntity): DataResult<Unit> {
        return try {
            profileDao.insertProfile(profile)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menyimpan profil ke cache",
                throwable = e
            )
        }
    }

    override suspend fun deleteProfile(userId: Long): DataResult<Unit> {
        return try {
            profileDao.deleteProfile(userId)
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal menghapus profil dari cache",
                throwable = e
            )
        }
    }

    override suspend fun clearAllProfiles(): DataResult<Unit> {
        return try {
            profileDao.deleteAllProfiles()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(
                message = e.localizedMessage ?: "Gagal membersihkan cache profil",
                throwable = e
            )
        }
    }
}
