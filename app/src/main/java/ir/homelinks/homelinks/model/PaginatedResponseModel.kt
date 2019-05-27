package ir.homelinks.homelinks.model

class PaginatedResponseModel {
    var count: Int = 0
    var next: String? = null
    var previous: String? = null
    lateinit var results: List<LinkModel>
}