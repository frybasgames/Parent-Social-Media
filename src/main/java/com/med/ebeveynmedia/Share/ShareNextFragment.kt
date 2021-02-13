package com.med.ebeveynmedia.Share


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.*
import com.med.ebeveynmedia.Home.HomeActivity
import com.med.ebeveynmedia.Model.Posts
import com.med.ebeveynmedia.Profile.YukleniyorFragment

import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.DosyaIslemleri
import com.med.ebeveynmedia.utils.EventbusDataEvents
import com.med.ebeveynmedia.utils.UniversalImageLoader
import kotlinx.android.synthetic.main.fragment_share_next.*
import kotlinx.android.synthetic.main.fragment_share_next.view.*
import kotlinx.android.synthetic.main.fragment_share_next.view.tvIleriButton
import kotlinx.android.synthetic.main.fragment_yukleniyor.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception

class ShareNextFragment : Fragment() {

    var secilenDosyaYolu:String?=null
    var dosyaTuruResimMi:Boolean? = null
    lateinit var photoURI:Uri

    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var mStorageReference: StorageReference



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_share_next, container, false)

        UniversalImageLoader.setImage(secilenDosyaYolu!!,view!!.imgSecilenResim,null,"file://")


      //  photoURI= Uri.parse("file://"+secilenDosyaYolu)

        mAuth = FirebaseAuth.getInstance()
        mUser = mAuth.currentUser!!
        mRef=FirebaseDatabase.getInstance().reference
        mStorageReference=FirebaseStorage.getInstance().reference


        view.tvIleriButton.setOnClickListener {


            //resim dosyasını sıkıştır
            if (dosyaTuruResimMi == true) {

                DosyaIslemleri.compressResimDosya(this, secilenDosyaYolu)

            }
            //video dosyasını sıkıştır
            else if (dosyaTuruResimMi == false) {

                DosyaIslemleri.compressVideoDosya(this,secilenDosyaYolu!!)

            }


        }

        view.imgClose.setOnClickListener {


            this.activity!!.onBackPressed()

        }

        return view
    }

    private fun veritabaninaBilgileriYaz(yuklenenFotoURL: String) {

        var postID= mRef.child("posts").child(mUser.uid).push().key

        var yuklenenPost = Posts(mUser.uid,postID,0,etPostAciklama.text.toString(),yuklenenFotoURL)

        mRef.child("posts").child(mUser.uid).child(postID.toString()).setValue(yuklenenPost)
        mRef.child("posts").child(mUser.uid).child(postID.toString()).child("yuklenme_tarih").setValue(ServerValue.TIMESTAMP)

        if(!etPostAciklama.text.toString().isNullOrEmpty()){
           // var yorumKey = mRef.child("comments").child(postID).push().key
            mRef.child("comments").child(postID!!).child(postID!!).child("user_id").setValue(mUser.uid)
            mRef.child("comments").child(postID!!).child(postID!!).child("yorum_tarih").setValue(ServerValue.TIMESTAMP)
            mRef.child("comments").child(postID!!).child(postID!!).child("yorum").setValue(etPostAciklama.text.toString())
            mRef.child("comments").child(postID!!).child(postID!!).child("yorum_begeni").setValue("0")

        }

        var intent= Intent(activity,HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)

    }


    /////////////////EVENTBUS /////////////////
    @Subscribe(sticky = true)
    internal fun onSecilenDosyaEvent(SecilenResim : EventbusDataEvents.PaylasilacakResmiGonder){
        secilenDosyaYolu=SecilenResim!!.dosyaYolu!!
        dosyaTuruResimMi=SecilenResim!!.dosyaTuruResimMi

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    fun uploadStorage(filePath: String?) {

        var fileUri = Uri.parse("file://"+filePath)
        Log.e("EEE","FILE URLL:"+fileUri.toString())
        var dialogYuklenior = YukleniyorFragment()
        dialogYuklenior.show(activity!!.supportFragmentManager,"yukleniyorFragmenti")
        dialogYuklenior.isCancelable=false

        var reff= mStorageReference.child("users").child(mUser.uid).child(fileUri.lastPathSegment)
        var uploadTask=reff.putFile(fileUri)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            reff.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                veritabaninaBilgileriYaz(downloadUri.toString())
                dialogYuklenior.dismiss()

            } else {
                Log.e("eee","hata cıktııı")
            }
        }


//oldu ama bundan sonra yonlendirme yapmıs mıydın eğitime yönelik gitmiştim ama bir yere yönlendirmemiştiniz  siz ıyı neyse duzelttık
        //sımdı sen External.getstorage diye devam eden yerleri bnm yaptıgım sekilde değiştir
        //bide upload edilen imagenin downloadurli bnm yukarda yaptgm gibi alınıyor artık videodaki derslerdeki gibi değil
        //bunları yaparsan sorun cıkmaz sanırım bidaha tamam hocam ben bu kısmı iyice bir karşılaştırıp öğreneyim o şekilde devam ederim
        //profil foto guncelleyi de unutma orayı da yoruma almıstm






          /*uploadTask.addOnSuccessListener {

          }.addOnFailureListener {

          }
            .addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot> {
                override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                    if(p0!!.isSuccessful){
                        dialogYuklenior.dismiss()
                        veritabaninaBilgileriYaz(p0!!.getResult()!!.downloadUrl.toString())
                    }
                }

            })

            .addOnFailureListener(object: OnFailureListener{
                override fun onFailure(p0: Exception) {
                    dialogYuklenior.dismiss()
                    Toast.makeText(activity,"Hata oluştu"+p0!!.message,Toast.LENGTH_SHORT).show()
                }

            })

*/


    }

    }


