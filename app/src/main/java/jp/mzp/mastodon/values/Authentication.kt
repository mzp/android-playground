package jp.mzp.mastodon.values

import android.os.Parcelable
import auto.parcelgson.AutoParcelGson
import com.google.gson.annotations.SerializedName
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken

@AutoParcelGson
abstract class Authentication() : Parcelable {

    @SerializedName("hostName")
    abstract fun hostName(): String
    @SerializedName("accessToken")
    abstract fun accessToken(): AccessToken

    companion object {
        fun create(hostName: String, accessToken: AccessToken): Authentication {
            return AutoParcelGson_Authentication(hostName, accessToken)
        }
    }
}