package com.med.ebeveynmedia.Share

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.PermissionRequestErrorListener
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.med.ebeveynmedia.Login.LoginActivity
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.SharePagerAdapter
import kotlinx.android.synthetic.main.activity_share.*

class ShareActivity : AppCompatActivity() {

    private val ACTIVITY_NO =2
    private val TAG="ShareActivity"


    lateinit var mAuthListener: FirebaseAuth.AuthStateListener






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

     //  storageVeKameraIzniIste()

        setupAuthListener()


        setupShareViewPager()
    }




    /*  private fun storageVeKameraIzniIste() {


         Dexter.withActivity(this@ShareActivity)
              .withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                  android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                  android.Manifest.permission.CAMERA)
              .withListener(object : MultiplePermissionsListener{
                  override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                      if (report!!.areAllPermissionsGranted()){
                          setupShareViewPager()
                      }
                      if (report!!.isAnyPermissionPermanentlyDenied){

                      }

                  }

                  override fun onPermissionRationaleShouldBeShown(
                      permissions: MutableList<PermissionRequest>?,
                      token: PermissionToken?) {

                  }

              })
              .withErrorListener(object : PermissionRequestErrorListener{
                  override fun onError(p0: DexterError?) {

                  }

              }).check()
      } */

    private fun setupShareViewPager() {

        var tabAdlari=ArrayList<String>()
        tabAdlari.add("GALERI")
        tabAdlari.add("FOTOĞRAF")
        tabAdlari.add("VIDEO")
        var sharePagerAdapter=SharePagerAdapter(supportFragmentManager,tabAdlari)
        sharePagerAdapter.addFragment(ShareGalleryFragment())
        sharePagerAdapter.addFragment(ShareCameraFragment())
        sharePagerAdapter.addFragment(ShareVideoFragment())

        shareViewPager.adapter=sharePagerAdapter

        shareViewPager.offscreenPageLimit=1

        sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,1)
        sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,2)

        shareViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {


            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int) {


            }

            override fun onPageSelected(position: Int) {

                if (position==0){

                    sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,1)
                    sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,2)
                    sharePagerAdapter.secilenFragmentiViewPageraEkle(shareViewPager,0)

                }
                if (position==1){

                    sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,0)
                    sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,2)
                    sharePagerAdapter.secilenFragmentiViewPageraEkle(shareViewPager,1)

                }
                if (position==2){

                    sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,0)
                    sharePagerAdapter.secilenFragmentiViewPagerdanSil(shareViewPager,1)
                    sharePagerAdapter.secilenFragmentiViewPageraEkle(shareViewPager,2)

                }

            }

        })

        sharetablayout.setupWithViewPager(shareViewPager)

    }

    override fun onBackPressed() {
        anaLayout.visibility=View.VISIBLE
        fragmentContainerLayout.visibility=View.GONE
        super.onBackPressed()
    }


    private fun setupAuthListener() {

        mAuthListener=object  : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if (user == null){

                    var intent = Intent(this@ShareActivity, LoginActivity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()

                }else{

                }
            }

        }
    }

}
