package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.ResponseModel
import ir.homelinks.homelinks.model.UserChangePasswordModel
import ir.homelinks.homelinks.utility.AppController
import kotlinx.android.synthetic.main.activity_user_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserChangePassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_change_password)

        setSupportActionBar(user_change_password_toolbar)
        user_update_layout.setOnClickListener(null)


        submit_user_update_button.setOnClickListener {

            val password1 = password1_text.text.toString()
            val password2 = password2_text.text.toString()

            val token = "token 91647edd1ac4b3a5cd70370bcfbedd83e7dc7982" // admin token
            val changePassword = UserChangePasswordModel(password1, password2)

            val call = AppController.apiInterface.userChangePassword(token, changePassword)

            call.enqueue(object: Callback<ResponseModel> {
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(baseContext,
                        getString(R.string.failed_to_connect_to_server).toString(),
                        Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ResponseModel>,
                                        response: Response<ResponseModel>
                ) {
                    if (response.isSuccessful) {
                        val response = response.body()!!
                        Toast.makeText(baseContext, response.detail, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Failed to change password!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }
}
