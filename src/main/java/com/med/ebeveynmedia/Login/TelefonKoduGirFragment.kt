package com.med.ebeveynmedia.Login


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.EventbusDataEvents
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.*
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.view.*
import kotlinx.android.synthetic.main.fragment_telefon_kodu_gir.view.etOnayKodu
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.concurrent.TimeUnit


class TelefonKoduGirFragment : Fragment() {

    var gelenTelNo = ""
    lateinit var mCallbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationID = ""
    var gelenKod = ""
    lateinit var  progressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view= inflater.inflate(R.layout.fragment_telefon_kodu_gir, container, false)
        view.tvKullaniciTelNo.setText(gelenTelNo)
        progressBar=view.pbTelNoOnayla
        setupCallback()

        view.btnTelKodIleri.setOnClickListener {
            if(gelenKod.equals(view.etOnayKodu.text.toString())){
                EventBus.getDefault().postSticky(EventbusDataEvents.KayitBilgileriniGonder(gelenTelNo,null,verificationID,gelenKod,false))
                var transaction=activity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.loginContainer,KayitFragment())
                transaction.addToBackStack("kayitFragmentEklendi")
                transaction.commit()
            }else{
                Toast.makeText(activity,"kod hatali",Toast.LENGTH_SHORT).show()

            }
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            gelenTelNo,
            60,
            TimeUnit.SECONDS,
            this!!.activity!!,
            mCallbacks)

        return view
    }

    private fun setupCallback() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
               if (!credential.smsCode.isNullOrEmpty())
                   gelenKod = credential.smsCode!!
                progressBar.visibility=View.INVISIBLE
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.e("hata","hata çıktı:"+e.message)
                progressBar.visibility=View.INVISIBLE
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                verificationID =verificationId!!
                progressBar.visibility=View.VISIBLE

            }
        }
    }

    @Subscribe (sticky = true)
    internal fun onTelefonNoEvent(kayitBilgileri : EventbusDataEvents.KayitBilgileriniGonder){
        gelenTelNo=kayitBilgileri.telNo!!
        Log.e("omer","gelen tel no: "+gelenTelNo )
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
