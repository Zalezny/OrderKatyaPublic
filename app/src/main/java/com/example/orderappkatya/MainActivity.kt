package com.example.orderappkatya

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.orderappkatya.adapters.FragmentAdapter
import com.example.orderappkatya.Fragments.ArchiveFragment
import com.example.orderappkatya.interfaces.Communicator
import com.example.orderappkatya.Fragments.CurrentFragment
import com.example.orderappkatya.interfaces.RefreshCom
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity(), Communicator, RefreshCom {
    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result
                Log.d("klaja", "$token")
            })
        FirebaseMessaging.getInstance().subscribeToTopic("fcm")
            .addOnCompleteListener { task ->
                var msg = "Subscribed Successfully"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d("Subscription", "$msg")
            }

        //Code of ViewPager (slashing between CURRENT and ARCHIVE)
        val tabLayout : TabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        val adapter = FragmentAdapter(this, supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = adapter

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })


    }

    //Function responsible for communication between fragments in sending data
    override fun passDataCom(isArchive: Boolean, nrOrder: Int, pos: Int) {
        //tag which show our activity fragment
        val tag = "android:switcher:" + R.id.view_pager.toString() + ":" + 0
        val currFr = supportFragmentManager.findFragmentByTag(tag) as CurrentFragment?
        //tag which show our activity fragment
        val tag2 = "android:switcher:" + R.id.view_pager.toString() + ":" + 1
        val archFr = supportFragmentManager.findFragmentByTag(tag2) as ArchiveFragment?
        //currentFragment
        if(isArchive) {
            //Delete from currentFragment and refresh in archiveFragment
            currFr!!.removeData(pos)
            archFr!!.refreshData()

        }
        //archiveFragment
        if(!isArchive) {
            //Delete from archiveFragment and refresh in currentFragment
            archFr!!.removeData(pos)
            currFr!!.refreshData()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1) {
            if(resultCode == RESULT_OK) {
                val resultNrOrder = data!!.getIntExtra("numberOfOrder", 0)
                val resultIsArchive = data.getBooleanExtra("isArchive", false)
                val resultPosition = data.getIntExtra("position", 0)
                passDataCom(resultIsArchive, resultNrOrder, resultPosition)

            }
            if(resultCode == RESULT_CANCELED) {
                Log.e("RESULT_CANCELED", "Result is empty in MainActivity")
            }
        }
    }

    override fun refreshFragment(msg: Boolean) {
        //tag which show our activity fragment
        val tag = "android:switcher:" + R.id.view_pager.toString() + ":" + 0
        val currFr = supportFragmentManager.findFragmentByTag(tag) as CurrentFragment?
        //tag which show our activity fragment
        val tag2 = "android:switcher:" + R.id.view_pager.toString() + ":" + 1
        val archFr = supportFragmentManager.findFragmentByTag(tag2) as ArchiveFragment?

        if(msg)
        {
            currFr!!.refreshData()
        }
        if(!msg)
        {
            archFr!!.refreshData()
        }
    }




}





