package ir.homelinks.homelinks.model.channel

import com.google.gson.annotations.SerializedName
import ir.homelinks.homelinks.model.CategoryModel

class ChannelModel {
    lateinit var application: String
    lateinit var title: String
    lateinit var slug: String
    var category: Int = 0
    lateinit var description: String
    lateinit var image: String

    @SerializedName("channel_id")
    lateinit var channelId: String
}
