package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.model.instagram.InstagramModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.ClientConstants
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_create_or_update_instagram.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class InstagramCreateOrUpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_instagram)

        setSupportActionBar(create_instagram_toolbar)
        create_instagram_layout.setOnClickListener(null)

        // if slug is provided do update,
        // else create instagram
        var slug = ""

        val callGetCategories = AppController.apiInterface.getCategories()

        callGetCategories.enqueue(object : Callback<List<CategoryModel>> {
            override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<List<CategoryModel>>,
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

                        // Going to update instagram

                        slug = extras.getString("slug", "")
                        if (slug.isNotEmpty()) {

                            // get instagram detail
                            val callInstagramDetail = AppController.apiInterface.instagramDetail(slug)

                            callInstagramDetail.enqueue(object : Callback<InstagramModel> {
                                override fun onFailure(call: Call<InstagramModel>, t: Throwable) {
                                    Toast.makeText(
                                        baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                override fun onResponse(
                                    call: Call<InstagramModel>,
                                    response: Response<InstagramModel>
                                ) {
                                    if (response.isSuccessful) {
                                        val instagram = response.body()!!

                                        title_text.setText(instagram.title)
                                        page_id_text.setText(instagram.pageId)
                                        description_text.setText(instagram.description)

                                        val category = LinkUtility.findCategoryById(categories!!, instagram.category)
                                        val instagramCategoryPosition = categoryAdapter.getPosition(category)
                                        category_spinner.setSelection(instagramCategoryPosition)

                                    } else {
                                        Toast.makeText(
                                            baseContext, "Unable to fetch instagram detail",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            })
                        }
                    }


                    // Send create/update request
                    submit_instagram_button.setOnClickListener {

                        val token = "token ${ClientConstants.TOKEN}"
                        val title = title_text.text.toString()
                        val pageId = page_id_text.text.toString().toLowerCase()
                        val category = category_spinner.selectedItem
                        val categoryId = (category as CategoryModel).id
                        val description = description_text.text.toString()

                        val titleReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, title)
                        val pageIdReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, pageId)
                        val categoryReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, categoryId.toString())
                        val descriptionReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, description)

                        val imagePath = "/storage/emulated/0/Download/428934669_312761.jpg"
                        val file = File(imagePath)
                        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

                        val image: MultipartBody.Part =
                            MultipartBody.Part.createFormData("image", file.name, requestFile)


                        if (slug.isNotEmpty()) {
                            // Make update request
                            updateInstagram(
                                token, slug, titleReqBody, pageIdReqBody,
                                categoryReqBody, descriptionReqBody, image
                            )
                            Toast.makeText(baseContext, "Update!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Make create request
                            createInstagram(
                                token, titleReqBody, pageIdReqBody,
                                categoryReqBody, descriptionReqBody, image
                            )
                            Toast.makeText(baseContext, "Create!", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(baseContext, "Failed to fetch categories!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun updateInstagram(token: String, slug: String, titleReqBody: RequestBody?,
                                pageIdReqBody: RequestBody?, categoryReqBody: RequestBody?,
                                descriptionReqBody: RequestBody?, image: MultipartBody.Part?) {

        val callInstagramChannel = AppController.apiInterface.updateInstagram(token, slug, titleReqBody,
            pageIdReqBody, categoryReqBody, descriptionReqBody, image)

        callInstagramChannel.enqueue(object : Callback<InstagramModel> {
            override fun onFailure(call: Call<InstagramModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<InstagramModel>,
                response: Response<InstagramModel>
            ) {

                if (response.isSuccessful) {
                    val instagram = response.body()!!
                    Toast.makeText(baseContext, instagram.title, Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, instagram.description, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Failed to update instagram!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun createInstagram(token: String, titleReqBody: RequestBody,
                              pageIdReqBody: RequestBody, categoryReqBody: RequestBody,
                              descriptionReqBody: RequestBody, image: MultipartBody.Part) {

        val callCreateChannel = AppController.apiInterface.createInstagram(token, titleReqBody,
            pageIdReqBody, categoryReqBody, descriptionReqBody, image)

        callCreateChannel.enqueue(object : Callback<InstagramModel> {
            override fun onFailure(call: Call<InstagramModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<InstagramModel>,
                response: Response<InstagramModel>
            ) {

                if (response.isSuccessful) {
                    val instagram = response.body()!!
                    Toast.makeText(baseContext, instagram.title, Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, instagram.description, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Failed to create instagram!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}

