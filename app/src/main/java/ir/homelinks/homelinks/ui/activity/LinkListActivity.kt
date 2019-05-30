package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.LinkAdapter
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.model.LinkResults
import ir.homelinks.homelinks.utility.AppController
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.activity_link_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LinkListActivity : AppCompatActivity() {

    private lateinit var linkAdapter: LinkAdapter
    private var page = 1
    private var totalItems = 0
    private var receivedItems = 0
    private var isLoading = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_list)

        setSupportActionBar(link_list_toolbar)

        val extras = intent.extras
        if (extras != null) {
            val link = extras.getString("link", "")
            if (link.isNotEmpty()) {
                getLinks(link)
            }
        } else {
            Toast.makeText(baseContext, "No category was selected!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupRecyclerView(link: String, linkList: List<LinkModel>) {
        linkAdapter = LinkAdapter(baseContext, linkList)

        val linearLayoutManager = LinearLayoutManager(baseContext,
            LinearLayoutManager.VERTICAL, false)

        linkRecycler.layoutManager = linearLayoutManager
        linkRecycler.adapter = linkAdapter

        val slideInBottomAnimatorAdapter = SlideInBottomAnimatorAdapter(linkAdapter, linkRecycler)
        linkRecycler.adapter = slideInBottomAnimatorAdapter

        linkRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

                if (recyclerView?.canScrollVertically(1) == false) {
                    if (receivedItems < totalItems) {
                        page += 1
                        if (!isLoading) {
                            getPaginatedLinks(link, page, linkAdapter)
                        } else {
                            page -= 1
                        }
                    }
                }
            }
        })
    }


    private fun getLinks(link: String) {
        val call = AppController.apiInterface.links(link)

        call.enqueue(object: Callback<LinkResults> {

            override fun onFailure(call: Call<LinkResults>, t: Throwable) {
                Toast.makeText(baseContext, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<LinkResults>,
                                    response: Response<LinkResults>) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    setupRecyclerView(link, links.results)
                    totalItems = links.count // set total items
                    receivedItems = links.results.size // set received items
                } else {
                    Toast.makeText(baseContext, "Failed to retrieve channels!",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun getPaginatedLinks(link: String, page: Int, linkAdapter: LinkAdapter) {
        isLoading = true

        val call = AppController.apiInterface.links(link, page)

        call.enqueue(object: Callback<LinkResults> {

            override fun onFailure(call: Call<LinkResults>, t: Throwable) {
                Toast.makeText(baseContext, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<LinkResults>,
                                    response: Response<LinkResults>) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    linkAdapter.addLinks(links.results)
                    // increment receivedItems each time new links were received
                    receivedItems += links.results.size
                    isLoading = false
                } else {
                    Toast.makeText(baseContext, "Failed to retrieve channels!",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
