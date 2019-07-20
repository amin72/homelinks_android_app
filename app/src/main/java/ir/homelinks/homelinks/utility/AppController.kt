package ir.homelinks.homelinks.utility

import android.app.Application
import android.content.Context
import ir.homelinks.homelinks.api.*
import ir.tapsell.sdk.*


class AppController : Application() {

    companion object {
        lateinit var appContext: Context
        val apiInterface = APIClient.getClient().create(APIInterface::class.java)!!
    }


    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        Tapsell.initialize(this, "tatrrmtctbpkbkaegpjksodkpcqflfhdcddaafsegaehqgfeamdllhrceqkntgktpoqbfh")
    }
}