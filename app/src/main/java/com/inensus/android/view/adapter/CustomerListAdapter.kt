package com.inensus.android.view.adapter

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.inensus.android.R
import com.inensus.android.model.Customer

class CustomerListAdapter(private val context: Context, private val listener: DeleteListener, private val mCustomerList: List<Customer>) : RecyclerView.Adapter<CustomerListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_customer, parent, false))
    }

    override fun getItemCount(): Int {
        return mCustomerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mViewHolder = holder

        val customer = mCustomerList[position]

        holder.tvName.text = StringBuilder().append(customer.name).append(" ").append(customer.surname)

        if (customer.isLocal) {
            holder.tvPhone.text = StringBuilder().append(customer.phone)
            holder.tvSerialNumber.text = StringBuilder().append(customer.serialNumber)
            holder.tvLocale.visibility = View.VISIBLE
            holder.ivLocale.visibility = View.VISIBLE
            holder.cvRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
        } else {
            holder.tvLocale.visibility = View.GONE
            holder.ivLocale.visibility = View.GONE
            holder.cvRoot.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        holder.ivLocale.setOnClickListener { listener.onDelete(customer) }
    }

    private var mViewHolder: ViewHolder? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cvRoot: CardView = view.findViewById(R.id.cvRoot)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvPhone: TextView = view.findViewById(R.id.tvPhone)
        val tvSerialNumber: TextView = view.findViewById(R.id.tvSerialNumber)
        val tvLocale: TextView = view.findViewById(R.id.tvLocale)
        val ivLocale: ImageView = view.findViewById(R.id.ivLocale)
    }

    interface DeleteListener {
        fun onDelete(customer: Customer)
    }
}