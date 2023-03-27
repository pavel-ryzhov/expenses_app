package com.example.expenses.presentation

import android.app.Activity
import android.view.View
import com.example.expenses.data.preferences.AppPreferences
import com.example.expenses.presentation.dialogs.NoNetworkConnectionDialog

class NoNetworkHelper {
    companion object {
        fun notifyNoNetwork(activity: Activity, rootView: View, appPreferences: AppPreferences) {
            if (appPreferences.getShowDialogOnNetworkError())
                NoNetworkConnectionDialog(activity, appPreferences).show()
            else
                CustomSnackbar.makeAndShow(rootView, "No network connection!")
        }
    }
}