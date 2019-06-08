package ir.homelinks.homelinks.model.instagram

import com.google.gson.annotations.SerializedName
import ir.homelinks.homelinks.model.CategoryModel


class InstagramModel {
    lateinit var title: String
    lateinit var slug: String
    @SerializedName("page_id")
    lateinit var pageId: String
    var category: Int = 0
    lateinit var description: String
    lateinit var image: String
}