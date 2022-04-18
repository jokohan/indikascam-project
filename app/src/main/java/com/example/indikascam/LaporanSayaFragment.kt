package com.example.indikascam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.adapter.CommentAdapter
import com.example.indikascam.databinding.FragmentHasilPencarianBinding
import com.example.indikascam.databinding.FragmentLaporanSayaBinding
import com.example.indikascam.model.CommentItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LaporanSayaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LaporanSayaFragment : Fragment() {
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

    private var _binding: FragmentLaporanSayaBinding? = null
    private val binding get() = _binding!!

    private val commentList = ArrayList<CommentItem>()
    private val commentListAdapter = CommentAdapter(commentList){
        val action = LaporanSayaFragmentDirections.actionLaporanSayaFragmentToDetailLaporanSayaFragment(it)
        Navigation.findNavController(binding.root).navigate(action)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLaporanSayaBinding.inflate(inflater, container, false)

        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = SimpleDateFormat("dd MMM yyyy H:m:s", Locale("in", "ID"))
        for(i in 0..19){
            val list = CommentItem("Laporan " +
                    "${i+1}", "Type-Report", R.drawable.ic_profil, sdf.format(c.time),"Status Laporan")
            commentList.add(list)
        }

        binding.laporanSayaFragmentRcvDaftarLaporan.adapter = commentListAdapter
        binding.laporanSayaFragmentRcvDaftarLaporan.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)



        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LaporanSayaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LaporanSayaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}