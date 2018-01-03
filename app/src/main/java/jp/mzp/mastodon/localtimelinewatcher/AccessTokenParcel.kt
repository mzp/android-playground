package jp.mzp.mastodon.localtimelinewatcher

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken

class AccessTokenParcel(val value: AccessToken) : Parcelable {
    constructor(parcel: Parcel): this(Gson().fromJson<AccessToken>(parcel.readString(), AccessToken::class.java)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Gson().toJson(value))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AccessTokenParcel> {
        override fun createFromParcel(parcel: Parcel): AccessTokenParcel {
            return AccessTokenParcel(parcel)
        }

        override fun newArray(size: Int): Array<AccessTokenParcel?> {
            return arrayOfNulls(size)
        }
    }
}