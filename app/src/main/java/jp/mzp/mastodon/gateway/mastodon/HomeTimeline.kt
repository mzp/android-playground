package jp.mzp.mastodon.gateway.mastodon

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.method.Timelines
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import jp.mzp.mastodon.values.Authentication
import okhttp3.OkHttpClient

class HomeTimeline(authentication: Authentication) {
    private val client: MastodonClient by lazy {
        val hostName = authentication.hostName()
        val accessToken = authentication.accessToken()
        MastodonClient.Builder(hostName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken.accessToken)
                .build()
    }

    val toots: Observable<List<Status>>
        get() {
            return Observable.fromCallable {
                Timelines(client).getHome().execute().part
            }.subscribeOn(Schedulers.io())
        }
}