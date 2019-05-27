package ir.homelinks.homelinks.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.model.group.GroupModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.ClientConstants
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_create_or_update_group.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class GroupCreateOrUpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_group)

        setSupportActionBar(create_group_toolbar)
        create_group_layout.setOnClickListener(null)

        // if slug is provided do update,
        // else create group
        var slug = ""

        val groupApplicationsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.group_applications,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            application_spinner.adapter = adapter
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

                        // Going to update group

                        slug = extras.getString("slug", "")
                        if (slug.isNotEmpty()) {

                            // get group detail
                            val callGroupDetail = AppController.apiInterface.groupDetail(slug)

                            callGroupDetail.enqueue(object: Callback<GroupModel> {
                                override fun onFailure(call: Call<GroupModel>, t: Throwable) {
                                    Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                                        Toast.LENGTH_SHORT).show()
                                }

                                override fun onResponse(call: Call<GroupModel>, response: Response<GroupModel>) {
                                    if (response.isSuccessful) {
                                        val group = response.body()!!

                                        title_text.setText(group.title)
                                        url_text.setText(group.url)
                                        description_text.setText(group.description)

                                        val category = LinkUtility.findCategoryById(categories!!, group.category)
                                        val groupCategoryPosition = categoryAdapter.getPosition(category)
                                        category_spinner.setSelection(groupCategoryPosition)

                                        val groupApplication = group.application.capitalize()
                                        val groupApplicationPosition = groupApplicationsAdapter.getPosition(groupApplication)
                                        application_spinner.setSelection(groupApplicationPosition)

                                    } else {
                                        Toast.makeText(baseContext, "Unable to fetch group detail",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        }
                    }


                    // Send create/update request
                    submit_group_button.setOnClickListener {

                        val token = "token ${ClientConstants.TOKEN}"
                        val application = application_spinner.selectedItem.toString().toLowerCase()
                        val title = title_text.text.toString()
                        val url = url_text.text.toString().toLowerCase()
                        val category = category_spinner.selectedItem
                        val categoryId = (category as CategoryModel).id
                        val description = description_text.text.toString()

                        val applicationReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, application)
                        val titleReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, title)
                        val urlReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, url)
                        val categoryReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, categoryId.toString())
                        val descriptionReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, description)

                        val imagePath = "/storage/emulated/0/Download/428934669_312761.jpg"
                        val file = File(imagePath)
                        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

                        val image: MultipartBody.Part =
                            MultipartBody.Part.createFormData("image", file.name, requestFile)


                        if (slug.isNotEmpty()) {
                            // Make update request
                            updateGroup(token, slug, applicationReqBody, titleReqBody, urlReqBody,
                                categoryReqBody, descriptionReqBody, image)
                            Toast.makeText(baseContext, "Update!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Make create request
                            createGroup(token, applicationReqBody, titleReqBody, urlReqBody,
                                categoryReqBody, descriptionReqBody, image)
                            Toast.makeText(baseContext, "Create!", Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {
                    Toast.makeText(baseContext, "Failed to fetch categories!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun updateGroup(token: String, slug: String, applicationReqBody: RequestBody,
                              titleReqBody: RequestBody, urlReqBody: RequestBody,
                              categoryReqBody: RequestBody, descriptionReqBody: RequestBody,
                              image: MultipartBody.Part) {

        val callUpdateGroup = AppController.apiInterface.updateGroup(token, slug, applicationReqBody,
            titleReqBody, urlReqBody, categoryReqBody, descriptionReqBody, image)

        callUpdateGroup.enqueue(object : Callback<GroupModel> {
            override fun onFailure(call: Call<GroupModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<GroupModel>, response: Response<GroupModel>
            ) {

                if (response.isSuccessful) {
                    val group = response.body()!!
                    Toast.makeText(baseContext, group.title, Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, group.description, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Failed to update group!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun createGroup(token: String, applicationReqBody: RequestBody, titleReqBody: RequestBody,
                              urlReqBody: RequestBody, categoryReqBody: RequestBody,
                              descriptionReqBody: RequestBody, image: MultipartBody.Part) {

        val callCreateGroup = AppController.apiInterface.createGroup(token, applicationReqBody,
            titleReqBody, urlReqBody, categoryReqBody, descriptionReqBody, image)

        callCreateGroup.enqueue(object : Callback<GroupModel> {
            override fun onFailure(call: Call<GroupModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<GroupModel>,
                response: Response<GroupModel>
            ) {

                if (response.isSuccessful) {
                    val group = response.body()!!
                    Toast.makeText(baseContext, group.title, Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, group.description, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Failed to create group!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
