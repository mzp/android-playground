package jp.mzp.mastodon.localtimelinewatcher

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.R.menu
import android.support.v4.app.Fragment
import android.app.FragmentTransaction
import android.content.Intent
import android.os.PersistableBundle
import com.sys1yagi.mastodon4j.api.entity.auth.AccessToken
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.jvm.javaClass


class MainActivity : AppCompatActivity() {
    val drawerToggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    private val accessTokenStore: AccessTokenStore? by lazy {
        AccessTokenStore(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        drawer_navigation.setNavigationItemSelectedListener { menu ->
            if(menu.itemId == R.id.sign_out) {
                accessTokenStore?.clear()
                startLoginActivity()
            }
            true
        }

        val hostName = accessTokenStore?.hostName
        val accessToken = accessTokenStore?.accessToken
        if (hostName == null || accessToken == null) {
            startLoginActivity()
            return
        }

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment.newInstance(hostName, accessToken))
                    true
                }
                R.id.notification -> {
                    loadFragment(NotificationFragment.newInstance())
                    true
                }
                R.id.me -> {
                    loadFragment(AccountFragment.newInstance())
                    true
                }
                else -> {
                    false
                }
            }
        }
        loadFragment(HomeFragment.newInstance(hostName, accessToken))
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.
                beginTransaction().
                replace(R.id.frame,fragment).
                commit()

    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}