package com.med.ebeveynmedia.Home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.med.ebeveynmedia.Login.LoginActivity
import com.med.ebeveynmedia.Model.Posts
import com.med.ebeveynmedia.Model.UserPosts
import com.med.ebeveynmedia.Model.Users
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.BottomnavigationViewHelper
import com.med.ebeveynmedia.utils.HomeFragmentRecyclerAdapter
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_home.view.recyclerview

class HomeFragment : Fragment() {

    lateinit var fragmentView:View
    private val ACTIVITY_NO=0

    lateinit var tumGonderiler:ArrayList<UserPosts>

    lateinit var mAuth: FirebaseAuth
    lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentView = inflater?.inflate(R.layout.fragment_home, container, false)

        setupAuthListener()

        mAuth=FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef=FirebaseDatabase.getInstance().reference
        tumGonderiler=ArrayList<UserPosts>()

        kullaniciPostlariniGetir(mUser.uid!!)

        fragmentView.imgTabCamera.setOnClickListener {
            (activity as HomeActivity).homeViewPager.setCurrentItem(0)
        }
        fragmentView.imgTabDirectMessage.setOnClickListener {
            (activity as HomeActivity).homeViewPager.setCurrentItem(2)
        }



        return fragmentView
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

                                var eklenecekUserPosts=UserPosts()
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
                        setupRecyclerView()
                    }

                })



            }

        })

    }

    private fun setupRecyclerView() {
        var recyclerView = fragmentView.recyclerview
        var recyclerAdapter = HomeFragmentRecyclerAdapter(this.activity!!,tumGonderiler)

        recyclerView.adapter=recyclerAdapter


        recyclerView.layoutManager=LinearLayoutManager(this.activity!!,LinearLayoutManager.VERTICAL,false)


    }


    fun setupNavigationView(){

        var fragmentBottomNavView = fragmentView.bottomNavigationView
        BottomnavigationViewHelper.setupBottomNavigationView(fragmentBottomNavView)
        BottomnavigationViewHelper.setupNavigation(activity!!,fragmentBottomNavView)
        var menu=fragmentBottomNavView.menu
        var menuItem=menu.getItem(ACTIVITY_NO)
        menuItem.setChecked(true)
    }

    private fun setupAuthListener() {

        mAuthListener=object  : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user=FirebaseAuth.getInstance().currentUser
                if (user == null){

                    var intent = Intent(activity, LoginActivity::class.java)
                    intent.flags= Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    activity!!.finish()

                }else{

                }
            }

        }
    }

    override fun onResume() {
        setupNavigationView()
        super.onResume()
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