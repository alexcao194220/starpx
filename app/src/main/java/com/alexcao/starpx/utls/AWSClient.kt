package com.alexcao.starpx.utls

import android.content.Context
import android.util.Log
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.UserState
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AWSClient {

    companion object {
        const val TAG = "AWSClient"
    }

    private lateinit var awsConfiguration: AWSConfiguration

    fun getAWSConfiguration(context: Context) {
        val inputStream = context.assets.open("awsconfiguration.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        val json = String(buffer)
        awsConfiguration = AWSConfiguration(JSONObject(json))
    }

    suspend fun loginWithAWS(
        context: Context,
        username: String,
        password: String,
    ): CognitoUserSession? {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                val callback: Callback<UserStateDetails> = object : Callback<UserStateDetails> {
                    override fun onResult(userStateDetails: UserStateDetails) {
                        when (userStateDetails.userState) {
                            UserState.SIGNED_OUT -> {
                                val userPool =
                                    CognitoUserPool(
                                        context,
                                        AWSMobileClient.getInstance().configuration
                                    )
                                userPool.getUser(username).getSessionInBackground(object :
                                    AuthenticationHandler {
                                    override fun onSuccess(
                                        userSession: CognitoUserSession?,
                                        newDevice: CognitoDevice?
                                    ) {
                                        Log.d(TAG, "onSuccess: User session: $userSession")
                                        if (userSession != null) {
                                            continuation.resume(userSession)
                                        } else {
                                            continuation.resumeWithException(Exception("User session is null"))
                                        }
                                    }

                                    override fun onFailure(exception: Exception?) {
                                        Log.e(TAG, "onFailure: ", exception)
                                        continuation.resumeWithException(
                                            exception ?: Exception("Unknown error")
                                        )
                                    }

                                    override fun getAuthenticationDetails(
                                        authenticationContinuation: AuthenticationContinuation?,
                                        userId: String?
                                    ) {
                                        authenticationContinuation?.setAuthenticationDetails(
                                            AuthenticationDetails(
                                                username,
                                                password,
                                                null
                                            )
                                        )
                                        Log.d(TAG, "getAuthenticationDetails: ")
                                        authenticationContinuation?.continueTask()
                                    }

                                    override fun authenticationChallenge(continuation: ChallengeContinuation?) {
                                        // Handle authentication challenge
                                        Log.d(TAG, "authenticationChallenge: $continuation")
                                    }

                                    override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
                                        // Handle multi-factor authentication if needed
                                        Log.d(TAG, "getMFACode: $continuation")
                                    }
                                })
                            }

                            else -> {
                                // User already signed in or in some other state
                                Log.d(TAG, "onResult: ${userStateDetails.userState}")
                                continuation.resumeWithException(Exception("User already signed in"))
                            }
                        }
                    }

                    override fun onError(e: Exception?) {
                        // Error occurred during initialization
                        Log.e(TAG, "Initialization error.", e)
                    }
                }

                AWSMobileClient.getInstance().initialize(context, awsConfiguration, callback)
            }
        }
    }
}