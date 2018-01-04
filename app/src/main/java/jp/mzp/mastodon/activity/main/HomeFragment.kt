package jp.mzp.mastodon.activity.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.method.Timelines
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.OkHttpClient
import kotlin.concurrent.thread
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.values.Authentication


class HomeFragment : Fragment() {
    private val AUTHENTICATION_FIELD = "authentication"

    private val authentication: Authentication? by lazy {
        arguments[AUTHENTICATION_FIELD] as Authentication
    }

    val accessToken: AccessToken? by lazy {
        authentication?.accessToken()
    }
    val hostName: String? by lazy {
        authentication?.hostName()
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
        fun newInstance(authentication: Authentication): HomeFragment {
            return HomeFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(AUTHENTICATION_FIELD, authentication)
                arguments = bundle
            }
        }
    }
}
