package ir.homelinks.homelinks.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Toast

import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.LinkAdapter
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.model.LinkResults
import ir.homelinks.homelinks.utility.AppController
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.fragment_group.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_PARAM1 = "app"


class GroupFragment : Fragment() {
    private var app: String? = null
    private lateinit var linkAdapter: LinkAdapter
    private var page = 1
    private var totalItems = 0
    private var receivedItems = 0
    private var isLoading = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            app = it.getString(ARG_PARAM1)
        }

        if (arguments != null) {
            app = arguments!!.getString("app", "")
            getLinks()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_group, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(app: String) =
            GroupFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, app)
                }
            }
    }


    private fun setupRecyclerView(linkList: List<LinkModel>) {
        linkAdapter = LinkAdapter(context!!, linkList)

        val linearLayoutManager = LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL, false)

        links_recycler_view.layoutManager = linearLayoutManager
        links_recycler_view.adapter = linkAdapter

        val slideInBottomAnimatorAdapter =
            SlideInBottomAnimatorAdapter(linkAdapter, links_recycler_view)
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
        val call = AppController.apiInterface.links("groups", app=app)

        call.enqueue(object: Callback<LinkResults> {

            override fun onFailure(call: Call<LinkResults>, t: Throwable) {
                Toast.makeText(context, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<LinkResults>, response: Response<LinkResults>) {
                if (response.isSuccessful) {
                    val links = response.body()!!
                    setupRecyclerView(links.results)
                    totalItems = links.count // set total items
                    receivedItems = links.results.size // set received items
                }
            }
        })
    }


    private fun getPaginatedLinks(page: Int, linkAdapter: LinkAdapter) {
        isLoading = true

        val call = AppController.apiInterface.links("groups", page, app=app)

        call.enqueue(object: Callback<LinkResults> {

            override fun onFailure(call: Call<LinkResults>, t: Throwable) {
                Toast.makeText(context, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
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
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater?.inflate(R.menu.group_list_menu, menu)

        val searchView = menu.findItem(R.id.search)!!.actionView as SearchView

        menu.findItem(R.id.search).apply {
            setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
            actionView = searchView
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                linkAdapter.filter.filter(query!!)
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                linkAdapter.filter.filter(query!!)
                return false
            }
        })
    }
}
