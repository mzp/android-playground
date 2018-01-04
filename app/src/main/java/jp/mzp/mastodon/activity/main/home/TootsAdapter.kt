package jp.mzp.mastodon.activity.main.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.sys1yagi.mastodon4j.api.entity.Status
import jp.mzp.mastodon.activity.R

class TootsAdapter(private val context: Context): RecyclerView.Adapter<TootViewHolder>() {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val toots: ArrayList<Status> = ArrayList()

    fun addAll(newToots: List<Status>) {
        this.toots.addAll(newToots)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TootViewHolder {
        val view = inflater.inflate(R.layout.toot_view, parent, false)
        return TootViewHolder(view)
    }

    override fun onBindViewHolder(holder: TootViewHolder?, position: Int) {
        val toot = toots[position]

        holder?.content?.setText(fromHtml(toot.content), TextView.BufferType.SPANNABLE)
        val account = toot.account
        if (account != null) {
            holder?.account?.text = account.displayName
            Picasso.with(context).load(account.avatar).into(holder?.avator)
        }
    }

    override fun getItemCount(): Int {
        return toots.size
    }

    private fun fromHtml(html: String): Spanned {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(html)
        }
    }
}