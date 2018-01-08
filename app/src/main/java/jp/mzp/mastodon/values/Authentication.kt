package jp.mzp.mastodon.values

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken

class RawAuthentication {
    @SerializedName("hostName")
    var hostName: String = ""

    @SerializedName("accessTokenRaw")
    var accessToken: AccessToken = AccessToken()
}

class Authentication(value: RawAuthentication): GsonParcelable<RawAuthentication>(value) {
    val hostName = value.hostName
    val accessToken = value.accessToken
    companion object {
        val CREATOR: Parcelable.Creator<Authentication> = GsonParcelable.make<RawAuthentication, Authentication>({
            Authentication(it)
        })

        fun create(hostName: String, accessToken: AccessToken): Authentication {
            return Authentication(RawAuthentication().apply {
                this.hostName = hostName
                this.accessToken = accessToken
            })
        }
    }
}