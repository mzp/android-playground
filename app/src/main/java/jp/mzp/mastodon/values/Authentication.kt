package jp.mzp.mastodon.values

import android.os.Parcelable
import auto.parcelgson.AutoParcelGson
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken

@AutoParcelGson
abstract class Authentication() : Parcelable {

    @SerializedName("hostName")
    abstract fun hostName(): String
    @SerializedName("accessTokenRaw")
    abstract fun accessTokenRaw(): String

    fun accessToken(): AccessToken {
        val gson = Gson()
        return gson.fromJson<AccessToken>(this.accessTokenRaw(), AccessToken::class.java)
    }

    companion object {
        fun create(hostName: String, accessToken: AccessToken): Authentication {
            val gson = Gson()
            val accessTokenRaw = gson.toJson(accessToken)
            return AutoParcelGson_Authentication(hostName, accessTokenRaw)
        }
    }
}