package jp.mzp.mastodon.gateway.mastodon

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import jp.mzp.mastodon.values.Authentication
import okhttp3.OkHttpClient

/**
 * Created by mzp on 2018/01/05.
 */
open class AuthenticateMethod(authentication: Authentication) {
    protected val client: MastodonClient by lazy {
        val hostName = authentication.hostName
        val accessToken = authentication.accessToken
        MastodonClient.Builder(hostName, OkHttpClient.Builder(), Gson())
                .accessToken(accessToken.accessToken)
                .build()
    }
}