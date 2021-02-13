package com.med.ebeveynmedia.Profile


import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import kotlinx.android.synthetic.main.fragment_profile_edit.view.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.med.ebeveynmedia.Model.Users
import com.med.ebeveynmedia.utils.EventbusDataEvents
import com.med.ebeveynmedia.utils.UniversalImageLoader
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_profile.circleProfileImage
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.lang.Exception


/**
 * A simple [Fragment] subclass.
 */
class ProfileEditFragment : Fragment() {

    lateinit var circleProfileImageFragment:CircleImageView
    lateinit var gelenkullaniciBilgileri:Users
    lateinit var mDatabaseRef:DatabaseReference
    lateinit var mStorageRef: StorageReference

    var profilPhotoUri: Uri? = null
    val RESIM_SEC=100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater!!.inflate(com.med.ebeveynmedia.R.layout.fragment_profile_edit, container, false)

        mDatabaseRef=FirebaseDatabase.getInstance().reference
        mStorageRef=FirebaseStorage.getInstance().reference

        setupKullaniciBilgileri(view)


        view.imgClose.setOnClickListener {
            activity!!.onBackPressed()
        }

        view.tvFotografiDegistir.setOnClickListener {

            var intent= Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_PICK)
            startActivityForResult(intent,RESIM_SEC)

        }

        view.imgBtnDegisiklikleriKaydet.setOnClickListener {

            if (profilPhotoUri !=null){

                var dialogYukleniyor=YukleniyorFragment()
                dialogYukleniyor.show(activity!!.supportFragmentManager,"yukleniyorFragmenti")
                dialogYukleniyor.isCancelable=false

                //yenı halını alır buraya uyarlarsn tamam hocam burada çalışmıyordu zaten aslında kursta guncelleme vıdeolarında anlatmıstm sanırım ama
                //olmadı onlara da bakarsn ilerisinde kaldığı için adım adım gittim ilerisini açıp bakayım iyice
                /*var uploadTask=mStorageRef.child("users").child(gelenkullaniciBilgileri!!.user_id!!).child(profilPhotoUri!!.lastPathSegment).putFile(profilPhotoUri!!)
                    .addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot>{
                        override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                            if (p0!!.isSuccessful){
                           //     Toast.makeText(activity,"resim yüklendi",Toast.LENGTH_SHORT).show()

                                mDatabaseRef.child("users").child(gelenkullaniciBilgileri!!.user_id!!).child("user_detail").child("profile_picture")
                                    .setValue(p0!!.result!!.downloadUrl.toString())
                                dialogYukleniyor.dismiss()
                                kullaniciAdiniGuncelle(view,true)
                            }
                        }

                    })
                    .addOnFailureListener(object : OnFailureListener{
                        override fun onFailure(p0: Exception) {
                            Log.e("hata",p0!!.message)
                            kullaniciAdiniGuncelle(view,false)
                        }

                    })*/

            }else{
                kullaniciAdiniGuncelle(view,null)
            }
        }


        return view
    }
    //profilresmi degisti
    //true: basarılı bir şekilde resim storage yüklenmiş ve veritabanına yazılmıştır
    //false : resim yüklenirken hata oluşmuştur
    //null: kullanıcı resmini değiştirmek istememiştir
    private fun kullaniciAdiniGuncelle(view: View, profilResmiDegisti: Boolean?) {
        if (!gelenkullaniciBilgileri!!.user_name!!.equals(view.etUserName.text.toString())){
            mDatabaseRef.child("users").orderByChild("user_name").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {

                    var userNameKullanımdaMi=false

                    for (ds in p0!!.children){

                        var okunanKullaniciAdi=ds!!.getValue(Users::class.java)!!.user_name

                        if (okunanKullaniciAdi!!.equals(view.etUserName.text.toString())){
                            userNameKullanımdaMi=true
                            profilBilgileriniGuncelle(view,profilResmiDegisti,false)
                            break
                        }


                    }
                    if (userNameKullanımdaMi==false){
                        mDatabaseRef.child("users").child(gelenkullaniciBilgileri!!.user_id.toString()).child("user_name").setValue(view.etUserName.text.toString())
                        profilBilgileriniGuncelle(view,profilResmiDegisti,true)

                    }

                }

            })


        }else{
            profilBilgileriniGuncelle(view,profilResmiDegisti,null)
        }
    }

    private fun profilBilgileriniGuncelle(view: View, profilResmiDegisti: Boolean?, userNameDegisti: Boolean?) {

        var profilGuncellendiMi:Boolean?=null

        if (!gelenkullaniciBilgileri!!.adi_soyadi!!.equals(view.etProfileName.text.toString())){
            mDatabaseRef.child("users").child(gelenkullaniciBilgileri!!.user_id.toString()).child("adi_soyadi").setValue(view.etProfileName.text.toString())
            profilGuncellendiMi=true
        }
        if (!gelenkullaniciBilgileri!!.user_detail!!.biography!!.equals(view.etUserBio.text.toString())){
            mDatabaseRef.child("users").child(gelenkullaniciBilgileri!!.user_id.toString()).child("user_detail").child("biography").setValue(view.etUserBio.text.toString())
            profilGuncellendiMi=true
        }
        if (!gelenkullaniciBilgileri!!.user_detail!!.web_site!!.equals(view.etUserWebSite.text.toString())){
            mDatabaseRef.child("users").child(gelenkullaniciBilgileri!!.user_id.toString()).child("user_detail").child("web_site").setValue(view.etUserWebSite.text.toString())
            profilGuncellendiMi=true
        }

        if (profilResmiDegisti == null && userNameDegisti == null && profilGuncellendiMi == null){
            Toast.makeText(activity,"degisiklik yok",Toast.LENGTH_SHORT).show()
        }
        else if (userNameDegisti==false && (profilGuncellendiMi==true || profilResmiDegisti==true)){
            Toast.makeText(activity,"bilgiler güncellendi ama kullanıcı adı KULLANIMDA",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(activity,"kullanıcı güncellendi",Toast.LENGTH_SHORT).show()
            activity!!.onBackPressed()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==RESIM_SEC && resultCode== AppCompatActivity.RESULT_OK && data!!.data!=null){

            profilPhotoUri=data!!.data!!

            circleProfileImage.setImageURI(profilPhotoUri)

        }

    }

    private fun setupKullaniciBilgileri(view: View?) {

        view!!.etProfileName.setText(gelenkullaniciBilgileri!!.adi_soyadi)
        view!!.etUserName.setText(gelenkullaniciBilgileri!!.user_name)

        if (!gelenkullaniciBilgileri!!.user_detail!!.biography!!.isNullOrEmpty()){
            view!!.etUserBio.setText(gelenkullaniciBilgileri!!.user_detail!!.biography)
        }
        if (!gelenkullaniciBilgileri!!.user_detail!!.web_site!!.isNullOrEmpty()){
            view!!.etUserWebSite.setText(gelenkullaniciBilgileri!!.user_detail!!.web_site)
        }

        var imgUrl=gelenkullaniciBilgileri!!.user_detail!!.profile_picture
        UniversalImageLoader.setImage(imgUrl!!,view!!.circleProfileImage!!,view!!.progressBar,"")

    }



    /////////////////EVENTBUS /////////////////
    @Subscribe(sticky = true)
    internal fun onKullaniciBilgileriEvent(kullaniciBilgileri : EventbusDataEvents.KullaniciBilgileriniGonder){

        gelenkullaniciBilgileri=kullaniciBilgileri!!.kullanici!!

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }


}
