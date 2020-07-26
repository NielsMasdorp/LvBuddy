package com.nielsmasdorp.lvbnotifier.presentation.stock

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nielsmasdorp.lvbnotifier.R
import kotlinx.android.synthetic.main.list_item_stock.view.*

class StocksAdapter : ListAdapter<StockViewData, StocksAdapter.StockViewHolder>(DIFF_CALLBACK) {

    lateinit var clickListener: ((StockViewData) -> Unit)

    lateinit var longPressListener: ((StockViewData) -> Unit)

    lateinit var recycler: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recycler = recyclerView
    }

    override fun onCurrentListChanged(
        previousList: MutableList<StockViewData>,
        currentList: MutableList<StockViewData>
    ) {
        super.onCurrentListChanged(previousList, currentList)
        recycler.scrollToPosition(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item_stock, parent, false)
        return StockViewHolder(v)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) =
        holder.bindStock(getItem(position))

    inner class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindStock(stockData: StockViewData) {
            with(itemView) {
                productTitle.text = stockData.productTitle
                if (stockData.hasBuyBox) {
                    buyBoxIcon.setImageResource(R.drawable.ic_buy_box)
                } else {
                    buyBoxIcon.setImageResource(R.drawable.ic_no_buy_box)
                }
                amount.text = stockData.amount
                setOnClickListener { clickListener.invoke(stockData) }
                setOnLongClickListener {
                    longPressListener.invoke(stockData)
                    true
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StockViewData>() {

            override fun areItemsTheSame(oldItem: StockViewData, newItem: StockViewData): Boolean {
                return oldItem.productId == newItem.productId
            }

            override fun areContentsTheSame(
                oldItem: StockViewData,
                newItem: StockViewData
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}