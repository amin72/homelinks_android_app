package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.ChoiceModel
import ir.homelinks.homelinks.model.report_links.ReportLinkModel
import ir.homelinks.homelinks.model.report_links.ReportLinkOptions
import ir.homelinks.homelinks.utility.AppController
import kotlinx.android.synthetic.main.activity_report_link.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReportLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_link)

        setSupportActionBar(report_link_toolbar)
        report_link_layout.setOnClickListener(null)


        submit_report_button.setOnClickListener {

            val type = report_type_spinner.selectedItem.toString().toLowerCase()
            val email = email_text.text.toString()
            val description = description_text.text.toString()
            val report = ReportLinkModel(type, email, description)

            val callReportLink = AppController.apiInterface.reportLink("website", "website1-com", report)

            callReportLink.enqueue(object : Callback<ReportLinkModel> {
                override fun onFailure(call: Call<ReportLinkModel>, t: Throwable) {
                    Toast.makeText(
                        baseContext,
                        getString(R.string.failed_to_connect_to_server).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<ReportLinkModel>,
                    response: Response<ReportLinkModel>
                ) {

                    if (response.isSuccessful) {
                        val report = response.body()!!
                        Toast.makeText(baseContext, report.type, Toast.LENGTH_SHORT).show()
                        Toast.makeText(baseContext, report.email, Toast.LENGTH_SHORT).show()
                        Toast.makeText(baseContext, report.text, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Failed to report link!", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        }


        val callGetChoices = AppController.apiInterface.reportLinkChoices()

        callGetChoices.enqueue(object : Callback<ReportLinkOptions> {
            override fun onFailure(call: Call<ReportLinkOptions>, t: Throwable) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<ReportLinkOptions>,
                response: Response<ReportLinkOptions>
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
                        report_type_spinner.adapter = adapter
                    }

                } else {
                    Toast.makeText(baseContext, "Failed to fetch report options!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
