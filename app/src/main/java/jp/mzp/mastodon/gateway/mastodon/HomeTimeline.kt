package jp.mzp.mastodon.gateway.mastodon

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Handler
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.method.Streaming
import com.sys1yagi.mastodon4j.api.method.Timelines
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.*
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

    val toots: Observable<Status>
        get() {
            return Observable.create<Status> { emit ->
                Timelines(client).getHome().execute().part.reversed().forEach {
                    emit.onNext(it)
                }
                emit.onComplete()
            }.subscribeOn(io())
        }

    val stream: Observable<Status>
        get() {
            return Observable.create<Status> { emit ->
                val streaming = Streaming(client)
                streaming.user(object: Handler {
                    override fun onDelete(id: Long) {
                    }

                    override fun onNotification(notification: Notification) {
                    }

                    override fun onStatus(status: Status) {
                        emit.onNext(status)
                    }
                })
            }.subscribeOn(io())
        }
}