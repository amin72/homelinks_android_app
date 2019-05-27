package ir.homelinks.homelinks.model

class CategoryModel() {
    var id: Int = 0
    lateinit var name: String


    constructor(id: Int, name: String): this() {
        this.id = id
        this.name = name
    }


    override fun toString(): String {
        return this.name
    }


    override fun equals(other: Any?): Boolean {
        if (this.name == (other as CategoryModel).name) {
            return true
        }

        return super.equals(other)
    }
}