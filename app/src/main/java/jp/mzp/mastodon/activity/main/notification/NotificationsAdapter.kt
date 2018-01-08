package jp.mzp.mastodon.activity.main.notification

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.activity.main.home.TootViewHolder
import jp.mzp.mastodon.values.Notification
import jp.mzp.mastodon.values.Toot

/**
 * Created by mzp on 2018/01/08.
 */

class NotificationsAdapter(private val context: Context, private val notifications: MutableList<Notification>): RecyclerView.Adapter<NotificationViewHolder>() {
    private var inflater: LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun add(element: Notification) {
        notifications.add(0, element)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NotificationViewHolder {
        val view = inflater?.inflate(R.layout.notification_view, parent, false)
        return NotificationViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder?, position: Int) {
        val notification = notifications[position].value


        holder?.content?.setText(fromHtml(notification.status?.content ?: ""), TextView.BufferType.SPANNABLE)
        val account = notification.account
        if (account != null) {
            holder?.action?.text = "${notification.type} ${notification.account?.displayName}"
            Picasso.with(context).load(account.avatar).into(holder?.avator)
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    private fun fromHtml(html: String): Spanned {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(html)
        }
    }
}