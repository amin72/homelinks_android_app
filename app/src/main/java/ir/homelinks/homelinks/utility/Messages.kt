package ir.homelinks.homelinks.utility

import android.content.Context
import android.widget.Toast
import org.json.JSONObject
import retrofit2.Response


class Messages {
    companion object {
        fun <T> getErrors(response: Response<T>, keys: List<String>):
        Map<String, MutableList<String>> {

            var errorValues = mutableMapOf<String, MutableList<String>>()
            
            try {
                val error = JSONObject(response.errorBody()!!.string())
                

                for (key in keys) {
                    if (error.has(key)) {
                        val messages = error.getJSONArray(key)
                        var messagesList = mutableListOf<String>()

                        for (i in 0 until messages.length()) {
                            messagesList.add(messages[i].toString())
                        }

                        errorValues.put(key, messagesList)
                    }
                }
            } catch (e: Exception) {
            }
            
            return errorValues            
        }
    }
}