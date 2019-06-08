package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.CategoryAdapter
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.LinkUtility
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.activity_category_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryListActivity : AppCompatActivity() {

    private lateinit var categoryAdapter: CategoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        category_list_toolbar.title = getString(R.string.categories)
        setSupportActionBar(category_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getCategories() // get all categories and set recycler view
    }


    private fun setupRecyclerView(categoryList: List<CategoryModel>) {
        categoryAdapter = CategoryAdapter(baseContext, categoryList)

        val linearLayoutManager = LinearLayoutManager(baseContext,
            LinearLayoutManager.VERTICAL, false)

        categoryRecycler.layoutManager = linearLayoutManager
        categoryRecycler.adapter = categoryAdapter

        val slideInBottomAnimatorAdapter = SlideInBottomAnimatorAdapter(categoryAdapter, categoryRecycler)
        categoryRecycler.adapter = slideInBottomAnimatorAdapter
    }


    private fun getCategories() {
        val call = AppController.apiInterface.getCategories()

        call.enqueue(object: Callback<List<CategoryModel>> {
            override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable) {
                Toast.makeText(baseContext,
                    getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<List<CategoryModel>>,
                                    response: Response<List<CategoryModel>>
            ) {

                if (response.isSuccessful) {
                    val categories = response.body()!!
                    setupRecyclerView(categories)
                } else {
                    Toast.makeText(baseContext, "Failed to retrieve tasks!",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.categories_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}
