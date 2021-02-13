package com.med.ebeveynmedia.Search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.BottomnavigationViewHelper
import kotlinx.android.synthetic.main.activity_home.*

class SearchActivity : AppCompatActivity() {

    private val ACTIVITY_NO =1
    private val TAG="SearchActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
                // setupNavigationView()
    }
   /* fun setupNavigationView(){
        //BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this,bottomNavigationView)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }*/
}
