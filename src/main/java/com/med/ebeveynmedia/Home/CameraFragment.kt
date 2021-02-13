package com.med.ebeveynmedia.Home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.EventbusDataEvents
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.gesture.Gesture
import com.otaliastudios.cameraview.gesture.GestureAction
import kotlinx.android.synthetic.main.fragment_share_camera.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CameraFragment : Fragment() {

    var myCamera:CameraView?=null
    var kameraIzniVerildiMi=false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater?.inflate(R.layout.fragment_camera, container, false)

     //   myCamera=view!!.cameraView
//        myCamera!!.mapGesture(Gesture.PINCH,GestureAction.ZOOM)
  //      myCamera!!.mapGesture(Gesture.TAP,GestureAction.AUTO_FOCUS)

       return view
    }


   /* override fun onResume() {
        super.onResume()
        Log.e("hata2","camera fragmenti on resume")
        //cameraView.open()

            if (kameraIzniVerildiMi==true) {
                Log.e("hataOpen", "kameraizinverildi")
               // myCamera.open()

            }

    }

    override fun onPause() {
        super.onPause()
        Log.e("hata2","camera fragmenti on pause")

      //  myCamera!!.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("hata2","camera fragmenti on destroy")
        myCamera!!.destroy()
    }

    /////////////////EVENTBUS /////////////////
    @Subscribe(sticky = true)
    internal fun onKameraIzinEvent(IzinDurumu : EventbusDataEvents.KameraIzinBilgisiGonder){
        kameraIzniVerildiMi=IzinDurumu.KameraIzniVerildiMi!!
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }
*/
}