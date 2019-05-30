package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.CategoryAdapter
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.ClientConstants
import it.gmariotti.recyclerview.adapter.SlideInBottomAnimatorAdapter
import kotlinx.android.synthetic.main.activity_category_list.*
import kotlinx.android.synthetic.main.category_list_row.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CategoryListActivity : AppCompatActivity() {

    private lateinit var categoryAdapter: CategoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_list)

        setSupportActionBar(category_list_toolbar)

        getCategories() // get all categories and set recycler view
    }


    private fun setupRecylerView(categoryList: List<CategoryModel>) {
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
                    getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<List<CategoryModel>>,
                                    response: Response<List<CategoryModel>>
            ) {

                if (response.isSuccessful) {
                    val categories = response.body()!!
                    setupRecylerView(categories)
                } else {
                    Toast.makeText(baseContext, "Failed to retrieve tasks!",
                        Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
