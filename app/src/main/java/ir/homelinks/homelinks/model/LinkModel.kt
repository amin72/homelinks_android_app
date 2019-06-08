package ir.homelinks.homelinks.model

class LinkModel {
    // Common fields
    var id: Int = 0
    lateinit var title: String
    lateinit var slug: String
    lateinit var url: String
    lateinit var detail_url: String
    lateinit var thumbnail: String
    lateinit var created: String
    lateinit var image: String
    lateinit var updated: String
    lateinit var description: String
    lateinit var author: UserModel
    var category: Int = 0

    // Website fields
    lateinit var type: String

    // Channel fields
    lateinit var channel_id: String

    // Group fields

    // Instagram fields
    lateinit var page_id: String

    // Telegram and Group fields
    lateinit var application: String

    var status: String = ""
}