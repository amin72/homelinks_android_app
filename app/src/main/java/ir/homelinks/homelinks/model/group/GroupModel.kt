package ir.homelinks.homelinks.model.group

import ir.homelinks.homelinks.model.CategoryModel

class GroupModel {
    lateinit var application: String
    lateinit var title: String
    lateinit var url: String
    var category: Int = 0
    lateinit var description: String
    lateinit var image: String
}