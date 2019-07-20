package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.contact_us.ContactUsModel
import ir.homelinks.homelinks.model.contact_us.ContactUsOptions
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.ClientConstants
import ir.homelinks.homelinks.utility.LinkUtility
import ir.homelinks.homelinks.utility.Messages
import kotlinx.android.synthetic.main.activity_contact_us.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_us)

        contact_us_layout.setOnClickListener(null)
        contact_us_toolbar.title = getString(R.string.contact_us)
        setSupportActionBar(contact_us_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        submit_contact_us_button.setOnClickListener {

            val type = LinkUtility.translate(contact_us_type_spinner.selectedItem.toString().toLowerCase())
            val email = email_text.text.toString()
            val description = description_text.text.toString()
            val contactUs = ContactUsModel(type, email, description)

            if (email.isEmpty() || description.isEmpty()) {
                if (email.isEmpty()) {
                    val message = getString(R.string.email_cant_be_blank)
                    setFieldMessage(email_input_layout, message)
                } else {
                    email_input_layout.isErrorEnabled = false
                }

                if (description.isEmpty()) {
                    val message = getString(R.string.description_cant_be_blank)
                    setFieldMessage(description_input_layout, message)
                } else {
                    description_input_layout.isErrorEnabled = false
                }
            } else {

                // clear error messages
                email_input_layout.isErrorEnabled = false
                description_input_layout.isErrorEnabled = false

                val callContactUs = AppController.apiInterface.contactUs(contactUs)

                callContactUs.enqueue(object : Callback<ContactUsModel> {
                    override fun onFailure(call: Call<ContactUsModel>, t: Throwable) {
                        Toast.makeText(baseContext,
                            getString(R.string.failed_connect_to_server).toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<ContactUsModel>, response: Response<ContactUsModel>) {
                        if (response.isSuccessful) {
                            Toast.makeText(baseContext, getString(R.string.message_sent), Toast.LENGTH_SHORT).show()
                            LinkUtility.goHome(baseContext)
                        } else {
                            val errors = Messages.getErrors(
                                response, listOf("email", "description", "non_field_errors")
                            )

                            var emailErrorMessage = ""
                            var descriptionErrorMessage = ""

                            for (error in errors) {
                                if (error.key == "email") {
                                    emailErrorMessage += error.value.joinToString("\n")
                                } else if (error.key == "description") {
                                    descriptionErrorMessage += error.value.joinToString("\n")
                                } else {
                                    for (value in error.value) {
                                        Toast.makeText(baseContext, value, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }

                            email_input_layout.isErrorEnabled = true
                            email_input_layout.error = emailErrorMessage

                            description_input_layout.isErrorEnabled = true
                            description_input_layout.error = descriptionErrorMessage
                        }
                    }
                })
            }
        }

        val callGetChoices = AppController.apiInterface.contactUsChoices()

        callGetChoices.enqueue(object : Callback<ContactUsOptions> {
            override fun onFailure(call: Call<ContactUsOptions>, t: Throwable) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ContactUsOptions>,
                response: Response<ContactUsOptions>
            ) {

                if (response.isSuccessful) {
                    val contactUs = response.body()!!
                    var contactUsChoices = mutableListOf<String>()

                    for (choice in contactUs.actions.POST.type.choices) {
                        if (LinkUtility.getLanguage(baseContext) == ClientConstants.FARSI_LANGUAGE) {
                            contactUsChoices.add(LinkUtility.translate(choice.value))
                        } else {
                            contactUsChoices.add(choice.value)
                        }
                    }

                    ArrayAdapter<String>(
                        baseContext,
                        android.R.layout.simple_spinner_item,
                        contactUsChoices
                    ).also { adapter ->
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        contact_us_type_spinner.adapter = adapter
                    }
                } else {
                    Toast.makeText(baseContext, getString(R.string.failed_fetch_data), Toast.LENGTH_SHORT).show()
                }
            }
        })

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.contact_us_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }


    private fun setFieldMessage(field: TextInputLayout, message: String) {
        field.isErrorEnabled = true
        field.error = message
    }
}
