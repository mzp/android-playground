package jp.mzp.mastodon.values

import android.os.Parcelable
import com.sys1yagi.mastodon4j.api.entity.Notification as RawNotification

class Notification(value: RawNotification): GsonParcelable<RawNotification>(value), Entity {
    override val id: Long = value.id

    companion object {
        val CREATOR: Parcelable.Creator<Notification> = GsonParcelable.make<RawNotification, Notification>({
            Notification(it)
        })
    }
}