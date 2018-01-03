package jp.mzp.mastodon.localtimelinewatcher

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.MastodonRequest
import com.sys1yagi.mastodon4j.api.Scope
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import com.sys1yagi.mastodon4j.api.method.Apps
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val redirectUri = "mzp://mstdn-login"
    private var apps: Apps? = null
    private var appRegistration: AppRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sign_in.setOnClickListener {
            thread {
                try {
                    login()
                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val authCode = intent?.data?.getQueryParameter("code")
        appRegistration?.let { appRegistration ->
            authCode?.let { authCode ->
                thread {
                    try {
                        apps?.getAccessToken(
                                appRegistration.clientId,
                                appRegistration.clientSecret,
                                redirectUri,
                                authCode,
                                "authorization_code"
                        )?.execute()?.let { accessToken ->
                            AccessTokenStore(this).write(host_name.text.toString(), accessToken)

                            runOnUiThread {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun login() {
        val client: MastodonClient = MastodonClient.Builder(host_name.text.toString(), OkHttpClient.Builder(), Gson()).build()
        val apps = Apps(client)
        val appRegistration = apps.createApp(
                    clientName = "mastodon client dev",
                    redirectUris = redirectUri,
                    scope = Scope(Scope.Name.ALL),
                    website = "https://mzp.jp"
        ).execute()

        this.apps = apps
        this.appRegistration = appRegistration

        val url = apps.getOAuthUrl(appRegistration.clientId, Scope(Scope.Name.ALL), redirectUri)
        runOnUiThread {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }
}
