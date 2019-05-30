package ir.homelinks.homelinks.ui.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import ir.homelinks.homelinks.R
import kotlinx.android.synthetic.main.activity_add_link.*

class AddLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_link)

        setSupportActionBar(add_link_toolbar)
        add_link_layout.setOnClickListener(null)

        ArrayAdapter.createFromResource(
            this,
            R.array.link_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            add_link_spinner.adapter = adapter
        }


        submit_link_type_button.setOnClickListener {
            var intent: Intent? = null

            when (add_link_spinner.selectedItem.toString()) {

                resources.getString(R.string.website) ->
                    intent = Intent(this, WebsiteCreateOrUpdateActivity::class.java)

                resources.getString(R.string.channel) ->
                    intent = Intent(this, ChannelCreateOrUpdateActivity::class.java)

                resources.getString(R.string.group) ->
                    intent = Intent(this, GroupCreateOrUpdateActivity::class.java)

                resources.getString(R.string.instagram) ->
                    intent = Intent(this, InstagramCreateOrUpdateActivity::class.java)
            }

            if (intent != null) {
                startActivity(intent)
            } else {
                Toast.makeText(baseContext, resources.getString(R.string.select_link_type),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
