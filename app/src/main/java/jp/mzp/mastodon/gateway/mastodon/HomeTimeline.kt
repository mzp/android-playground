package jp.mzp.mastodon.gateway.mastodon

import com.sys1yagi.mastodon4j.api.Handler
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import com.sys1yagi.mastodon4j.api.method.Streaming
import com.sys1yagi.mastodon4j.api.method.Timelines
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers.*
import jp.mzp.mastodon.values.Authentication
import java.util.concurrent.TimeUnit

class HomeTimeline(authentication: Authentication): AuthenticateMethod(authentication) {
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
                try {
                    println("start streaming")
                    val streaming = Streaming(client)
                    streaming.user(object : Handler {
                        override fun onDelete(id: Long) {
                        }

                        override fun onNotification(notification: Notification) {
                        }

                        override fun onStatus(status: Status) {
                            emit.onNext(status)
                        }
                    })
                } catch (e : Mastodon4jRequestException) {
                    println("onError")
                    emit.onError(e)
                }
            }.subscribeOn(io()).retryWhen { attempts ->
                attempts.zipWith<Int, Long>(Observable.range(1, 3), BiFunction { _, n ->  fib(n.toLong()) }).flatMap { i ->
                    println("waiting $i")
                    Observable.timer(i, TimeUnit.SECONDS)
                }
            }
        }

    private fun fib(n: Long): Long {
        return if(n == 0L) {
            1L
        } else if (n == 1L) {
            1L
        } else {
            fib(n - 2L) * fib (n - 1L)
        }
    }
}