package com.example.indikascam.fragmentsProfile

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.R
import com.example.indikascam.adapter.ProofAdapter
import com.example.indikascam.databinding.FragmentMyReportDetailBinding
import com.example.indikascam.dialog.DialogZoomInProof
import com.example.indikascam.modelsRcv.Proof

class MyReportDetailFragment : Fragment() {
    private var _binding : FragmentMyReportDetailBinding? = null
    private val binding get() = _binding!!

    private val args: MyReportDetailFragmentArgs by navArgs()

    private val proofList = ArrayList<Proof>()
    private val proofAdapter = ProofAdapter(proofList, "MyReportDetailFragment"){
        val string = it.split(" ").toTypedArray()
        val intention = string[0]
        val position = string[1].toInt()
        if(intention == "zoomFile"){
            if (proofList[position].isItImage){
                val zoomInProofDialog = DialogZoomInProof(proofList[position].image, proofList[position].title, proofList[position].isItImage)
                zoomInProofDialog.show(parentFragmentManager, "Zoom In Proof")
            } else{
                val pdfIntent = Intent(Intent.ACTION_VIEW)
                pdfIntent.setDataAndType(proofList[position].image, "application/pdf")
                pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(pdfIntent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReportDetailBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.myReportDetailFragmentTvReportNumber.text = args.reportNumber.toString()

        for(i in 0..5){
            val imageUri: Uri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.ic_flag))
                .appendPath(resources.getResourceTypeName(R.drawable.ic_flag))
                .appendPath(resources.getResourceEntryName(R.drawable.ic_flag))
                .build()
            val newItem = Proof(imageUri, proofList.size.toString(),true)
            proofList.add(newItem)
        }
        binding.myReportDetailFragmentTvPhoneNumber.text = proofList.size.toString()

        binding.myReportDetailFragmentRcvProof.adapter = proofAdapter
        binding.myReportDetailFragmentRcvProof.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        return view
    }

}