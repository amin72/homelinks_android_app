package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.ResponseModel
import ir.homelinks.homelinks.model.UserChangePasswordModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.AppPreferenceTools
import ir.homelinks.homelinks.utility.LinkUtility
import ir.homelinks.homelinks.utility.Messages
import kotlinx.android.synthetic.main.activity_user_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserChangePassword : AppCompatActivity() {

    private lateinit var appPreferenceTools: AppPreferenceTools


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_change_password)

        user_change_password_layout.setOnClickListener(null)
        user_change_password_toolbar.title = getString(R.string.change_password)
        setSupportActionBar(user_change_password_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appPreferenceTools = AppPreferenceTools(this)

        submit_user_update_button.setOnClickListener {

            if (appPreferenceTools.isAuthorized()) {

                val password1 = password1_text.text.toString()
                val password2 = password2_text.text.toString()

                if (password1.isEmpty() && password2.isEmpty()) {
                    val message = getString(R.string.this_field_cant_be_blank)

                    if (password1.isEmpty()) {
                        getFieldMessage(password1_input_layout, message)
                    } else {
                        password1_input_layout.isErrorEnabled = false
                    }

                    if (password2.isEmpty()) {
                        getFieldMessage(password2_input_layout, message)
                    } else {
                        password2_input_layout.isErrorEnabled = false
                    }

                } else {
                    val token = "token ${appPreferenceTools.getUserToken()}"
                    val changePassword = UserChangePasswordModel(password1, password2)

                    val call = AppController.apiInterface.userChangePassword(token, changePassword)

                    call.enqueue(object : Callback<ResponseModel> {
                        override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                            Toast.makeText(
                                baseContext,
                                getString(R.string.failed_connect_to_server).toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                            if (response.isSuccessful) {
                                val userResponse = response.body()!!
                                Toast.makeText(baseContext, userResponse.detail, Toast.LENGTH_SHORT).show()
                            } else {
                                val errors = Messages.getErrors(response,
                                    listOf("new_password1", "new_password2", "non_field_errors"))

                                var password1ErrorMessage = ""
                                var password2ErrorMessage = ""

                                for (error in errors) {
                                    if (error.key == "new_password1") {
                                        password1ErrorMessage += error.value.joinToString("\n")
                                    } else if (error.key == "new_password2" || error.key == "non_field_errors") {
                                        password2ErrorMessage += error.value.joinToString("\n")
                                    } else {
                                        for (value in error.value) {
                                            Toast.makeText(baseContext, value, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }

                                password1_input_layout.isErrorEnabled = true
                                password1_input_layout.error = password1ErrorMessage

                                password2_input_layout.isErrorEnabled = true
                                password2_input_layout.error = password2ErrorMessage
                            }
                        }
                    })
                }
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_change_password_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }


    private fun getFieldMessage(field: TextInputLayout, message: String) {
        field.isErrorEnabled = true
        field.error = message
    }
}
