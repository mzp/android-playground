package jp.mzp.mastodon.activity.main.home

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.sys1yagi.mastodon4j.api.entity.Status
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.values.Toot

class TootsAdapter(private val context: Context, private val toots: MutableList<Toot>): RecyclerView.Adapter<TootViewHolder>() {
    private var inflater: LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    fun add(toot: Status) {
        toots.add(0, Toot(toot))
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TootViewHolder {
        val view = inflater?.inflate(R.layout.toot_view, parent, false)
        return TootViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: TootViewHolder?, position: Int) {
        val toot = toots[position]

        holder?.content?.setText(fromHtml(toot.value.content), TextView.BufferType.SPANNABLE)
        holder?.content?.movementMethod = LinkMovementMethod.getInstance()
        val account = toot.value.account
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