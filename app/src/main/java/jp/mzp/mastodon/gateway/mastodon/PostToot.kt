package jp.mzp.mastodon.gateway.mastodon

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import com.sys1yagi.mastodon4j.api.method.Statuses
import jp.mzp.mastodon.values.Authentication
import okhttp3.OkHttpClient

/**
 * Created by mzp on 2018/01/05.
 */
class PostToot(authentication: Authentication): AuthenticateMethod(authentication) {
    private val statuses = Statuses(client)

    fun invoke(content: String) {
        statuses.postStatus(content,null, null, false, null).execute()
    }
}