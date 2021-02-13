package com.med.ebeveynmedia.Profile


import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth

import com.med.ebeveynmedia.R

/**
 * A simple [Fragment] subclass.
 */
class SignOutFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        var alert=AlertDialog.Builder(this.activity!!)
            .setTitle("Ebeveyn Media'dan Çıkış Yap")
            .setMessage("Emin misiniz ?")
            .setPositiveButton("Çıkış Yap",object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {

                    FirebaseAuth.getInstance().signOut()
                    activity!!.finish()


                }

            })
            .setNegativeButton("İptal", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dismiss()
                }

            }).create()
        return alert
    }

}
