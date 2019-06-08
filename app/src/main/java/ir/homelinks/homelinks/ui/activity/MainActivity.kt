package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.HorizontalLinkAdapter
import ir.homelinks.homelinks.model.IndexResult
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.AppPreferenceTools
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreferenceTools
    private lateinit var websitesAdapter: HorizontalLinkAdapter
    private lateinit var channelsAdapter: HorizontalLinkAdapter
    private lateinit var groupsAdapter: HorizontalLinkAdapter
    private lateinit var instagramsAdapter: HorizontalLinkAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_toolbar)

        appPreference = AppPreferenceTools(baseContext)

        if (appPreference.isAuthorized()) {
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
    }


    private fun setIndexItems() {
        // index page
        val call = AppController.apiInterface.index()

        call.enqueue(object: Callback<IndexResult> {
            override fun onFailure(call: Call<IndexResult>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_connect_to_server), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<IndexResult>, response: Response<IndexResult>) {
                val paginatedResponse = response.body()!!
                val websites = paginatedResponse.websites
                val channels = paginatedResponse.channels
                val groups = paginatedResponse.groups
                val instagrams = paginatedResponse.instagrams

                // create adapters
                websitesAdapter = HorizontalLinkAdapter(baseContext, websites)
                channelsAdapter = HorizontalLinkAdapter(baseContext, channels)
                groupsAdapter = HorizontalLinkAdapter(baseContext, groups)
                instagramsAdapter = HorizontalLinkAdapter(baseContext, instagrams)

                // set recyclers' layout managers and adapters
                websites_recyclerView.layoutManager = LinearLayoutManager(baseContext,
                    LinearLayoutManager.HORIZONTAL, false)
                websites_recyclerView.adapter = websitesAdapter

                channels_recyclerView.layoutManager = LinearLayoutManager(baseContext,
                    LinearLayoutManager.HORIZONTAL, false)
                channels_recyclerView.adapter = channelsAdapter

                groups_recyclerView.layoutManager = LinearLayoutManager(baseContext,
                    LinearLayoutManager.HORIZONTAL, false)
                groups_recyclerView.adapter = groupsAdapter

                instagrams_recyclerView.layoutManager = LinearLayoutManager(baseContext,
                    LinearLayoutManager.HORIZONTAL, false)
                instagrams_recyclerView.adapter = instagramsAdapter
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }


    override fun onResume() {
        super.onResume()

        if (appPreference.isAuthorized()) {
            setIndexItems()
        } else {
            startActivity(Intent(baseContext, SignInActivity::class.java))
            finish()
        }
    }
}
