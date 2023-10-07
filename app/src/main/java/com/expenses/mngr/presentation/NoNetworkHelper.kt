package com.expenses.mngr.presentation

import android.app.Activity
import android.view.View
import com.expenses.mngr.R
import com.expenses.mngr.data.preferences.AppPreferences
import com.expenses.mngr.presentation.dialogs.NoNetworkConnectionDialog

class NoNetworkHelper {
    companion object {
        fun notifyNoNetwork(activity: Activity, rootView: View, appPreferences: AppPreferences) {
            if (appPreferences.getShowDialogOnNetworkError())
                NoNetworkConnectionDialog(activity, appPreferences).show()
            else
                CustomSnackbar.makeAndShow(rootView, rootView.context.getString(R.string.no_network_connection))
        }
        fun showSnackbarNoNetwork(rootView: View){
            CustomSnackbar.makeAndShow(rootView, rootView.context.getString(R.string.no_network_connection))
        }
    }
}