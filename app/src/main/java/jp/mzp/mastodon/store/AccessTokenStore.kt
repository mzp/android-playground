package jp.mzp.mastodon.store

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import jp.mzp.mastodon.values.Authentication
import jp.mzp.mastodon.values.RawAuthentication

class AccessTokenStore(context: Context) {
    private val authenticateField = "authentication"

    private val data : SharedPreferences by lazy {
        context.getSharedPreferences("credential", Context.MODE_PRIVATE)
    }

    var authentication: Authentication?
        get() {
            val json = data.getString(authenticateField, "")
            return if (json == "") {
                null
            } else {
                try {
                    val value = Gson().fromJson<RawAuthentication>(json, RawAuthentication::class.java)
                    return Authentication(value)
                } catch (exception: JsonSyntaxException) {
                    exception.printStackTrace()
                    return null
                }
            }
        }
        set(value) {
            val json = Gson().toJson(value?.value)
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
