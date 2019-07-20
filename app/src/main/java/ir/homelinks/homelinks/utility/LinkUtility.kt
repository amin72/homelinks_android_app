package ir.homelinks.homelinks.utility

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.model.ResponseModel
import ir.homelinks.homelinks.ui.activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import android.os.Build


class LinkUtility {
    companion object {

        fun findCategoryById(categories: List<CategoryModel>, id: Int): CategoryModel? {
            for (category in categories) {
                if (category.id == id) {
                    return category
                }
            }
            return null
        }


        /*
            Convert timezone date time to persian date
            example: 2019-05-15T02:21:20.463025+04:30
            result: 1398/03/01
         */
        fun convertDate(date: String): String {
            val splitDate = date.split("T")[0].split("-")
            val year = splitDate[0].toInt()
            val month = splitDate[1].toInt()
            val day = splitDate[2].toInt()

            val roozh = Roozh()
            roozh.gregorianToPersian(year, month, day)
            return roozh.toString().replace("-", "/")
        }


        fun logout(context: Context) {
            val appPreference = AppPreferenceTools(context)

            val call = AppController.apiInterface.logout()

            call.enqueue(object : Callback<ResponseModel> {

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.failed_connect_to_server).toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    appPreference.removeUserAuthenticationInfo()
                    val intent = Intent(context, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            })
        }


        fun handleMenuItem(context: Context, itemId: Int?) {
            when (itemId) {
                R.id.home -> {
                    goHome(context)
                }

                R.id.my_links -> {
                    context.startActivity(Intent(context, UserLinksActivity::class.java))
                }

                R.id.edit_account -> {
                    context.startActivity(Intent(context, UpdateUserActivity::class.java))
                }

                R.id.change_password -> {
                    context.startActivity(Intent(context, UserChangePassword::class.java))
                }

                R.id.sign_out -> {
                    logout(context)
                }

                R.id.websites -> {
                    context.startActivity(Intent(context, WebsiteListActivity::class.java))
                }

                R.id.channels -> {
                    context.startActivity(Intent(context, ChannelListActivity::class.java))
                }

                R.id.groups -> {
                    context.startActivity(Intent(context, GroupListActivity::class.java))
                }

                R.id.instagrams -> {
                    context.startActivity(Intent(context, InstagramListActivity::class.java))
                }

                R.id.search_all_links -> {
                    context.startActivity(Intent(context, SearchLinksActivity::class.java))
                }

                R.id.add_new_link -> {
                    val links = arrayOf(context.getString(R.string.website),
                        context.getString(R.string.channel),
                        context.getString(R.string.group),
                        context.getString(R.string.instagram))

                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setTitle(context.getString(R.string.add_new_link))
                    alertDialog.setSingleChoiceItems(links, -1,
                        DialogInterface.OnClickListener { dialog, which ->
                            var intent: Intent? = null

                            when (which) {
                                0 -> {
                                    intent = Intent(context, WebsiteCreateOrUpdateActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                }

                                1 -> {
                                    intent = Intent(context, ChannelCreateOrUpdateActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                }

                                2 -> {
                                    intent = Intent(context, GroupCreateOrUpdateActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                }

                                3 -> {
                                    intent = Intent(context, InstagramCreateOrUpdateActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                }
                            }

                            if (intent != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, context.getString(R.string.select_link_type),
                                    Toast.LENGTH_SHORT).show()
                            }

                            dialog.dismiss()
                        })

                    val dialog = alertDialog.create()
                    dialog.show()
                }

                R.id.categories -> {
                    context.startActivity(Intent(context, CategoryListActivity::class.java))
                }

                R.id.contact_us -> {
                    context.startActivity(Intent(context, ContactUsActivity::class.java))
                }

                R.id.about_us -> {
                    context.startActivity(Intent(context, AboutUsActivity::class.java))
                }

                android.R.id.home -> {
                    (context as Activity).onBackPressed()
                }
            }
        }


        fun checkRuntimePermissionIsGranted(context: Context, permissionType: String): Boolean {
            return PackageManager.PERMISSION_GRANTED ==
                    ActivityCompat.checkSelfPermission(context, permissionType)
        }


        fun requestRunTimePermission(activity: Activity, permissionType: String, requestCode: Int,
                                     permissionEventListener: PermissionEventListener): PermissionEventListener {
            ActivityCompat.requestPermissions(activity, arrayOf(permissionType), requestCode)
            return permissionEventListener
        }


        fun translate(sentence: String): String {
            return when (sentence.toLowerCase()) {
                "inappropriate content" -> "محتوای نامناسب"
                "mismatched title and description" -> "عدم تطابق با عنوان و توضیحات"
                "broken link" -> "لینک خراب"
                "request" -> "درخواست"
                "suggestion and recommendation" -> "پیشنهاد و انتقاد"
                "advertisement" -> "تبلیغات"
                "support" -> "پشتیبانی"

                "محتوای نامناسب" -> "inappropriate content"
                "عدم تطابق با عنوان و توضیحات" -> "mismatched title and description"
                "لینک خراب" -> "broken link"
                "درخواست" -> "request"
                "پیشنهاد و انتقاد" -> "suggestion and recommendation"
                "تبلیغات" -> "advertisement"
                "پشتیبانی" -> "support"

                "واتس اپ" -> "whatsapp"
                "تلگرام" -> "telegram"
                "سروش" -> "soroush"
                "گپ" -> "gap"
                "ای گپ" -> "igap"
                "ایتا" -> "eitaa"
                "ایرانی" -> "iranian"
                "خارجی" -> "foreign"

                else -> sentence
            }
        }


        fun removeErrors(textFieldLayout: TextInputLayout, textInput: AppCompatEditText) {
            textInput.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textFieldLayout.isErrorEnabled = false
                }
            })
        }


        fun goHome(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }


        fun getLanguage(context: Context): String {
            return getCurrentLocale(context).language
        }


        fun getCurrentLocale(context: Context): Locale {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales.get(0)
            } else {

                context.resources.configuration.locale
            }
        }
    }


    interface PermissionEventListener {
        fun onGranted(requestCode: Int, permissions: Array<out String>)

        fun onFailure(requestCode: Int, permissions: Array<out String>)
    }
}