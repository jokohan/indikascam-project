package com.example.indikascam

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.indikascam.adapter.ScammedBannerAdapter
import com.example.indikascam.adapter.ScammedProductAdapter
import com.example.indikascam.databinding.FragmentHomeBinding
import com.example.indikascam.dialog.StatisticsDialog
import com.example.indikascam.sessionManager.SessionManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.textfield.TextInputEditText
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import java.text.SimpleDateFormat
import java.util.*


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

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

//        val textView = view.findViewById<TextInputEditText>(R.id.signUpFragment_et_searchNomor)
//        val extras = activity?.intent?.extras
//        textView.setText(extras?.getString("a"))

        view.findViewById<Button>(R.id.homeFragment_btn_searchNomor).setOnClickListener{
            val someText = arrayOf<String>(view.findViewById<TextInputEditText>(R.id.homeFragment_et_searchNomor).text.toString(), view.findViewById<TabLayout>(R.id.homeFragment_tl_teleponRekening).selectedTabPosition.toString())
            val action = HomeFragmentDirections.actionHomeFragmentToHasilPencarianFragment(someText)
            Navigation.findNavController(view).navigate(action)
        }

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

        binding.homeFragmentTlTeleponRekening.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.position == 0) {
                    binding.homeFragmentTvContoh.text = "Contoh: 081234567890"
                    binding.homeFragmentTilSearchNomor.hint = "Cari Nomor Telepon"
                } else {
                    binding.homeFragmentTvContoh.text = "Contoh: 100123456789"
                    binding.homeFragmentTilSearchNomor.hint = "Cari Nomor Rekening"
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        return view
    }

    override fun onResume() {
        super.onResume()
        sessionManager = SessionManager(requireContext())

        var namaUser = ""
        sharedViewModel.nama.observe(viewLifecycleOwner, Observer { nama ->
            namaUser = nama
        })
        val prefs = requireContext().getSharedPreferences("first_time", 0)
        if (prefs.getBoolean("first_time", true)) {

            val targetView =
                activity?.requireViewById(R.id.bottomNavigationView) as BottomNavigationView
            val a = targetView.getChildAt(0) as BottomNavigationMenuView
            GuideView.Builder(context)
                .setTitle("Lapor Penipuan")
                .setContentText("Gunakan fitur ini jika Anda mengalami penipuan sehingga user lain bisa memblokir nomor tersebut dan tidak mengalami hal yang sama")
                .setGravity(Gravity.auto)
                .setTargetView(a[1])
                .setTitleTypeFace(Typeface.DEFAULT_BOLD)
                .setContentTypeFace(Typeface.defaultFromStyle(Typeface.ITALIC))
                .setDismissType(DismissType.anywhere)
                .setGuideListener {
                    GuideView.Builder(context)
                        .setTitle("Cari Nomor Telepon/Rekening")
                        .setContentText("Gunakan fitur ini sebelum Anda bertransaksi agar terhindar dari penipuan, blokir nomor telepon jika diperlukan")
                        .setGravity(Gravity.auto)
                        .setTargetView(binding.homeFragmentClGuideSearchNomor)
                        .setTitleTypeFace(Typeface.DEFAULT_BOLD)
                        .setContentTypeFace(Typeface.defaultFromStyle(Typeface.ITALIC))
                        .setDismissType(DismissType.anywhere)
                        .setGuideListener {
                            GuideView.Builder(context)
                                .setTitle("Lihat Statistik")
                                .setContentText("Pelajari modus-modus yang paling sering digunakan untuk menipu")
                                .setGravity(Gravity.auto)
                                .setTargetView(binding.homeFragmentIvStatistics)
                                .setTitleTypeFace(Typeface.DEFAULT_BOLD)
                                .setContentTypeFace(Typeface.defaultFromStyle(Typeface.ITALIC))
                                .setDismissType(DismissType.anywhere)
                                .setGuideListener {
                                    val statisticsDialog = StatisticsDialog("Mulai Blokir")
                                    statisticsDialog.show(parentFragmentManager, "Statistics Dialog")
                                }
                                .build()
                                .show()
                        }
                        .build()
                        .show()
                }
                .build()
                .show()

            prefs.edit().putBoolean("first_time", false).apply()
        }
    }



    private fun setupBarChart(barChart: BarChart){
        //add animation
        barChart.animateY(1000)
        val labels = ArrayList<String>()
        val c: Calendar = GregorianCalendar()
        c.time = Date()
        val sdf = SimpleDateFormat("MMM", Locale("in", "ID"))
        c.add(Calendar.MONTH, -3)
        labels.add(sdf.format(c.time))
        c.add(Calendar.MONTH, 1)
        labels.add(sdf.format(c.time))
        c.add(Calendar.MONTH, 1)
        labels.add(sdf.format(c.time))
        c.add(Calendar.MONTH, 1)
        labels.add(sdf.format(c.time))

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.granularity = 1f
        barChart.xAxis.textSize = 14f
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        barChart.legend.isEnabled = false
        barChart.axisLeft.setDrawLabels(false)
        barChart.description.text = ""
        barChart.xAxis.setDrawAxisLine(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.axisLeft.setDrawAxisLine(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.textSize = 14f
        barChart.axisRight.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.setScaleEnabled(false)
        barChart.setPinchZoom(false)
        barChart.isDoubleTapToZoomEnabled = false
        barChart.setTouchEnabled(false)
    }

    private fun loadBarChartData(barChart: BarChart){
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 12f))
        entries.add(BarEntry(1f, 23f))
        entries.add(BarEntry(2f, 39f))
        entries.add(BarEntry(3f, 50f))

        val colors = arrayListOf<Int>()
        colors.add(Color.parseColor("#789bfa"))
//        colors.add(com.google.android.material.R.color.design_default_color_primary)
        val dataset = BarDataSet(entries, "")
        dataset.colors = colors
        dataset.valueTextSize = 16F
        dataset.valueFormatter = DefaultValueFormatter(0)
        dataset.setDrawValues(false)
        val data = BarData(dataset)
        data.barWidth = 0.8F

        barChart.data = data
        barChart.invalidate()
    }

    private fun setupPieChart(pieChart: PieChart) {
        pieChart.isDrawHoleEnabled = true
        pieChart.setUsePercentValues(true)
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



    private fun loadPieChartData(pieChart: PieChart) {
        val entries = arrayListOf<PieEntry>()
        entries.add(PieEntry(0.2f, "Robocall"))
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