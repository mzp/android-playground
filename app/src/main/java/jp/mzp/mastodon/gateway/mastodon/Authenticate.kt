package jp.mzp.mastodon.gateway.mastodon

import android.net.Uri
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Scope
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import com.sys1yagi.mastodon4j.api.method.Apps
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient

class Authenticate(private val redirectUrl: String) {
    fun oauthUrl(hostName: String): Single<Triple<Apps, AppRegistration, Uri>> {
        return Single.fromCallable {
            val client: MastodonClient = MastodonClient.Builder(hostName, OkHttpClient.Builder(), Gson()).build()
            val apps = Apps(client)
            val appRegistration = apps.createApp(
                    clientName = "🍣.gq",
                    redirectUris = redirectUrl,
                    scope = Scope(Scope.Name.ALL),
                    website = "https://mzp.jp"
            ).execute()

            val url = apps.getOAuthUrl(appRegistration.clientId, Scope(Scope.Name.ALL), redirectUrl)
            Triple(apps, appRegistration, Uri.parse(url))
        }.subscribeOn(Schedulers.io())
    }

    fun accessToken(hostName: String, apps: Apps, appRegistration: AppRegistration, authCode: String): Single<AccessToken?> {
        return Single.fromCallable {
            val accessToken = apps.getAccessToken(
                    appRegistration.clientId,
                    appRegistration.clientSecret,
                    redirectUrl,
                    authCode,
                    "authorization_code"
            )
            accessToken.execute()
        }.subscribeOn(Schedulers.io())
    }
}