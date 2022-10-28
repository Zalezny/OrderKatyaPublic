package com.example.orderappkatya.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.orderappkatya.Fragments.ArchiveFragment
import com.example.orderappkatya.Fragments.CurrentFragment

internal class FragmentAdapter(var context: Context, fm: FragmentManager, var totalTabs: Int): FragmentPagerAdapter(fm) {




    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> {
                CurrentFragment()
            }
            1 -> {
                ArchiveFragment()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

    override fun getPageTitle(position: Int): CharSequence? {
        var title : String? = null
        when(position){
            0-> title = "Do wysyłki"
            1-> title = "Wysłane"
        }

        return title
    }

}