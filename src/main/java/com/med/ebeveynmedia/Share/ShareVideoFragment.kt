package com.med.ebeveynmedia.Share


import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.EventbusDataEvents
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.VideoResult
import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_video.*
import kotlinx.android.synthetic.main.fragment_share_video.view.*
import kotlinx.android.synthetic.main.fragment_share_video.view.imgVideoCek
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * A simple [Fragment] subclass.
 */
class ShareVideoFragment : Fragment() {

    lateinit var videoView: CameraView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =inflater.inflate(R.layout.fragment_share_video, container, false)

        videoView=view.videoView

        var olusacakVideoDosyaAdi = System.currentTimeMillis()
        var olusacakVideoDosya = File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/"+olusacakVideoDosyaAdi+".mp4")


        videoView.addCameraListener(object : CameraListener(){
            override fun onVideoTaken(result: VideoResult) {
                super.onVideoTaken(result)
                activity!!.anaLayout.visibility = View.GONE
                activity!!.fragmentContainerLayout.visibility=View.VISIBLE
                var transaction = activity!!.supportFragmentManager.beginTransaction()
                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(result.file!!.absolutePath.toString(),false))
                transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi")
                transaction.commit()
            }
        })


       /* videoView.addCameraListener(object : CameraListener(){

            override fun onVideoTaken(video: File?) {
                super.onVideoTaken(video)

                activity!!.anaLayout.visibility = View.GONE
                activity!!.fragmentContainerLayout.visibility=View.VISIBLE
                var transaction = activity!!.supportFragmentManager.beginTransaction()
                EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(video!!.absolutePath.toString(),false))
                transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())
                transaction.addToBackStack("shareNextFragmentEklendi")
                transaction.commit()
            }

        })
*/
        view.imgVideoCek.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                if(event!!.action==MotionEvent.ACTION_DOWN){

                    videoView.
                        //bunun yeni adını hatırlıonmu bu kısımları eskiye göre yapıp çalıştırmıştım yeni haline bakaym
                    videoView.takeVideo(olusacakVideoDosya)
                    Toast.makeText(activity,"video kaydediliyor",Toast.LENGTH_SHORT).show()
                    return true

                }else if (event!!.action==MotionEvent.ACTION_UP){
                    Toast.makeText(activity,"video kaydedildi",Toast.LENGTH_SHORT).show()
                    videoView.stopVideo()
                    return true
                }

                return false

            }

        })

        return view
//bulamadım ama write kısmında hata veriyordu kamera calısıodu ama dosyaya mı yazmıyodu videosu var gösteriyim bu şekilde güncelleyelm ozman kütüphaneyi önce
    }

    override fun onResume() {
        super.onResume()
        Log.e("hata2","video fragmenti on resume")
    videoView.open()
    }

    override fun onPause() {
        super.onPause()
        Log.e("hata2","video fragmenti on pause")
      videoView.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("hata2","video fragmenti on destroy")
      videoView.destroy()
    }


}
