package ir.homelinks.homelinks.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import ir.homelinks.homelinks.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Toast.makeText(baseContext, "Settings!", Toast.LENGTH_SHORT).show()
    }
}
