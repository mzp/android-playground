package jp.mzp.mastodon.activity.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v4.app.Fragment
import android.content.Intent
import jp.mzp.mastodon.activity.login.LoginActivity
import jp.mzp.mastodon.activity.R
import jp.mzp.mastodon.activity.draft.TootActivity
import jp.mzp.mastodon.activity.main.home.HomeFragment
import jp.mzp.mastodon.store.AccessTokenStore
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val drawerToggle: ActionBarDrawerToggle by lazy {
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

        val authentication = accessTokenStore?.authentication
        if (authentication == null) {
            startLoginActivity()
            return
        }

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment.newInstance(authentication))
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

        loadFragment(HomeFragment.newInstance(authentication))

        toot.setOnClickListener({
            val intent = Intent(this, TootActivity::class.java)
            startActivity(intent)
        })
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