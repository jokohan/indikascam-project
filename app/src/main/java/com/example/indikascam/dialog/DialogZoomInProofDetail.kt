package com.example.indikascam.dialog

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.indikascam.R

class DialogZoomInProofDetail(private val image: Bitmap?,
                        private val title: String,
                        private val isItImage: Boolean) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.dialog_zoom_in_proof, container, false)

        if (isItImage){
            rootView.findViewById<TextView>(R.id.dialogZoomInProof_tv_title).text = title
            Log.i("asdasdasd", image.toString())
            rootView.findViewById<ImageView>(R.id.dialogZoomInProof_iv_image).setImageBitmap(image)
        } else{
            dismiss()
        }

        return rootView
    }
}