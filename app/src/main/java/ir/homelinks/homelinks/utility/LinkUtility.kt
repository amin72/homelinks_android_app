package ir.homelinks.homelinks.utility

import ir.homelinks.homelinks.model.CategoryModel


class LinkUtility {
    companion object {

        fun findCategoryById(categories: List<CategoryModel>, id: Int): CategoryModel? {
            for (category in categories) {
                if (category.id == id) {
                    return category
                }
            }
            return null
        }


        /*
            Convert timezone date time to persian date
            example: 2019-05-15T02:21:20.463025+04:30
            result: 1398-03-01
         */
        fun convertDate(date: String): String {
            val splitDate = date.split("T")[0].split("-")
            val year = splitDate[0].toInt()
            val month = splitDate[1].toInt()
            val day = splitDate[2].toInt()

            val roozh = Roozh()
            roozh.gregorianToPersian(year, month, day)
            return roozh.toString()
        }
    }
}