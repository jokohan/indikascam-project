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
import com.example.indikascam.adapter.BlockedNumberAdapter
import com.example.indikascam.api.RetroInstance
import com.example.indikascam.api.requests.PostTokenRequest
import com.example.indikascam.databinding.FragmentBlockListBinding
import com.example.indikascam.dialog.DialogProgressBar
import com.example.indikascam.modelsRcv.BlockedNumber
import com.example.indikascam.sessionManager.SessionManager
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class BlockListFragment : Fragment() {

    private var _binding : FragmentBlockListBinding? = null
    private val binding get() = _binding!!

    private val blockedNumberList = ArrayList<BlockedNumber>()
    private val blockedNumberAdapter = BlockedNumberAdapter(blockedNumberList){
        val phoneNumber = arrayOf("phoneNumber", blockedNumberList[it].phoneNumber)
        val action = BlockListFragmentDirections.actionBlockListFragmentToSearchResultFragment(phoneNumber)
        Navigation.findNavController(binding.root).navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlockListBinding.inflate(inflater, container, false)
        val view = binding.root

        val sessionManager = SessionManager(requireContext())
        val loadingDialog = DialogProgressBar.progressDialog(requireContext())

        lifecycleScope.launchWhenCreated {
            loadingDialog.show()
            val response = try{
                RetroInstance.apiNotification.getBlockHistory(PostTokenRequest("Bearer ${sessionManager.fetchAuthToken()}"))
            }catch (e: IOException){
                Log.i("BlockListIOException", e.message!!)
                loadingDialog.dismiss()
                return@launchWhenCreated
            }catch (e: HttpException){
                Log.i("BlockListHttpException", e.message!!)
                loadingDialog.dismiss()
                return@launchWhenCreated
            }
            if(response.isSuccessful && response.body() != null){
                for(data in response.body()!!.data)
                blockedNumberList.add(BlockedNumber(data.phone_number, data.block_status))
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
            binding.blockListFragmentRcvNumber.adapter = blockedNumberAdapter
            binding.blockListFragmentRcvNumber.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            loadingDialog.dismiss()
        }

        return view
    }
}