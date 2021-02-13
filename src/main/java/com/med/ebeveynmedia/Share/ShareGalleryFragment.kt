package com.med.ebeveynmedia.Share


import android.content.Context
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter

import android.net.Uri
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.med.ebeveynmedia.R
import com.med.ebeveynmedia.utils.*

import kotlinx.android.synthetic.main.activity_share.*
import kotlinx.android.synthetic.main.fragment_share_gallery.*
import kotlinx.android.synthetic.main.fragment_share_gallery.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


/**
 * A simple [Fragment] subclass.
 */
class ShareGalleryFragment : Fragment() {

    var secilenDosyaYolu:String?=null
    var dosyaTuruResimMi:Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        var view =inflater.inflate(com.med.ebeveynmedia.R.layout.fragment_share_gallery, container, false)

        var klasorPaths=ArrayList<String>()
        var klasorAdlari=ArrayList<String>()

        var root = Environment.getExternalStorageDirectory().path


        var test= root+"/DCIM/TestKlasor"
        var kameraResimleri = root+"/DCIM/Camera"
        var indirilenResimler = root+"/Download"
        var whatsappResimleri = root+"/WhatsApp/Media/WhatsApp Images"

        klasorPaths.add(test)
        klasorPaths.add(kameraResimleri)
        klasorPaths.add(indirilenResimler)
        klasorPaths.add(whatsappResimleri)

        klasorAdlari.add(("Test"))
        klasorAdlari.add("Kamera")
        klasorAdlari.add("Indirilenler")
        klasorAdlari.add("Whatsapp")

        var spinnerArrayAdapter=ArrayAdapter(activity,android.R.layout.simple_spinner_item,klasorAdlari)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        view.spnKlasorAdlari.adapter=spinnerArrayAdapter
        view.spnKlasorAdlari.setSelection(0)
        view.spnKlasorAdlari.onItemSelectedListener=object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.e("hata","dosyaYolu:"+DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(0)))
                //  setupGridView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position)))
                setupRecyclerView(DosyaIslemleri.klasordekiDosyalariGetir(klasorPaths.get(position)))
            }

        }

            view.tvIleriButton.setOnClickListener {
            activity!!.anaLayout.visibility = View.GONE
            activity!!.fragmentContainerLayout.visibility=View.VISIBLE
            var transaction = activity!!.supportFragmentManager.beginTransaction()
            EventBus.getDefault().postSticky(EventbusDataEvents.PaylasilacakResmiGonder(secilenDosyaYolu,dosyaTuruResimMi))
            videoView.stopPlayback()
            transaction.replace(R.id.fragmentContainerLayout,ShareNextFragment())

            transaction.addToBackStack("shareNextFragmentEklendi")
            transaction.commit()

        }


       return view
    }

    private fun setupRecyclerView(klasordekiDosyalar: ArrayList<String>) {

        var recyclerViewAdapter= ShareGaleryRecyclerAdapter(klasordekiDosyalar,this.activity!!)
        recyclerViewDosyalar.adapter=recyclerViewAdapter

        var layoutManager= GridLayoutManager(this.activity,4)
        recyclerViewDosyalar.layoutManager=layoutManager

        recyclerViewDosyalar.setHasFixedSize(true);
        recyclerViewDosyalar.setItemViewCacheSize(30);
        recyclerViewDosyalar.setDrawingCacheEnabled(true);
        recyclerViewDosyalar.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        //ilk açılldığında ilk dosya gosterilir
        secilenDosyaYolu=klasordekiDosyalar.get(0)
        resimVeyaVideoGoster(secilenDosyaYolu!!)
    }

  /*  fun setupGridView(secilenKlasordekiDosyalar: ArrayList<String>){
        var gridAdapter=ShareActivityGridViewAdapter(activity,R.layout.tek_sutun_grid_resim,secilenKlasordekiDosyalar)

        recyclerViewDosyalar.adapter=gridAdapter
        //ilk açılldığında ilk dosya gosterilir
        secilenDosyaYolu=secilenKlasordekiDosyalar.get(0)
        resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(0))

        recyclerViewDosyalar.setOnItemClickListener( AdapterView.OnItemClickListener { adapterView, view, position, arg3 ->
           secilenDosyaYolu= secilenKlasordekiDosyalar.get(position)

            resimVeyaVideoGoster(secilenKlasordekiDosyalar.get(position))
        })
       /* gridResimler.setOnClickListener(object : AdapterView.OnItemClickListener{
           override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                UniversalImageLoader.setImage(secilenKlasordekiDosyalar.get(position),imgBuyukResim,null,"")
            }

        })*/
    }
*/

    private fun resimVeyaVideoGoster(dosyaYolu: String) {

        var dosyaTuru = dosyaYolu.substring(dosyaYolu.lastIndexOf("."))

        if (dosyaTuru != null) {

            if (dosyaTuru != null && dosyaTuru.equals(".mp4")) {


                videoView.visibility = View.VISIBLE
                imgCropView.visibility = View.GONE
                dosyaTuruResimMi=false
                videoView.setVideoURI(Uri.parse("file://"+dosyaYolu))
                videoView.start()

            }else{
                videoView.visibility=View.GONE
                imgCropView.visibility=View.VISIBLE
                dosyaTuruResimMi=true
                UniversalImageLoader.setImage(dosyaYolu,imgCropView,null,"file://")
            }

        }

    }

    override fun onResume() {
        super.onResume()
        Log.e("hata2","camera fragmenti on resume")

    }

    override fun onPause() {
        super.onPause()
        Log.e("hata2","camera fragmenti on pause")

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("hata2","camera fragmenti on destroy")

    }

    /////////////////EVENTBUS /////////////////
    @Subscribe
    internal fun onSecilenDosyaEvent(secilenDosya : EventbusDataEvents.GalerySecilenDosyaYolunuGonder){
        secilenDosyaYolu=secilenDosya!!.dosyaYolu


        resimVeyaVideoGoster(secilenDosyaYolu!!)
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
