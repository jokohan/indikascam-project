package com.example.indikascam.fragmentsNotification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.R
import com.example.indikascam.adapter.NotificationAdapter
import com.example.indikascam.databinding.FragmentInboxBinding
import com.example.indikascam.modelsRcv.Notification
import java.util.*

class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!

    private val notifikasiList = ArrayList<Notification>()
    private val notifikasiListAdapter = NotificationAdapter(notifikasiList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.inboxFragmentClBlockList.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_inboxFragment_to_blockListFragment)
        }

        if(notifikasiList.size == 0){
            val c: Calendar = GregorianCalendar()
            c.time = Date()
            for(i in 0..6){
                val list = Notification(
                    c.time,
                    when(i){
                        0 -> "Laporan Anda terhadap nomor telepon 087711887732 telah terkirim dengan status pending"
                        1 -> "Laporan Anda terhadap nomor rekening 100123456789 telah diterima"
                        2 -> "Laporan Anda terhadap nomor rekening 100123456789 telah terkirim dengan status Pending"
                        3 -> "Laporan Anda terhadap nomor telepon 08123456789 telah ditolak"
                        4 -> "Laporan Anda terhadap nomor telepon telah terkirim dengan status Pending"
                        5 -> "Pengajuan review ulang terhadap nomor telepon 08111111112 diterima"
                        6 -> "Pengajuan review ulang terhadap nomor telepon 08111111112 telah terkirim"
                        else -> "asd"
                    }
                )
                c.add(Calendar.DATE, -1)
                notifikasiList.add(list)
            }
        }

        binding.inboxFragmentRcvNotification.adapter = notifikasiListAdapter
        binding.inboxFragmentRcvNotification.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)

        return view
    }

}