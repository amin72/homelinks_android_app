package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.LinkAdapter
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.model.LinkResults
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.LinkUtility
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.activity_instagram_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class InstagramListActivity : AppCompatActivity() {

    private lateinit var linkAdapter: LinkAdapter
    private var page = 1
    private var totalItems = 0
    private var receivedItems = 0
    private var isLoading = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instagram_list)

        instagrams_list_layout.setOnClickListener(null)
        instagram_list_toolbar.title = getString(R.string.instagrams)
        setSupportActionBar(instagram_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getLinks()
    }


    private fun setupRecyclerView(linkList: List<LinkModel>) {
        linkAdapter = LinkAdapter(baseContext, linkList)

        val linearLayoutManager = LinearLayoutManager(baseContext,
            LinearLayoutManager.VERTICAL, false)

        links_recycler_view.layoutManager = linearLayoutManager
        links_recycler_view.adapter = linkAdapter

        val slideInBottomAnimatorAdapter = SlideInBottomAnimatorAdapter(linkAdapter, links_recycler_view)
        links_recycler_view.adapter = slideInBottomAnimatorAdapter

        links_recycler_view.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {

                if (recyclerView?.canScrollVertically(1) == false) {
                    if (receivedItems < totalItems) {
                        page += 1
                        if (!isLoading) {
                            getPaginatedLinks(page, linkAdapter)
                        } else {
                            page -= 1
                        }
                    }
                }
            }
        })
    }


    private fun getLinks() {
        val call = AppController.apiInterface.links("instagrams")

        call.enqueue(object: Callback<LinkResults> {

            override fun onFailure(call: Call<LinkResults>, t: Throwable) {
                Toast.makeText(baseContext, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<LinkResults>,
                                    response: Response<LinkResults>
            ) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    setupRecyclerView(links.results)
                    totalItems = links.count // set total items
                    receivedItems = links.results.size // set received items
                } else {
                    Toast.makeText(baseContext, "Failed to retrieve channels!",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun getPaginatedLinks(page: Int, linkAdapter: LinkAdapter) {
        isLoading = true

        val call = AppController.apiInterface.links("instagrams", page)

        call.enqueue(object: Callback<LinkResults> {

            override fun onFailure(call: Call<LinkResults>, t: Throwable) {
                Toast.makeText(baseContext, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<LinkResults>,
                                    response: Response<LinkResults>
            ) {

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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.instagram_list_menu, menu)

        val searchView = menu?.findItem(R.id.search)!!.actionView as SearchView

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                linkAdapter.filter.filter(query!!)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                linkAdapter.filter.filter(query!!)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}
