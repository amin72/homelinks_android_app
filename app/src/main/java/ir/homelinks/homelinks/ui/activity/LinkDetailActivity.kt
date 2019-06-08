package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Picasso
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.AppPreferenceTools
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_link_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LinkDetailActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreferenceTools
    var alertDialog: AlertDialog? = null
    var alertBuilder: AlertDialog.Builder? = null
    private lateinit var linkUrl: String
    private lateinit var slug: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_detail)

        setSupportActionBar(link_detail_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appPreference =  AppPreferenceTools(this)

        val extras = intent.extras
        if (extras != null) {
            linkUrl = extras.getString("link", "")
            slug = extras.getString("slug", "")

            if (linkUrl.isNotEmpty() && slug.isNotEmpty()) {
                val call = AppController.apiInterface.linkDetail(linkUrl, slug)

                call.enqueue(object: Callback<LinkModel> {
                    override fun onFailure(call: Call<LinkModel>, t: Throwable) {
                        Toast.makeText(baseContext,
                            getString(R.string.failed_connect_to_server).toString(),
                            Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<LinkModel>, response: Response<LinkModel>) {
                        if (response.isSuccessful) {
                            val link = response.body()!!

                            // make delete and edit button visible for linkUrl's owner
                            if (appPreference.isAuthorized() && appPreference.getUserName().equals(link.author.username)) {
                                action_layout.visibility = View.VISIBLE
                            }

                            link_title.text = link.title
                            link_description.text = link.description

                            val author = "${getString(R.string.posted_by)}: ${link.author.username}"
                            link_author.text = author

                            val createdAt = "${getString(R.string.created_at)}: ${LinkUtility.convertDate(link.created)}"
                            link_created_at.text = createdAt

                            Picasso.get().load(link.image)
                                .into(link_image)


                            join_link_button.setOnClickListener {
                                when (linkUrl) {
                                    getString(R.string.website).toLowerCase() -> {
                                        val uri = Uri.parse(link.url)
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        startActivity(intent)
                                    }

                                    getString(R.string.channel).toLowerCase() -> {
                                        Toast.makeText(baseContext, "Channel!", Toast.LENGTH_SHORT).show()
                                    }

                                    getString(R.string.group).toLowerCase() -> {
                                        Toast.makeText(baseContext, "Group!", Toast.LENGTH_SHORT).show()
                                    }

                                    getString(R.string.instagram).toLowerCase() -> {
                                        Toast.makeText(baseContext, "Instagram!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                })

                report_link_button.setOnClickListener {
                    val reportIntent = Intent(this, ReportLinkActivity::class.java)
                    reportIntent.putExtra("link", linkUrl)
                    reportIntent.putExtra("slug", slug)
                    startActivity(reportIntent)
                }

                edit_link_button.setOnClickListener {
                    when (linkUrl) {
                        getString(R.string.website).toLowerCase() -> {
                            val intent = Intent(this, WebsiteCreateOrUpdateActivity::class.java)
                            intent.putExtra("slug", slug)
                            startActivity(intent)
                        }

                        getString(R.string.channel).toLowerCase() -> {
                            val intent = Intent(this, ChannelCreateOrUpdateActivity::class.java)
                            intent.putExtra("slug", slug)
                            startActivity(intent)
                        }

                        getString(R.string.group).toLowerCase() -> {
                            val intent = Intent(this, GroupCreateOrUpdateActivity::class.java)
                            intent.putExtra("slug", slug)
                            startActivity(intent)
                        }

                        getString(R.string.instagram).toLowerCase() -> {
                            val intent = Intent(this, InstagramCreateOrUpdateActivity::class.java)
                            intent.putExtra("slug", slug)
                            startActivity(intent)
                        }
                    }
                }

                delete_link_button.setOnClickListener {
                    // open dialog
                    alertBuilder = AlertDialog.Builder(this)
                    //alertBuilder?.setTitle(getString(R.string.are_you_sure))
                    alertBuilder?.setMessage(getString(R.string.you_want_to_delete_this_link))

                    // Delete button
                    alertBuilder?.setPositiveButton(getString(R.string.delete) ) { dialog, which ->
                        // user is sure so remove the linkUrl
                        val token = "token ${appPreference.getUserToken()}"
                        val removeLinkCall = AppController.apiInterface.removeLink(token, linkUrl, slug)

                        removeLinkCall.enqueue(object: Callback<Unit> {
                            override fun onFailure(call: Call<Unit>, t: Throwable) {
                                Toast.makeText(baseContext, getString(R.string.link_remove_failed), Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                                Toast.makeText(baseContext, getString(R.string.link_removed), Toast.LENGTH_LONG).show()
                                startActivity(Intent(baseContext, MainActivity::class.java))
                            }
                        })
                    }

                    // Cancel button
                    alertBuilder?.setNegativeButton(getString(R.string.cancel)) { dialog, which -> }

                    alertDialog = alertBuilder?.create()
                    alertDialog?.show()
                }
            }
        } else {
            Toast.makeText(baseContext, getString(R.string.link_or_slug_not_set), Toast.LENGTH_SHORT).show()
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.link_detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}
