package ir.homelinks.homelinks.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.model.channel.ChannelModel
import ir.homelinks.homelinks.utility.AppController
import ir.homelinks.homelinks.utility.ClientConstants
import ir.homelinks.homelinks.utility.LinkUtility
import kotlinx.android.synthetic.main.activity_create_or_update_channel.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class ChannelCreateOrUpdateActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_channel)

        setSupportActionBar(create_channel_toolbar)
        create_channel_layout.setOnClickListener(null)

        // if slug is provided do update,
        // else create channel
        var slug = ""

        val channelApplicationsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.channel_applications,
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

                        // Going to update channel

                        slug = extras.getString("slug", "")
                        if (slug.isNotEmpty()) {

                            // get channel detail
                            //val callChannelDetail = AppController.apiInterface.linkDetail("channel", slug)
                            val callChannelDetail = AppController.apiInterface.channelDetail(slug)

                            callChannelDetail.enqueue(object: Callback<ChannelModel> {
                                override fun onFailure(call: Call<ChannelModel>, t: Throwable) {
                                    Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                                        Toast.LENGTH_SHORT).show()
                                }

                                override fun onResponse(call: Call<ChannelModel>, response: Response<ChannelModel>) {
                                    if (response.isSuccessful) {
                                        val channel = response.body()!!

                                        title_text.setText(channel.title)
                                        channel_id_text.setText(channel.channelId)
                                        description_text.setText(channel.description)

                                        val category = LinkUtility.findCategoryById(categories!!, channel.category)
                                        val channelCategoryPosition = categoryAdapter.getPosition(category)
                                        category_spinner.setSelection(channelCategoryPosition)

                                        val channelApplication = channel.application.capitalize()
                                        val channelApplicationPosition = channelApplicationsAdapter.getPosition(channelApplication)
                                        application_spinner.setSelection(channelApplicationPosition)

                                    } else {
                                        Toast.makeText(baseContext, "Unable to fetch channel detail",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        }
                    }


                    // Send create/update request
                    submit_channel_button.setOnClickListener {

                        val token = "token ${ClientConstants.TOKEN}"
                        val application = application_spinner.selectedItem.toString().toLowerCase()
                        val title = title_text.text.toString()
                        val channelId = channel_id_text.text.toString().toLowerCase()
                        val category = category_spinner.selectedItem
                        val categoryId = (category as CategoryModel).id
                        val description = description_text.text.toString()

                        val applicationReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, application)
                        val titleReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, title)
                        val channelIdReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, channelId)
                        val categoryReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, categoryId.toString())
                        val descriptionReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, description)

                        val imagePath = "/storage/emulated/0/Download/428934669_312761.jpg"
                        val file = File(imagePath)
                        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)

                        val image: MultipartBody.Part =
                            MultipartBody.Part.createFormData("image", file.name, requestFile)


                        if (slug.isNotEmpty()) {
                            // Make update request
                            updateChannel(token, slug, applicationReqBody, titleReqBody, channelIdReqBody,
                                categoryReqBody, descriptionReqBody, image)
                            Toast.makeText(baseContext, "Update!", Toast.LENGTH_SHORT).show()
                        } else {
                            // Make create request
                            createChannel(token, applicationReqBody, titleReqBody, channelIdReqBody,
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


    private fun updateChannel(token: String, slug: String, applicationReqBody: RequestBody? = null,
                              titleReqBody: RequestBody? = null, channelIdReqBody: RequestBody? = null,
                              categoryReqBody: RequestBody? = null, descriptionReqBody: RequestBody? = null,
                              image: MultipartBody.Part? = null) {

        val callUpdateChannel = AppController.apiInterface.updateChannel(token, slug, applicationReqBody,
            titleReqBody, channelIdReqBody, categoryReqBody, descriptionReqBody, image)

        callUpdateChannel.enqueue(object : Callback<ChannelModel> {
            override fun onFailure(call: Call<ChannelModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ChannelModel>,
                response: Response<ChannelModel>
            ) {

                if (response.isSuccessful) {
                    val channel = response.body()!!
                    Toast.makeText(baseContext, channel.title, Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, channel.description, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Failed to update channel!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun createChannel(token: String, applicationReqBody: RequestBody, titleReqBody: RequestBody,
                              channelIdReqBody: RequestBody, categoryReqBody: RequestBody,
                              descriptionReqBody: RequestBody, image: MultipartBody.Part) {

        val callCreateChannel = AppController.apiInterface.createChannel(token, applicationReqBody,
            titleReqBody, channelIdReqBody, categoryReqBody, descriptionReqBody, image)

        callCreateChannel.enqueue(object : Callback<ChannelModel> {
            override fun onFailure(call: Call<ChannelModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_to_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ChannelModel>,
                response: Response<ChannelModel>
            ) {

                if (response.isSuccessful) {
                    val channel = response.body()!!
                    Toast.makeText(baseContext, channel.title, Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, channel.description, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Failed to create channel!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
