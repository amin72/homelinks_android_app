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
import ir.homelinks.homelinks.model.report_links.ReportLinkModel
import ir.homelinks.homelinks.model.report_links.ReportLinkOptions
import ir.homelinks.homelinks.utility.*
import kotlinx.android.synthetic.main.activity_report_link.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReportLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_link)

        report_link_layout.setOnClickListener(null)
        report_link_toolbar.title = getString(R.string.report_link)
        setSupportActionBar(report_link_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        LinkUtility.removeErrors(email_input_layout, email_text)
        LinkUtility.removeErrors(description_input_layout, description_text)

        submit_report_button.setOnClickListener {
            val type = LinkUtility.translate(report_type_spinner.selectedItem.toString().toLowerCase())

            val email = email_text.text.toString()
            val description = description_text.text.toString()
            val report = ReportLinkModel(type, email, description)

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
                val extras = intent.extras
                if (extras != null) {
                    val link = extras.getString("link", "")
                    val slug = extras.getString("slug", "")

                    if (link.isNotEmpty() && slug.isNotEmpty()) {
                        val callReportLink = AppController.apiInterface.reportLink(link, slug, report)

                        callReportLink.enqueue(object : Callback<ReportLinkModel> {
                            override fun onFailure(call: Call<ReportLinkModel>, t: Throwable) {
                                Toast.makeText(baseContext, getString(R.string.failed_connect_to_server).toString(),
                                    Toast.LENGTH_SHORT).show()
                            }

                            override fun onResponse(call: Call<ReportLinkModel>, response: Response<ReportLinkModel>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(baseContext, getString(R.string.report_sent), Toast.LENGTH_LONG).show()
                                    finish()
                                } else {
                                    val errors = Messages.getErrors(
                                        response, listOf("email", "description", "non_field_errors"))

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
            }
        }

        val callGetChoices = AppController.apiInterface.reportLinkChoices()

        callGetChoices.enqueue(object : Callback<ReportLinkOptions> {
            override fun onFailure(call: Call<ReportLinkOptions>, t: Throwable) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(call: Call<ReportLinkOptions>,
                response: Response<ReportLinkOptions>) {
                if (response.isSuccessful) {
                    val report = response.body()!!
                    var reportChoices = mutableListOf<String>()

                    for (choice in report.actions.POST.type.choices) {
                        if (LinkUtility.getLanguage(baseContext) == ClientConstants.FARSI_LANGUAGE) {
                            reportChoices.add(LinkUtility.translate(choice.value))
                        } else {
                            reportChoices.add(choice.value)
                        }
                    }

                    ArrayAdapter<String>(
                        baseContext,
                        android.R.layout.simple_spinner_item,
                        reportChoices
                    ).also { adapter ->
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        report_type_spinner.adapter = adapter
                    }
                } else {
                    Toast.makeText(baseContext, getString(R.string.failed_fetch_data), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.report_link_menu, menu)
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