package ir.homelinks.homelinks.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.squareup.picasso.Picasso
import ir.homelinks.homelinks.R
import ir.homelinks.homelinks.model.CategoryModel
import ir.homelinks.homelinks.model.instagram.InstagramModel
import ir.homelinks.homelinks.utility.*
import kotlinx.android.synthetic.main.activity_create_or_update_instagram.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class InstagramCreateOrUpdateActivity : AppCompatActivity() {

    private lateinit var appPreference: AppPreferenceTools
    private var imageUri: Uri = Uri.EMPTY
    private var permissionEventListener: LinkUtility.PermissionEventListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_or_update_instagram)


        create_instagram_layout.setOnClickListener(null)
        create_instagram_toolbar.title = getString(R.string.create_instagram)
        setSupportActionBar(create_instagram_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        appPreference = AppPreferenceTools(baseContext)

        // if slug is provided do update,
        // else create instagram
        var slug = ""

        select_image_view.setOnClickListener {
            getImage()
        }

        change_image_text_view.setOnClickListener {
            getImage()
        }

        val callGetCategories = AppController.apiInterface.getCategories()

        callGetCategories.enqueue(object : Callback<List<CategoryModel>> {
            override fun onFailure(call: Call<List<CategoryModel>>, t: Throwable) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.failed_connect_to_server).toString(),
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

                            change_image_text_view.text = getString(R.string.change_image)

                            // get instagram detail
                            val callInstagramDetail = AppController.apiInterface.instagramDetail(slug)

                            callInstagramDetail.enqueue(object : Callback<InstagramModel> {
                                override fun onFailure(call: Call<InstagramModel>, t: Throwable) {
                                    Toast.makeText(
                                        baseContext, getString(R.string.failed_connect_to_server).toString(),
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
                                        Picasso.get().load(instagram.image).into(select_image_view)

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

                        val token = "token ${appPreference.getUserToken()}"
                        val title = title_text.text.toString()
                        val pageId = page_id_text.text.toString().toLowerCase()
                        val category = category_spinner.selectedItem
                        val categoryId = (category as CategoryModel).id
                        val description = description_text.text.toString()

                        var showToastFlag = 0

                        // use slug.isEmpty() to distinguish create from update action
                        if (title.isEmpty() || pageId.isEmpty() || description.isEmpty() ||
                            (imageUri == Uri.EMPTY && slug.isEmpty())
                        ) {

                            if (title.isEmpty()) {
                                val message = getString(R.string.title_cant_be_blank)
                                setFieldMessage(title_input_layout, message)
                                showToastFlag += 1
                            } else {
                                title_input_layout.isErrorEnabled = false
                            }

                            if (pageId.isEmpty()) {
                                val message = getString(R.string.page_id_cant_be_blank)
                                setFieldMessage(page_id_input_layout, message)
                                showToastFlag += 1
                            } else {
                                page_id_input_layout.isErrorEnabled = false
                            }

                            if (description.isEmpty()) {
                                val message = getString(R.string.description_cant_be_blank)
                                setFieldMessage(description_input_layout, message)
                                showToastFlag += 1
                            } else {
                                description_input_layout.isErrorEnabled = false
                            }

                            if (imageUri == Uri.EMPTY) {
                                if (slug.isEmpty()) {
                                    if (showToastFlag == 0) {
                                        Toast.makeText(
                                            baseContext, getString(R.string.provide_image),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        } else {

                            val titleReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, title)
                            val pageIdReqBody = RequestBody.create(okhttp3.MultipartBody.FORM, pageId)
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
                                updateInstagram(
                                    token, slug, titleReqBody, pageIdReqBody,
                                    categoryReqBody, descriptionReqBody, image
                                )
                            } else {
                                // Make create request
                                createInstagram(
                                    token, titleReqBody, pageIdReqBody,
                                    categoryReqBody, descriptionReqBody, image!!
                                )
                            }
                        }
                    }

                } else {
                    Toast.makeText(baseContext, getString(R.string.failed_to_fetch_categories), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }


    private fun updateInstagram(
        token: String, slug: String, titleReqBody: RequestBody?,
        pageIdReqBody: RequestBody?, categoryReqBody: RequestBody?,
        descriptionReqBody: RequestBody?, image: MultipartBody.Part?
    ) {

        val callUpdateInstagram = AppController.apiInterface.updateInstagram(
            token, slug, titleReqBody,
            pageIdReqBody, categoryReqBody, descriptionReqBody, image
        )

        callUpdateInstagram.enqueue(object : Callback<InstagramModel> {
            override fun onFailure(call: Call<InstagramModel>, t: Throwable) {
                Toast.makeText(
                    baseContext, getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<InstagramModel>,
                response: Response<InstagramModel>
            ) {

                if (response.isSuccessful) {
                    val instagram = response.body()!!
                    Toast.makeText(baseContext, instagram.title, Toast.LENGTH_SHORT).show()
                    val intent = Intent(baseContext, LinkDetailActivity::class.java)
                    intent.putExtra("link", "instagram")
                    intent.putExtra("slug", instagram.slug)
                    startActivity(intent)
                    finish()
                } else {
                    managerErrors(response)
                }
            }
        })
    }


    private fun createInstagram(
        token: String, titleReqBody: RequestBody,
        pageIdReqBody: RequestBody, categoryReqBody: RequestBody,
        descriptionReqBody: RequestBody, image: MultipartBody.Part
    ) {

        val callCreateInstagram = AppController.apiInterface.createInstagram(
            token, titleReqBody,
            pageIdReqBody, categoryReqBody, descriptionReqBody, image
        )

        callCreateInstagram.enqueue(object : Callback<InstagramModel> {
            override fun onFailure(call: Call<InstagramModel>, t: Throwable) {
                Toast.makeText(
                    baseContext, getString(R.string.failed_connect_to_server).toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onResponse(
                call: Call<InstagramModel>,
                response: Response<InstagramModel>
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


    private fun managerErrors(response: Response<InstagramModel>) {
        val errors = Messages.getErrors(
            response, listOf(
                "title", "page_id",
                "category", "description", "image", "non_field_errors"
            )
        )

        var titleErrorMessage = ""
        var pageIdErrorMessage = ""
        var descriptionErrorMessage = ""

        for (error in errors) {
            if (error.key == "title") {
                titleErrorMessage += error.value.joinToString("\n")
            } else if (error.key == "page_id") {
                pageIdErrorMessage += error.value.joinToString("\n")
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

        page_id_input_layout.isErrorEnabled = true
        page_id_input_layout.error = pageIdErrorMessage

        description_input_layout.isErrorEnabled = true
        description_input_layout.error = descriptionErrorMessage
    }


    private fun getImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"

        if (LinkUtility.checkRuntimePermissionIsGranted(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            startActivityForResult(intent, ClientConstants.GET_CONTENT_REQUEST_CODE)
        } else {
            permissionEventListener =
                LinkUtility.requestRunTimePermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ClientConstants.FOR_OPEN_GALLERY_REQUEST_WRITE_EXTERNAL_STORAGE_PER,
                    object : LinkUtility.PermissionEventListener {
                        override fun onGranted(requestCode: Int, permissions: Array<out String>) {
                            startActivityForResult(intent, ClientConstants.GET_CONTENT_REQUEST_CODE)
                        }

                        override fun onFailure(requestCode: Int, permissions: Array<out String>) {
                            Toast.makeText(
                                baseContext, getString(R.string.dont_have_permission),
                                Toast.LENGTH_SHORT
                            ).show()
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