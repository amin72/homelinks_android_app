package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.LinkAdapter
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.model.PaginatedResponseModel
import ir.homelinks.homelinks.utility.AppController
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.activity_search_links.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.regex.Pattern


class SearchLinksActivity : AppCompatActivity() {

    private lateinit var linkAdapter: LinkAdapter
    private var page = 1
    private var totalItems = 0
    private var receivedItems = 0
    private var isLoading = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_links)

        search_layout.setOnClickListener(null)
        search_toolbar.title = getString(R.string.instagrams)
        setSupportActionBar(search_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        search_text.addTextChangedListener(object: TextWatcher {
            var timer: Timer? = null

            override fun afterTextChanged(s: Editable?) {
                // user typed: start the timer
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        getLinks(s.toString())
                    }
                }, 400) // 600ms delay before the timer executes the "run" method from TimerTask
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing to do here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // user is typing: reset already started timer (if existing)
                if (timer != null) {
                    timer?.cancel()
                }
            }
        })
    }


    private fun setupRecyclerView(searchText: String, linkList: List<LinkModel>) {
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
                            getPaginatedLinks(searchText, page, linkAdapter)
                        } else {
                            page -= 1
                        }
                    }
                }
            }
        })
    }


    private fun getLinks(searchText: String) {
        val call = AppController.apiInterface.search(searchText)

        call.enqueue(object: Callback<PaginatedResponseModel> {

            override fun onFailure(call: Call<PaginatedResponseModel>, t: Throwable) {
                Toast.makeText(baseContext, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<PaginatedResponseModel>,
                                    response: Response<PaginatedResponseModel>
            ) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    setupRecyclerView(searchText, links.results)
                    totalItems = links.count // set total items
                    receivedItems = links.results.size // set received items
                } else {
                }
            }
        })
    }


    private fun getPaginatedLinks(searchText: String, page: Int, linkAdapter: LinkAdapter) {
        isLoading = true

        val call = AppController.apiInterface.search(searchText, page)

        call.enqueue(object: Callback<PaginatedResponseModel> {

            override fun onFailure(call: Call<PaginatedResponseModel>, t: Throwable) {
                Toast.makeText(baseContext, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<PaginatedResponseModel>,
                                    response: Response<PaginatedResponseModel>
            ) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    linkAdapter.addLinks(links.results)
                    // increment receivedItems each time new links were received
                    receivedItems += links.results.size
                    isLoading = false
                } else {
                }
            }
        })
    }


    private fun search(term: String, page: Int = 1) {

        val call = AppController.apiInterface.search("website", page)

        call.enqueue(object: Callback<PaginatedResponseModel> {
            override fun onFailure(call: Call<PaginatedResponseModel>, t: Throwable) {
                Toast.makeText(baseContext,
                    getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
                Log.d("------------", t.message)
            }

            override fun onResponse(call: Call<PaginatedResponseModel>,
                                    response: Response<PaginatedResponseModel>
            ) {

                if (response.isSuccessful) {
                    val paginatedObj = response.body()!!

                    for (link in paginatedObj.results) {
                        Toast.makeText(baseContext, "${link.title} : ${link.detail_url}",
                            Toast.LENGTH_SHORT).show()
                    }

                    if (paginatedObj.next != null) {
                        Toast.makeText(baseContext, paginatedObj.next, Toast.LENGTH_SHORT).show()
                        var page: Int = 0

                        val p = Pattern.compile("-?\\d+")
                        val m = p.matcher(paginatedObj.next)
                        while (m.find()) {
                            page = m.group().toInt()
                        }

                        Toast.makeText(baseContext, "Next page #$page", Toast.LENGTH_SHORT).show()

                        if (page > 0) {
                            search(term, page)
                        }
                    } else {
                        Toast.makeText(baseContext, "No Next", Toast.LENGTH_SHORT).show()
                    }


                    if (paginatedObj.previous != null) {
                        Toast.makeText(baseContext, paginatedObj.previous, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "No Previous", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(baseContext, "Failed to find $term!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
