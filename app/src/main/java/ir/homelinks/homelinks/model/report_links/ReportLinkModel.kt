package ir.homelinks.homelinks.model.report_links

class ReportLinkModel() {
    lateinit var type: String
    lateinit var email: String
    lateinit var text: String


    constructor(type: String, email: String, text: String): this() {
        this.type = type
        this.email = email
        this.text = text
    }
}