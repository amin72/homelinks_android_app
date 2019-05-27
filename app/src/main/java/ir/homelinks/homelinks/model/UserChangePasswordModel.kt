package ir.homelinks.homelinks.model

class UserChangePasswordModel(new_password1: String, new_password2: String) {

    var new_password1: String
    var new_password2: String


    init {
        this.new_password1 = new_password1
        this.new_password2 = new_password2
    }
}
