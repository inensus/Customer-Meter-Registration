package com.inensus.android.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inensus.android.R
import com.inensus.android.model.Customer
import com.inensus.android.util.SharedPreferencesWrapper
import com.inensus.android.util.Utils
import com.inensus.android.view.adapter.CustomerListAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_customer_list.*

/**
 * A simple [Fragment] subclass.
 * Use the [CustomerListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class CustomerListFragment : BaseFragment(), CustomerListAdapter.DeleteListener {

    private var isSwipeRefreshing = false

    override fun onResume() {
        super.onResume()

        getCustomers()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_customer_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setProgressViewOffset(false, 0, Utils.convertDpToPixel(100))
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(activity as Context, R.color.colorPrimaryDark))
        swipeRefreshLayout.setOnRefreshListener {
            isSwipeRefreshing = true
            getCustomers()
        }
    }

    @SuppressLint("CheckResult")
    fun getCustomers() {
        mCustomerViewModel.getCustomers()!!
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showDialog() }
                .subscribe(
                        { customerList ->
                            hideDialog()
                            onGetCustomers(customerList)
                        },
                        { error ->
                            showErrorMessage(error)
                            hideDialog()
                            onGetCustomers(null)
                        }
                )
    }

    @SuppressLint("CheckResult")
    private fun onGetCustomers(customerList: List<Customer>?) {
        if (customerList != null && customerList.isNotEmpty()) {
            rvCustomerList.layoutManager = LinearLayoutManager(activity)
            rvCustomerList.itemAnimator = DefaultItemAnimator()
            rvCustomerList.adapter = CustomerListAdapter(activity as Context, this, customerList)
            llEmpty?.visibility = View.GONE

            if (context != null && SharedPreferencesWrapper.getInstance().networkState == 1) {
                val localCustomerList = customerList.filter { it.isLocal }

                if (!localCustomerList.isEmpty()) {
                    val customer = localCustomerList[0]
                    mCustomerViewModel.addCustomer(customer)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe { showDialog() }
                            .subscribe(
                                    {
                                        mCustomerViewModel.deleteCustomerInDb(customer)
                                        hideDialog()

                                        getCustomers()
                                    },
                                    { error ->
                                        showErrorMessage(error, customer, object : BaseFragment.DialogClickListener {
                                            override fun onDialogClick() {
                                                getCustomers()
                                                mCustomerViewModel.deleteCustomerInDb(customer)
                                            }
                                        })

                                        hideDialog()
                                    }
                            )
                }
            }
        } else {
            llEmpty?.visibility = View.VISIBLE
        }

        swipeRefreshLayout?.isRefreshing = false
    }

    override fun onDelete(customer: Customer) {
        mCustomerViewModel.deleteCustomerInDb(customer)

        getCustomers()
    }

    private fun showDialog() {
        if (!swipeRefreshLayout.isRefreshing) {
            progressBar?.visibility = View.VISIBLE
        }
    }

    private fun hideDialog() {
        progressBar?.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance() = CustomerListFragment().apply {}
    }
}
