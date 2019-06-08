package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.LinkAdapter
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.model.PaginatedResponseModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.LinkUtility
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.activity_categorized_items.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CategorizedItemsActivity : AppCompatActivity() {

    private lateinit var linkAdapter: LinkAdapter
    private var page = 1
    private var totalItems = 0
    private var receivedItems = 0
    private var isLoading = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorized_items)

        categorized_items_layout.setOnClickListener(null)


        val extras = intent.extras
        if (extras != null) {
            val categoryId = extras.getInt("id", 0)
            val categoryName = extras.getString("name", "")

            if (categoryName.isNotEmpty()) {
                categorized_items_toolbar.title = categoryName
            }
            setSupportActionBar(categorized_items_toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            if (categoryId > 0) {
                getCategorizedItems(categoryId)
            }
        } else {
            Toast.makeText(baseContext, "No category was selected!", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupRecyclerView(id: Int, linkList: List<LinkModel>) {
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
                            getPaginatedLinks(id, page, linkAdapter)
                        } else {
                            page -= 1
                        }
                    }
                }
            }
        })
    }


    private fun getCategorizedItems(categoryId: Int) {
        val call = AppController.apiInterface.getCategorizedItems(categoryId)

        call.enqueue(object: Callback<PaginatedResponseModel> {
            override fun onFailure(call: Call<PaginatedResponseModel>, t: Throwable) {
                Toast.makeText(baseContext,
                    getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<PaginatedResponseModel>,
                                    response: Response<PaginatedResponseModel>
            ) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    setupRecyclerView(categoryId, links.results)
                    totalItems = links.count // set total items
                    receivedItems = links.results.size // set received items

                } else {
                    Toast.makeText(baseContext, "Failed to fetch categorized items!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun getPaginatedLinks(categoryId: Int, page: Int, linkAdapter: LinkAdapter) {
        isLoading = true

        val call = AppController.apiInterface.getCategorizedItems(categoryId, page)

        call.enqueue(object: Callback<PaginatedResponseModel> {

            override fun onFailure(call: Call<PaginatedResponseModel>, t: Throwable) {
                Toast.makeText(baseContext, "Failed\n${t.message}", Toast.LENGTH_LONG).show()
            }


            override fun onResponse(call: Call<PaginatedResponseModel>,
                                    response: Response<PaginatedResponseModel>) {

                if (response.isSuccessful) {
                    val links = response.body()!!
                    linkAdapter.addLinks(links.results)
                    // increment receivedItems each time new links were received
                    receivedItems += links.results.size
                    isLoading = false
                } else {
                    Toast.makeText(baseContext, "Failed to retrieve links!", Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.categorized_items_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}
