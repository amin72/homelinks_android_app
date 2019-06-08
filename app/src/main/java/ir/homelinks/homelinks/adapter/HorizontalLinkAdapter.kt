package ir.homelinks.homelinks.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.ui.activity.LinkDetailActivity
import ir.homelinks.homelinks.utility.ClientConstants
import kotlinx.android.synthetic.main.link_list_row.view.*


class HorizontalLinkAdapter(private var context: Context,
                            private val links: List<LinkModel>):
    RecyclerView.Adapter<HorizontalLinkAdapter.ViewHolder>() {

    var linkList = links.toMutableList()
    var filteredLinks = linkList


    // RecyclerView.Adapter method
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalLinkAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.link_list_horizontal_row, parent, false)
        return ViewHolder(view)
    }


    // RecyclerView.Adapter method
    override fun getItemCount(): Int {
        return filteredLinks.size
    }


    // RecyclerView.Adapter method
    override fun onBindViewHolder(holder: HorizontalLinkAdapter.ViewHolder, position: Int) {
        val link = filteredLinks[position]

        holder.title.text = link.title

        val thumbnailPath = "${ClientConstants.HOMELINKS_URL}${link.thumbnail}"
        Picasso.get().load(thumbnailPath).into(holder.thumbnail)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, LinkDetailActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            val splitDetailUrl = link.detail_url.split("/").takeLast(3)
            val link = splitDetailUrl[0]
            val slug = splitDetailUrl[1]

            intent.putExtra("link", link)
            intent.putExtra("slug", slug)
            context.startActivity(intent)
        }
    }


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val title: TextView = view.link_title
        val thumbnail: ImageView = view.link_thumbnail
    }
}