package com.atom.payments.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.atom.payments.R
import com.atom.payments.data.dto.Payment

class PaymentAdapter(private val payments: List<Payment>) :
    RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder>() {

    class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        private val createdTextView: TextView = itemView.findViewById(R.id.createdTextView)

        fun bind(payment: Payment) {
            titleTextView.text = payment.title
            // Обработка поля "amount"
            if (payment.amount != null) {
                try {
                    val amountValue = payment.amount.toDouble()
                    amountTextView.text = amountValue.toString()
                } catch (e: NumberFormatException) {
                    amountTextView.text = "Неверный формат числа"
                }
            } else {
                amountTextView.text = "Данных нет"
            }

            // Обработка поля "created"
            if (payment.created != null) {
                createdTextView.text = payment.created.toString()
            } else {
                createdTextView.text = "Данных нет"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.card_for_payment, parent, false)
        return PaymentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        holder.bind(payment)
    }

    override fun getItemCount(): Int = payments.size

}