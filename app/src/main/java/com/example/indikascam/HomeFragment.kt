package com.example.indikascam

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.adapter.ScammedBannerAdapter
import com.example.indikascam.adapter.ScammedProductAdapter
import com.example.indikascam.dialog.StatisticsDialog
import com.example.indikascam.sessionManager.SessionManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: MainViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<ScammedBannerAdapter.ViewHolder>? = null

    private var productLayoutManager: RecyclerView.LayoutManager? = null
    private var productAdapter: RecyclerView.Adapter<ScammedProductAdapter.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

//        val textView = view.findViewById<TextInputEditText>(R.id.signUpFragment_et_searchNomor)
//        val extras = activity?.intent?.extras
//        textView.setText(extras?.getString("a"))

        view.findViewById<Button>(R.id.homeFragment_btn_searchNoHP).setOnClickListener{
            val someText = arrayOf<String>(view.findViewById<TextInputEditText>(R.id.signUpFragment_et_searchNomor).text.toString(), view.findViewById<TabLayout>(R.id.homeFragment_tl_teleponRekening).selectedTabPosition.toString())
            val action = HomeFragmentDirections.actionHomeFragmentToHasilPencarianFragment(someText)
            Navigation.findNavController(view).navigate(action)}

        view.findViewById<ImageView>(R.id.homeFragment_iv_statistics).setOnClickListener {
            val statisticsDialog = StatisticsDialog("Tutup")
            statisticsDialog.show(parentFragmentManager, "Statistics Dialog")
        }

        val pieChart = view.findViewById<PieChart>(R.id.homeFragment_pc_blokingPerformance)
        setupPieChart(pieChart)
        loadPieChartData(pieChart)

        val barChart = view.findViewById<BarChart>(R.id.homeFragment_bc_blokingPerformance)
        loadBarChartData(barChart)
        setupBarChart(barChart)

        return view
    }

    private fun setupBarChart(barChart: BarChart){
        //        hide grid lines
        barChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = barChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        //remove right y-axis
        barChart.axisRight.isEnabled = false

        //remove legend
        barChart.legend.isEnabled = false

        //remove description label
        barChart.description.isEnabled = false

        //add animation
        barChart.animateY(1000)


        val labels = ArrayList<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = SimpleDateFormat("MMMM", Locale("in", "ID"))
        labels.add(sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        labels.add(sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        labels.add(sdf.format(c.time))
        c.add(Calendar.MONTH, -1)
        labels.add(sdf.format(c.time))

        // to draw label on xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.setDrawLabels(true)
        xAxis.granularity = 1f
        xAxis.textColor = Color.BLACK
        xAxis.textSize = 14F
    }

    private fun setupPieChart(pieChart: PieChart) {
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
//        pieChart.setEntryLabelTextSize(12F)
//        pieChart.setEntryLabelColor(Color.BLACK)
        pieChart.setDrawEntryLabels(false)
        pieChart.centerText = "Blokir Panggilan"
        pieChart.setCenterTextSize(14F)
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(-10f,0f,-10f,-12f)

        val l = pieChart.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(false)
        l.isEnabled = true
        l.textSize = 14F
    }

    private fun loadBarChartData(barChart: BarChart){
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 12f))
        entries.add(BarEntry(1f, 39f))
        entries.add(BarEntry(2f, 23f))
        entries.add(BarEntry(3f, 9f))

        val colors = arrayListOf<Int>()
        colors.add(Color.parseColor("#062C30"))
        val dataset = BarDataSet(entries, "")
        dataset.colors = colors
        dataset.valueTextSize = 16F
        dataset.valueFormatter = DefaultValueFormatter(0)
        val data = BarData(dataset)
        barChart.data = data


        barChart.invalidate()
    }

    private fun loadPieChartData(pieChart: PieChart) {
        val entries = arrayListOf<PieEntry>()
        entries.add(PieEntry(0.2f, "Telemarketer"))
        entries.add(PieEntry(0.3f, "Spam"))
        entries.add(PieEntry(0.5f, "Scam"))

        val colors = arrayListOf<Int>()
        for (color in ColorTemplate.MATERIAL_COLORS){
            colors.add(color)
        }

        val dataset = PieDataSet(entries, "")
        dataset.colors = colors

        val data = PieData(dataset)
        data.setDrawValues(true)
        data.setValueFormatter(PercentFormatter(pieChart))
        data.setValueTextSize(14f)
        data.setValueTextColor(Color.BLACK)

        pieChart.data = data
        pieChart.invalidate()
        pieChart.isHighlightPerTapEnabled = false

        pieChart.animateY(1000, Easing.EaseInQuad)
    }

    override fun onResume() {
        super.onResume()
        sessionManager = SessionManager(requireContext())

        var namaUser = ""
        sharedViewModel.nama.observe(viewLifecycleOwner, Observer {  nama ->
            namaUser = nama
        })

//        if(sessionManager.fetchAuthToken() != "" && namaUser == "nama"){
//            try{
//                val repository = Repository()
//                val viewModelFactory = MainViewModelFactory(repository)
//                viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
//                Log.d("Token", "Bearer ${sessionManager.fetchAuthToken().toString()}")
//                viewModel.meGet("Bearer ${sessionManager.fetchAuthToken().toString()}")
//                viewModel.myResponseMe.observe(viewLifecycleOwner, Observer { response ->
//                    if (response.isSuccessful){
//                        val jsonObject = Gson().toJsonTree(response.body()).asJsonObject
//                        Log.d("HomeResponse", jsonObject["name"].toString().removeSurrounding("\""))
//                        sharedViewModel.saveNama(jsonObject["name"].toString().removeSurrounding("\""))
//                        sharedViewModel.saveEmail(jsonObject["email"].toString().removeSurrounding("\""))
//                        sharedViewModel.saveAccessToken(sessionManager.fetchAuthToken().toString())
//                    } else{
//                        Log.d("HomeResponseError", response.code().toString())
//                        Log.d("HomeResponseError", response.message())
//                    }
//                })
//            }catch (e: IOException){
//                when(e){
//                    is SocketTimeoutException -> {
//                        Log.d("timeout",e.stackTraceToString())
//                    }
//                    else -> throw e
//                }
//            }
//        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}