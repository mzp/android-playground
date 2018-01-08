package jp.mzp.mastodon.gateway.mastodon

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Handler
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import com.sys1yagi.mastodon4j.api.method.Streaming
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.schedulers.Schedulers
import jp.mzp.mastodon.values.Authentication
import okhttp3.OkHttpClient
import java.io.InterruptedIOException

/**
 * Created by mzp on 2018/01/05.
 */
open class AuthenticateMethod(authentication: Authentication) {
    protected val client: MastodonClient by lazy {
        val hostName = authentication.hostName
        val accessToken = authentication.accessToken
        MastodonClient.Builder(hostName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken.accessToken)
                .build()
    }

    protected fun <T> flow(handler: (FlowableEmitter<T>) -> Handler) : Flowable<T> {
        return Flowable.create<T>({ emit ->
            try {
                println("start streaming")
                val streaming = Streaming(client)
                val shutdown = streaming.user(handler(emit))
                emit.setCancellable(shutdown::shutdown)
            } catch (e: Mastodon4jRequestException) {
                if(!(e.cause is InterruptedIOException)) {
                    println("onError")
                    emit.onError(e)
                }
            }
        }, BackpressureStrategy.LATEST).subscribeOn(Schedulers.io()).retry(3)
    }
}