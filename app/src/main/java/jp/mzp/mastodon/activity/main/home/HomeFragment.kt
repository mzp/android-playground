package jp.mzp.mastodon.activity.main.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sys1yagi.mastodon4j.api.Range
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.serialState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_home.*
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.gateway.mastodon.HomeTimeline
import jp.mzp.mastodon.values.Authentication
import jp.mzp.mastodon.values.Toot

class HomeFragment() : TimelineFragment<Toot, TootViewHolder>() {
    private val homeTimeline: HomeTimeline by lazy {
        HomeTimeline(authentication)
    }

    private val tootAdapter: TootsAdapter? by lazy {
        context?.let { TootsAdapter(it, this.elements) }
    }

    override val timelineView: RecyclerView
        get() = home_timeline

    override val swipeRefreshLayout: SwipeRefreshLayout
        get() = refresh

    override fun get(range: Range): Observable<Toot> {
        return homeTimeline.toots(range).map { Toot(it) }
    }

    override fun stream(): Flowable<Toot> {
        return homeTimeline.stream().map { Toot(it) }
    }

    override fun add(element: Toot) {
        tootAdapter?.add(element)
    }

    override fun notifyChanges() {
        tootAdapter?.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        home_timeline.adapter = tootAdapter
    }

    companion object {
        fun newInstance(authentication: Authentication): HomeFragment {
            return HomeFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(authenticateField, authentication)
                arguments = bundle
            }
        }
    }
}