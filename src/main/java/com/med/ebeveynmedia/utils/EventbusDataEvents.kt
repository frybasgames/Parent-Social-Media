package com.med.ebeveynmedia.utils

import com.med.ebeveynmedia.Model.Users

class EventbusDataEvents{
    internal class KayitBilgileriniGonder(var telNo:String?, var email:String?, var verificationID:String?, var code:String?,var emailkayit:Boolean)

    internal class KullaniciBilgileriniGonder(var kullanici:Users?)

    internal class PaylasilacakResmiGonder(var dosyaYolu:String?, var dosyaTuruResimMi:Boolean?)

    internal class GalerySecilenDosyaYolunuGonder(var dosyaYolu:String?)

    internal class KameraIzinBilgisiGonder(var KameraIzniVerildiMi: Boolean?)

    internal class YorumYapilacakGonderininIDsiniGonder(var gonderiID:String?)

}
