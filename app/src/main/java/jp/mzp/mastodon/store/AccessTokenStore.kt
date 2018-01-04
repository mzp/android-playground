package jp.mzp.mastodon.store

import android.content.Context
import android.content.SharedPreferences
import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import jp.mzp.mastodon.values.Authentication

class AccessTokenStore(context: Context) {
    private val AUTHENTICATION_FIELD = "authentication"

    private val data : SharedPreferences by lazy {
        context.getSharedPreferences("credential", Context.MODE_PRIVATE)
    }

    private val gson = GsonBuilder().registerTypeAdapterFactory(AutoParcelGsonTypeAdapterFactory()).create()

    var authentication: Authentication?
        get() {
            val json = data.getString(AUTHENTICATION_FIELD, "")
            if (json == "") {
                return null
            } else {
                return gson.fromJson<Authentication>(json, Authentication::class.java)
            }
        }
        set(value) {
            val json = gson.toJson(value)
            val edit = data.edit()
            edit.putString(AUTHENTICATION_FIELD, json)
            edit.apply()
        }

    fun clear() {
        val edit = data.edit()
        edit.remove(AUTHENTICATION_FIELD)
        edit.apply()
    }
}
