package ir.homelinks.homelinks.ui

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

        setSupportActionBar(sign_in_toolbar)

        login_layout.setOnClickListener(null)

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
                val call = AppController.apiInterface.login(user)

                call.enqueue(object : Callback<TokenModel> {
                    override fun onFailure(call: Call<TokenModel>, t: Throwable) {
                        // show a dialog instead of toast
                        Toast.makeText(
                            baseContext,
                            getString(R.string.failed_to_connect_to_server).toString(),
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
                            var appPreference = AppPreferenceTools(baseContext)
                            appPreference.saveUserToken(token)
                            startActivity(Intent(baseContext, MainActivity::class.java))

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


        get_help_sign_in.setOnClickListener {
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
