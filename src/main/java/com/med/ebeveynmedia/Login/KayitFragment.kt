package com.med.ebeveynmedia.Login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.med.ebeveynmedia.Model.UserDetails
import com.med.ebeveynmedia.Model.Users

import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.EventbusDataEvents
import kotlinx.android.synthetic.main.fragment_kayit.*
import kotlinx.android.synthetic.main.fragment_kayit.view.*
import kotlinx.android.synthetic.main.fragment_kayit.view.btnGiris
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class KayitFragment : Fragment() {

    var telNo=""
    var verificationID=""
    var gelenKod=""
    var gelenEmail=""
    var emailIleKayitIslemi=true
    lateinit var mAuth: FirebaseAuth
    lateinit var  mRef:DatabaseReference
    lateinit var progressBar:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_kayit, container, false)

        progressBar=view.pbKullaniciKayit

        mAuth = FirebaseAuth.getInstance()

        view.tvGirisYap.setOnClickListener {
            var intent = Intent(activity,LoginActivity::class.java)
            startActivity(intent)
        }


        mRef = FirebaseDatabase.getInstance().reference
        view.etAdSoyad.addTextChangedListener(watcher)
        view.etKullaniciAdi.addTextChangedListener(watcher)
        view.etSifre.addTextChangedListener(watcher)

        view.btnGiris.setOnClickListener {

            var userNameKullanimdaMi=false

            mRef.child("users").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if (p0!!.getValue()!=null){
                        for (user in p0!!.children){
                            var okunanKullanici=user.getValue(Users::class.java)
                            if (okunanKullanici!!.user_name!!.equals(view.etKullaniciAdi.text.toString())){
                                Toast.makeText(activity,"kullanıcı adı kullanımda", Toast.LENGTH_SHORT).show()
                                userNameKullanimdaMi=true
                                break
                            }
                        }
                        if (userNameKullanimdaMi==false){

                            progressBar.visibility=View.VISIBLE

                            //kullanıcı email ile kaydolmak istiyor
                            if (emailIleKayitIslemi) {
                                var sifre = view.etSifre.text.toString()
                                var adSoyad = view.etAdSoyad.text.toString()
                                var userName = view.etKullaniciAdi.text.toString()

Log.e("EEE","BURADA ")
                                mAuth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                        override fun onComplete(p0: Task<AuthResult>) {

                                            if (p0!!.isSuccessful) {
                                                Toast.makeText(
                                                    activity,
                                                    "oturum email ile açıldı" + mAuth.currentUser!!.uid,
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                                var userID = mAuth.currentUser!!.uid.toString()

                                                // oturum açan kullanıcının verilerini databaseye kaydedelim...
                                                var kaydedilecekKullaniciDetaylari= UserDetails("0","0","0","","","")
                                                var kaydedilecekKullanici = Users(gelenEmail, sifre, userName, adSoyad, "", "", userID,kaydedilecekKullaniciDetaylari)

                                                mRef.child("users").child(userID).setValue(kaydedilecekKullanici)
                                                    .addOnCompleteListener(object : OnCompleteListener<Void> {
                                                        override fun onComplete(p0: Task<Void>) {
                                                            if (p0!!.isSuccessful) {
                                                                Toast.makeText(
                                                                    activity,
                                                                    "kullanıcı kaydedildi",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                                progressBar.visibility=View.INVISIBLE

                                                            } else {
                                                                progressBar.visibility=View.INVISIBLE
                                                                mAuth.currentUser!!.delete()
                                                                    .addOnCompleteListener(object : OnCompleteListener<Void>{
                                                                        override fun onComplete(p0: Task<Void>) {
                                                                            if(p0!!.isSuccessful){
                                                                                Toast.makeText(activity, "kullanıcı kaydedilmedi, Tekrar Deneyin", Toast.LENGTH_SHORT).show()

                                                                            }
                                                                        }
                                                                    })

                                                            }
                                                        }


                                                    })

                                            } else {
                                                progressBar.visibility=View.INVISIBLE
                                                Toast.makeText(
                                                    activity,
                                                    "oturum açılmadı: " + p0!!.exception,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }

                                        }

                                    })

                            }
                            //kullanıcı telefon no ile kayıt olmak istiyor
                            else {
                                var sifre = view.etSifre.text.toString()
                                var sahteEmail = telNo + "@emre.com" // "+905553423@emre.com"
                                var adSoyad = view.etAdSoyad.text.toString()
                                var userName = view.etKullaniciAdi.text.toString()

                                mAuth.createUserWithEmailAndPassword(sahteEmail, sifre)
                                    .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                                        override fun onComplete(p0: Task<AuthResult>) {


                                            if (p0!!.isSuccessful) {
                                                Toast.makeText(activity, "oturum tel no ile açıldı Uid:" + mAuth.currentUser!!.uid, Toast.LENGTH_SHORT).show()
                                                var userID = mAuth.currentUser!!.uid.toString()
                                                // oturum açan kullanıcının verilerini databaseye kaydedelim...

                                                var kaydedilecekKullaniciDetaylari= UserDetails("0","0","0","","","")
                                                var kaydedilecekKullanici = Users("", sifre, userName, adSoyad, telNo, sahteEmail, userID,kaydedilecekKullaniciDetaylari)
                                                Toast.makeText(activity, "kaydedilecek kullanici" + kaydedilecekKullanici, Toast.LENGTH_SHORT).show()

                                                mRef.child("users").child(userID).setValue(kaydedilecekKullanici).addOnCompleteListener(object : OnCompleteListener<Void>{
                                                    override fun onComplete(p0: Task<Void>) {

                                                        if (p0!!.isSuccessful) {
                                                            Toast.makeText(activity, "kullanıcı kaydedildi", Toast.LENGTH_SHORT).show()
                                                            progressBar.visibility=View.INVISIBLE
                                                        } else {
                                                            progressBar.visibility=View.INVISIBLE
                                                            mAuth.currentUser!!.delete()
                                                                .addOnCompleteListener(object : OnCompleteListener<Void>{
                                                                    override fun onComplete(p0: Task<Void>) {
                                                                        if(p0!!.isSuccessful){
                                                                            Toast.makeText(activity, "kullanıcı kaydedilmedi, Tekrar Deneyin", Toast.LENGTH_SHORT).show()

                                                                        }
                                                                    }
                                                                })

                                                        }
                                                    }
                                                })
                                            } else {
                                                progressBar.visibility=View.INVISIBLE
                                                Toast.makeText(activity, "oturum açılmadı: " + p0!!.exception, Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    })
                            }
                        }


                    }
                }

            })



        }
//ssdk var ı pcde siparişte gelior  2 güne daha ıyı olur 3 yıldır formatta atmadım yavaş biraz 4gb ram pcye baglanıp emulşator kurmaya calıstgm oldu:)
        return view
    }


    var watcher : TextWatcher = object : TextWatcher{
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length >= 1 && view!!.etAdSoyad.text.toString().trim().length>=1 && view!!.etKullaniciAdi.text.toString().trim().length>=1 && view!!.etSifre.text.toString().trim().length>=1) {
                btnGiris.isEnabled = true
                btnGiris.setTextColor(ContextCompat.getColor(activity!!, R.color.beyaz))
                btnGiris.setBackgroundResource(R.drawable.register_button_aktif)
            }
            else {
                btnGiris.isEnabled = false
                btnGiris.setTextColor(ContextCompat.getColor(activity!!, R.color.sonukmavi))
                btnGiris.setBackgroundResource(R.drawable.register_button)
            }
        }
        /*override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s!!.length>5){

                if (etAdSoyad.text.toString().length>5 && etKullaniciAdi.text.toString().length>5 && etSifre.text.toString().length>5){
                    btnGiris.isEnabled=true
                    btnGiris.setTextColor(ContextCompat.getColor(activity!!,R.color.beyaz))
                    btnGiris.setBackgroundResource(R.drawable.register_button_aktif)
                }else{
                    btnGiris.isEnabled=false
                    btnGiris.setTextColor(ContextCompat.getColor(activity!!,R.color.sonukmavi))
                    btnGiris.setBackgroundResource(R.drawable.register_button)
                }

            }else{
                btnGiris.isEnabled=false
                btnGiris.setTextColor(ContextCompat.getColor(activity!!,R.color.sonukmavi))
                btnGiris.setBackgroundResource(R.drawable.register_button)
            }
        }*/

    }


    /////////////////EVENTBUS /////////////////
    @Subscribe(sticky = true)
    internal fun onKayitEvent(kayitBilgileri : EventbusDataEvents.KayitBilgileriniGonder){

        if (kayitBilgileri.emailkayit==true) {
            emailIleKayitIslemi=true
            gelenEmail = kayitBilgileri.email!!
            Toast.makeText(activity,"gelen email: "+gelenEmail,Toast.LENGTH_SHORT).show()

            Log.e("omer", "gelen email: " + gelenEmail)
        }else{
            emailIleKayitIslemi=false
            telNo=kayitBilgileri.telNo!!
            verificationID = kayitBilgileri.verificationID!!
            gelenKod= kayitBilgileri.code!!

            Toast.makeText(activity,"gelen kod: "+gelenKod+"VerificationId:"+verificationID,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
/////////////////EVENTBUS /////////////////

}
