package ir.homelinks.homelinks.api

import ir.homelinks.homelinks.utility.ClientConstants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.ConnectionSpec
import java.util.*
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.CipherSuite


class APIClient {
    companion object {
        private var retrofit: Retrofit? = null


        fun getClient(): Retrofit {
            val spec = ConnectionSpec.Builder(ConnectionSpec.COMPATIBLE_TLS)
                .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_1, TlsVersion.TLS_1_0)
                .cipherSuites(
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                    CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA)
                .build();

            val client = OkHttpClient.Builder()
                .connectionSpecs(Collections.singletonList(spec))
                .build()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(ClientConstants.HOMELINKS_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    //.client(client)
                    .build()
            }

            return retrofit!!
        }
    }
}