package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.IndexResult
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.AppPreferenceTools
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreferenceTools


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_toolbar)

        appPreference = AppPreferenceTools(baseContext)

        if (appPreference.isAuthrorized()) {
            setIndexItems()
        } else {
            startActivity(Intent(baseContext, SignInActivity::class.java))
            finish()
        }


        load_websites_button.setOnClickListener {
            startActivity(Intent(this, WebsiteListActivity::class.java))
        }

        load_channels_button.setOnClickListener {
            startActivity(Intent(this, ChannelListActivity::class.java))
        }

        load_groups_button.setOnClickListener {
            startActivity(Intent(this, GroupListActivity::class.java))
        }

        load_instagrams_button.setOnClickListener {
            startActivity(Intent(this, InstagramListActivity::class.java))
        }

        //startActivity(Intent(this, LinkListActivity::class.java)) // +
        //startActivity(Intent(this, SignInActivity::class.java)) // +
        //startActivity(Intent(this, SignUpActivity::class.java)) // +

        // Add `update` to next four activities
        // handle errors

//        val websiteIntent = Intent(this, WebsiteCreateOrUpdateActivity::class.java)
//        websiteIntent.putExtra("slug", "website6-com")
//        startActivity(websiteIntent)

//        val channelIntent = Intent(this, ChannelCreateOrUpdateActivity::class.java)
//        channelIntent.putExtra("slug", "telegram-channel3")
//        startActivity(channelIntent)

//        val groupIntent = Intent(this, GroupCreateOrUpdateActivity::class.java)
//        groupIntent.putExtra("slug", "whatsapp-group4-fb621b6cf-e9e7-4eba-9066-bf14bbd1c8e2")
//        startActivity(groupIntent)

//        val instagramIntent = Intent(this, InstagramCreateOrUpdateActivity::class.java)
//        instagramIntent.putExtra("slug", "ig-page4")
//        startActivity(instagramIntent)

        //startActivity(Intent(this, WebsiteCreateOrUpdateActivity::class.java)) // + add image field
        //startActivity(Intent(this, ChannelCreateOrUpdateActivity::class.java)) // + add image field
        //startActivity(Intent(this, GroupCreateOrUpdateActivity::class.java)) // + add image field
        //startActivity(Intent(this, InstagramCreateOrUpdateActivity::class.java)) // + add image field

        //startActivity(Intent(this, ReportLinkActivity::class.java)) // +
        //startActivity(Intent(this, ContactUsActivity::class.java)) // +
        //startActivity(Intent(this, UpdateUserActivity::class.java)) // +
        //startActivity(Intent(this, UserChangePassword::class.java)) // +
        //startActivity(Intent(this, ResetPasswordActivity::class.java)) // +
        //startActivity(Intent(this, CategoryListActivity::class.java)) //‌ +

//        val categorizedItemsIntent = Intent(this, CategorizedItemsActivity::class.java)
//        categorizedItemsIntent.putExtra("id", 9)
//        startActivity(categorizedItemsIntent)

        //startActivity(Intent(this, UsersLinksActivity::class.java)) // + handle pagination

//        val taggedItemsIntent = Intent(this, TaggedItemsActivity::class.java)
//        taggedItemsIntent.putExtra("tag", "sport")
//        startActivity(taggedItemsIntent)

        //startActivity(Intent(this, SearchLinksActivity::class.java))

        // find a way to display tags in link detail activity
        // handle ui and scrolling
        //startActivity(Intent(this, LinkDetailActivity::class.java)) // +

        //startActivity(Intent(this, AddLinkActivity::class.java)) // select link type
    }


    private fun setIndexItems() {
        // index page
        val call = AppController.apiInterface.index()

        call.enqueue(object: Callback<IndexResult> {
            override fun onFailure(call: Call<IndexResult>, t: Throwable) {
                Toast.makeText(baseContext, "Fail!", Toast.LENGTH_SHORT).show()
                //Log.d("---------Fail", t.message)
            }

            override fun onResponse(call: Call<IndexResult>, response: Response<IndexResult>) {
                Toast.makeText(baseContext, "Success!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.add_new_link -> {
                startActivity(Intent(this, AddLinkActivity::class.java))
            }

            R.id.dashboard -> {
                startActivity(Intent(this, DashboardActivity::class.java))
            }

            R.id.categories -> {
                startActivity(Intent(this, CategoryListActivity::class.java))
            }

            R.id.contact_us -> {
                startActivity(Intent(this, ContactUsActivity::class.java))
            }

            R.id.about_us -> {
                startActivity(Intent(this, AboutUsActivity::class.java))
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
