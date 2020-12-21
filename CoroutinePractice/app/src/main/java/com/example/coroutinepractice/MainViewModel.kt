package com.example.coroutinepractice

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.random.Random

class MainViewModel: ViewModel() {

    // LiveData
    val job1LiveData = MutableLiveData<Int>().apply { value = 0 }
    val job2LiveData = MutableLiveData<Int>().apply { value = 0 }
    val job3LiveData = MutableLiveData<Int>().apply { value = 0 }
    val statusLiveData = MutableLiveData<Boolean>().apply { value = false }

    fun runAllProcess() {
        viewModelScope.launch(Dispatchers.Main) {
            val result1 = async { increase(200, job1LiveData) }
            val result2 = async { increase(100, job2LiveData) }
            val result3 = async { increase(50, job3LiveData) }
            val deferredList = listOf(result1, result2, result3)
            if (!deferredList.awaitAll().contains(false)) {
                statusLiveData.postValue(true)
            }
        }
    }

    private suspend fun increase(speed: Long? = null, liveData: MutableLiveData<Int>): Boolean {
        return withContext(Dispatchers.IO) {
            var percent = 0
            while (percent < 100) {
                Thread.sleep(speed ?: random())
                liveData.postValue(++percent)
            }

            return@withContext true
        }
    }

    private fun random() = Random.nextLong(20, 1000)
}