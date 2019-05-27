package ir.homelinks.homelinks.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.model.website.WebsiteModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.ClientConstants
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_create_or_update_website.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class WebsiteCreateOrUpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_website)

        setSupportActionBar(create_website_toolbar)
        create_website_layout.setOnClickListener(null)

        // if slug is provided do update,
        // else create website
        var slug = ""

        val websiteTypesAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.website_types,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            type_spinner.adapter = adapter
        }


        val callGetCategories = AppController.apiInterface.getCategories()

        callGetCategories.enqueue(object: Callback<List<CategoryModel>> {
            override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable) {
                Toast.makeText(baseContext,
                    getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<CategoryModel>>,
                                    response: Response<List<CategoryModel>>
            ) {

                if (response.isSuccessful) {
                    val categories = response.body()

                    val categoryAdapter = ArrayAdapter<CategoryModel>(
                        baseContext,
                        android.R.layout.simple_spinner_item,
                        categories
                    ).also { adapter ->
                        // Specify the layout to use when the list of choices appears
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        // Apply the adapter to the spinner
                        category_spinner.adapter = adapter
                    }


                    // if categories are ready, do the rest
                    val extras = intent.extras
                    if (extras != null) {

                        // Going to update website

                        slug = extras.getString("slug", "")
                        if (slug.isNotEmpty()) {
                            // get website detail
                            val callWebsiteDetail = AppController.apiInterface.websiteDetail(slug)

                            callWebsiteDetail.enqueue(object: Callback<WebsiteModel> {
                                override fun onFailure(call: Call<WebsiteModel>, t: Throwable) {
                                    Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                                        Toast.LENGTH_SHORT).show()
                                    Log.d("--------", t.message)
                                }

                                override fun onResponse(call: Call<WebsiteModel>, response: Response<WebsiteModel>) {
                                    if (response.isSuccessful) {
                                        val website = response.body()!!

                                        title_text.setText(website.title)
                                        url_text.setText(website.url)
                                        description_text.setText(website.description)

                                        val category = LinkUtility.findCategoryById(categories!!, website.category)
                                        val websiteCategoryPosition = categoryAdapter.getPosition(category)
                                        category_spinner.setSelection(websiteCategoryPosition)

                                        val websiteType = website.type.capitalize()
                                        val websiteTypePosition = websiteTypesAdapter.getPosition(websiteType)
                                        type_spinner.setSelection(websiteTypePosition)

                                    } else {
                                        Toast.makeText(baseContext, "Unable to fetch website detail",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        }
                    }


                    // Send create/update request
                    submit_website_button.setOnClickListener {

                        val token = "token ${ClientConstants.TOKEN}"
                        val title = title_text.text.toString()
                        val url = url_text.text.toString()
                        val type = type_spinner.selectedItem.toString().toLowerCase()
                        val category = category_spinner.selectedItem
                        val categoryId = (category as CategoryModel).id
                        val description = description_text.text.toString()


                        val titleReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, title)
                        val urlReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, url)
                        val typeReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, type)
                        val categoryReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, categoryId.toString())
                        val descriptionReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, description)

                        val imagePath = "/storage/emulated/0/Download/428934669_312761.jpg"
                        val file = File(imagePath)
                        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

                        val image: MultipartBody.Part =
                            MultipartBody.Part.createFormData("image", file.name, requestFile)


                        if (slug.isNotEmpty()) {
                            // Make update request
                            updateWebsite(token, slug, titleReqBody, urlReqBody, typeReqBody,
                                categoryReqBody, descriptionReqBody, image)
                        } else {
                            // Make create request
                            createWebsite(token, titleReqBody, urlReqBody, typeReqBody,
                                categoryReqBody, descriptionReqBody, image)
                        }
                    }

                } else {
                    Toast.makeText(baseContext, "Failed to fetch categories!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun updateWebsite(token: String, slug: String, titleReqBody: RequestBody, urlReqBody: RequestBody,
                              typeReqBody: RequestBody, categoryReqBody: RequestBody,
                              descriptionReqBody: RequestBody, image: MultipartBody.Part) {

        val callUpdateWebsite = AppController.apiInterface.updateWebsite(token, slug, titleReqBody,
            urlReqBody, typeReqBody, categoryReqBody, descriptionReqBody, image)

        callUpdateWebsite.enqueue(object : Callback<WebsiteModel> {
            override fun onFailure(call: Call<WebsiteModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<WebsiteModel>,
                response: Response<WebsiteModel>
            ) {

                if (response.isSuccessful) {
                    val website = response.body()!!
                    Toast.makeText(baseContext, website.title, Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, website.description, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Failed to update website!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun createWebsite(token: String, titleReqBody: RequestBody, urlReqBody: RequestBody,
                              typeReqBody: RequestBody, categoryReqBody: RequestBody,
                              descriptionReqBody: RequestBody, image: MultipartBody.Part) {

        val callCreateWebsite = AppController.apiInterface.createWebsite(token, titleReqBody,
            urlReqBody, typeReqBody, categoryReqBody, descriptionReqBody, image)

        callCreateWebsite.enqueue(object : Callback<WebsiteModel> {
            override fun onFailure(call: Call<WebsiteModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<WebsiteModel>,
                response: Response<WebsiteModel>
            ) {

                if (response.isSuccessful) {
                    val website = response.body()!!
                    Toast.makeText(baseContext, website.title, Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, website.description, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Failed to create website!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
