package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.UserModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.AppPreferenceTools
import ir.homelinks.homelinks.utility.LinkUtility
import ir.homelinks.homelinks.utility.Messages
import kotlinx.android.synthetic.main.activity_update_user.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserActivity : AppCompatActivity() {

    private lateinit var appPreferenceTools: AppPreferenceTools


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        user_update_layout.setOnClickListener(null)
        user_update_toolbar.title = getString(R.string.edit_account)
        setSupportActionBar(user_update_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appPreferenceTools = AppPreferenceTools(this)

        val token = "token ${appPreferenceTools.getUserToken()}"

        // fill fields
        setUserInfo(token)

        submit_user_update_button.setOnClickListener {

            val firstName = first_name_text.text.toString()
            val lastName = last_name_text.text.toString()
            val email = email_text.text.toString()
            val phoneNumber = phone_number_text.text.toString()

            if (email.isEmpty()) {
                email_input_layout.isErrorEnabled = true
                email_input_layout.error = getString(R.string.email_cant_be_blank)
            } else {
                email_input_layout.isErrorEnabled = false

                val user = UserModel()
                user.firstName = firstName
                user.lastName = lastName
                user.email = email
                user.phoneNumber = phoneNumber

                val call = AppController.apiInterface.userUpdate(token, user)

                call.enqueue(object : Callback<UserModel> {
                    override fun onFailure(call: Call<UserModel>, t: Throwable) {
                        Toast.makeText(
                            baseContext,
                            getString(R.string.failed_connect_to_server).toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                baseContext, getString(R.string.account_updated),
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(baseContext, UserLinksActivity::class.java))
                            finish()
                        } else {
                            val errors = Messages.getErrors(response, listOf("first_name", "last_name",
                                "email", "phone_number", "non_field_errors"))

                            var firstNameErrorMessage = ""
                            var lastNameErrorMessage = ""
                            var emailErrorMessage = ""
                            var phoneNumberErrorMessage = ""

                            for (error in errors) {
                                if (error.key == "first_name") {
                                    firstNameErrorMessage += error.value.joinToString("\n")
                                } else if (error.key == "last_name") {
                                    lastNameErrorMessage += error.value.joinToString("\n")
                                } else if (error.key == "email") {
                                    emailErrorMessage += error.value.joinToString("\n")
                                } else if (error.key == "phone_number") {
                                    phoneNumberErrorMessage += error.value.joinToString("\n")
                                } else {
                                    for (value in error.value) {
                                        Toast.makeText(baseContext, value, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            first_name_input_layout.isErrorEnabled = true
                            first_name_input_layout.error = firstNameErrorMessage

                            last_name_input_layout.isErrorEnabled = true
                            last_name_input_layout.error = lastNameErrorMessage

                            email_input_layout.isErrorEnabled = true
                            email_input_layout.error = emailErrorMessage

                            phone_number_input_layout.isErrorEnabled = true
                            phone_number_input_layout.error = phoneNumberErrorMessage
                        }
                    }
                })
            }
        }
    }


    private fun setUserInfo(token: String) {
        val call = AppController.apiInterface.getUserInfo(token)

        call.enqueue(object : Callback<UserModel> {
            override fun onFailure(call: Call<UserModel>, t: Throwable) {
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
                    val user = response.body()!!

                    first_name_text.setText(user.firstName)
                    last_name_text.setText(user.lastName)
                    email_text.setText(user.email)
                    phone_number_text.setText(user.phoneNumber)

                } else {
                    Toast.makeText(baseContext, "Failed to fetch user data!",
                        Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.update_user_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }
}
