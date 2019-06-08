package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.ResetPasswordModel
import ir.homelinks.homelinks.model.ResponseModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.Messages
import kotlinx.android.synthetic.main.activity_reset_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ResetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        reset_password_layout.setOnClickListener(null)
        setSupportActionBar(reset_password_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        reset_password_button.setOnClickListener {
            val email = email_text.text.toString()

            if (email.isEmpty()) {
                val message = getString(R.string.email_cant_be_blank)
                getFieldMessage(email_input_layout, message)
            } else {
                email_input_layout.isErrorEnabled = false

                val resetPassword = ResetPasswordModel(email)
                val call = AppController.apiInterface.resetPassword(resetPassword)

                call.enqueue(object : Callback<ResponseModel> {
                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            baseContext,
                            getString(R.string.failed_connect_to_server).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        if (response.isSuccessful) {
                            val response = response.body()!!
                            Toast.makeText(baseContext, response.detail, Toast.LENGTH_SHORT).show()
                        } else {

                            val errors = Messages.getErrors(
                                response, listOf(
                                    "email", "non_field_errors"
                                )
                            )

                            var emailErrorMessage = ""

                            for (error in errors) {
                                if (error.key == "email") {
                                    emailErrorMessage += error.value.joinToString("\n")
                                } else {
                                    for (value in error.value) {
                                        Toast.makeText(baseContext, value, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                })
            }

            back_to_login.setOnClickListener {
                // back to sign in activity
                finish()
            }
        }

        back_to_login.setOnClickListener {
            finish()
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
