package com.alejot.kotlin.customviews

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_act.*

class MainActivity : AppCompatActivity()
{
    // ViewPager adapter
    private var customViewPagerAdapter: CustomViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_act)

        // Set up toolbar.
        setSupportActionBar(toolbar)

        // Set up view pager.
        customViewPagerAdapter = CustomViewPagerAdapter(supportFragmentManager)
        container.adapter = customViewPagerAdapter

        // Add custom views to display.
        customViewPagerAdapter?.addPage(R.layout.circular_data_display_view)
        customViewPagerAdapter?.addPage(R.layout.circular_data_display_view)
    }

    /**
     * FragmentPagerAdapter holds the collection of custom views to browse through
     *
     * @author Alejandro Torroella
     * @param fm the FragmentManager of the context.
     */
    inner class CustomViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private val availablePages: ArrayList<Fragment> = ArrayList()

        /**
         * Add a new custom view.
         *
         * @param layoutRes layout resource for the custom view
         */
        fun addPage(layoutRes: Int)
        {
            val container: CustomViewFragment = CustomViewFragment.newInstance(layoutRes)
            availablePages.add(container)
            notifyDataSetChanged()
        }

        override fun getItem(position: Int): Fragment
        {
            return availablePages[position]
        }

        override fun getCount(): Int {

            return availablePages.size
        }
    }
}