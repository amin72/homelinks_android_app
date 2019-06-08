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

        viewPagerAdapter.addFragment(GroupFragment.newInstance(""), "All")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("whatsapp"), "Whatsapp")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("telegram"), "Telegram")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("soroush"), "Soroush")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("gap"), "Gap")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("igap"), "IGap")
        viewPagerAdapter.addFragment(GroupFragment.newInstance("eitaa"), "Eitaa")

        group_list_view_pager.adapter = viewPagerAdapter
        group_list_tab_layout.setupWithViewPager(group_list_view_pager)
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
