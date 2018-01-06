package jp.mzp.mastodon.activity.main.home

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.serialState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_home.*
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.gateway.mastodon.HomeTimeline
import jp.mzp.mastodon.values.Authentication
import jp.mzp.mastodon.values.Toot

class HomeFragment : Fragment() {
    private val authenticateField = "authentication"

    private val authentication: Authentication by lazy {
        arguments!![authenticateField] as Authentication
    }
    private val homeTimeline: HomeTimeline by lazy {
        HomeTimeline(authentication)
    }
    private var toots: ArrayList<Toot> by serialState(ArrayList<Toot>())
    private val tootAdapter: TootsAdapter? by lazy {
        context?.let { TootsAdapter(it, this.toots) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

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

        home_timeline.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tootAdapter
        }

        if(toots.isEmpty()) {
            withProgress({ homeTimeline.toots }).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    {
                        tootAdapter?.add(it)
                    },
                    {
                        it.printStackTrace()
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG)
                    })
        }
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

