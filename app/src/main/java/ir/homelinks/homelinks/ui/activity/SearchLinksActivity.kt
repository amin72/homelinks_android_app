package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.PaginatedResponseModel
import ir.homelinks.homelinks.utility.AppController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern


class SearchLinksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_links)

        search("website")
    }


    private fun search(term: String, page: Int = 1) {

        val call = AppController.apiInterface.search("website", page)

        call.enqueue(object: Callback<PaginatedResponseModel> {
            override fun onFailure(call: Call<PaginatedResponseModel>, t: Throwable) {
                Toast.makeText(baseContext,
                    getString(R.string.failed_to_connect_to_server).toString(),
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
