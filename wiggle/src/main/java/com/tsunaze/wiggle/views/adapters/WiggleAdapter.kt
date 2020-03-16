package com.tsunaze.wiggle.views.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class WiggleAdapter(
    fm: FragmentManager,
    private val fragments: MutableList<Fragment>
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size
}