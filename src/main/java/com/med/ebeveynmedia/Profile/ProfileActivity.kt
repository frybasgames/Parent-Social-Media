package com.med.ebeveynmedia.Profile

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.med.ebeveynmedia.Login.LoginActivity
import com.med.ebeveynmedia.Model.Posts
import com.med.ebeveynmedia.Model.UserPosts
import com.med.ebeveynmedia.Model.Users
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.*
import kotlinx.android.synthetic.main.activity_profile.*
import org.greenrobot.eventbus.EventBus


class ProfileActivity : AppCompatActivity() {

    private val ACTIVITY_NO =4
    private val TAG="ProfileActivity"

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser:FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var tumGonderiler:ArrayList<UserPosts>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setupAuthListener()
        mAuth = FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef=FirebaseDatabase.getInstance().reference


        tumGonderiler=ArrayList<UserPosts>()
        setupToolbar()
        setupNavigationView()
        kullaniciBilgileriGetir()
        kullaniciPostlariniGetir(mUser.uid)


        imgGrid.setOnClickListener {
            setupRecyclerView(1)
        }

        imgList.setOnClickListener {
            setupRecyclerView(2)
        }
    }

    private fun kullaniciBilgileriGetir() {

        tvProfilDuzenleButon.isEnabled=false
        imgProfileSettings.isEnabled=false

        mRef.child("users").child(mUser!!.uid).addValueEventListener(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0!!.getValue() != null) {
                        var okunanKullaniciBilgileri = p0!!.getValue(Users::class.java)

                        var kullanici=Users()

                        EventBus.getDefault().postSticky(EventbusDataEvents.KullaniciBilgileriniGonder(okunanKullaniciBilgileri))

                        tvProfilDuzenleButon.isEnabled=true
                        imgProfileSettings.isEnabled=true

                        tvProfilAdiToolbar.setText(okunanKullaniciBilgileri!!.user_name)
                        tvProfiliGercekAdi.setText(okunanKullaniciBilgileri!!.adi_soyadi)
                        tvFollowerSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.follower)
                        tvFollowingSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.following)
                        tvPostSayisi.setText(okunanKullaniciBilgileri!!.user_detail!!.post)

                        var imgUrl=okunanKullaniciBilgileri!!.user_detail!!.profile_picture!!
                        UniversalImageLoader.setImage(imgUrl,circleProfileImage,progressBar,"")

                        if (!okunanKullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()){
                            tvBiyografi.visibility=View.VISIBLE
                            tvBiyografi.setText(okunanKullaniciBilgileri!!.user_detail!!.biography!!)
                        }
                        if (!okunanKullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()){
                            tvWebSitesi.visibility=View.VISIBLE
                            tvWebSitesi.setText(okunanKullaniciBilgileri!!.user_detail!!.web_site!!)
                        }
                    }
                }

            })

    }




    private fun setupToolbar() {
        imgProfileSettings.setOnClickListener {
            var intent = Intent(this,ProfileSettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

            startActivity(intent)
        }
        tvProfilDuzenleButon.setOnClickListener {
            tumlayout.visibility = View.GONE
            profileContainer.visibility=View.VISIBLE
            var transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.profileContainer, ProfileEditFragment())
            transaction.addToBackStack("editProfileFragmentEklendi")
            transaction.commit()
        }
    }

    private fun kullaniciPostlariniGetir(kullaniciID:String) {




        mRef.child("users").child(kullaniciID!!).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                Log.e("hata","kullaniciId"+kullaniciID.toString())
                var userID = kullaniciID
                var kullaniciAdi = p0!!.getValue(Users::class.java)!!.user_name
                var kullaniciFotoURL = p0!!.getValue(Users::class.java)!!.user_detail!!.profile_picture

                mRef.child("posts").child(kullaniciID!!).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0!!.hasChildren()){
                            for (ds in p0!!.children){

                                var eklenecekUserPosts= UserPosts()
                                eklenecekUserPosts.userID=userID
                                eklenecekUserPosts.userName=kullaniciAdi
                                eklenecekUserPosts.userPhotoURL=kullaniciFotoURL

                                eklenecekUserPosts.postID=ds.getValue(Posts::class.java)!!.post_id
                                eklenecekUserPosts.postURL=ds.getValue(Posts::class.java)!!.file_url
                                eklenecekUserPosts.postAciklama=ds.getValue(Posts::class.java)!!.aciklama
                                eklenecekUserPosts.postYuklenmeTarih=ds.getValue(Posts::class.java)!!.yuklenme_tarih


                                tumGonderiler.add(eklenecekUserPosts)
                            }
                        }
                        setupRecyclerView(1)
                    }

                })



            }

        })

    }
    //1 ise grid 2 ise list view şeklinde veriler gösterilir
    private fun setupRecyclerView(layoutCesidi: Int) {

        if (layoutCesidi==1){

            imgGrid.setColorFilter(ContextCompat.getColor(this,R.color.colorAccent),PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this,R.color.siyah),PorterDuff.Mode.SRC_IN)


            var kullaniciPostListe = profileRecyclerView
            kullaniciPostListe.adapter=ProfilePostGridRecyclerAdapter(tumGonderiler,this)

            kullaniciPostListe.layoutManager=GridLayoutManager(this,3)

        }else if (layoutCesidi==2){
            imgGrid.setColorFilter(ContextCompat.getColor(this,R.color.siyah),PorterDuff.Mode.SRC_IN)
            imgList.setColorFilter(ContextCompat.getColor(this,R.color.colorAccent),PorterDuff.Mode.SRC_IN)

            var kullaniciPostListe = profileRecyclerView
            kullaniciPostListe.adapter=ProfilePostListRecyclerAdapter(this,tumGonderiler)
            kullaniciPostListe.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        }
    }

    fun setupNavigationView(){
        //BottomnavigationViewHelper.setupBottomNavigationView(bottomNavigationView)
        BottomnavigationViewHelper.setupNavigation(this,bottomNavigationView)
        var menu=bottomNavigationView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    override fun onBackPressed() {
        tumlayout.visibility=View.VISIBLE
        super.onBackPressed()
    }

    private fun setupAuthListener() {

        mAuthListener=object  : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if (user == null){

                    var intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                    intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
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


