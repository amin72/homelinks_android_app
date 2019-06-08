package ir.homelinks.homelinks.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.model.ResponseModel
import ir.homelinks.homelinks.ui.activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
            result: 1398-03-01
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
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
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

                R.id.add_new_link -> {
                    context.startActivity(Intent(context, AddLinkActivity::class.java))
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


        fun getImage(activity: Activity) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            activity.startActivityForResult(intent, ClientConstants.GET_CONTENT_REQUEST_CODE)
        }
    }


    interface PermissionEventListener {
        fun onGranted(requestCode: Int, permissions: Array<out String>)

        fun onFailure(requestCode: Int, permissions: Array<out String>)
    }
}