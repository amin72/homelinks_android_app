package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.ViewPagerAdapter
import ir.homelinks.homelinks.ui.fragment.AllUserLinksFragment
import ir.homelinks.homelinks.ui.fragment.UserLinksFragment
import ir.homelinks.homelinks.utility.AppPreferenceTools
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_dashboard.*


class UserLinksActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreferenceTools
    private lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        dashboard_layout.setOnClickListener(null)
        dashboard_toolbar.title = getString(R.string.dashboard)
        setSupportActionBar(dashboard_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        appPreference = AppPreferenceTools(baseContext)

        if (appPreference.isAuthorized()) {
            viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
            viewPagerAdapter.notifyDataSetChanged()

            viewPagerAdapter.addFragment(AllUserLinksFragment(), "All")
            viewPagerAdapter.addFragment(UserLinksFragment.newInstance("websites"), "Websites")
            viewPagerAdapter.addFragment(UserLinksFragment.newInstance("channels"), "Channels")
            viewPagerAdapter.addFragment(UserLinksFragment.newInstance("groups"), "Groups")
            viewPagerAdapter.addFragment(UserLinksFragment.newInstance("instagrams"), "Instagrams")

            dashboard_view_pager.adapter = viewPagerAdapter
            dashboard_tab_layout.setupWithViewPager(dashboard_view_pager)
        } else {
            Toast.makeText(baseContext, "No category was selected!", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_links_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}