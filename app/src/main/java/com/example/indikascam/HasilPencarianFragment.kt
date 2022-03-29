package com.example.indikascam

import android.Manifest.permission.*
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.adapter.CommentAdapter
import com.example.indikascam.databinding.FragmentHasilPencarianBinding
import com.example.indikascam.dialog.ReviewUlangDialog
import com.example.indikascam.model.CommentItem


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HasilPencarianFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HasilPencarianFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    private var _binding: FragmentHasilPencarianBinding? = null
    private val binding get() = _binding!!

    private val args: HasilPencarianFragmentArgs by navArgs()

    private val commentList = ArrayList<CommentItem>()
    private val commentListAdapter = CommentAdapter(commentList)

    private var findDevicePhoneNumber = ""

    private var titles = arrayOf("Ucok", "Anonim", "Doe", "Butet")

    private var description= arrayOf("Scam", "Scam", "Spam", "Robocall")

    private var icons = intArrayOf(R.drawable.business_partnership_illustration, R.drawable.ic_profil, R.drawable.configuration_protection, R.drawable.thank_you, R.drawable.ic_profil)

    private var tanggal = arrayOf("29 Mar 2022", "27 Jan 2022", "19 Des 2021", "18 Des 2021", " 17 Des 2021")

    private var status = arrayOf("Pending", "Ditolak", "Diterima", "Review Ulang", "Diterima")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHasilPencarianBinding.inflate(inflater, container, false)

        setupHasilPencarianFragment(args.searchNumber)
        binding.hasilPencarianFragmentBtnLapor.setOnClickListener {
            val action =
                HasilPencarianFragmentDirections.actionHasilPencarianFragmentToLaporFragment(args.searchNumber)
            Navigation.findNavController(binding.root).navigate(action)
        }



        return binding.root
    }


    private fun getNumber() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                READ_PHONE_NUMBERS
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {
            val telephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val phoneNumber = telephonyManager.line1Number
            findDevicePhoneNumber = phoneNumber
            return
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                Log.d("TAGc", findDevicePhoneNumber)

                val telephonyManager = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (ActivityCompat.checkSelfPermission(requireContext(), READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        READ_PHONE_NUMBERS
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        READ_PHONE_STATE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }else{

                    val phoneNumber = telephonyManager.line1Number
                    findDevicePhoneNumber = phoneNumber
                }
            }
            else -> throw IllegalStateException("Unexpected value: $requestCode")
        }
    }

    private fun setupHasilPencarianFragment(searchNumber: Array<String>) {
        //searchNumber[0] == nomor yang dicari, searchNumber[1] == jenis nomor
        if (searchNumber[0].isNotBlank()) {
            binding.hasilPencarianFragmentTvNomor.text = searchNumber[0]
        } else {
            binding.hasilPencarianFragmentTvNomor.text = "Tidak Ditemukan"
        }

        if (searchNumber[1] == "0") {
            binding.hasilPencarianFragmentTvCaptionNomor.text = "Nomor Telepon"
        } else {
            binding.hasilPencarianFragmentTvCaptionNomor.text = "Nomor Rekening"
            binding.hasilPencarianFragmentBtnBlokir.visibility = View.GONE
        }
        if(commentList.size == 0){
            var i = 0
            for(title in titles){
                val list = CommentItem(titles[i], description[i], icons[i], tanggal[i], status[i])
                commentList.add(list)
                i++
            }
        }

        binding.hasilPencarianFragmentRcvRiwayat.adapter = commentListAdapter
        binding.hasilPencarianFragmentRcvRiwayat.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.hasilPencarianFragmentIvReviewUlang.setOnClickListener {
            getNumber()
            if(findDevicePhoneNumber == searchNumber[0]){
                val laporan = ArrayList<CommentItem>()
                for (item in commentList){
                    if (item.status == "Pending" || item.status == "Diterima") {
                        laporan.add(item)
                    }
                }
                val reviewUlangDialog = ReviewUlangDialog(searchNumber[0], searchNumber[1], laporan)
                reviewUlangDialog.show(requireActivity().supportFragmentManager, "Review Ulang Dialog")
            } else{
                Toast.makeText(context, "Maaf Anda tidak bisa melakukan review ulang terhadap nomor ini karena nomor telepon yang digunakan berbeda", Toast.LENGTH_LONG).show()
            }
        }

        binding.hasilPencarianFragmentBtnBlokir.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            if(binding.hasilPencarianFragmentBtnBlokir.text == "Blokir"){
                if(searchNumber[0].isEmpty()){
                    Toast.makeText(context, "Silahkan cari nomor telepon yang ingin diblokir", Toast.LENGTH_SHORT).show()
                } else{

                    builder.setTitle("Blokir")
                    builder.setMessage("Anda ingin blokir panggilan masuk dari ${searchNumber[0]}?")
                    builder.setIcon(R.drawable.ic_phone_disabled)

                    builder.setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        binding.hasilPencarianFragmentBtnBlokir.text = "Buka Blokir"
                        binding.hasilPencarianFragmentBtnBlokir.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_phone_enabled,0,0)
                        binding.hasilPencarianFragmentBtnBlokir.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                    })

                    builder.setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    builder.show()
                }
            } else{
                if(searchNumber[0].isEmpty()){
                    Toast.makeText(context, "Silahkan cari nomor telepon yang ingin diblokir", Toast.LENGTH_SHORT).show()
                } else{
                    builder.setTitle("Blokir")
                    builder.setMessage("Batal blokir panggilan masuk dari ${searchNumber[0]}?")
                    builder.setIcon(R.drawable.ic_phone_disabled)

                    builder.setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                        binding.hasilPencarianFragmentBtnBlokir.text = "Blokir"
                        binding.hasilPencarianFragmentBtnBlokir.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_phone_disabled,0,0)
                        binding.hasilPencarianFragmentBtnBlokir.compoundDrawableTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
                    })

                    builder.setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    builder.show()
                }
            }


        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HasilPencarianFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HasilPencarianFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }
}
