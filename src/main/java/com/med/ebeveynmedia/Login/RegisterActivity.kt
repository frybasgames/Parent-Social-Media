package com.med.ebeveynmedia.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.med.ebeveynmedia.Home.HomeActivity
import com.med.ebeveynmedia.Model.Users
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.EventbusDataEvents
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus

class RegisterActivity : AppCompatActivity(), FragmentManager.OnBackStackChangedListener{

    lateinit var manager:FragmentManager
    lateinit var mRef:DatabaseReference
    lateinit var mAuth : FirebaseAuth
    lateinit var mAuthListener : FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setupAuthListener()

        mAuth=FirebaseAuth.getInstance()
        mRef=FirebaseDatabase.getInstance().reference

        manager=supportFragmentManager
        manager.addOnBackStackChangedListener(this)

        init()
    }

    private fun init() {

        tvGirisYap.setOnClickListener {
            var intent = Intent(this@RegisterActivity,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        tvEposta.setOnClickListener {
            viewTelefon.visibility= View.INVISIBLE
            viewEposta.visibility=View.VISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType=InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etGirisYontemi.setHint("E-Posta")

            btnIleri.isEnabled=false
            btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
            btnIleri.setBackgroundResource(R.drawable.register_button)
        }
        tvTelefon.setOnClickListener {
            viewTelefon.visibility= View.VISIBLE
            viewEposta.visibility=View.INVISIBLE
            etGirisYontemi.setText("")
            etGirisYontemi.inputType=InputType.TYPE_CLASS_NUMBER
            etGirisYontemi.setHint("Telefon")

            btnIleri.isEnabled=false
            btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
            btnIleri.setBackgroundResource(R.drawable.register_button)
        }

        etGirisYontemi.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length >= 10){
                    btnIleri.isEnabled=true
                    btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.beyaz))
                    btnIleri.setBackgroundResource(R.drawable.register_button_aktif)
                }else{
                    btnIleri.isEnabled=false
                    btnIleri.setTextColor(ContextCompat.getColor(this@RegisterActivity,R.color.sonukmavi))
                    btnIleri.setBackgroundColor(ContextCompat.getColor(this@RegisterActivity,R.color.beyaz))
                    btnIleri.setBackgroundResource(R.drawable.register_button)
                }
            }

        })

        btnIleri.setOnClickListener {

            if(etGirisYontemi.hint.toString().equals("Telefon")){

                var ceptelefonuKullanimdaMi=false

                if(isValidTelefon(etGirisYontemi.text.toString())) {

                    mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if(p0!!.getValue()!=null){
                                for(user in p0!!.children){
                                    var okunanKullanici=user.getValue(Users::class.java)
                                    if (okunanKullanici!!.phone_number!!.equals(etGirisYontemi.text.toString())){
                                        Toast.makeText(this@RegisterActivity,"Telefon numarasi Kullanimda",Toast.LENGTH_SHORT).show()
                                        ceptelefonuKullanimdaMi=true
                                        break
                                    }
                                }

                                if (ceptelefonuKullanimdaMi==false){
                                    loginRoot.visibility=View.GONE
                                    loginContainer.visibility=View.VISIBLE

                                    var transaction = supportFragmentManager.beginTransaction()
                                    transaction.replace(R.id.loginContainer, TelefonKoduGirFragment())
                                    transaction.addToBackStack("telefonKoduGirFragmentEklendi")
                                    transaction.commit()

                                    EventBus.getDefault().postSticky(
                                        EventbusDataEvents.KayitBilgileriniGonder(
                                            etGirisYontemi.text.toString(),
                                            null,
                                            null,
                                            null,
                                            false
                                        )
                                    )
                                }


                            }

                        }

                    })


                }else{
                    Toast.makeText(this,"lütfe geçerli bir telefon numarası giriniz",Toast.LENGTH_SHORT).show()
                }



                }else{
                if(isValidEmail(etGirisYontemi.text.toString())){

                    var emailKullanimdaMi=false
                    Log.e("EEE","BURADA MAIL"+etGirisYontemi.text.toString())
                    mRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            if(p0!!.getValue()!=null){
                                Log.e("EEE","BURADA NULL DEGIL"+etGirisYontemi.text.toString())
                                for(user in p0!!.children){ //piksel'i bidaha denesek mi CALISMIO CAMERA ORDA BURDA LOGLARI GOSTERMEMESI GARIP
                                    var okunanKullanici=user.getValue(Users::class.java)
                                    if(okunanKullanici!!.email!!.equals(etGirisYontemi.text.toString())){
                                        Toast.makeText(this@RegisterActivity,"Email Kullanılıyor",Toast.LENGTH_SHORT).show()
                                        emailKullanimdaMi=true
                                        break
                                    }
                                }//alette internet yokmus
                                Log.e("EEE","BURADA DONGUDEN CIKILDI"+etGirisYontemi.text.toString())
                                if (emailKullanimdaMi==false){
                                    loginRoot.visibility=View.GONE
                                    loginContainer.visibility=View.VISIBLE
                                    Log.e("EEE","BURADA fragment acılacak"+etGirisYontemi.text.toString())
                                    var transaction=supportFragmentManager.beginTransaction()
                                    transaction.replace(R.id.loginContainer,KayitFragment())
                                    transaction.addToBackStack("emailGirisFragmentEklendi")
                                    transaction.commit()

                                    EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(null,etGirisYontemi.text.toString(),null,null,true))


                                }
                            }else{
                                Log.e("EEE","BURADA dbde kayıt yoksa bişe yapmıyor")
                            }
                        }

                    })

                }else{
                    Toast.makeText(this,"lütfe geçerli bir email giriniz",Toast.LENGTH_SHORT).show()

                }

            }
        }
    }

    override fun onBackStackChanged() {

        val elemanSayisi = manager.backStackEntryCount

        if (elemanSayisi==0) {
            loginRoot.visibility=View.VISIBLE
        }

    }
    fun isValidEmail(kontrolEdilecekMail:String):Boolean{
        if (kontrolEdilecekMail == null){
            return false
        }
        return android.util.Patterns.EMAIL_ADDRESS.matcher(kontrolEdilecekMail).matches()
    }
    fun isValidTelefon(kontrolEdilecekTelefon:String):Boolean{
        if (kontrolEdilecekTelefon == null || kontrolEdilecekTelefon.length > 14){
                return false
        }
        return android.util.Patterns.PHONE.matcher(kontrolEdilecekTelefon).matches()
    }

    private fun setupAuthListener() {
        mAuthListener=object  : FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                var user= FirebaseAuth.getInstance().currentUser
                if (user != null){
                    var intent = Intent(this@RegisterActivity, HomeActivity::class.java)
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
