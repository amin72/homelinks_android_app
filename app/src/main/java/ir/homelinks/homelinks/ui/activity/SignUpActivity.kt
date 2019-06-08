package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.UserModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.Messages
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpActivity : AppCompatActivity(), TextWatcher {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        sign_up_layout.setOnClickListener(null)
        sign_up_toolbar.title = getString(R.string.signup)
        setSupportActionBar(sign_up_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sign_up_button.setOnClickListener {

            val username = username_text.text.toString()
            val email = email_text.text.toString()
            val password = password_text.text.toString()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {

                if (username.isEmpty()) {
                    val message = getString(R.string.username_cant_be_blank)
                    getFieldMessage(username_input_layout, message)
                } else {
                    username_input_layout.isErrorEnabled = false
                }

                if (email.isEmpty()) {
                    val message = getString(R.string.email_cant_be_blank)
                    getFieldMessage(email_input_layout, message)
                } else {
                    email_input_layout.isErrorEnabled = false
                }

                if (password.isEmpty()) {
                    val message = getString(R.string.password_cant_be_blank)
                    getFieldMessage(password_input_layout, message)
                } else {
                    password_input_layout.isErrorEnabled = false
                }

            } else {

                val user = UserModel(username, password, email)
                val call = AppController.apiInterface.register(user)

                call.enqueue(object : Callback<UserModel> {
                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        // show a dialog instead of toast
                        Toast.makeText(
                            baseContext,
                            getString(R.string.failed_connect_to_server).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<UserModel>,
                        response: Response<UserModel>
                    ) {

                        if (response.isSuccessful) {
                            username_input_layout.isErrorEnabled = false
                            password_input_layout.isErrorEnabled = false

                            val user = response.body()!!
                            Toast.makeText(baseContext, user.username, Toast.LENGTH_SHORT).show()
                            Toast.makeText(baseContext, user.email, Toast.LENGTH_SHORT).show()

                        } else {

                            val errors = Messages.getErrors(
                                response, listOf(
                                    "username", "password",
                                    "email", "non_field_errors"
                                )
                            )

                            var usernameErrorMessage = ""
                            var emailErrorMessage = ""
                            var passwordErrorMessage = ""

                            for (error in errors) {
                                if (error.key == "username") {
                                    usernameErrorMessage += error.value.joinToString("\n")
                                } else if (error.key == "email") {
                                    emailErrorMessage += error.value.joinToString("\n")
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

                            email_input_layout.isErrorEnabled = true
                            email_input_layout.error = emailErrorMessage

                            password_input_layout.isErrorEnabled = true
                            password_input_layout.error = passwordErrorMessage
                        }
                    }
                })
            }
        }

        sign_in.setOnClickListener {
            // back to sign in activity
            finish()
        }

        username_text.addTextChangedListener(this)
        email_text.addTextChangedListener(this)
        password_text.addTextChangedListener(this)
    }


    override fun afterTextChanged(s: Editable?) {
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }


    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (username_text.text.isNotEmpty()) {
            username_input_layout.isErrorEnabled = false
        }

        if (email_text.text.isNotEmpty()) {
            email_input_layout.isErrorEnabled = false
        }

        if (password_text.text.isNotEmpty()) {
            password_input_layout.isErrorEnabled = false
        }
    }


    private fun getFieldMessage(field: TextInputLayout, message: String) {
        field.isErrorEnabled = true
        field.error = message
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
