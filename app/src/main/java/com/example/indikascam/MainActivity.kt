package com.example.indikascam

import android.app.role.RoleManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.indikascam.dialog.WelcomeDialog
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    val REQUEST_ID = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(ROLE_SERVICE) as RoleManager
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            startActivityForResult(intent, REQUEST_ID)
        }

        val prefs = this.getPreferences(MODE_PRIVATE).getBoolean("first_time", true)
        if(prefs){

            val welcomeDialog = WelcomeDialog()

            welcomeDialog.isCancelable = false

            welcomeDialog.show(supportFragmentManager, "Welcome Dialog")

            getPreferences(MODE_PRIVATE).edit().putBoolean("first_time", false).apply()
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
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
    }

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