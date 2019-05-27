package ir.homelinks.homelinks.model

class ChoiceModel {
    lateinit var value: String
    lateinit var display_name: String


    override fun toString(): String {
        return this.display_name
    }
}