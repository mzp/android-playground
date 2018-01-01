package jp.mzp.mastodon.localtimelinewatcher

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.R.menu
import android.support.v4.app.Fragment
import android.app.FragmentTransaction
import android.os.PersistableBundle
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    val drawerToggle: ActionBarDrawerToggle by lazy {
        ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.drawer_open, R.string.drawer_close)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment.newInstance())
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
        loadFragment(HomeFragment.newInstance())

    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.
                beginTransaction().
                replace(R.id.frame,fragment).
                commit()

    }
}
