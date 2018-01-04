package jp.mzp.mastodon.activity.main.home

import android.opengl.Visibility
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
import java.util.*


class HomeFragment : Fragment() {
    private val AUTHENTICATION_FIELD = "authentication"

    private val authentication: Authentication by lazy {
        arguments[AUTHENTICATION_FIELD] as Authentication
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tootAdapter = TootsAdapter(context)
        home_timeline.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tootAdapter
        }

        Observable.using({
            progressBar.apply {
                activity.runOnUiThread {
                    visibility = View.VISIBLE
                }
            }
        }, {
            HomeTimeline(authentication).toots
        }, { progressBar ->
            activity.runOnUiThread {
                progressBar.visibility = View.GONE
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe({
            tootAdapter.addAll(it)
        })
    }

    companion object {
        fun newInstance(authentication: Authentication): HomeFragment {
            return HomeFragment().apply {
                val bundle = Bundle()
                bundle.putParcelable(AUTHENTICATION_FIELD, authentication)
                arguments = bundle
            }
        }
    }
}
