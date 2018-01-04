package jp.mzp.mastodon.gateway.mastodon

import android.net.Uri
import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.Scope
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import com.sys1yagi.mastodon4j.api.entity.auth.AppRegistration
import com.sys1yagi.mastodon4j.api.method.Apps
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient

/**
 * Created by mzp on 2018/01/04.
 */
class Authenticate(val redirectUrl: String) {
    fun oauthUrl(hostName: String): Observable<Triple<Apps, AppRegistration, Uri>> {
        return Observable.just(hostName)
                .subscribeOn(Schedulers.io())
                .map {
                    val client: MastodonClient = MastodonClient.Builder(it, OkHttpClient.Builder(), Gson()).build()
                    val apps = Apps(client)
                    val appRegistration = apps.createApp(
                            clientName = "mastodon client dev",
                            redirectUris = redirectUrl,
                            scope = Scope(Scope.Name.ALL),
                            website = "https://mzp.jp"
                    ).execute()

                    val url = apps.getOAuthUrl(appRegistration.clientId, Scope(Scope.Name.ALL), redirectUrl)
                    return@map Triple(apps, appRegistration, Uri.parse(url))
                }
    }

    fun accessToken(hostName: String, apps: Apps, appRegistration: AppRegistration, authCode: String): Observable<AccessToken?> {
        return Observable.just(Unit)
                .subscribeOn(Schedulers.io())
                .map {
                    val accessToken = apps?.getAccessToken(
                            appRegistration.clientId,
                            appRegistration.clientSecret,
                            redirectUrl,
                            authCode,
                            "authorization_code"
                    )
                    accessToken.execute()
                }
    }
}