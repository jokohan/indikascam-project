package com.example.indikascam.fragmentsHome

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.indikascam.R
import com.example.indikascam.databinding.FragmentHomeBinding
import com.example.indikascam.dialog.DialogStatistic
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
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        setupHomeFragment()

        return view
    }

    override fun onResume() {
        super.onResume()

        loginOrNot()

        binding.homeFragmentTilSearchNumber.hint = String.format(resources.getString(R.string.cari_nomor),"Telepon")
        binding.homeFragmentTvNumberExample.text = String.format(resources.getString(R.string.numberExample),"08123456789")
        binding.homeFragmentEtSearchNumber.text = null

    }

    private fun loginOrNot() {
        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.fetchAuthToken()

        if(token.isNullOrEmpty()){
            Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_loginFragment)
        } else{

            val prefs = requireContext().getSharedPreferences("first_time", 0)
            if (prefs.getBoolean("first_time", true)) {
                val targetView = activity?.requireViewById(R.id.mainActivity_bnv_mainNavigation) as BottomNavigationView
                val bottomNavigationView = targetView.getChildAt(0) as BottomNavigationMenuView
                GuideView.Builder(context)
                    .setTitle("Lapor Penipuan")
                    .setContentText("Gunakan fitur ini jika Anda mengalami penipuan sehingga user lain bisa memblokir nomor tersebut dan tidak mengalami hal yang sama")
                    .setGravity(Gravity.auto)
                    .setTargetView(bottomNavigationView[1])
                    .setTitleTypeFace(Typeface.DEFAULT_BOLD)
                    .setContentTypeFace(Typeface.defaultFromStyle(Typeface.ITALIC))
                    .setDismissType(DismissType.anywhere)
                    .setGuideListener {
                        GuideView.Builder(context)
                            .setTitle("Cari Nomor Telepon/Rekening")
                            .setContentText("Gunakan fitur ini sebelum Anda bertransaksi agar terhindar dari penipuan, blokir nomor telepon jika diperlukan")
                            .setGravity(Gravity.auto)
                            .setTargetView(binding.homeFragmentClGuideSearchNumber)
                            .setTitleTypeFace(Typeface.DEFAULT_BOLD)
                            .setContentTypeFace(Typeface.defaultFromStyle(Typeface.ITALIC))
                            .setDismissType(DismissType.anywhere)
                            .setGuideListener {
                                GuideView.Builder(context)
                                    .setTitle("Lihat Statistik")
                                    .setContentText("Pelajari modus-modus yang paling sering digunakan untuk menipu")
                                    .setGravity(Gravity.auto)
                                    .setTargetView(binding.homeFragmentIvStatistic)
                                    .setTitleTypeFace(Typeface.DEFAULT_BOLD)
                                    .setContentTypeFace(Typeface.defaultFromStyle(Typeface.ITALIC))
                                    .setDismissType(DismissType.anywhere)
                                    .setGuideListener {
                                        val statisticsDialog = DialogStatistic("Mulai Blokir")
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
    }

    private fun setupHomeFragment() {

        binding.homeFragmentIvStatistic.setOnClickListener {
            val statisticDialog = DialogStatistic("Tutup")
            statisticDialog.show(parentFragmentManager,"Dialog Statistik")
        }

        binding.homeFragmentTilSearchNumber.hint = String.format(resources.getString(R.string.cari_nomor),"Telepon")
        binding.homeFragmentTvNumberExample.text = String.format(resources.getString(R.string.numberExample),"08123456789")

        binding.homeFragmentBtnSearchNumber.setOnClickListener {
            val numberType = when(binding.homeFragmentTlChooseNumberType.selectedTabPosition){
                0 -> "phoneNumber"
                else -> "accountNumber"
            }
            val searchNumber = binding.homeFragmentEtSearchNumber.text.toString()
            val bundleNumber = arrayOf(numberType, searchNumber)
            val action = HomeFragmentDirections.actionHomeFragmentToSearchResultFragment(bundleNumber)
            Navigation.findNavController(binding.root).navigate(action)
        }

        binding.homeFragmentTlChooseNumberType.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                if(tab.position == 0){
                    binding.homeFragmentTilSearchNumber.hint = String.format(resources.getString(R.string.cari_nomor),"Telepon")
                    binding.homeFragmentTvNumberExample.text = String.format(resources.getString(R.string.numberExample),"08123456789")
                } else{
                    binding.homeFragmentTilSearchNumber.hint = String.format(resources.getString(R.string.cari_nomor),"Rekening")
                    binding.homeFragmentTvNumberExample.text = String.format(resources.getString(R.string.numberExample),"100123456789")
                }
            }
        })

        setupPieChart(binding.homeFragmentPcBlokingPerformance)
        setupBarChart(binding.homeFragmentBcBlokingPerformance)
    }

    private fun setupBarChart(barChart: BarChart) {
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

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 12f))
        entries.add(BarEntry(1f, 23f))
        entries.add(BarEntry(2f, 39f))
        entries.add(BarEntry(3f, 50f))

        val colors = arrayListOf<Int>()
        colors.add(Color.parseColor("#789bfa"))
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

        val entries = arrayListOf<PieEntry>()
        entries.add(PieEntry(0.2f, "Panggilan Robot"))
        entries.add(PieEntry(0.3f, "Panggilan Spam"))
        entries.add(PieEntry(0.5f, "Penipu"))

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

}