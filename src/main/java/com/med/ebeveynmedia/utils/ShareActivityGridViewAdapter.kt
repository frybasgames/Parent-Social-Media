package com.med.ebeveynmedia.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.med.ebeveynmedia.R
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_messages.view.*
import kotlinx.android.synthetic.main.tek_sutun_grid_resim.view.*
import java.lang.Exception

class ShareActivityGridViewAdapter(context: Context?, resource: Int, var klasordekiDosyalar: ArrayList<String>) :
    ArrayAdapter<String>(context, resource, klasordekiDosyalar) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var tek_sutun_resim= convertView
        lateinit var progressBar: ProgressBar
        lateinit var textView : TextView
        var inflater=LayoutInflater.from(context)

        if (tek_sutun_resim==null) {
            tek_sutun_resim = inflater.inflate(R.layout.tek_sutun_grid_resim, parent, false)


        }

        var imgView = tek_sutun_resim!!.imgTekSutunImage
        var imgProgres= tek_sutun_resim!!.progressBar
        var tvSure = tek_sutun_resim!!.textView

        var imgURL = klasordekiDosyalar.get(position)

        var dosyaYolu = klasordekiDosyalar.get(position)
        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))

        if (dosyaTuru.equals(".mp4")){
            tvSure.visibility=View.VISIBLE
            var retriver=MediaMetadataRetriever()
            retriver.setDataSource(context, Uri.parse("file://"+dosyaYolu))

            var videoSuresi = retriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            var videoSuresiLong = videoSuresi.toLong()


            tvSure.setText(converDuration(videoSuresiLong))
            UniversalImageLoader.setImage(imgURL,imgView,imgProgres,"file:/")


        }else{
            tvSure.visibility=View.GONE
            UniversalImageLoader.setImage(imgURL,imgView,imgProgres,"file:/")

        }


        return tek_sutun_resim!!
    }

    fun converDuration(duration: Long):String{
        val second = duration /1000 %60
        val minute = duration / (1000*60) % 60
        val hour = duration / (1000 * 60 * 60) % 24

        var time = ""
        if(hour>0){
            time = String.format("%02d:%02d:%02d",hour,minute,second)
        }else {
            time = String.format("%02d:%02d",minute,second)
        }
        return time
    }


}