package com.example.indikascam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.adapter.CommentAdapter
import com.example.indikascam.databinding.FragmentHasilPencarianBinding
import com.example.indikascam.dialog.ReviewUlangDialog

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

    private var commentLayoutManager: RecyclerView.LayoutManager? = null
    private var commentAdapter: RecyclerView.Adapter<CommentAdapter.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHasilPencarianBinding.inflate(inflater, container, false)

        setupHasilPencarianFragment(args.searchNumber)
        binding.hasilPencarianFragmentBtnLapor.setOnClickListener {
            val action = HasilPencarianFragmentDirections.actionHasilPencarianFragmentToLaporFragment(args.searchNumber)
            Navigation.findNavController(binding.root).navigate(action)
        }
        return binding.root
    }

    private fun setupHasilPencarianFragment(searchNumber: Array<String>) {
        //searchNumber[0] == nomor yang dicari, searchNumber[1] == jenis nomor
        if (searchNumber[0].isNotBlank()){
            binding.hasilPencarianFragmentTvNomor.text = searchNumber[0]
        } else{
            binding.hasilPencarianFragmentTvNomor.text = "Tidak Ditemukan"
        }

        if(searchNumber[1] == "0") {
            binding.hasilPencarianFragmentTvCaptionNomor.text = "Nomor Telepon"
        }else {
            binding.hasilPencarianFragmentTvCaptionNomor.text = "Nomor Rekening"
            binding.hasilPencarianFragmentBtnBlokir.visibility = View.GONE
        }

        val rcvRiwayat = binding.hasilPencarianFragmentRcvRiwayat
        commentLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rcvRiwayat.layoutManager = commentLayoutManager
        commentAdapter = CommentAdapter()
        rcvRiwayat.adapter = commentAdapter

        binding.hasilPencarianFragmentIvReviewUlang.setOnClickListener {
            val reviewUlangDialog = ReviewUlangDialog(searchNumber[0], searchNumber[1])
            reviewUlangDialog.show(requireActivity().supportFragmentManager, "Welcome Dialog")
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