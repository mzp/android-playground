package jp.mzp.mastodon.values

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson

/**
 * Created by mzp on 2018/01/08.
 */
open class GsonParcelable<T>(val value : T): Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Gson().toJson(value))
    }

    override fun describeContents(): Int {
        return 0
    }

    // dummy field for Android Studio Lint Checker
    val CREATOR: Parcelable.Creator<T>? = null

    companion object {
        inline fun <reified T, reified U : GsonParcelable<T>> make(crossinline f : (T) -> U): Parcelable.Creator<U> {
            return object: Parcelable.Creator<U> {
                override fun newArray(size: Int): Array<U?> {
                    return arrayOfNulls<U>(size)
                }

                override fun createFromParcel(source: Parcel): U {
                    val value = Gson().fromJson(source.readString(), T::class.java)
                    return f(value)
                }
            }
        }
    }
}