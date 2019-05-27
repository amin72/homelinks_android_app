package ir.homelinks.homelinks.model.contact_us

class ContactUsModel() {
    lateinit var type: String
    lateinit var email: String
    lateinit var text: String


    constructor(type: String, email: String, text: String): this() {
        this.type = type
        this.email = email
        this.text = text
    }
}