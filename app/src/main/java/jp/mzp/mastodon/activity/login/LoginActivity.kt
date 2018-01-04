package jp.mzp.mastodon.activity.login

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Scope
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import com.sys1yagi.mastodon4j.api.method.Apps
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.activity.main.MainActivity
import jp.mzp.mastodon.gateway.mastodon.Authenticate
import jp.mzp.mastodon.store.AccessTokenStore
import jp.mzp.mastodon.values.Authentication
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private var apps: Apps? = null
    private var appRegistration: AppRegistration? = null
    private var hostName: String? = null
    private val authenticate = Authenticate("mzp://mstdn-login")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sign_in.setOnClickListener {
            val hostName = host_name.text.toString()
            authenticate.oauthUrl(hostName)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                this.apps = it.first
                                this.appRegistration = it.second
                                this.hostName = hostName
                                startActivity(Intent(Intent.ACTION_VIEW, it.third))
                            },
                            {
                                it.printStackTrace()
                                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                            }
                    )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val hostName = this.hostName
        val apps = this.apps
        val appRegistration = this.appRegistration
        val authCode = intent?.data?.getQueryParameter("code")

        if ( hostName != null && apps != null && appRegistration != null && authCode != null) {
            authenticate.accessToken(hostName, apps, appRegistration, authCode)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                if( it != null) {
                                    AccessTokenStore(this).authentication = Authentication.create(hostName, it)
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Sign in failed", Toast.LENGTH_LONG).show()
                                }
                            },
                            {
                                it.printStackTrace()
                                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                            }
                    )
        }
    }


}