package jp.mzp.mastodon.localtimelinewatcher

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.method.Timelines
import jp.mzp.mastodon.localtimelinewatcher.R.layout.fragment_home
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.OkHttpClient
import kotlin.concurrent.thread

class TootViewHolder(view : View): RecyclerView.ViewHolder(view) {
    val avator = view.findViewById<ImageView>(R.id.avator)
    val account = view.findViewById<TextView>(R.id.account)
    val content = view.findViewById<TextView>(R.id.content)
}

class TootsAdapter(private val toots: List<Status>, private val context: Context): RecyclerView.Adapter<TootViewHolder>() {
    private val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TootViewHolder {
        val view = inflater.inflate(R.layout.toot_view, parent, false)
        return TootViewHolder(view)
    }

    override fun onBindViewHolder(holder: TootViewHolder?, position: Int) {
        val toot = toots[position]

        holder?.content?.text = toot.content

        val account = toot.account
        if (account != null) {
            holder?.account?.text = account.displayName
            Picasso.with(context).load(account.avatar).into(holder?.avator)
        }
    }

    override fun getItemCount(): Int {
        return toots.size
    }
}


class HomeFragment : Fragment() {
    val accessToken: AccessToken? by lazy {
        (arguments["accessToken"] as? AccessTokenParcel)?.value
    }
    val hostName: String? by lazy {
        arguments["hostName"] as? String
    }

    val client: MastodonClient? by lazy {
        this.hostName?.let { hostName ->
            this.accessToken?.let { accessToken ->
                MastodonClient.Builder(hostName, OkHttpClient.Builder(), Gson())
                        .accessToken(accessToken.accessToken)
                        .build()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val client = this.client
        if (client == null) {
            return
        }

        home_timeline.layoutManager = LinearLayoutManager(context)

        thread {
            val statuses = Timelines(client).getHome().execute().part
            val adapter = TootsAdapter(statuses, this.context)
            activity.runOnUiThread {
                home_timeline.adapter = adapter
            }
        }
    }


    companion object {
        fun newInstance(hostName: String, accessToken: AccessToken): HomeFragment {
            return HomeFragment().apply {
                val bundle = Bundle()
                bundle.putString("hostName", hostName)
                bundle.putParcelable("accessToken", AccessTokenParcel(accessToken))
                arguments = bundle
            }
        }
    }
}
