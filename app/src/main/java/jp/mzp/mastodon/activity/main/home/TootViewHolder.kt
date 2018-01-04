package jp.mzp.mastodon.activity.main.home

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import jp.mzp.mastodon.activity.R

class TootViewHolder(view : View): RecyclerView.ViewHolder(view) {
    val avator = view.findViewById<ImageView>(R.id.avator)!!
    val account = view.findViewById<TextView>(R.id.account)!!
    val content = view.findViewById<TextView>(R.id.content)!!
}