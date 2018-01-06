package jp.mzp.mastodon.values

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.api.entity.Status

class Toot(val value : Status) : Parcelable {
    constructor(parcel: Parcel) : this(Gson().fromJson(parcel.readString(), Status::class.java)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Gson().toJson(value))
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Toot> {
        override fun createFromParcel(parcel: Parcel): Toot {
            return Toot(parcel)
        }

        override fun newArray(size: Int): Array<Toot?> {
            return arrayOfNulls(size)
        }
    }
}