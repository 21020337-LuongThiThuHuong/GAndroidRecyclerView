package com.example.gandroidrecyclerview

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import java.io.IOException

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val _orderList = MutableLiveData<MutableList<OrderModel>>()
    val orderList: LiveData<MutableList<OrderModel>> get() = _orderList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> get() = _isRefreshing

    private var currentPage = 1
    private val PAGE_SIZE = 20

    init {
        _orderList.value = mutableListOf()
        loadMoreData()
    }

    fun loadMoreData() {
        _isLoading.value = true

        val jsonFileString = getJsonDataFromAsset("orders.json")
        if (jsonFileString.isNullOrEmpty()) {
            _isLoading.value = false
            return
        }

        try {
            val gson = Gson()
            val listType = object : TypeToken<List<OrderModel>>() {}.type
            val newOrders: List<OrderModel> = gson.fromJson(jsonFileString, listType)

            if (newOrders.isNotEmpty()) {
                val updatedList = _orderList.value ?: mutableListOf()
                updatedList.addAll(newOrders)
                _orderList.value = updatedList
                currentPage++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            _isLoading.value = false
        }
    }

    fun refreshData() {
        currentPage = 1
        _orderList.value?.clear()
        loadMoreData()
        _isRefreshing.value = false
    }

    private fun getJsonDataFromAsset(fileName: String): String? {
        return try {
            val inputStream = getApplication<Application>().assets.open(fileName)
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
}
