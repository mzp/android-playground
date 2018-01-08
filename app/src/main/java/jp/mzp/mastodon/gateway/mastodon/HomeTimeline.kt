package jp.mzp.mastodon.gateway.mastodon

import com.sys1yagi.mastodon4j.api.Handler
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import com.sys1yagi.mastodon4j.api.method.Streaming
import com.sys1yagi.mastodon4j.api.method.Timelines
import com.sys1yagi.mastodon4j.rx.RxStreaming
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.*
import jp.mzp.mastodon.values.Authentication
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit

class HomeTimeline(authentication: Authentication): AuthenticateMethod(authentication) {
    fun toots(range : Range = Range()): Observable<Status> {
        return Observable.create<Status> { emit ->
            Timelines(client).getHome(range).execute().part.reversed().forEach {
                emit.onNext(it)
            }
            emit.onComplete()
        }.subscribeOn(io())
    }

    fun stream() : Flowable<Status> {
        return flow({ emit ->
            object : Handler {
                    override fun onDelete(id: Long) {
                    }

                    override fun onNotification(notification: Notification) {
                    }

                    override fun onStatus(status: Status) {
                        emit.onNext(status)
                    }
            }
        })
    }
}