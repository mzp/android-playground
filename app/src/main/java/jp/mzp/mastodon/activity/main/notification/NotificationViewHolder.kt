package jp.mzp.mastodon.activity.main.notification

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import jp.mzp.mastodon.activity.R

/**
 * Created by mzp on 2018/01/08.
 */
class NotificationViewHolder(view : View): RecyclerView.ViewHolder(view) {
    val avator = view.findViewById<ImageView>(R.id.avator)!!
    val action = view.findViewById<TextView>(R.id.action)!!
    val content = view.findViewById<TextView>(R.id.content)!!
}