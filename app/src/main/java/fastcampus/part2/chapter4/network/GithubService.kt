package fastcampus.part2.chapter4.network

import fastcampus.part2.chapter4.model.Repo
import fastcampus.part2.chapter4.model.UserDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    // 사용자의 레포지토리 조회
    @GET("users/{username}/repos")
    fun listRepos(@Path("username") username: String, @Query("page") page: Int): Call<List<Repo>>
    // 사용자 정보 조회
    @GET("search/users")
    fun searchUsers(@Query("q") query: String): Call<UserDto>
}