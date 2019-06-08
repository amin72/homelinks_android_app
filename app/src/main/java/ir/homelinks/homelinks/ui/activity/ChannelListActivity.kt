package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.ViewPagerAdapter
import ir.homelinks.homelinks.ui.fragment.ChannelFragment
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_channel_list.*

class ChannelListActivity : AppCompatActivity() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_list)

        channel_list_layout.setOnClickListener(null)
        channel_list_toolbar.title = getString(R.string.channels)
        setSupportActionBar(channel_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.notifyDataSetChanged()

        viewPagerAdapter.addFragment(ChannelFragment.newInstance(""), "All")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("telegram"), "Telegram")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("soroush"), "Soroush")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("gap"), "Gap")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("igap"), "IGap")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("eitaa"), "Eitaa")

        channel_list_view_pager.adapter = viewPagerAdapter
        channel_list_tab_layout.setupWithViewPager(channel_list_view_pager)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.channel_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}
