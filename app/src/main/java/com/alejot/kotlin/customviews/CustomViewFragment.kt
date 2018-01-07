package com.alejot.kotlin.customviews

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Fragment which is inflated to a given layout resource.
 *
 * @author Alejandro Torroella
 */
class CustomViewFragment : Fragment()
{
    var viewLayout: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        var rootView: View? = inflater.inflate(viewLayout, container, false)
        return rootView
    }

    companion object {

        /**
         * Create a new {@link CustomViewFragment} instance
         *
         * @param layout layout resource used to inflate
         */
        fun newInstance(layout: Int): CustomViewFragment
        {
            val custom = CustomViewFragment()
            custom.viewLayout = layout
            return custom
        }
    }
}