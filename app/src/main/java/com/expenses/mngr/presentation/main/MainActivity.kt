package com.expenses.mngr.presentation.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.expenses.mngr.R
import com.expenses.mngr.extensions.hideSystemUI
import com.expenses.mngr.presentation.BackPressBlockable
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        if (viewModel.isMainCurrencySaved()){
            if (viewModel.areSecondaryCurrenciesSaved()){
                navController.navigate(R.id.generalFragment)
            }else{
                navController.navigate(R.id.chooseSecondaryCurrenciesFragment)
            }
        }else{
            navController.navigate(R.id.chooseMainCurrencyFragment)
        }

    }
    override fun onBackPressed() {
        with(navHostFragment.childFragmentManager.fragments.first()){
            if (this is BackPressBlockable && !this.isBackPressAllowed()) return
        }
        if (navHostFragment.childFragmentManager.backStackEntryCount == 1) finish()
        else navController.navigateUp()
    }

    override fun onResume() {
        hideSystemUI()
        super.onResume()
    }
}