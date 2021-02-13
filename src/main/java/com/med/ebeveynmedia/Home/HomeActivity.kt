package com.med.ebeveynmedia.Home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager.LayoutParams.*
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.karumi.dexter.Dexter
import com.med.ebeveynmedia.Login.LoginActivity
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.BottomnavigationViewHelper
import com.med.ebeveynmedia.utils.EventbusDataEvents
import com.med.ebeveynmedia.utils.HomePagerAdapter
import com.med.ebeveynmedia.utils.UniversalImageLoader
import com.nostra13.universalimageloader.core.ImageLoader
import kotlinx.android.synthetic.main.activity_home.*
import org.greenrobot.eventbus.EventBus

class HomeActivity : AppCompatActivity() {

    private val ACTIVITY_NO =0
    private val TAG="HomeActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        setupAuthListener()

        mAuth = FirebaseAuth.getInstance()

        initImageLoader()
        setupHomeViewPager()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupHomeViewPager(){
        var homePagerAdapter=HomePagerAdapter(supportFragmentManager)
        homePagerAdapter.addFragment(CameraFragment()) //id = 0
        homePagerAdapter.addFragment(HomeFragment()) //id = 1
        homePagerAdapter.addFragment(MessagesFragment()) //id = 2


        homeViewPager.adapter=homePagerAdapter

        homeViewPager.setCurrentItem(1)
        homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,0)
        homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,2)

        homeViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if (position==0){
                    this@HomeActivity.window.addFlags(FLAG_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(FLAG_FORCE_NOT_FULLSCREEN)

                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,1)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,2)
                    kameraIzniIste()
                    homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager,0)


                }
                if (position==1){
                    this@HomeActivity.window.addFlags(FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(FLAG_FULLSCREEN)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,0)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,2)
                    homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager,1)
                }
                if (position==2){
                    this@HomeActivity.window.addFlags(FLAG_FORCE_NOT_FULLSCREEN)
                    this@HomeActivity.window.clearFlags(FLAG_FULLSCREEN)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,0)
                    homePagerAdapter.secilenFragmentiViewPagerdanSil(homeViewPager,1)
                    homePagerAdapter.secilenFragmentiViewPageraEkle(homeViewPager,2)
                }
            }

        })

    }

    private fun kameraIzniIste() {
        EventBus.getDefault().postSticky(EventbusDataEvents.KameraIzinBilgisiGonder(true))
        homeViewPager.setCurrentItem(1)
    }

    override fun onBackPressed() {

        if (homeViewPager.currentItem == 1){
            homeViewPager.visibility=View.VISIBLE
            homeFragmentContainer.visibility=View.GONE
            super.onBackPressed()
        }else{
            homeViewPager.visibility=View.VISIBLE
            homeFragmentContainer.visibility=View.GONE
            homeViewPager.setCurrentItem(1)
        }

    }

    private fun initImageLoader(){
        var universalImageLoader= UniversalImageLoader(this)
        ImageLoader.getInstance().init(universalImageLoader.config)
    }
    private fun setupAuthListener() {
        mAuthListener=object  : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if (user == null){
                    var intent = Intent(this@HomeActivity,LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }else{

                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        mAuth.addAuthStateListener(mAuthListener)
    }

    override fun onStop() {
        super.onStop()
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener)
        }
    }
}
