package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.adapter.ViewPagerAdapter
import ir.homelinks.homelinks.ui.fragment.GroupFragment
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_group_list.*


class GroupListActivity : AppCompatActivity() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_list)

        group_list_layout.setOnClickListener(null)
        group_list_toolbar.title = getString(R.string.groups)
        setSupportActionBar(group_list_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.notifyDataSetChanged()

        viewPagerAdapter.addFragment(GroupFragment.newInstance(""), getString(R.string.all))
        viewPagerAdapter.addFragment(GroupFragment.newInstance("whatsapp"), "")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("telegram"), "")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("soroush"), "")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("gap"), "")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("igap"), "")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("eitaa"), "")

        group_list_view_pager.adapter = viewPagerAdapter
        group_list_tab_layout.setupWithViewPager(group_list_view_pager)

        try {
            group_list_tab_layout.getTabAt(1)?.setIcon(R.mipmap.whatsapp)
            group_list_tab_layout.getTabAt(2)?.setIcon(R.mipmap.telegram)
            group_list_tab_layout.getTabAt(3)?.setIcon(R.mipmap.soroush)
            group_list_tab_layout.getTabAt(4)?.setIcon(R.mipmap.gap)
            group_list_tab_layout.getTabAt(5)?.setIcon(R.mipmap.igap)
            group_list_tab_layout.getTabAt(6)?.setIcon(R.mipmap.eitaa)
        } catch (e: Exception) {}
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.group_list_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}
