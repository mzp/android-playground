package jp.mzp.mastodon.activity.draft

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.gateway.mastodon.PostToot
import jp.mzp.mastodon.store.AccessTokenStore
import kotlinx.android.synthetic.main.activity_toot.*
import kotlin.concurrent.thread

class TootActivity : AppCompatActivity() {
    private val post : PostToot? by lazy {
        AccessTokenStore(this).authentication?.let {
            PostToot(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toot)

        send_toot.setOnClickListener({
            thread {
                println(content.text.toString())
                post?.invoke(content.text.toString())
            }
            this.finish()
        })
    }
}
