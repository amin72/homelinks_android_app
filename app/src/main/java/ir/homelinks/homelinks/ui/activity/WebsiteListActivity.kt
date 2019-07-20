package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.ViewPagerAdapter
import ir.homelinks.homelinks.ui.fragment.WebsiteFragment
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_website_list.*


class WebsiteListActivity : AppCompatActivity() {
    private lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_website_list)

        website_list_layout.setOnClickListener(null)
        website_list_toolbar.title = getString(R.string.websites)
        setSupportActionBar(website_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.notifyDataSetChanged()

        // set fragments
        viewPagerAdapter.addFragment(WebsiteFragment.newInstance(""), getString(R.string.all))
        viewPagerAdapter.addFragment(WebsiteFragment.newInstance("iranian"), getString(R.string.iranian))
        viewPagerAdapter.addFragment(WebsiteFragment.newInstance("foreign"), getString(R.string.foreign))

        website_list_view_pager.adapter = viewPagerAdapter
        website_list_tab_layout.setupWithViewPager(website_list_view_pager)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.website_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}
