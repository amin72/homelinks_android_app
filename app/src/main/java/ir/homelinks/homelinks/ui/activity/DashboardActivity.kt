package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.ViewPagerAdapter
import ir.homelinks.homelinks.ui.fragment.AllLinksFragment
import ir.homelinks.homelinks.ui.fragment.UserLinksFragment
import ir.homelinks.homelinks.utility.AppPreferenceTools
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreferenceTools
    private lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        dashboard_toolbar.title = "Dashboard"
        setSupportActionBar(dashboard_toolbar)

        appPreference = AppPreferenceTools(baseContext)

        if (appPreference.isAuthrorized()) {
            viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
            viewPagerAdapter.notifyDataSetChanged()

            viewPagerAdapter.addFragment(AllLinksFragment(), "All")
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
}
