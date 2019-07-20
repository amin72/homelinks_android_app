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

        viewPagerAdapter.addFragment(ChannelFragment.newInstance(""), getString(R.string.all))
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("telegram"), "")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("soroush"), "")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("gap"), "")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("igap"), "")
        viewPagerAdapter.addFragment(ChannelFragment.newInstance("eitaa"), "")

        channel_list_view_pager.adapter = viewPagerAdapter
        channel_list_tab_layout.setupWithViewPager(channel_list_view_pager)

        try {
            channel_list_tab_layout.getTabAt(1)?.setIcon(R.mipmap.telegram)
            channel_list_tab_layout.getTabAt(2)?.setIcon(R.mipmap.soroush)
            channel_list_tab_layout.getTabAt(3)?.setIcon(R.mipmap.gap)
            channel_list_tab_layout.getTabAt(4)?.setIcon(R.mipmap.igap)
            channel_list_tab_layout.getTabAt(5)?.setIcon(R.mipmap.eitaa)
        } catch (e: Exception) {}
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
