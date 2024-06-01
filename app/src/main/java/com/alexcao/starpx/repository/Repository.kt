@file:OptIn(ApolloExperimental::class)

package com.alexcao.starpx.repository

import android.content.Context
import android.util.Log
import com.alexcao.starpx.GetImageSetSummariesQuery
import com.alexcao.starpx.model.Account
import com.alexcao.starpx.model.GetImageSetSummariesResponse
import com.alexcao.starpx.model.ImageSet
import com.alexcao.starpx.utls.AWSClient
import com.alexcao.starpx.utls.RxPreferences
import com.alexcao.starpx.utls.getApolloClient
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.annotations.ApolloExperimental
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.api.toJsonString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class Repository @Inject constructor(
    private val awsClient: AWSClient,
    private val context: Context,
    private val rxPreferences: RxPreferences
) {
    private var apolloClient: ApolloClient? = null

    companion object {
        const val TAG = "Repository"
    }

    init {
        awsClient.getAWSConfiguration(context)
        val jwt = rxPreferences.getJwt()

        if (jwt != null) {
            apolloClient = getApolloClient(repository = this)
        }
    }

    suspend fun login(account: Account) {
        val username = account.username
        val password = account.password
        val session = awsClient.loginWithAWS(context, username, password)
        val jwt = session?.accessToken?.jwtToken ?: throw Exception("Failed to login")
        val refreshToken = session.refreshToken?.token ?: throw Exception("Failed to login")
        Log.d(TAG, "login: $jwt")
        rxPreferences.saveJwt(jwt)
        rxPreferences.saveRefreshToken(refreshToken)
        apolloClient = getApolloClient(repository = this)
    }

    suspend fun getImages(
        token: String?,
        limit: Int,
    ): List<ImageSet> {
        if (apolloClient == null) {
            throw Exception("Apollo client is not initialized")
        }

        val response = apolloClient?.query(
            GetImageSetSummariesQuery(
                customerId = "aabb1234",
                limit = Optional.present(limit),
                nextToken = Optional.presentIfNotNull(token)
            )
        )?.execute()

        val jsonResponse = response?.data?.toJsonString()
        jsonResponse?.let { json ->
            Log.d(TAG, "getImages: $json")
            val parsedResponse = Json.decodeFromString<GetImageSetSummariesResponse>(json)

            val nextToken = parsedResponse.getImageSetSummaries.nextToken
            rxPreferences.saveNextToken(nextToken)

            val imageSets = parsedResponse.getImageSetSummaries.imageSets
            return imageSets
        }

        return emptyList()
    }

    fun getNextToken(): String? {
        return rxPreferences.getNextToken()
    }

    fun getJwt(): String? {
        return rxPreferences.getJwt()
    }

    fun getRefreshToken(): String? {
        return rxPreferences.getRefreshToken()
    }

    fun getUsername(): String {
        return rxPreferences.getUsername()
    }

    fun saveUsername(username: String) {
        rxPreferences.saveUsername(username)
    }

    fun logout() {
        rxPreferences.clear()
    }
}