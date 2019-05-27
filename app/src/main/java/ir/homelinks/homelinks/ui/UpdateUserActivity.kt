package ir.homelinks.homelinks.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.UserModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.ClientConstants
import kotlinx.android.synthetic.main.activity_update_user.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        setSupportActionBar(user_update_toolbar)
        user_update_layout.setOnClickListener(null)

        val token = "token 91647edd1ac4b3a5cd70370bcfbedd83e7dc7982" // admin token

        // fill fields
        setUserInfo(token)

        submit_user_update_button.setOnClickListener {

            val firstName = first_name_text.text.toString()
            val lastName = last_name_text.text.toString()
            val email = email_text.text.toString()
            val phoneNumber = phone_number_text.text.toString()

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
                        getString(R.string.failed_to_connect_to_server).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<UserModel>,
                    response: Response<UserModel>
                ) {

                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "User was updated successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Failed to update user data!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }


    private fun setUserInfo(token: String) {
        val call = AppController.apiInterface.getUserInfo(token)

        call.enqueue(object : Callback<UserModel> {
            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.failed_to_connect_to_server).toString(),
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
}
