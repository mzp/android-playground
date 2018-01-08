package jp.mzp.mastodon.gateway.mastodon

import com.sys1yagi.mastodon4j.api.Handler
import com.sys1yagi.mastodon4j.api.Range
import com.sys1yagi.mastodon4j.api.entity.Notification
import com.sys1yagi.mastodon4j.api.entity.Status
import com.sys1yagi.mastodon4j.api.exception.Mastodon4jRequestException
import com.sys1yagi.mastodon4j.api.method.Notifications
import com.sys1yagi.mastodon4j.api.method.Streaming
import com.sys1yagi.mastodon4j.api.method.Timelines
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import jp.mzp.mastodon.values.Authentication
import java.io.InterruptedIOException

/**
 * Created by mzp on 2018/01/08.
 */
class Notifications(authentication: Authentication): AuthenticateMethod(authentication) {
    fun notifications(range : Range = Range()): Observable<Notification> {
        return Observable.create<Notification> { emit ->
            Notifications(client).getNotifications(range).execute().part.reversed().forEach {
                emit.onNext(it)
            }
            emit.onComplete()
        }.subscribeOn(Schedulers.io())
    }

    fun stream() : Flowable<Notification> {
        return flow { emit ->
            object : Handler {
                override fun onDelete(id: Long) {
                }

                override fun onNotification(notification: Notification) {
                    emit.onNext(notification)
                }

                override fun onStatus(status: Status) {
                }
            }
        }
    }

}