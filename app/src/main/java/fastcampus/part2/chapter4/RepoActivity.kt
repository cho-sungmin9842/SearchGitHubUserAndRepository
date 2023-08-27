package fastcampus.part2.chapter4

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fastcampus.part2.chapter4.adapter.RepoAdapter
import fastcampus.part2.chapter4.databinding.ActivityRepoBinding
import fastcampus.part2.chapter4.model.Repo
import fastcampus.part2.chapter4.network.GithubService
import retrofit2.*

class RepoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRepoBinding
    private lateinit var repoAdapter: RepoAdapter
    private var page = 0
    private var hasMore = true
    private val apiClient = APIClient(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // intent에서 "username"으로 온 값
        val username = intent.getStringExtra("username") ?: return
        // 텍스트뷰의 텍스트에 username 설정
        binding.usernameTextView.text = username
        // recyclerView에 해당 항목 클릭시 해당주소로 이동하는 intent
        repoAdapter = RepoAdapter {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.htmlUrl))
            startActivity(intent)
        }
        val linearLayoutManager = LinearLayoutManager(this@RepoActivity)
        // recyclerView 설정
        binding.repoRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = repoAdapter
        }
        // recyclerView에 스크롤 리스너 등록
        binding.repoRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 현재 관리하고 있는 전체 아이템의 수
                val totalCount = linearLayoutManager.itemCount
                // 마지막으로 보여지는 아이템의 위치
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                // 마지막으로 보여지는 아이템의 위치가 현재 관리하고 있는 전체 아이템의 수-1 보다 크거나 같고 hasMore의 값이 true라면
                if (lastVisiblePosition >= (totalCount - 1) && hasMore) {
                    // page 1 증가
                    page += 1
                    // listRepo 함수 호출 인자로 username과 page값
                    listRepo(username, page)
                }
            }
        })
        // listRepo 함수 호출 인자로 username과 0
        listRepo(username, 0)
    }

    private fun listRepo(username: String, page: Int) {
        val githubService = apiClient.retrofit.create(GithubService::class.java)
        githubService.listRepos(username, page).enqueue(object : Callback<List<Repo>> {
            // 응답 있을 시(성공시)
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                Log.e("MainActivity", "List Repo : ${response.body().toString()}")
                if (response.isSuccessful) {
                    // response의 body의 list가 30개면 true를 30개가 아니면 false를 반환
                    hasMore = response.body()?.count() == 30
                    // repoAdapter의 현재 리스트+response의 body의 리스트(response의 body의 리스트가 null이면 emptyList())
                    repoAdapter.submitList(repoAdapter.currentList + response.body().orEmpty())
                }
            }
            // 응답 없을 시(실패시)
            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                t.printStackTrace()
                println("에러=${t.message}")
            }
        })
    }
}