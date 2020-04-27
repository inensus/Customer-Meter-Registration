package com.inensus.android.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.inensus.android.R
import com.inensus.android.util.SharedPreferencesWrapper
import com.inensus.android.util.Utils
import com.inensus.android.view.custom.InputBottomSheetFragment
import com.inensus.android.view.fragment.AddCustomerFragment
import com.inensus.android.view.fragment.CustomerListFragment
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : DaggerAppCompatActivity(), SharedPreferencesWrapper.Listener {

    private var mBackPressed: Long = 0

    private lateinit var mCustomerListFragment: CustomerListFragment
    private lateinit var mAddCustomerFragment: AddCustomerFragment
    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setTheme(R.style.AppTheme)

        SharedPreferencesWrapper.getInstance().setListener(this)

        mCustomerListFragment = customerListFragment as CustomerListFragment
        mAddCustomerFragment = addCustomerFragment as AddCustomerFragment

        bottomBar.setOnTabSelectListener { tabId ->
            when (tabId) {
                R.id.tab_customer_list -> {
                    showFragment(mCustomerListFragment)
                    mCustomerListFragment.getCustomers()
                    hideFragment(mAddCustomerFragment)
                }
                R.id.tab_add_customer -> {
                    showFragment(mAddCustomerFragment)
                    hideFragment(mCustomerListFragment)
                }
            }

            invalidateOptionsMenu()
        }

        alertDialog = AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Please connect to the internet.")
                .setPositiveButton("Wifi Settings") { _, _ ->
                    val myIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(myIntent)
                }
                .setCancelable(false)
                .create()
    }

    override fun onResume() {
        super.onResume()

        Utils.checkConnectivity(this)
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .show(fragment)
                .commit()
        supportFragmentManager.executePendingTransactions()
    }

    private fun hideFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .hide(fragment)
                .commit()
        supportFragmentManager.executePendingTransactions()
    }

    override fun onBackPressed() {
        if (mBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
            return
        } else {
            Toast.makeText(baseContext, getString(R.string.exit), Toast.LENGTH_SHORT).show()
        }

        mBackPressed = System.currentTimeMillis()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        menu.findItem(R.id.actionCheck).isVisible = mAddCustomerFragment.isVisible
        menu.findItem(R.id.actionSettings).isVisible = mCustomerListFragment.isVisible

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.actionSettings -> {
                    showInputBottomSheet()
                    true
                }
                R.id.actionCheck -> {
                    mAddCustomerFragment.addCustomer()
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }

    private fun showInputBottomSheet() {
        InputBottomSheetFragment.newInstance().apply {
            show(supportFragmentManager, BOTTOM_SHEET_FRAGMENT_TAG)
        }
    }

    override fun onSharedPreferencesValueChange() {
        if (SharedPreferencesWrapper.getInstance().networkState == 0
                && SharedPreferencesWrapper.getInstance().manufacturers.isEmpty()
                && !alertDialog.isShowing) {

            alertDialog.show()
        } else if (SharedPreferencesWrapper.getInstance().networkState == 1 && SharedPreferencesWrapper.getInstance().manufacturers.isEmpty()) {
            if (mAddCustomerFragment.isAdded) {
                mAddCustomerFragment.getManufacturers()
                mAddCustomerFragment.getMeterTypes()
                mAddCustomerFragment.getTariffs()
                mAddCustomerFragment.getCities()
            }
        }
    }

    companion object {
        private const val BOTTOM_SHEET_FRAGMENT_TAG = "BOTTOM_SHEET_FRAGMENT_TAG"
    }
}
