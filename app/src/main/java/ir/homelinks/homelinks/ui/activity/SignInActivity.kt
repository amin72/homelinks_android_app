package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.TokenModel
import ir.homelinks.homelinks.model.UserModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.AppPreferenceTools
import ir.homelinks.homelinks.utility.Messages
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignInActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)


        sign_in_layout.setOnClickListener(null)
        sign_in_toolbar.title = getString(R.string.sign_in)
        setSupportActionBar(sign_in_toolbar)


        login_button.setOnClickListener {

            val username = username_text.text.toString()
            val password = password_text.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                if (username.isEmpty()) {

                    val message = getString(R.string.username_cant_be_blank)
                    getFieldMessage(username_input_layout, message)
                } else {
                    username_input_layout.isErrorEnabled = false
                }

                if (password.isEmpty()) {
                    val message = getString(R.string.password_cant_be_blank)
                    getFieldMessage(password_input_layout, message)
                } else {
                    password_input_layout.isErrorEnabled = false
                }

            } else {

                val user = UserModel(username, password)

                // for later use
                user.email = ""
                user.firstName = ""
                user.lastName = ""

                val call = AppController.apiInterface.login(user)

                call.enqueue(object : Callback<TokenModel> {
                    override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                        // show a dialog instead of toast
                        Toast.makeText(
                            baseContext,
                            getString(R.string.failed_connect_to_server).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<TokenModel>,
                        response: Response<TokenModel>
                    ) {

                        if (response.isSuccessful) {
                            username_input_layout.isErrorEnabled = false
                            password_input_layout.isErrorEnabled = false

                            val token = response.body()!!

                            // Save token in shared preference tools
                            val appPreference = AppPreferenceTools(baseContext)
                            appPreference.saveUserToken(token)

                            // get user information
                            val userInfoCall = AppController.apiInterface.getUserInfo("token ${token.key}")

                            userInfoCall.enqueue(object: Callback<UserModel> {
                                override fun onFailure(call: Call<UserModel>, t: Throwable) {
                                    // save user information in shared preference tools
                                    appPreference.saveUserInfo(user)
                                }

                                override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                                    val userInfo = response.body()!!

                                    // save user information in shared preference tools
                                    user.email = userInfo.email
                                    user.firstName = userInfo.firstName
                                    user.lastName = userInfo.lastName
                                    appPreference.saveUserInfo(user)
                                }
                            })

                            startActivity(Intent(baseContext, MainActivity::class.java))
                            finish()

                        } else {

                            val errors = Messages.getErrors(response, listOf("username", "password", "non_field_errors"))

                            var usernameErrorMessage = ""
                            var passwordErrorMessage = ""

                            for (error in errors) {
                                if (error.key == "username") {
                                    usernameErrorMessage += error.value.joinToString("\n")
                                } else if (error.key == "password" || error.key == "non_field_errors") {
                                    passwordErrorMessage += error.value.joinToString("\n")
                                } else {
                                    for (value in error.value) {
                                        Toast.makeText(baseContext, value, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            username_input_layout.isErrorEnabled = true
                            username_input_layout.error = usernameErrorMessage

                            password_input_layout.isErrorEnabled = true
                            password_input_layout.error = passwordErrorMessage
                        }
                    }
                })
            }
        }


        reset_password.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }


        sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }


    private fun getFieldMessage(field: TextInputLayout, message: String) {
        field.isErrorEnabled = true
        field.error = message
    }
}
