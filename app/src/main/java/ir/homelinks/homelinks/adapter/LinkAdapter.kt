package ir.homelinks.homelinks.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.LinkModel
import ir.homelinks.homelinks.ui.activity.LinkDetailActivity
import ir.homelinks.homelinks.utility.ClientConstants
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.link_list_row.view.*


class LinkAdapter(private var context: Context,
                  private val links: List<LinkModel>):
    RecyclerView.Adapter<LinkAdapter.ViewHolder>(), Filterable {

    var linkList = links.toMutableList()
    var filteredLinks = linkList


    // RecyclerView.Adapter method
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.link_list_row, parent, false)
        return ViewHolder(view)
    }


    // RecyclerView.Adapter method
    override fun getItemCount(): Int {
        return filteredLinks.size
    }


    // RecyclerView.Adapter method
    override fun onBindViewHolder(holder: LinkAdapter.ViewHolder, position: Int) {
        val link = filteredLinks[position]

        holder.title.text = link.title

        val thumbnailPath = "${ClientConstants.HOMELINKS_URL}${link.thumbnail}"
        Picasso.get().load(thumbnailPath).into(holder.thumbnail)

        val createdAt = "${context.getString(R.string.created_at)}: ${LinkUtility.convertDate(link.created)}"
        holder.created.text = createdAt

        if (link.status.isNotEmpty()) {
            holder.status.visibility = View.VISIBLE
            val status = "${context.getString(R.string.status)}: ${link.status.capitalize()}"
            holder.status.text = status
        }

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
        val created: TextView = view.link_created
        val thumbnail: ImageView = view.link_thumbnail
        val status: TextView = view.status
    }


    // Filterable method
    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val query = constraint?.toString()

                try {
                    if (query!!.isEmpty()) {
                        filteredLinks = linkList
                    } else {
                        var filteredList = mutableListOf<LinkModel>()

                        for (link in linkList) {

                            if (link.title.toLowerCase().contains(query)) {
                                filteredList.add(link)
                            }
                        }

                        filteredLinks = filteredList
                    }
                } catch (e: Exception) {
                    filteredLinks = linkList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredLinks
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }


    fun addLinks(links: List<LinkModel>) {
        for (link in links) {
            linkList.add(link)
        }
        notifyDataSetChanged()
    }
}