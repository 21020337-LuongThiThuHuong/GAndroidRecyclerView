package com.example.gandroidrecyclerview

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var orderAdapter: OrderAdapter
    private val orderViewModel: OrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderAdapter = OrderAdapter(emptyList())
        binding.rvOrder.layoutManager = LinearLayoutManager(this)
        binding.rvOrder.adapter = orderAdapter
        binding.rvOrder.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        // Setup SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            orderViewModel.refreshData()
        }

        // Add scroll listener for loading more data
        binding.rvOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!orderViewModel.isLoading.value!! && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    orderViewModel.loadMoreData()
                }
            }
        })

        // Observe LiveData from ViewModel
        orderViewModel.orderList.observe(this, Observer {
            orderAdapter.updateData(it)
        })

        orderViewModel.isLoading.observe(this, Observer {
            // Handle loading state, show/hide progress bar
        })

        orderViewModel.isRefreshing.observe(this, Observer {
            binding.swipeRefreshLayout.isRefreshing = it
        })
    }

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }
}
