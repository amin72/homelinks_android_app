package ir.homelinks.homelinks.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.LinkAdapter
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.model.LinkResults
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.AppPreferenceTools
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.links_fragment.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllUserLinksFragment: Fragment() {

    private lateinit var appPreference: AppPreferenceTools
    private lateinit var linkAdapter: LinkAdapter
    private var page = 1
    private var totalItems = 0
    private var receivedItems = 0
    private var isLoading = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.links_fragment, container, false)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appPreference = AppPreferenceTools(context!!)
        getLinks()
    }


    private fun setupRecyclerView(linkList: List<LinkModel>) {
        linkAdapter = LinkAdapter(context!!, linkList)

        val linearLayoutManager = LinearLayoutManager(context,
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
        val token = "token ${appPreference.getUserToken()}"
        val call = AppController.apiInterface.dashboard(token)

        call.enqueue(object: Callback<LinkResults> {

            override fun onFailure(call: Call<LinkResults>, t: Throwable) {
                Toast.makeText(context, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<LinkResults>,
                                    response: Response<LinkResults>) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    setupRecyclerView(links.results)
                    totalItems = links.count // set total items
                    receivedItems = links.results.size // set received items
                } else {
                    Toast.makeText(context, "Failed to retrieve user's links!",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    private fun getPaginatedLinks(page: Int, linkAdapter: LinkAdapter) {
        isLoading = true

        val token = "token ${appPreference.getUserToken()}"
        val call = AppController.apiInterface.dashboard(token, page)

        call.enqueue(object: Callback<LinkResults> {

            override fun onFailure(call: Call<LinkResults>, t: Throwable) {
                Toast.makeText(context, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(context, "Failed to retrieve user's links!",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}