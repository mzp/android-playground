package jp.mzp.mastodon.localtimelinewatcher

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.R.menu
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
    }
}
