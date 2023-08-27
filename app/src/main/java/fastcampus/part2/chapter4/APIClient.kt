package fastcampus.part2.chapter4

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIClient(context: Context) {
    // GitHub API 기본 URL
    companion object {
        private const val BASE_URL = "https://api.github.com/"
    }
    // OkHttpClient 객체 생성 및 설정
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor {
            val request = it.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ${context.getString(R.string.github_token)}")
                .build()
            it.proceed(request)
        }
        .build()
    // Retrofit 객체 생성 및 설정
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}