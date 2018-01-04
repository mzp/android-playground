package jp.mzp.mastodon.store

import android.content.Context
import android.content.SharedPreferences
import auto.parcelgson.gson.AutoParcelGsonTypeAdapterFactory
import com.google.gson.GsonBuilder
import jp.mzp.mastodon.values.Authentication

class AccessTokenStore(context: Context) {
    private val authenticateField = "authentication"

    private val data : SharedPreferences by lazy {
        context.getSharedPreferences("credential", Context.MODE_PRIVATE)
    }

    private val gson = GsonBuilder().registerTypeAdapterFactory(AutoParcelGsonTypeAdapterFactory()).create()

    var authentication: Authentication?
        get() {
            val json = data.getString(authenticateField, "")
            return if (json == "") {
                null
            } else {
                gson.fromJson<Authentication>(json, Authentication::class.java)
            }
        }
        set(value) {
            val json = gson.toJson(value)
            val edit = data.edit()
            edit.putString(authenticateField, json)
            edit.apply()
        }

    fun clear() {
        val edit = data.edit()
        edit.remove(authenticateField)
        edit.apply()
    }
}
