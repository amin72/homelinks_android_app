package ir.homelinks.homelinks.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.ui.activity.CategorizedItemsActivity
import kotlinx.android.synthetic.main.category_list_row.view.*


class CategoryAdapter(private var context: Context,
                  private var categoryList: List<CategoryModel>):
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {


    // RecyclerView.Adapter method
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.category_list_row, parent, false)
        return ViewHolder(view)
    }


    // RecyclerView.Adapter method
    override fun getItemCount(): Int {
        return categoryList.size
    }


    // RecyclerView.Adapter method
    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolder, position: Int) {
        val category = categoryList[position]

        holder.name.text = category.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CategorizedItemsActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("id", category.id)
            context.startActivity(intent)
        }
    }


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        //val id = view.category_id
        val name: TextView = view.category_name
    }
}