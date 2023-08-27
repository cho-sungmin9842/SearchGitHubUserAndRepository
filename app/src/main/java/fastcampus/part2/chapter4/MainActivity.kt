package fastcampus.part2.chapter4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import fastcampus.part2.chapter4.adapter.UserAdapter
import fastcampus.part2.chapter4.databinding.ActivityMainBinding
import fastcampus.part2.chapter4.model.UserDto
import fastcampus.part2.chapter4.network.GithubService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userAdapter: UserAdapter
    private val apiClient=APIClient(this)
    // 메인 스레드가 사용하는 Looper를 처리하는 Handler
    private val handler = Handler(Looper.getMainLooper())
    private var searchFor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // recyclerView의 아이템 클릭시 RepoActivity로 이동하고 User 데이터 클래스의 username을 전달해줌
        userAdapter = UserAdapter {
            val intent = Intent(this@MainActivity, RepoActivity::class.java)
            intent.putExtra("username", it.username)
            startActivity(intent)
        }
        // recyclerView설정
        binding.userRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
        }
        // SAM으로 Single Abstract Method. 즉 추상 메소드가 단 1개 있는 인터페이스
        val runnable = Runnable {
            // 람다함수가 run함수를 호출하는 게 된다.
            searchUser()
        }
        // searchEditText의 텍스트 변경리스너
        binding.searchEditText.addTextChangedListener {
            searchFor = it.toString()
            // runnable 실행을 멈춤
            handler.removeCallbacks(runnable)
            // 일정시간(0.3초) 이후 runnable을 실행함
            handler.postDelayed(runnable, 300)
        }
    }
    // GitHub에서 사용자를 검색하는 함수
    private fun searchUser() {
        val githubService = apiClient.retrofit.create(GithubService::class.java)
        githubService.searchUsers(searchFor).enqueue(object : Callback<UserDto> {
            // 응답 있을 시(성공 시)
            override fun onResponse(call: Call<UserDto>, response: Response<UserDto>) {
                println(response.code())
                Log.e("MainActivity", "Search User : ${response.body().toString()}")
                if (response.isSuccessful)
                    userAdapter.submitList(response.body()?.items)
                else
                    userAdapter.submitList(emptyList())
            }
            // 응답 없을 시(실패 시)
            override fun onFailure(call: Call<UserDto>, t: Throwable) {
                Toast.makeText(this@MainActivity, "에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
                println("에러 = ${t.message}")
            }
        })
    }
}