package com.example.gandroidrecyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidproject.R
import com.example.androidproject.databinding.ActivityMainBinding
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var orderAdapter: OrderAdapter
    private var orderList: MutableList<OrderModel> = mutableListOf()
    private var isLoading = false
    private var currentPage = 1
    private val PAGE_SIZE = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderAdapter = OrderAdapter(orderList)
        binding.rvOrder.layoutManager = LinearLayoutManager(this)
        binding.rvOrder.adapter = orderAdapter
        binding.rvOrder.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        // Add scroll listener for loading more data
        binding.rvOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    loadMoreData()
                }
            }
        })

        // Load initial data
        loadMoreData()
    }

    private fun loadMoreData() {
        isLoading = true

        val jsonFileString = getJsonDataFromAsset("orders.json")
        if (jsonFileString.isNullOrEmpty()) {
            isLoading = false
            return
        }

        try {
            val gson = Gson()
            val listType = object : TypeToken<List<OrderModel>>() {}.type
            val newOrders: List<OrderModel> = gson.fromJson(jsonFileString, listType)

            if (newOrders.isNotEmpty()) {
                orderList.addAll(newOrders)
                orderAdapter.notifyDataSetChanged()
                currentPage++
            } else {
            }
        } catch (e: Exception) {
        } finally {
            isLoading = false
        }
    }

    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            val inputStream = assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }


    private fun refreshData() {
        currentPage = 1
        orderList.clear()
        loadMoreData()
        binding.swipeRefreshLayout.isRefreshing = false
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }
}
