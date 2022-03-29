package com.example.indikascam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.example.indikascam.databinding.FragmentLaporBinding
import com.example.indikascam.databinding.FragmentPengaturanTingkatProteksiBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PengaturanTingkatProteksiFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PengaturanTingkatProteksiFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentPengaturanTingkatProteksiBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPengaturanTingkatProteksiBinding.inflate(inflater, container, false)
        binding.pengaturanTingkatProteksiFragmentRbProteksiTinggi.setOnCheckedChangeListener { compoundButton, b ->
            if(binding.pengaturanTingkatProteksiFragmentRbProteksiTinggi.isChecked){
                binding.pengaturanTingkatProteksiFragmentBtnTerapkanProteksi.text = "Rp 14k/bulan"
            } else{
                binding.pengaturanTingkatProteksiFragmentBtnTerapkanProteksi.text = "Terapkan"
            }
        }
        binding.pengaturanTingkatProteksiFragmentBtnTerapkanProteksi.setOnClickListener {
            if(binding.pengaturanTingkatProteksiFragmentBtnTerapkanProteksi.text == "Terapkan"){
                Navigation.findNavController(binding.root).navigateUp()
            } else{
                Navigation.findNavController(binding.root).navigate(R.id.action_pengaturanTingkatProteksiFragment_to_billingFragment)
            }
        }

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PengaturanTingkatProteksiFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PengaturanTingkatProteksiFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}