package com.example.coroutinepractice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.random.Random

class MainViewModel: ViewModel() {
    enum class JobStatus {
        WAITING, RUNNING, COMPLETED
    }

    // Job
    var job1: Job? = null
    var job2: Job? = null
    var job3: Job? = null
    var jobsInOrder: Job? = null

    // LiveData
    val job1LiveData = MutableLiveData<Int>().apply { value = 0 }
    val job2LiveData = MutableLiveData<Int>().apply { value = 0 }
    val job3LiveData = MutableLiveData<Int>().apply { value = 0 }
    val statusLiveData = MutableLiveData<JobStatus>().apply { value = JobStatus.WAITING }

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
                setStatus(JobStatus.COMPLETED)
            }

//            val deferredList = listOf(result1, result2, result3)
//            if (!deferredList.awaitAll().contains(false)) {
//                statusLiveData.postValue(true)
//            }
        }
    }

    fun runAllJobInOrder() {
        // reset old status and each job value
        reset()

        jobsInOrder = viewModelScope.launch {
            startJob1()
            job1?.join()

            if (jobsInOrder?.isCancelled == false) {
                startJob2()
                job2?.join()

                if (jobsInOrder?.isCancelled == false) {
                    startJob3()
                    job3?.join()
                }
            }
        }
    }

    fun startJob1() {
        job1 = viewModelScope.launch(Dispatchers.IO) {
            setStatus(JobStatus.RUNNING)

            var percent = 0
            while (percent < 100 && isActive) {
                // in case of running with synchronize job
                if (jobsInOrder != null && jobsInOrder?.isCancelled == true) {
                    break
                }

                Thread.sleep(200 ?: random())
                job1LiveData.postValue(++percent)
            }

            setStatusCompletedIfAllDone()
        }
    }

    fun startJob2() {
        job2 = viewModelScope.launch(Dispatchers.IO) {
            setStatus(JobStatus.RUNNING)

            var percent = 0
            while (percent < 100 && isActive) {
                // in case of running with synchronize job
                if (jobsInOrder != null && jobsInOrder?.isCancelled == true) {
                    break
                }

                Thread.sleep(100 ?: random())
                job2LiveData.postValue(++percent)
            }

            setStatusCompletedIfAllDone()
        }
    }

    fun startJob3() {
        job3 = viewModelScope.launch(Dispatchers.IO) {
            setStatus(JobStatus.RUNNING)

            var percent = 0
            while (percent < 100 && isActive) {
                // in case of running with synchronize job
                if (jobsInOrder != null && jobsInOrder?.isCancelled == true) {
                    break
                }

                Thread.sleep(50 ?: random())
                job3LiveData.postValue(++percent)
            }

            setStatusCompletedIfAllDone()
        }
    }

    fun cancelJob1() {
        viewModelScope.launch {
            jobsInOrder?.cancel()
            job1?.cancelAndJoin()
            job1LiveData.postValue(0)

            setStatusWaitingIfAllDone()
        }
    }

    fun cancelJob2() {
        viewModelScope.launch {
            jobsInOrder?.cancel()
            job2?.cancelAndJoin()
            job2LiveData.postValue(0)

            setStatusWaitingIfAllDone()
        }
    }

    fun cancelJob3() {
        viewModelScope.launch {
            jobsInOrder?.cancel()
            job3?.cancelAndJoin()
            job3LiveData.postValue(0)

            setStatusWaitingIfAllDone()
        }
    }

    fun cancelAllJob() {
        viewModelScope.launch {
            cancelJob1()
            cancelJob2()
            cancelJob3()
            jobsInOrder?.cancelAndJoin()
        }
    }

    private fun setStatusWaitingIfAllDone() {
        val hasAllJobIsEnd = (job1?.isCompleted != false &&
                job2?.isCompleted != false && job3?.isCompleted != false)
        if (hasAllJobIsEnd) {
            statusLiveData.postValue(JobStatus.WAITING)
        }
    }

    private fun setStatusCompletedIfAllDone() {
        val hasAllJobComplete = (job1?.isCompleted == true && job1?.isCancelled == false) &&
                                (job2?.isCompleted == true && job2?.isCancelled == false) &&
                                (job3?.isCompleted == true && job3?.isCancelled == false)

        if (hasAllJobComplete) {
            statusLiveData.postValue(JobStatus.COMPLETED)
        }
    }

    private fun random() = Random.nextLong(20, 1000)

    private fun reset() {
        job1LiveData.postValue(0)
        job2LiveData.postValue(0)
        job3LiveData.postValue(0)
        statusLiveData.postValue(JobStatus.WAITING)
    }

    private fun setStatus(status: JobStatus) {
        if (statusLiveData.value != status) {
            statusLiveData.postValue(status)
        }
    }
}