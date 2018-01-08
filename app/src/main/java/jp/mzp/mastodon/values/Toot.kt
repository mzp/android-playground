package jp.mzp.mastodon.values

import android.os.Parcelable
import com.sys1yagi.mastodon4j.api.entity.Status

class Toot(value: Status): GsonParcelable<Status>(value) {
    companion object {
        val CREATOR: Parcelable.Creator<Toot> = GsonParcelable.make<Status, Toot>({
            Toot(it)
        })
    }
}