package jp.mzp.mastodon.localtimelinewatcher

import android.content.Context
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken

class AccessTokenStore(context: Context) {
    private val data = context.getSharedPreferences("credential", Context.MODE_PRIVATE)
    private val gson = Gson()

    val accessToken: AccessToken?
        get() {
            val json = data.getString("access-token", "")
            if (json == "") {
                return null
            } else {
                return gson.fromJson<AccessToken>(json, AccessToken::class.java)
            }
        }

    val hostName: String?
        get() {
            return data.getString("host-name", null)
        }

    fun write(hostName: String, accessToken: AccessToken) {
        val json = gson.toJson(accessToken)
        val edit = data.edit()
        edit.putString("host-name", hostName)
        edit.putString("access-token", json)
        edit.apply()
    }

    fun clear() {
        val edit = data.edit()
        edit.remove("host-name")
        edit.remove("access-token")
        edit.apply()

    }
}
