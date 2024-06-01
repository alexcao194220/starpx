package com.alexcao.starpx.utls

import android.content.SharedPreferences
import javax.inject.Inject

class RxPreferences @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_TOKEN = "token"
        const val KEY_NEXT_TOKEN = "nextToken"
        const val KEY_REFRESH_TOKEN = "refreshToken"
    }

    fun saveUsername(username: String) {
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String {
        return sharedPreferences.getString(KEY_USERNAME, "") ?: ""
    }

    fun saveJwt(jwt: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, jwt).apply()
    }

    fun getJwt(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun getNextToken(): String? {
        return sharedPreferences.getString(KEY_NEXT_TOKEN, null)
    }

    fun saveNextToken(token: String?) {
        sharedPreferences.edit().putString(KEY_NEXT_TOKEN, token).apply()
    }

    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, token).apply()
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, "")
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}