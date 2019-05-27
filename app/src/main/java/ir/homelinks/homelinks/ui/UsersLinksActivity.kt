package ir.homelinks.homelinks.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.LinkAdapter
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.model.LinkResults
import ir.homelinks.homelinks.model.PaginatedResponseModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.ClientConstants
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.activity_users_links.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class UsersLinksActivity : AppCompatActivity() {

    private lateinit var linkAdapter: LinkAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_links)

        setSupportActionBar(user_links_toolbar)

        getUsersLinks("instagrams") // get all links and set recycler view
    }


    private fun setupRecylerView(linkList: List<LinkModel>) {
        linkAdapter = LinkAdapter(baseContext, linkList)

        val linearLayoutManager = LinearLayoutManager(
            baseContext,
            LinearLayoutManager.VERTICAL, false
        )

        linkRecycler.layoutManager = linearLayoutManager
        linkRecycler.adapter = linkAdapter

        val slideInBottomAnimatorAdapter = SlideInBottomAnimatorAdapter(linkAdapter, linkRecycler)
        linkRecycler.adapter = slideInBottomAnimatorAdapter
    }


    // handle pagination
    private fun getUsersLinks(link: String, page: Int = 1) {
        val token = "token ${ClientConstants.TOKEN}"

        val call = AppController.apiInterface.getUsersLinks(token, link, page)

        call.enqueue(object : Callback<PaginatedResponseModel> {
            override fun onFailure(call: Call<PaginatedResponseModel>, t: Throwable) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }


            override fun onResponse(
                call: Call<PaginatedResponseModel>,
                response: Response<PaginatedResponseModel>
            ) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    setupRecylerView(links.results)
                } else {
                    Toast.makeText(
                        baseContext, "Failed to retrieve list!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }
}
