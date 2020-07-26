package com.nielsmasdorp.lvbnotifier.presentation.stock

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import com.nielsmasdorp.lvbnotifier.R
import com.nielsmasdorp.lvbnotifier.util.Status
import kotlinx.android.synthetic.main.activity_stocks.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import com.nielsmasdorp.lvbnotifier.presentation.settings.SettingsActivity

class StockActivity : AppCompatActivity(R.layout.activity_stocks) {

    private val stockViewModel: StockViewModel by viewModel()

    private lateinit var adapter: StocksAdapter
    private lateinit var searchView: SearchView
    private lateinit var searchViewItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.stocks_title)
        initList()
    }

    override fun onDestroy() {
        stockViewModel.filter = null
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        searchViewItem = menu.findItem(R.id.search)
        searchView = searchViewItem.actionView as SearchView
        initSearch()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                stockViewModel.onSettingsClicked()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initList() {
        stockRefreshLayout.setOnRefreshListener {
            searchView.onActionViewCollapsed()
            stockViewModel.onRefresh()
        }
        adapter = StocksAdapter().also { adapter ->
            stockList.adapter = adapter
            adapter.clickListener = stockViewModel::onStockClicked
            adapter.longPressListener = stockViewModel::onStockLongPress
        }
        val decoration = DividerItemDecoration(
            applicationContext,
            DividerItemDecoration.VERTICAL
        )
        stockList.addItemDecoration(decoration)
    }

    private fun initSearch() {
        with(searchView) {
            queryHint = getString(R.string.settings_search_hint)
            setOnCloseListener {
                stockViewModel.filter = null
                false
            }
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = false

                override fun onQueryTextChange(newText: String): Boolean {
                    stockViewModel.filter = newText
                    return true
                }
            })
        }
        initObservers()
    }

    private fun initObservers() {
        with(stockViewModel) {
            data.observe(this@StockActivity, Observer { resource ->
                when (resource.status) {
                    Status.LOADING -> showLoading()
                    Status.ERROR -> showError(resource.message)
                    Status.SUCCESS -> showData(resource.data!!)
                }
            })
            message.observe(this@StockActivity, Observer { message ->
                when (message) {
                    is StockViewModel.Message.FullProductDescription -> {
                        Toast.makeText(this@StockActivity, message.description, Toast.LENGTH_LONG)
                            .show()
                    }
                    is StockViewModel.Message.EANCopied -> {
                        Toast.makeText(
                            this@StockActivity,
                            R.string.ean_copied_message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            })
            navigation.observe(this@StockActivity, Observer { navigation ->
                when (navigation) {
                    is StockViewModel.Navigation.Settings -> {
                        startActivity(Intent(this@StockActivity, SettingsActivity::class.java))
                    }
                }
            })
        }
    }

    private fun showError(message: String?) {
        stockList.visibility = View.GONE
        errorView.visibility = View.VISIBLE
        errorText.text = message

        searchViewItem.isVisible = false
        stockRefreshLayout.isRefreshing = false
    }

    private fun showLoading() {
        searchViewItem.isVisible = false
        stockRefreshLayout.isRefreshing = true
    }

    private fun showData(data: List<StockViewData>) {
        if (data.isEmpty()) {
            stockList.visibility = View.GONE
            errorView.visibility = View.VISIBLE
            errorText.text = if (!stockViewModel.filter.isNullOrEmpty()) {
                getString(R.string.stocks_search_error)
            } else {
                getString(R.string.stocks_no_stock_error)
            }
        } else {
            stockList.visibility = View.VISIBLE
            errorView.visibility = View.GONE
        }

        searchViewItem.isVisible = true
        stockRefreshLayout.isRefreshing = false
        adapter.submitList(data)
        showSummary(data)
    }

    private fun showSummary(data: List<StockViewData>) {
        val amountOfProducts = data.size
        val totalAmountOfStock = data.sumBy { it.amount.toInt() }
        title = String.format(
            Locale.getDefault(),
            getString(R.string.stocks_full_title),
            totalAmountOfStock,
            amountOfProducts
        )
    }
}