package com.med.ebeveynmedia.utils

import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import com.iceteck.silicompressorr.SiliCompressor
import com.med.ebeveynmedia.Profile.YukleniyorFragment
import com.med.ebeveynmedia.Share.ShareNextFragment
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class DosyaIslemleri {

    companion object {
        fun klasordekiDosyalariGetir(klasorAdi:String):ArrayList<String> {


            var tumDosyalar = ArrayList<String>()

            var file = File(klasorAdi)

            //parametre olarak gonderdigimiz klasordeki tum dosyalar alınız
            var klasordekiTumdosyalar = file.listFiles()

            //parametre olarak gonderdiğimiz klasor yolunda eleman olup olmadıgı kontrol edildi.
            if (klasordekiTumdosyalar != null) {
                //galeriden getirilen resimlerin tarihe göre sondan başa listelenmesi
                if (klasordekiTumdosyalar.size>1){
                    Arrays.sort(klasordekiTumdosyalar,object : Comparator<File>{
                        override fun compare(o1: File?, o2: File?): Int {
                            if (o1!!.lastModified()>o2!!.lastModified()){
                                return -1
                            }else return 1
                        }

                    })
                }

                for (i in 0..klasordekiTumdosyalar.size-1) {

                    //sadece dosyalara bakılır
                    if (klasordekiTumdosyalar[i].isFile) {

                        //okudugumuz dosyanın telefondaki yeri ve adını içerir

                        //files://root/logo.png
                        var okunanDosyaYolu = klasordekiTumdosyalar[i].absolutePath
                        var dosyaTuru = okunanDosyaYolu.substring(okunanDosyaYolu.lastIndexOf("."))

                        if (dosyaTuru.equals(".jpg") || dosyaTuru.equals(".jpeg") || dosyaTuru.equals(".png") || dosyaTuru.equals(".mp4")) {
                            tumDosyalar.add(okunanDosyaYolu)
                        }
                    }

                }

            }

            return tumDosyalar

            }

        fun compressResimDosya(fragment: Fragment, secilenResimYolu: String?) {

            ResimCompressAsyncTask(fragment).execute(secilenResimYolu)



        }

        fun compressVideoDosya(fragment: Fragment, secilenDosyaYolu: String) {

            VideoCompressAsyncTask(fragment).execute(secilenDosyaYolu)

        }
    }

    internal class VideoCompressAsyncTask(fragment: Fragment):AsyncTask<String,String,String>(){

        var myFragment = fragment
        var compressFragment = YukleniyorFragment()

        override fun onPreExecute() {

            compressFragment.show(myFragment.activity!!.supportFragmentManager,"compressDialogBasladi")
            compressFragment.isCancelable=false
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String? {

            var yeniOlusanDosyaninKlasoru=File(Environment.getExternalStorageDirectory().absolutePath+"/DCIM/compressedVideo/")

            if (yeniOlusanDosyaninKlasoru.isDirectory || yeniOlusanDosyaninKlasoru.mkdirs()){
               var yeniDosyaninPath=SiliCompressor.with(myFragment.context).compressVideo(params[0],yeniOlusanDosyaninKlasoru.path)
                return yeniDosyaninPath
            }

            return null
        }

        override fun onPostExecute(yeniDosyaninPath: String?) {

            if (!yeniDosyaninPath.isNullOrEmpty()){

                compressFragment.dismiss()
                (myFragment as ShareNextFragment).uploadStorage(yeniDosyaninPath)
            }

            super.onPostExecute(yeniDosyaninPath)
        }


    }

    internal class ResimCompressAsyncTask(fragment: Fragment):AsyncTask<String,String,String>(){

        var myFragment=fragment
        var compressFragment=YukleniyorFragment()


        override fun onPreExecute() {


            compressFragment.show(myFragment.activity!!.supportFragmentManager,"compressDialogBasladi")
            compressFragment.isCancelable=false

            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): String {

            var yeniOlusanDosyaninKlasoru=File(myFragment.activity!!.applicationContext.getExternalFilesDir(null).absolutePath+"/DCIM/compressed/")
            var yeniDosyaYolu = SiliCompressor.with(myFragment.context).compress(params[0],yeniOlusanDosyaninKlasoru)


            //sıkıştırılarak oluşturulmuş yeni dosyanın yolunu verir
            return yeniDosyaYolu

        }

        override fun onPostExecute(filePath: String?) {

            Log.e("HATA", "yeni DOSYANIN PATHI : "+filePath)
            compressFragment.dismiss()
            (myFragment as ShareNextFragment).uploadStorage(filePath)
            super.onPostExecute(filePath)
        }
///storage/emulated/0/Android/data/com.med.ebeveynmedia/files/resimler/1589758924459.jpg

    }
}




