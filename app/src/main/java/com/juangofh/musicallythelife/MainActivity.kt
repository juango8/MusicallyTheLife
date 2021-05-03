package com.juangofh.musicallythelife

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

private const val NUM_PAGES = 2

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    title = getString(R.string.title_home)
                    viewPager.setCurrentItem(0, false)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_playlist -> {
                    title = getString(R.string.title_playlist)
                    viewPager.setCurrentItem(1, false)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private var onNavigationPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            when (position) {
                0 -> navigation.menu.findItem(R.id.navigation_home).isChecked = true
                1 -> navigation.menu.findItem(R.id.navigation_playlist).isChecked = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        Thread.sleep(2000)
        setTheme(R.style.Theme_MusicallyTheLife)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
        setupViewPager()

    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager.unregisterOnPageChangeCallback(onNavigationPageChangeCallback)

    }

    private fun setupNavigation() {
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun setupViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(this, NUM_PAGES)
        viewPager.adapter = viewPagerAdapter
        viewPager.registerOnPageChangeCallback(onNavigationPageChangeCallback)
    }
}