package ir.homelinks.homelinks.ui.activity

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
import kotlinx.android.synthetic.main.activity_user_links.*


class UserLinksActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreferenceTools
    private lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_links)

        dashboard_layout.setOnClickListener(null)
        dashboard_toolbar.title = getString(R.string.dashboard)
        setSupportActionBar(dashboard_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appPreference = AppPreferenceTools(baseContext)

        if (appPreference.isAuthorized()) {
            viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
            viewPagerAdapter.notifyDataSetChanged()

            viewPagerAdapter.addFragment(AllUserLinksFragment(), getString(R.string.all))
            viewPagerAdapter.addFragment(UserLinksFragment.newInstance("websites"), "")
            viewPagerAdapter.addFragment(UserLinksFragment.newInstance("channels"), "")
            viewPagerAdapter.addFragment(UserLinksFragment.newInstance("groups"), "")
            viewPagerAdapter.addFragment(UserLinksFragment.newInstance("instagrams"), "")

            dashboard_view_pager.adapter = viewPagerAdapter
            dashboard_tab_layout.setupWithViewPager(dashboard_view_pager)

            try {
                dashboard_tab_layout.getTabAt(1)?.setIcon(R.mipmap.website)
                dashboard_tab_layout.getTabAt(2)?.setIcon(R.mipmap.channel)
                dashboard_tab_layout.getTabAt(3)?.setIcon(R.mipmap.group)
                dashboard_tab_layout.getTabAt(4)?.setIcon(R.mipmap.instagram)
            } catch (e: Exception) {}
        } else {
            Toast.makeText(baseContext, getString(R.string.user_not_authorized), Toast.LENGTH_SHORT).show()
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