package jp.mzp.mastodon.localtimelinewatcher

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.method.Timelines
import jp.mzp.mastodon.localtimelinewatcher.R.layout.fragment_home
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.OkHttpClient
import kotlin.concurrent.thread

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

        thread {
            val statuses = Timelines(client).getHome().execute().part

            var s = ""
            for (status in statuses) {
                s += "${status.account?.displayName} ${status.content}"
                s += "\n"
            }

            activity.runOnUiThread {
                home_placeholder.text = s
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
