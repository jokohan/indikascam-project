package com.example.indikascam

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.adapter.CommentAdapter
import com.example.indikascam.adapter.ScammedBannerAdapter
import com.example.indikascam.adapter.ScammedProductAdapter

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

    val args: HasilPencarianFragmentArgs by navArgs()

    private var commentLayoutManager: RecyclerView.LayoutManager? = null
    private var commentAdapter: RecyclerView.Adapter<CommentAdapter.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hasil_pencarian, container, false)

        val searchNumber = args.searchNumber

        if (searchNumber[0].isNotBlank()){
            view.findViewById<TextView>(R.id.hasilPencarianFragment_tv_nomor).text = "${searchNumber[0]} ${searchNumber[1]}"
        }

        commentLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        view.findViewById<RecyclerView>(R.id.hasilPencarianFragment_rcv_comment).layoutManager = commentLayoutManager
        commentAdapter = CommentAdapter()
        view.findViewById<RecyclerView>(R.id.hasilPencarianFragment_rcv_comment).adapter = commentAdapter

        return view
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