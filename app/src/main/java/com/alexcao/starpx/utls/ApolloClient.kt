package com.alexcao.starpx.utls

import com.alexcao.starpx.repository.Repository
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.network.okHttpClient
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection


private const val BASE_URL = "https://api-dev.starpx.com/graphql"
fun getApolloClient(repository: Repository): ApolloClient? {
    if (repository.getJwt() == null) return null

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
                .header("Content-Type", "application/json")
            val request: Request = requestBuilder.build()
            chain.proceed(request)
        }
        .addInterceptor(AccessTokenInterceptor(repository))
        .build()

    val apolloClient = ApolloClient.Builder()
        .serverUrl(BASE_URL)
        .okHttpClient(okHttpClient)
        .build()

    return apolloClient
}

class AccessTokenInterceptor(val repository: Repository) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken: String = repository.getJwt() ?: return chain.proceed(chain.request())
        val request = newRequestWithAccessToken(chain.request(), accessToken)
        val response = chain.proceed(request)
        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            val newAccessToken = repository.getRefreshToken() ?: return response
            val newRequest = newRequestWithAccessToken(request, newAccessToken)
            return chain.proceed(newRequest)
        }
        else return response
    }

    private fun newRequestWithAccessToken(request: Request, accessToken: String): Request {
        return request.newBuilder()
            .header("Authorization", accessToken)
            .build()
    }

}