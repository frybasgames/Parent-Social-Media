package com.med.ebeveynmedia.utils

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.med.ebeveynmedia.Generic.CommentFragment
import com.med.ebeveynmedia.Home.HomeActivity
import com.med.ebeveynmedia.Model.UserPosts
import com.med.ebeveynmedia.R
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.tek_post_recycler_item.view.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class HomeFragmentRecyclerAdapter(var context: Context, var tumGonderiler:ArrayList<UserPosts>):RecyclerView.Adapter<HomeFragmentRecyclerAdapter.MyViewHolder>() {


    init {
        Collections.sort(tumGonderiler, object : Comparator<UserPosts>{
            override fun compare(o1: UserPosts?, o2: UserPosts?): Int {
                if(o1!!.postYuklenmeTarih!! > o2!!.postYuklenmeTarih!!){
                    return -1
                }else return 1
            }

        })
    }

    override fun getItemCount(): Int {
        return tumGonderiler.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        var viewHolder = LayoutInflater.from(context).inflate(R.layout.tek_post_recycler_item,parent,false)

        return MyViewHolder(viewHolder, context)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setData(position, tumGonderiler.get(position))


    }

    class MyViewHolder(itemView: View?, myHomActivity: Context) : RecyclerView.ViewHolder(itemView!!) {


        var tumLayout = itemView as ConstraintLayout
        var profileImage = tumLayout.imgUserProfile
        var userNameTitle = tumLayout.tvKullaniciAdiBaslik
        var gonderi = tumLayout.imgPostResim
        var userNameveAciklama = tumLayout.tvKullaniciAdiveAciklama
        var gonderiKacZamanOnce = tumLayout.tvKacZamanOnce
        var yorumYap = tumLayout.imgYorum
        var gonderiBegen = tumLayout.imgBegen
        var myHomeActivity= myHomActivity
        var begenmeSayisi = tumLayout.tvBegenmeSayisi


        fun setData(position: Int, oankiGonderi:UserPosts) {


            var link=oankiGonderi.postURL
            userNameTitle.setText(oankiGonderi.userName)
            UniversalImageLoader.setImage(link!!,gonderi!!,null,"")

            var userNameveAciklamaText ="<font color=#000>"+oankiGonderi.userName.toString()+"</font>"+" "+oankiGonderi.postAciklama
            var sonuc:Spanned?=null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                sonuc= Html.fromHtml(userNameveAciklamaText,Html.FROM_HTML_MODE_LEGACY)
            }else{
                sonuc=Html.fromHtml(userNameveAciklamaText)
            }
            userNameveAciklama.setText(sonuc)

            UniversalImageLoader.setImage(oankiGonderi.userPhotoURL!!,profileImage,null,"")
            gonderiKacZamanOnce.setText(TimeAgo.getTimeAgo(oankiGonderi.postYuklenmeTarih!!))

            begeniKontrol(oankiGonderi)

            yorumYap.setOnClickListener {

                EventBus.getDefault().postSticky(EventbusDataEvents.YorumYapilacakGonderininIDsiniGonder(oankiGonderi!!.postID))

                (myHomeActivity as HomeActivity).homeViewPager.visibility=View.INVISIBLE
                (myHomeActivity as HomeActivity).homeFragmentContainer.visibility=View.VISIBLE

                var transaction =(myHomeActivity as HomeActivity).supportFragmentManager.beginTransaction()
                transaction.replace(R.id.homeFragmentContainer, CommentFragment())
                transaction.addToBackStack("commentFragmentEklendi")
                transaction.commit()
            }

            gonderiBegen.setOnClickListener {
                var mRef = FirebaseDatabase.getInstance().reference
                var userID = FirebaseAuth.getInstance().currentUser!!.uid
                mRef.child("likes").child(oankiGonderi.postID!!).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0!!.hasChild(userID)){
                            mRef.child("likes").child(oankiGonderi.postID!!).child(userID).removeValue()
                            gonderiBegen.setImageResource(R.drawable.ic_begen)
                        }else {
                            mRef.child("likes").child(oankiGonderi.postID!!).child(userID).setValue(userID)
                            gonderiBegen.setImageResource(R.drawable.ic_begen_kirmizi)
                            begenmeSayisi.visibility=View.VISIBLE
                            begenmeSayisi.setText(""+p0!!.childrenCount!!.toString()+" beğenme")
                        }
                    }

                })
            }

        }

        fun begeniKontrol(oankiGonderi: UserPosts) {
            var mRef = FirebaseDatabase.getInstance().reference

            var userID = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("likes").child(oankiGonderi.postID!!).addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                   if(p0!!.getValue()!=null){
                        begenmeSayisi.visibility=View.VISIBLE
                        begenmeSayisi.setText(""+p0!!.childrenCount!!.toString()+" beğenme")
                    }else{
                        begenmeSayisi.visibility=View.GONE
                    }

                    if (p0!!.hasChild(userID)){
                        gonderiBegen.setImageResource(R.drawable.ic_begen_kirmizi)
                    }else{
                        gonderiBegen.setImageResource(R.drawable.ic_begen)
                    }
                }

            })
        }
    }


}