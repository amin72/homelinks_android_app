package ir.homelinks.homelinks.utility

import android.app.Application
import ir.homelinks.homelinks.api.*


class AppController : Application() {

    companion object {
        val apiInterface = APIClient.getClient().create(APIInterface::class.java)!!
    }
}