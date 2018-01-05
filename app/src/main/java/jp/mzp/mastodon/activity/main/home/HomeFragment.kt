package jp.mzp.mastodon.activity.main.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_home.*
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.gateway.mastodon.HomeTimeline
import jp.mzp.mastodon.values.Authentication


class HomeFragment : Fragment() {
    private val authenticateField = "authentication"

    private val authentication: Authentication by lazy {
        arguments!![authenticateField] as Authentication
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tootAdapter = context?.let { TootsAdapter(it) }
        home_timeline.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tootAdapter
        }

        val homeTimeline = HomeTimeline(authentication)

        val unified = Observable.concat(
                withProgress({ homeTimeline.toots }),
                homeTimeline.stream)



        unified.observeOn(AndroidSchedulers.mainThread()).subscribe(
                {
                    tootAdapter?.add(it)
                },
                {
                    it.printStackTrace()
                })
    }

    private fun <T> withProgress(f : () -> Observable<T>) : Observable<T> {
        return Observable.using({
            progressBar.apply {
                activity?.runOnUiThread {
                    visibility = View.VISIBLE
                }
            }
        }, { f() }, { progressBar ->
            activity?.runOnUiThread {
                progressBar.visibility = View.GONE
            }
        })
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
