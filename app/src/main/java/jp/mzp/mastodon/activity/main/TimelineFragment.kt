package jp.mzp.mastodon.activity.main.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.sys1yagi.mastodon4j.api.Range
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.serialState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import jp.mzp.mastodon.values.Authentication
import jp.mzp.mastodon.values.Entity
import jp.mzp.mastodon.values.GsonParcelable

abstract class TimelineFragment<T, VH: RecyclerView.ViewHolder> : Fragment()
    where  T: Entity, T: GsonParcelable<*> {

    abstract val timelineView: RecyclerView
    abstract val swipeRefreshLayout: SwipeRefreshLayout
    abstract fun get(range: Range = Range()): Observable<T>
    abstract fun stream(): Flowable<T>
    abstract fun add(element: T)
    abstract fun notifyChanges()

    val authenticateField = "authentication"

    protected val authentication: Authentication by lazy {
        arguments!![authenticateField] as Authentication
    }
    protected var elements: ArrayList<T> by serialState(ArrayList<T>())
    private var streamDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unfreezeInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        freezeInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timelineView.apply {
            val lm = LinearLayoutManager(context)
            layoutManager = lm
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val last = lm.findLastVisibleItemPosition()
                    if (last + 2 > elements.size) {
                        fetch(Range(elements.last().id))
                    }
                }
            })
        }

        swipeRefreshLayout.setOnRefreshListener {
            fetch(Range(null, this.elements.first().id))
        }

        if(elements.isEmpty()) {
            fetch()
        }
    }

    private fun fetch(range: Range = Range()) {
        withProgress({ get(range) }).observeOn(AndroidSchedulers.mainThread()).subscribe(
                this::add,
                this::onError,
                this::notifyChanges)
    }

    override fun onResume() {
        super.onResume()

        this.streamDisposable?.dispose()
        this.streamDisposable = stream().observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    add(it)
                    notifyChanges()
                },
                this::onError)
    }

    override fun onPause() {
        super.onPause()
        this.streamDisposable?.dispose()
        this.streamDisposable = null
    }

    private fun <T> withProgress(f : () -> Observable<T>) : Observable<T> {
        return Observable.using({
            swipeRefreshLayout.apply {
                activity?.runOnUiThread {
                    isRefreshing = true
                }
            }
        }, { f() }, { refresh ->
            activity?.runOnUiThread {
                refresh.isRefreshing = false
            }
        })
    }

    private fun onError(error: Throwable) {
        error.printStackTrace()
        activity?.runOnUiThread {
            Toast.makeText(context, error.message, Toast.LENGTH_LONG)
        }
    }
}

