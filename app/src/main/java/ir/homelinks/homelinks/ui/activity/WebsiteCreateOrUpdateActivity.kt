package ir.homelinks.homelinks.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.squareup.picasso.Picasso
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.model.website.WebsiteModel
import ir.homelinks.homelinks.utility.*
import kotlinx.android.synthetic.main.activity_create_or_update_website.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import ir.homelinks.homelinks.utility.LinkUtility.PermissionEventListener


class WebsiteCreateOrUpdateActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreferenceTools
    private var imageUri: Uri = Uri.EMPTY
    private var permissionEventListener: PermissionEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_website)

        create_website_layout.setOnClickListener(null)
        create_website_toolbar.title = getString(R.string.create_website)
        setSupportActionBar(create_website_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appPreference = AppPreferenceTools(this)

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

        select_image_view.setOnClickListener {
            getImage()
        }

        change_image_text_view.setOnClickListener {
            getImage()
        }

        val callGetCategories = AppController.apiInterface.getCategories()

        callGetCategories.enqueue(object: Callback<List<CategoryModel>> {
            override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable) {
                Toast.makeText(baseContext,
                    getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<CategoryModel>>,
                                    response: Response<List<CategoryModel>>) {

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

                            change_image_text_view.text = getString(R.string.change_image)

                            // get website detail
                            val callWebsiteDetail = AppController.apiInterface.websiteDetail(slug)

                            callWebsiteDetail.enqueue(object: Callback<WebsiteModel> {
                                override fun onFailure(call: Call<WebsiteModel>, t: Throwable) {
                                    Toast.makeText(baseContext, getString(R.string.failed_connect_to_server).toString(),
                                        Toast.LENGTH_SHORT).show()
                                }

                                override fun onResponse(call: Call<WebsiteModel>, response: Response<WebsiteModel>) {
                                    if (response.isSuccessful) {
                                        val website = response.body()!!

                                        title_text.setText(website.title)
                                        url_text.setText(website.url)
                                        description_text.setText(website.description)
                                        Picasso.get().load(website.image).into(select_image_view)

                                        val category = LinkUtility.findCategoryById(categories!!, website.category)
                                        val websiteCategoryPosition = categoryAdapter.getPosition(category)
                                        category_spinner.setSelection(websiteCategoryPosition)

                                        val websiteType = website.type.capitalize()
                                        val websiteTypePosition = websiteTypesAdapter.getPosition(websiteType)
                                        type_spinner.setSelection(websiteTypePosition)

                                    } else {
                                        Toast.makeText(baseContext, getString(R.string.unable_to_fetch_website),
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            })
                        }
                    }


                    // Send create/update request
                    submit_website_button.setOnClickListener {

                        val token = "token ${appPreference.getUserToken()}"
                        val title = title_text.text.toString()
                        val url = url_text.text.toString()
                        val type = type_spinner.selectedItem.toString().toLowerCase()
                        val category = category_spinner.selectedItem
                        val categoryId = (category as CategoryModel).id
                        val description = description_text.text.toString()

                        val defaultType = getString(R.string.default_website_type).toLowerCase()
                        var showToastFlag = 0

                        // use slug.isEmpty() to distinguish create from update action
                        if (title.isEmpty() || url.isEmpty() || description.isEmpty() ||
                            type == defaultType || (imageUri == Uri.EMPTY && slug.isEmpty())) {

                            if (title.isEmpty()) {
                                val message = getString(R.string.title_cant_be_blank)
                                setFieldMessage(title_input_layout, message)
                                showToastFlag += 1
                            } else {
                                title_input_layout.isErrorEnabled = false
                            }

                            if (url.isEmpty()) {
                                val message = getString(R.string.url_cant_be_blank)
                                setFieldMessage(url_input_layout, message)
                                showToastFlag += 1
                            } else {
                                url_input_layout.isErrorEnabled = false
                            }

                            if (description.isEmpty()) {
                                val message = getString(R.string.description_cant_be_blank)
                                setFieldMessage(description_input_layout, message)
                                showToastFlag += 1
                            } else {
                                description_input_layout.isErrorEnabled = false
                            }

                            if (type == defaultType) {
                                if (showToastFlag == 0) {
                                    val message = getString(R.string.select_website_type)
                                    Toast.makeText(baseContext, message, Toast.LENGTH_LONG).show()
                                    showToastFlag += 1
                                }
                            }

                            if (imageUri == Uri.EMPTY) {
                                if (slug.isEmpty()) {
                                    if (showToastFlag == 0) {
                                        Toast.makeText(baseContext, getString(R.string.provide_image),
                                            Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        } else {

                            val titleReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, title)
                            val urlReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, url)
                            val typeReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, type)
                            val categoryReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, categoryId.toString())
                            val descriptionReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, description)

                            val imagePath: String
                            val file: File
                            val image: MultipartBody.Part?
                            val requestFile: RequestBody

                            if (imageUri != Uri.EMPTY) {
                                imagePath = FileUtility.getPath(imageUri)
                                file = File(imagePath)
                                requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                                image = MultipartBody.Part.createFormData("image", file.name, requestFile)
                            } else {
                                image = null
                            }

                            if (slug.isNotEmpty()) {
                                // Make update request
                                updateWebsite(
                                    token, slug, titleReqBody, urlReqBody, typeReqBody,
                                    categoryReqBody, descriptionReqBody, image
                                )
                            } else {
                                // Make create request
                                createWebsite(
                                    token, titleReqBody, urlReqBody, typeReqBody,
                                    categoryReqBody, descriptionReqBody, image!!
                                )
                            }
                        }
                    }

                } else {
                    Toast.makeText(baseContext, getString(R.string.failed_to_fetch_categories), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    private fun updateWebsite(token: String, slug: String, titleReqBody: RequestBody?, urlReqBody: RequestBody?,
                              typeReqBody: RequestBody?, categoryReqBody: RequestBody?,
                              descriptionReqBody: RequestBody?, image: MultipartBody.Part?) {

        val callUpdateWebsite = AppController.apiInterface.updateWebsite(token, slug, titleReqBody,
            urlReqBody, typeReqBody, categoryReqBody, descriptionReqBody, image)

        callUpdateWebsite.enqueue(object : Callback<WebsiteModel> {
            override fun onFailure(call: Call<WebsiteModel>, t: Throwable) {
                Toast.makeText(baseContext, getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<WebsiteModel>,
                response: Response<WebsiteModel>
            ) {

                if (response.isSuccessful) {
                    val website = response.body()!!
                    Toast.makeText(baseContext, website.title, Toast.LENGTH_SHORT).show()
                    val intent = Intent(baseContext, LinkDetailActivity::class.java)
                    intent.putExtra("link", "website")
                    intent.putExtra("slug", website.slug)
                    startActivity(intent)
                    finish()
                } else {
                    managerErrors(response)
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
                Toast.makeText(baseContext, getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<WebsiteModel>,
                response: Response<WebsiteModel>
            ) {

                if (response.isSuccessful) {
                    Toast.makeText(baseContext, getString(R.string.link_created), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(baseContext, UserLinksActivity::class.java))
                    finish()
                } else {
                    managerErrors(response)
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.create_link_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        LinkUtility.handleMenuItem(this, item?.itemId)
        return super.onOptionsItemSelected(item)
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ClientConstants.GET_CONTENT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data!!
            select_image_view.setImageURI(imageUri)
        }
    }


    private fun setFieldMessage(field: TextInputLayout, message: String) {
        field.isErrorEnabled = true
        field.error = message
    }


    private fun managerErrors(response: Response<WebsiteModel>) {
        val errors = Messages.getErrors(response, listOf("type", "title", "url",
            "category", "description", "image", "non_field_errors"))

        var titleErrorMessage = ""
        var urlErrorMessage = ""
        var descriptionErrorMessage = ""

        for (error in errors) {
            if (error.key == "title") {
                titleErrorMessage += error.value.joinToString("\n")
            } else if (error.key == "url") {
                urlErrorMessage += error.value.joinToString("\n")
            } else if (error.key == "description") {
                descriptionErrorMessage += error.value.joinToString("\n")
            } else {
                for (value in error.value) {
                    Toast.makeText(baseContext, value, Toast.LENGTH_LONG).show()
                }
            }
        }

        title_input_layout.isErrorEnabled = true
        title_input_layout.error = titleErrorMessage

        url_input_layout.isErrorEnabled = true
        url_input_layout.error = urlErrorMessage

        description_input_layout.isErrorEnabled = true
        description_input_layout.error = descriptionErrorMessage
    }


    private fun getImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        if (LinkUtility.checkRuntimePermissionIsGranted(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startActivityForResult(intent, ClientConstants.GET_CONTENT_REQUEST_CODE)
        } else {
            permissionEventListener =
                LinkUtility.requestRunTimePermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ClientConstants.FOR_OPEN_GALLERY_REQUEST_WRITE_EXTERNAL_STORAGE_PER,
                    object: PermissionEventListener {
                        override fun onGranted(requestCode: Int, permissions: Array<out String>) {
                            startActivityForResult(intent, ClientConstants.GET_CONTENT_REQUEST_CODE)
                        }

                        override fun onFailure(requestCode: Int, permissions: Array<out String>) {
                            Toast.makeText(baseContext, getString(R.string.dont_have_permission),
                                Toast.LENGTH_SHORT).show()
                        }
                    })
        }
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissionEventListener != null) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionEventListener?.onGranted(requestCode, permissions)
            } else {
                permissionEventListener?.onFailure(requestCode, permissions)
            }
        }
    }
}
