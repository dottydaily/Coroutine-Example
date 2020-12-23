package com.example.coroutinepractice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.random.Random

class MainViewModel: ViewModel() {
    // Job
    var job1: Job? = null
    var job2: Job? = null
    var job3: Job? = null

    // LiveData
    val job1LiveData = MutableLiveData<Int>().apply { value = 0 }
    val job2LiveData = MutableLiveData<Int>().apply { value = 0 }
    val job3LiveData = MutableLiveData<Int>().apply { value = 0 }
    val statusLiveData = MutableLiveData<Boolean>().apply { value = false }

    fun runAllJob() {
        // reset old status and each job value
        reset()

        viewModelScope.launch(Dispatchers.Main) {
            startJob1()
            startJob2()
            startJob3()

            // wait each job to be finished.
            val jobs = listOfNotNull(job1, job2, job3)
            jobs.joinAll()

            val hasCompletedAllJob = jobs.find { it.isCancelled } == null
            if (hasCompletedAllJob) {
                statusLiveData.postValue(true)
            }

//            val deferredList = listOf(result1, result2, result3)
//            if (!deferredList.awaitAll().contains(false)) {
//                statusLiveData.postValue(true)
//            }
        }
    }

    fun startJob1() {
        job1 = viewModelScope.launch(Dispatchers.IO) {
            var percent = 0
            while (percent < 100 && isActive) {
                Thread.sleep(200 ?: random())
                job1LiveData.postValue(++percent)
            }

            job1 = null
        }
    }

    fun startJob2() {
        job2 = viewModelScope.launch(Dispatchers.IO) {
            var percent = 0
            while (percent < 100 && isActive) {
                Thread.sleep(100 ?: random())
                job2LiveData.postValue(++percent)
            }

            job2 = null
        }
    }

    fun startJob3() {
        job3 = viewModelScope.launch(Dispatchers.IO) {
            var percent = 0
            while (percent < 100 && isActive) {
                Thread.sleep(50 ?: random())
                job3LiveData.postValue(++percent)
            }

            job3 = null
        }
    }

    fun cancelJob1() {
        viewModelScope.launch {
            job1?.cancelAndJoin()
            job1LiveData.postValue(0)
            job1 = null
        }
    }

    fun cancelJob2() {
        viewModelScope.launch {
            job2?.cancelAndJoin()
            job2LiveData.postValue(0)
            job2 = null
        }
    }

    fun cancelJob3() {
        viewModelScope.launch {
            job3?.cancelAndJoin()
            job3LiveData.postValue(0)
            job3 = null
        }
    }

    fun cancelAllJob() {
        cancelJob1()
        cancelJob2()
        cancelJob3()
    }

    private fun random() = Random.nextLong(20, 1000)

    private fun reset() {
        job1LiveData.postValue(0)
        job2LiveData.postValue(0)
        job3LiveData.postValue(0)
        statusLiveData.postValue(false)
    }
}