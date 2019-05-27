package ir.homelinks.homelinks.model.instagram

import com.google.gson.annotations.SerializedName


class InstagramUpdateModel {
    lateinit var title: String
    @SerializedName("page_id")
    lateinit var pageId: String
    var category: Int = 0
    lateinit var description: String
    lateinit var image: String
}