package ir.homelinks.homelinks.utility

import android.content.Context
import android.content.SharedPreferences
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.AuthenticationModel
import ir.homelinks.homelinks.model.TokenModel
import ir.homelinks.homelinks.model.UserModel


class AppPreferenceTools(val context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("app_preference", Context.MODE_PRIVATE)


    companion object {
        const val STRING_PREF_UNAVAILABLE = "homelinks preference unavailable"
    }


    fun saveUserAuthentication(authentication: AuthenticationModel) {
        saveUserInfo(authentication.user)
        saveUserToken(authentication.token)
    }


    fun saveUserInfo(user: UserModel) {
        preferences.edit()
            .putString(context.getString(R.string.pref_user_name), user.username)
            .putString(context.getString(R.string.pref_user_email), user.email)
            .putString(context.getString(R.string.pref_user_first_name), user.firstName)
            .putString(context.getString(R.string.pref_user_last_name), user.lastName)
            .apply()
    }


    fun saveUserToken(token: TokenModel) {
        preferences.edit()
            .putString(context.getString(R.string.pref_user_token), token.key)
            .apply()
    }


    fun saveCredential(username: String, password: String, rememberCredential: Boolean=false) {
        preferences.edit()
            .putString(context.getString(R.string.pref_user_name), username)
            .putString(context.getString(R.string.pref_user_pass), password)
            .putBoolean(context.getString(R.string.pref_remember_credential), rememberCredential)
            .apply()
    }


    fun removeCredentials() {
        preferences.edit()
            .remove(context.getString(R.string.pref_user_name))
            .remove(context.getString(R.string.pref_user_pass))
            .remove(context.getString(R.string.pref_remember_credential))
            .apply()
    }


    fun getUserName(): String {
        return preferences.getString(context.getString(R.string.pref_user_name), "")
    }


    fun getUserCredentials(): Map<String, String> {
        val username = preferences.getString(context.getString(R.string.pref_user_name), "")
        val password = preferences.getString(context.getString(R.string.pref_user_pass), "")
        val isChecked = preferences.getBoolean(
            context.getString(R.string.pref_remember_credential), false).toString()

        return mapOf<String, String>(
            context.getString(R.string.pref_user_name) to username,
            context.getString(R.string.pref_user_pass) to password,
            context.getString(R.string.pref_remember_credential) to isChecked)
    }


    fun getUserEmail(): String {
        return preferences.getString(context.getString(R.string.pref_user_email), "")
    }


    // return token if available else `STRING_PREF_UNAVAILABLE` is returned
    fun getUserToken(): String {
        return preferences.getString(context.getString(R.string.pref_user_token),
            STRING_PREF_UNAVAILABLE)
    }


    fun removeUserAuthenticationInfo() {
        preferences.edit().clear().apply()
    }


    fun isAuthorized(): Boolean {
        return (getUserToken() == STRING_PREF_UNAVAILABLE).not()
    }


    fun setLanguage(lang: String) {
        preferences.edit().putString(context.getString(R.string.lang), lang).apply()
    }


    fun getLanguage(): String {
        return preferences.getString(context.getString(R.string.lang), context.getString(R.string.lang_fa))
    }
}