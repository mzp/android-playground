package jp.mzp.mastodon.activity.main.notification

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sys1yagi.mastodon4j.api.Range
import io.reactivex.Flowable
import io.reactivex.Observable
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.activity.main.home.TimelineFragment
import jp.mzp.mastodon.gateway.mastodon.Notifications
import jp.mzp.mastodon.values.Authentication
import jp.mzp.mastodon.values.Notification

import kotlinx.android.synthetic.main.fragment_notification.*

class NotificationFragment(): TimelineFragment<Notification, NotificationViewHolder>() {
    private val adapter: NotificationsAdapter? by lazy {
        context?.let { NotificationsAdapter(it, this.elements) }
    }

    private val notificationGateway: Notifications by lazy {
        Notifications(authentication)
    }

    override val timelineView: RecyclerView
        get() = notifications

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = refresh

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timelineView.adapter = adapter
    }

    override fun get(range: Range): Observable<Notification> {
        return notificationGateway.notifications(range).map { Notification(it) }
    }

    override fun stream(): Flowable<Notification> {
        return notificationGateway.stream().map { Notification(it) }
    }

    override fun add(element: Notification) {
        adapter?.add(element)
    }

    override fun notifyChanges() {
        adapter?.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(authentication: Authentication): NotificationFragment {
            return NotificationFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(authenticateField, authentication)
                arguments = bundle
            }
        }
    }
}