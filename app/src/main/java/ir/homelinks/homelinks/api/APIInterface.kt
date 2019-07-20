package ir.homelinks.homelinks.api

import ir.homelinks.homelinks.model.*
import ir.homelinks.homelinks.model.channel.ChannelModel
import ir.homelinks.homelinks.model.contact_us.ContactUsModel
import ir.homelinks.homelinks.model.contact_us.ContactUsOptions
import ir.homelinks.homelinks.model.group.GroupModel
import ir.homelinks.homelinks.model.instagram.InstagramModel
import ir.homelinks.homelinks.model.report_links.ReportLinkModel
import ir.homelinks.homelinks.model.report_links.ReportLinkOptions
import ir.homelinks.homelinks.model.website.WebsiteModel
import ir.homelinks.homelinks.utility.ClientConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface APIInterface {

    @GET(".")
    fun index(): Call<IndexResult>


    @GET("{link}/")
    fun links(
        @Path("link") link: String,
        @Query("page") page: Int = 1,
        @Query("type") type: String? = null,
        @Query("app") app: String? = null
    ): Call<LinkResults>


    @GET("{link}/{slug}/")
    fun linkDetail(
        @Path("link") link: String,
        @Path("slug") slug: String
    ): Call<LinkModel>


    @GET("website/{slug}/")
    fun websiteDetail(@Path("slug") slug: String): Call<WebsiteModel>


    @GET("channel/{slug}/")
    fun channelDetail(@Path("slug") slug: String): Call<ChannelModel>


    @GET("group/{slug}/")
    fun groupDetail(@Path("slug") slug: String): Call<GroupModel>


    @GET("instagram/{slug}/")
    fun instagramDetail(@Path("slug") slug: String): Call<InstagramModel>


    @POST("auth/login/")
    fun login(@Body user: UserModel): Call<TokenModel>


    @POST("auth/logout/")
    fun logout(): Call<ResponseModel>


    @POST("auth/register/")
    fun register(@Body user: UserModel): Call<UserModel>


    @GET("auth/user/")
    fun getUserInfo(@Header("Authorization") token: String): Call<UserModel>


    @Multipart
    @POST("websites/create/")
    fun createWebsite(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("url") url: RequestBody,
        @Part("type") type: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<WebsiteModel>


    @Multipart
    @PATCH("website/{slug}/update/")
    fun updateWebsite(
        @Header("Authorization") token: String,
        @Path("slug") slug: String,
        @Part("title") title: RequestBody? = null,
        @Part("url") url: RequestBody? = null,
        @Part("type") type: RequestBody? = null,
        @Part("category") category: RequestBody? = null,
        @Part("description") description: RequestBody? = null,
        @Part image: MultipartBody.Part? = null
    ): Call<WebsiteModel>


    @Multipart
    @POST("channels/create/")
    fun createChannel(
        @Header("Authorization") token: String,
        @Part("application") application: RequestBody,
        @Part("title") title: RequestBody,
        @Part("channel_id") channel_id: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<ChannelModel>


    // All channel fields are optional, User can update any of them
    @Multipart
    @PATCH("channel/{slug}/update/")
    fun updateChannel(
        @Header("Authorization") token: String,
        @Path("slug") slug: String,
        @Part("application") application: RequestBody? = null,
        @Part("title") title: RequestBody? = null,
        @Part("channel_id") channel_id: RequestBody? = null,
        @Part("category") category: RequestBody? = null,
        @Part("description") description: RequestBody? = null,
        @Part image: MultipartBody.Part? = null
    ): Call<ChannelModel>


    @Multipart
    @POST("groups/create/")
    fun createGroup(
        @Header("Authorization") token: String,
        @Part("application") application: RequestBody,
        @Part("title") title: RequestBody,
        @Part("url") url: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<GroupModel>


    @Multipart
    @PATCH("group/{slug}/update/")
    fun updateGroup(
        @Header("Authorization") token: String,
        @Path("slug") slug: String,
        @Part("application") application: RequestBody? = null,
        @Part("title") title: RequestBody? = null,
        @Part("url") url: RequestBody? = null,
        @Part("category") category: RequestBody? = null,
        @Part("description") description: RequestBody? = null,
        @Part image: MultipartBody.Part? = null
    ): Call<GroupModel>


    @Multipart
    @POST("instagrams/create/")
    fun createInstagram(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("page_id") page_id: RequestBody,
        @Part("category") category: RequestBody,
        @Part("description") description: RequestBody,
        @Part image: MultipartBody.Part
    ): Call<InstagramModel>


    @Multipart
    @PATCH("instagram/{slug}/update/")
    fun updateInstagram(
        @Header("Authorization") token: String,
        @Path("slug") slug: String,
        @Part("title") title: RequestBody? = null,
        @Part("page_id") page_id: RequestBody? = null,
        @Part("category") category: RequestBody? = null,
        @Part("description") description: RequestBody? = null,
        @Part image: MultipartBody.Part? = null
    ): Call<InstagramModel>


    @GET("categories/")
    fun getCategories(): Call<List<CategoryModel>>


    @GET("category/{id}/")
    fun getCategorizedItems(
        @Path("id") id: Int,
        @Query("page") page: Int = 1
    ): Call<PaginatedResponseModel>


    @POST("{link}/{slug}/report_link/")
    fun reportLink(
        @Path("link") link: String,
        @Path("slug") slug: String,
        @Body reportLink: ReportLinkModel
    ): Call<ReportLinkModel>


    @OPTIONS("link/slug/report_link/")
    fun reportLinkChoices(): Call<ReportLinkOptions>

    @OPTIONS("${ClientConstants.HOMELINKS_API_FA_URL}link/slug/report_link/")
    fun reportLinkChoicesFa(): Call<ReportLinkOptions>


    @POST("contact/contact_us/")
    fun contactUs(@Body contact: ContactUsModel): Call<ContactUsModel>


    @OPTIONS("contact/contact_us/")
    fun contactUsChoices(): Call<ContactUsOptions>


    @OPTIONS("${ClientConstants.HOMELINKS_API_FA_URL}contact/contact_us/")
    fun contactUsChoicesFa(): Call<ContactUsOptions>


    @POST("auth/password/change/")
    fun userChangePassword(
        @Header("Authorization") token: String,
        @Body password: UserChangePasswordModel
    ): Call<ResponseModel>


    @POST("auth/password/reset/")
    fun resetPassword(@Body resetPassword: ResetPasswordModel): Call<ResponseModel>


    @GET("tag/{tag}/")
    fun taggedItems(
        @Path("tag") tag: String,
        @Query("page") page: Int = 1
    ): Call<PaginatedResponseModel>


    @GET("search/")
    fun search(
        @Query("q") q: String,
        @Query("page") page: Int = 1
    ): Call<PaginatedResponseModel>



    // Dashboard
    @GET("dashboard/")
    fun dashboard(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1
    ): Call<LinkResults>


    @PUT("dashboard/user_update/")
    fun userUpdate(
        @Header("Authorization") token: String,
        @Body user: UserModel
    ): Call<UserModel>


    @GET("dashboard/users_{link}/")
    fun getUsersLinks(
        @Header("Authorization") token: String,
        @Path("link") link: String,
        @Query("page") page: Int = 1
    ): Call <LinkResults>


    @DELETE("{link}/{slug}/delete/")
    fun removeLink(
        @Header("Authorization") token: String,
        @Path("link") link: String,
        @Path("slug") slug: String
    ): Call<Unit>
}