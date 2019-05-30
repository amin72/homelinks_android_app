package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.squareup.picasso.Picasso
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_link_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LinkDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_detail)

        setSupportActionBar(link_detail_toolbar)

        val extras = intent.extras
        if (extras != null) {
            val link = extras.getString("link", "")
            val slug = extras.getString("slug", "")

            if (link.isNotEmpty() && slug.isNotEmpty()) {

                val call = AppController.apiInterface.linkDetail(link, slug)

                call.enqueue(object: Callback<LinkModel> {
                    override fun onFailure(call: Call<LinkModel>, t: Throwable) {
                        Toast.makeText(baseContext,
                            getString(R.string.failed_to_connect_to_server).toString(),
                            Toast.LENGTH_SHORT).show()
                        Log.d("------------", t.message)
                    }

                    override fun onResponse(call: Call<LinkModel>, response: Response<LinkModel>) {

                        if (response.isSuccessful) {
                            val link = response.body()!!

                            link_title.text = link.title
                            link_description.text = link.description

                            val author = "${getString(R.string.posted_by)}: ${link.author.username}"
                            link_author.text = author

                            val createdAt = "${getString(R.string.created_at)}: ${LinkUtility.convertDate(link.created)}"
                            link_created_at.text = createdAt

                            Picasso.get().load(link.image)
                                //.transform(CropCircleTransformation())
                                .into(link_image)
                        }
                    }
                })
            }
        } else {
            Toast.makeText(baseContext, "Link type or Slug field not set!", Toast.LENGTH_SHORT).show()
        }
    }
}
