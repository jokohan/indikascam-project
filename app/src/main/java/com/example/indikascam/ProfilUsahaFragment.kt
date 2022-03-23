package com.example.indikascam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.adapter.ListUsahaAdapter
import com.example.indikascam.databinding.FragmentLaporBinding
import com.example.indikascam.databinding.FragmentProfilUsahaBinding
import com.example.indikascam.dialog.DaftarUsahaDialog
import com.example.indikascam.model.UsahaItem

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfilUsahaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfilUsahaFragment : Fragment() {
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

    private var _binding: FragmentProfilUsahaBinding? = null
    private val binding get() = _binding!!

    private val usahaList = ArrayList<UsahaItem>()
    private val listUsahaAdapter = ListUsahaAdapter(usahaList){
        val string = it
        val action = ProfilUsahaFragmentDirections.actionProfilUsahaFragmentToDetailUsahaFragment(it)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfilUsahaBinding.inflate(inflater, container, false)







        binding.profilUsahaFragmentRcvListUsaha.adapter = listUsahaAdapter
        binding.profilUsahaFragmentRcvListUsaha.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    private var bayar = false

    override fun onResume() {
        super.onResume()
        if(usahaList.isEmpty()){
            val newItem = UsahaItem(R.drawable.ic_business, "Usaha 1")
            val newItem2 = UsahaItem(R.drawable.ic_business, "Usaha 2")
            val newItem3 = UsahaItem(R.drawable.ic_business, "Usaha 3")
            usahaList.add(newItem)
            usahaList.add(newItem2)
            usahaList.add(newItem3)
            binding.profilUsahaFragmentBtnDaftarUsaha.setOnClickListener {
                binding.profilUsahaFragmentLlBelumBayar.visibility = View.GONE
                binding.profilUsahaFragmentLlSudahBayar.visibility = View.VISIBLE
            }
        }
        val dialog = DaftarUsahaDialog()

        binding.profilUsahaFragmentIvInfo.setOnClickListener{
            dialog.show(parentFragmentManager, "Daftar Usaha Dialog")
        }
        if(bayar){
            binding.profilUsahaFragmentLlBelumBayar.visibility = View.GONE
            binding.profilUsahaFragmentLlSudahBayar.visibility = View.VISIBLE
        } else{
            binding.profilUsahaFragmentLlBelumBayar.visibility = View.VISIBLE
            binding.profilUsahaFragmentLlSudahBayar.visibility = View.GONE
            dialog.show(parentFragmentManager, "Daftar Usaha Dialog")
        }
        bayar = true

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfilUsahaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfilUsahaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}