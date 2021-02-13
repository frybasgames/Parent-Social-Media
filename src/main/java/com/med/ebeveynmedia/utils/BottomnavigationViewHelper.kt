package com.med.ebeveynmedia.utils

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx
import com.med.ebeveynmedia.Home.HomeActivity
import com.med.ebeveynmedia.News.NewsActivity
import com.med.ebeveynmedia.Profile.ProfileActivity
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.Search.SearchActivity
import com.med.ebeveynmedia.Share.ShareActivity

class BottomnavigationViewHelper {

    companion object {
        fun setupBottomNavigationView(bottomNavigationViewEx: BottomNavigationViewEx) {
    //        bottomNavigationViewEx.enableAnimation(false)
//            bottomNavigationViewEx.enableItemShiftingMode(false)
  //          bottomNavigationViewEx.enableShiftingMode(false)
    //        bottomNavigationViewEx.setTextVisibility(false)
        }

        fun setupNavigation(context: Context, bottomNavigationViewEx: BottomNavigationViewEx) {
            bottomNavigationViewEx.onNavigationItemSelectedListener =
                object : BottomNavigationView.OnNavigationItemSelectedListener {
                    override fun onNavigationItemSelected(item: MenuItem): Boolean {
                        when (item.itemId) {
                            R.id.ic_home -> {

                                val intent = Intent(context, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                            R.id.ic_search -> {
                                val intent = Intent(context, SearchActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                            R.id.ic_share -> {
                                val intent = Intent(context, ShareActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                            R.id.ic_news -> {
                                val intent = Intent(context, NewsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }
                            R.id.ic_profile -> {
                                val intent = Intent(context, ProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                context.startActivity(intent)
                                return true
                            }


                        }
                        return false

                    }
                }
        }
    }
}