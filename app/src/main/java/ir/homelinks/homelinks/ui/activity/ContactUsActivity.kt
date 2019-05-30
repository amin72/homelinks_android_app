package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.ChoiceModel
import ir.homelinks.homelinks.model.contact_us.ContactUsModel
import ir.homelinks.homelinks.model.contact_us.ContactUsOptions
import ir.homelinks.homelinks.utility.AppController
import kotlinx.android.synthetic.main.activity_contact_us.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        setSupportActionBar(contact_us_toolbar)
        contact_us_layout.setOnClickListener(null)


        submit_contact_us_button.setOnClickListener {

            val type = contact_us_type_spinner.selectedItem.toString().toLowerCase()
            val email = email_text.text.toString()
            val description = description_text.text.toString()
            val contact_us = ContactUsModel(type, email, description)

            val callContactUs = AppController.apiInterface.contactUs(contact_us)

            callContactUs.enqueue(object : Callback<ContactUsModel> {
                override fun onFailure(call: Call<ContactUsModel>, t: Throwable) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.failed_to_connect_to_server).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<ContactUsModel>,
                    response: Response<ContactUsModel>
                ) {

                    if (response.isSuccessful) {
                        val contact_us = response.body()!!
                        Toast.makeText(baseContext, contact_us.type, Toast.LENGTH_SHORT).show()
                        Toast.makeText(baseContext, contact_us.email, Toast.LENGTH_SHORT).show()
                        Toast.makeText(baseContext, contact_us.text, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Failed to send message to contact us section!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }


        val callGetChoices = AppController.apiInterface.contactUsChoices()

        callGetChoices.enqueue(object : Callback<ContactUsOptions> {
            override fun onFailure(call: Call<ContactUsOptions>, t: Throwable) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ContactUsOptions>,
                response: Response<ContactUsOptions>
            ) {

                if (response.isSuccessful) {
                    val report = response.body()!!


                    ArrayAdapter<ChoiceModel>(
                        baseContext,
                        android.R.layout.simple_spinner_item,
                        report.actions.POST.type.choices
                    ).also { adapter ->
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        contact_us_type_spinner.adapter = adapter
                    }

                } else {
                    Toast.makeText(baseContext, "Failed to fetch report options!", Toast.LENGTH_SHORT).show()
                }
            }
        })

    }
}
