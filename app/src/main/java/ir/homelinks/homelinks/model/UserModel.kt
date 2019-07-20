package ir.homelinks.homelinks.model

import com.google.gson.annotations.SerializedName

class UserModel() {

    constructor(username: String, password: String, email: String="",
                phone_number: String = ""): this() {

        this.username = username
        this.password = password
        this.email = email
        this.phoneNumber = phone_number
    }


    lateinit var username: String
    lateinit var password: String
    lateinit var email: String

    @SerializedName("first_name")
    lateinit var firstName: String

    @SerializedName("last_name")
    lateinit var lastName: String

    @SerializedName("phone_number")
    var phoneNumber = ""
}