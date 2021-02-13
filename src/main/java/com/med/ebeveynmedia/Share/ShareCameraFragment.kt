package com.med.ebeveynmedia.Share


import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.med.ebeveynmedia.R

import com.med.ebeveynmedia.utils.EventbusDataEvents
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_camera.view.*
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream





class ShareCameraFragment : Fragment() {

    lateinit var cameraView: CameraView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.e("EEE","VEWOLUSTRLDU")
        cameraView.open()
        /* cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM)
        cameraView.mapGesture(Gesture.TAP,GestureAction.AUTO_FOCUS)*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_share_camera, container, false)

        cameraView= view.cameraView
        cameraView.setMode(Mode.PICTURE)


        view.imgfotoCek.setOnClickListener {
            cameraView.takePicture()
        }

        cameraView.addCameraListener(object : CameraListener(){

            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)
                var cekilenFotoAdi = System.currentTimeMillis().toString()+".jpg"

                //var cekilenFoto=File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/"+cekilenFotoAdi+".jpg")
                //var cekilenFoto = File(Environment.getExternalStorageDirectory().absolutePath +"/DCIM/"+cekilenFotoAdi+".jpg")
                
                var root= activity!!.applicationContext.getExternalFilesDir(null)
                var cekilenFotoPath = File(root.absolutePath +"/resimler")

                cekilenFotoPath.mkdirs()

                var cekilenFoto = File(cekilenFotoPath, cekilenFotoAdi)

                Log.e("EEE","on pıcture taken ")

                var dosyaOlustur=FileOutputStream(cekilenFoto)

                dosyaOlustur.write(result.data)
                dosyaOlustur.close()
                Log.e("EEE","DOSYA OLUSTURULDU: PATH"+cekilenFoto.absolutePath.toString())
                activity!!.anaLayout.visibility = View.GONE
                activity!!.fragmentContainerLayout.visibility=View.VISIBLE
                var transaction = activity!!.supportFragmentManager.beginTransaction()

                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(cekilenFoto.absolutePath.toString(),true))
                transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi")
                transaction.commit()

//gennymotionda burada sıkıntı çıkıyor ama pixelde buralar sorunsuz all alllaa

                Log.e("HATA2","cekilen resim buraya kaydedildi :"+cekilenFoto.absolutePath.toString())
            }
        })


        /*
        cameraView.addCameraListener(object : CameraListener(){

            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)
                var cekilenFotoAdi=System.currentTimeMillis()
                var cekilenFoto=File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/"+cekilenFotoAdi+".jpg")


              var dosyaOlustur=FileOutputStream(cekilenFoto) //FileOutputStrea iyi de foto cekemedkki dosya olussun..
                // baska emulator var mı onda bi deneyek gennymotion var ama telefonda denedim çekioyr paylaş'a atıyor ama paylaş'a basınca sıkıştırırken
                //genymotiın ac orda deneyek adım adım gidelm
                //program farklı tel farklı emülatörlerde farklı hatalar veriyor s6 var üyelik kısmından devam etmiyor mesela gennymotion'da da yarısında patlıyordu bidaha deneyelim
                dosyaOlustur.write(jpeg)
                dosyaOlustur.close()

                activity!!.anaLayout.visibility = View.GONE
                activity!!.fragmentContainerLayout.visibility=View.VISIBLE
                var transaction = activity!!.supportFragmentManager.beginTransaction()
                //hata nerdeydi
                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(cekilenFoto.absolutePath.toString(),true))
                transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi")
                transaction.commit()

//gennymotionda burada sıkıntı çıkıyor ama pixelde buralar sorunsuz all alllaa

                Log.e("HATA2","cekilen resim buraya kaydedildi :"+cekilenFoto.absolutePath.toString())

            }
        })
*/
//yeni bi emulatorde denesek mi apisi yuksek? olur

        return view
    }


    override fun onResume() {
        super.onResume()
        Log.e("hata2","camera fragmenti on resume")
        //cameraView.open()
        try {
            //cameraView.open()
        } catch (e: Exception) {
            Log.e("EEE", e.message)
        }

    }

    override fun onPause() {
        super.onPause()
        Log.e("hata2","camera fragmenti on pause")
        cameraView.close() //eyvallah
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("hata2","camera fragmenti on destroy")
      cameraView.destroy()
    }


}
