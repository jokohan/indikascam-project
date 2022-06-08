package com.example.indikascam.fragmentsNotification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indikascam.R
import com.example.indikascam.adapter.NotificationAdapter
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.api.responses.GetNotificationResponse
import com.example.indikascam.databinding.FragmentInboxBinding
import com.example.indikascam.sessionManager.SessionManager
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class InboxFragment : Fragment() {

    private var _binding: FragmentInboxBinding? = null
    private val binding get() = _binding!!

    private val notificationTodayList = ArrayList<GetNotificationResponse.Today>()
    private val notificationMonthList = ArrayList<GetNotificationResponse.ThisMonth>()
    private val notificationListTodayAdapter = NotificationAdapter(notificationTodayList, null)
    private val notificationListMonthAdapter = NotificationAdapter(null, notificationMonthList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInboxBinding.inflate(inflater, container, false)
        val view = binding.root

        val sessionManager = SessionManager(requireContext())

        binding.inboxFragmentClBlockList.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_inboxFragment_to_blockListFragment)
        }

        lifecycleScope.launchWhenCreated {

            val response = try{
                RetroInstance.apiNotification.getNotification(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
            }catch (e: IOException){
                Log.e("NotificationIOError", e.message!!)
                return@launchWhenCreated
            }catch (e: HttpException){
                Log.e("NotificationHttpError", e.message!!)
                return@launchWhenCreated
            }

            if(response.isSuccessful && response.body() != null){
                if(response.body()!!.today != notificationTodayList){
                    notificationTodayList.clear()
                    notificationTodayList += response.body()!!.today
                }
                if(response.body()!!.this_month != notificationMonthList){
                    notificationMonthList.clear()
                    notificationMonthList += response.body()!!.this_month
                }

                binding.inboxFragmentRcvNotificationToday.adapter = notificationListTodayAdapter
                binding.inboxFragmentRcvNotificationToday.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                binding.inboxFragmentRcvNotificationThisMonth.adapter = notificationListMonthAdapter
                binding.inboxFragmentRcvNotificationThisMonth.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            }else{
                try{
                    @Suppress("BlockingMethodInNonBlockingContext") val jObjError = JSONObject(response.errorBody()!!.string())
                    val errorMessage = jObjError.getJSONObject("error").getString("message")
                    Log.e("logoutError", errorMessage)
                    Log.e("logoutError", response.code().toString())
                }catch (e: Exception){
                    Log.e("logoutError", e.toString())
                }
            }
        }


        return view
    }

}