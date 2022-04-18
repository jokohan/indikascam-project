package com.example.indikascam

import android.app.role.RoleManager
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.get
import androidx.core.view.size
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.indikascam.dialog.WelcomeDialog
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity


class MainActivity : AppCompatActivity() {

    val REQUEST_ID = 1
    private var idLaporView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            startActivityForResult(intent, REQUEST_ID)
        }


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu)
        val navController = findNavController(R.id.fragmentContainerView)


        bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> bottomNavigationView.visibility = View.VISIBLE
                R.id.notifikasiFragment -> bottomNavigationView.visibility = View.VISIBLE
                R.id.profileFragment -> bottomNavigationView.visibility = View.VISIBLE
                else -> bottomNavigationView.visibility = View.GONE
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.notifikasiFragment,
                R.id.profileFragment,
                R.id.authCongratulationFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)
//        Log.d("TAG", bottomNavigationView.childCount.toString())
//        Log.d("TAG", bottomNavigationView.children.count().toString())
//        Log.d("TAG", bottomNavigationView.itemIconSize.toString())
//        Log.d("TAG", bottomNavigationView.maxItemCount.toString())
        val a = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
        Log.d("TAG", a.toString())
//        GuideView.Builder(applicationContext)
//            .setTitle("Cari Nomor Telepon/Rekening")
//            .setContentText("Gunakan fitur pencarian ini sebelum Anda melakukan transaksi")
//            .setGravity(Gravity.auto)
//            .setTargetView()
//            .setContentTypeFace(Typeface.DEFAULT)
//            .setTitleTypeFace(Typeface.DEFAULT_BOLD)
//            .setDismissType(DismissType.anywhere)
//            .setGuideListener {
//
//            }
//            .build()
//            .show()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//
//        menuInflater.inflate(R.menu.bottom_navigation_menu, menu)
//
//        val btn = menu?.findItem(1)?.actionView
//
//        btn?.setOnClickListener {
//            Log.d("laporView", it.id.toString())
//        }
//        return true
//    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.fragmentContainerView).navigateUp()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        currentFocus?.let {
            val imm: InputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as (InputMethodManager)
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
