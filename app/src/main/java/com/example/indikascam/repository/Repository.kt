package com.example.indikascam.repository

import com.example.indikascam.api.RetrofitInstance
import com.example.indikascam.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Part

class Repository {

    suspend fun pushPost(post: Post): Response<Any> {
        return RetrofitInstance.api.pushPost(post)
    }

    suspend fun registerPost(registerPost: RegisterPost): Response<Any>{
        return RetrofitInstance.api.registerPost(registerPost)
    }

    suspend fun emailVerificationPost(token: String, emailVerification: EmailVerificationPost): Response<Any>{
        return RetrofitInstance.api.emailVerificationPost(token, emailVerification)
    }

    suspend fun forgotPasswordPost(forgotPassword: ForgotPasswordPost): Response<Any>{
        return RetrofitInstance.api.forgotPasswordPost(forgotPassword)
    }

    suspend fun changePasswordTokenPost(changePasswordToken: ChangePasswordTokenPost): Response<Any>{
        return RetrofitInstance.api.changePasswordTokenPost(changePasswordToken)
    }

    suspend fun newPasswordPost(newPassword: NewPasswordPost): Response<Any>{
        return RetrofitInstance.api.newPasswordPost(newPassword)
    }

    suspend fun logoutPost(token: String): Response<Any>{
        return RetrofitInstance.api.logoutPost(token)
    }

    suspend fun resendEmailToken(token: String): Response<Any>{
        return RetrofitInstance.api.resendEmailTokenPost(token)
    }

    suspend fun userReportPost(token: String, jenisGangguan: Int,bank: Int, noRek: String, namaPenipu: String, platform: Int, product: Int, kronologi: String, bukti: List<MultipartBody.Part>, totalKerugian: Int, noTelPenipu: String): Response<Any>{
        return RetrofitInstance.api.userReportPost(token, jenisGangguan, bank, noRek, namaPenipu, platform, product, kronologi, bukti, totalKerugian,noTelPenipu)
    }

    suspend fun meGet(token: String): Response<Any>{
        return RetrofitInstance.api.meGet(token)
    }

    suspend fun platformsGet(): Response<Any>{
        return RetrofitInstance.api.platformsGet()
    }
    suspend fun banksGet(): Response<Any>{
        return RetrofitInstance.api.banksGet()
    }
    suspend fun productsGet(): Response<Any>{
        return RetrofitInstance.api.productsGet()
    }
    suspend fun reportTypeGet(): Response<Any>{
        return RetrofitInstance.api.reportTypeGet()
    }
}