package ir.homelinks.homelinks.utility

import android.content.Context
import android.content.SharedPreferences
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.AuthenticationModel
import ir.homelinks.homelinks.model.TokenModel
import ir.homelinks.homelinks.model.UserModel


class AppPreferenceTools(val context: Context) {
    val preferences: SharedPreferences =
        context.getSharedPreferences("app_preference", Context.MODE_PRIVATE)


    companion object {
        const val STRING_PREF_UNAVAILABLE = "homelinks preference unavailable"
    }


    fun saveUserAuthenticationInfo(authentication: AuthenticationModel) {
        saveUserInfo(authentication.user)
        saveUserToken(authentication.token)
    }


    fun saveUserInfo(user: UserModel) {
        preferences.edit()
            .putString(context.getString(R.string.pref_user_name), user.username)
            .putString(context.getString(R.string.pref_user_email), user.email)
            .putString(context.getString(R.string.pref_user_name), user.firstName)
            .putString(context.getString(R.string.pref_user_name), user.lastName)
            .apply()
    }


    fun saveUserToken(token: TokenModel) {
        preferences.edit()
            .putString(context.getString(R.string.pref_user_token), token.key)
            .apply()
    }


    fun getUserName(): String {
        return preferences.getString(context.getString(R.string.pref_user_name), "")
    }


    fun getUserEmail(): String {
        return preferences.getString(context.getString(R.string.pref_user_email), "")
    }


    // return token if avialabel else `STRING_PREF_UNAVAILABLE` is returned
    fun getUserToken(): String {
        return preferences.getString(context.getString(R.string.pref_user_token),
            STRING_PREF_UNAVAILABLE)
    }


    fun removeUserAuthenticationInfo() {
        preferences.edit().clear().apply()
    }


    fun isAuthrorized(): Boolean {
        return getUserToken().equals(STRING_PREF_UNAVAILABLE).not()
    }
}