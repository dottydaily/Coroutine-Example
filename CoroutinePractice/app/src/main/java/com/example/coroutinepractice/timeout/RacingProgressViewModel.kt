package com.example.coroutinepractice.timeout

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.random.Random

class RacingProgressViewModel: ViewModel() {
    enum class RacingJobStatus {
        READY, RUNNING, COMPLETED, TIMEOUT
    }
    // Job
    var job1: Job? = null
    var job2: Job? = null

    // LiveData
    val progress1LiveData = MutableLiveData<Int>().apply { value = 0 }
    val progress2LiveData = MutableLiveData<Int>().apply { value = 0 }
    val statusLiveData = MutableLiveData<RacingJobStatus>().apply { value = RacingJobStatus.READY }

    /**
     * A method for start coroutines to increase each liveData.
     * @param timeoutMillis The timeout uses for cancel all Jobs.
     * @param target The target uses for set the maximum value that each progress can be reach.
     */
    fun start(timeoutMillis: Long, target: Long) {
        viewModelScope.launch {
            progress1LiveData.postValue(0)
            progress2LiveData.postValue(0)

            try {
                withTimeout(4000) {
                    startProgress1Job(target)
                    startProgress2Job(target)

                    val allJobs = listOfNotNull(job1, job2)
                    allJobs.joinAll()
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("COROUTINE-PRACTICES", e.localizedMessage)
                job1?.cancelAndJoin()
                job2?.cancelAndJoin()
                setStatusLiveDataIfNeeded(RacingJobStatus.TIMEOUT)
            } finally {
                if (job1?.isCancelled != true && job2?.isCancelled != true) {
                    println("Ready")
                    setStatusLiveDataIfNeeded(RacingJobStatus.READY)
                }
            }
        }
    }

    /**
     * A method for start coroutines to increase progress 1 [MutableLiveData]'s value.
     * @param target The target uses for set the maximum value that can be reach.
     */
    private suspend fun startProgress1Job(target: Long) {
        job1 = viewModelScope.launch(Dispatchers.IO) {
            setStatusLiveDataIfNeeded(RacingJobStatus.RUNNING)

            var currentProgress = 0
            while (currentProgress < target && isActive) {
                val random = Random.nextInt(1, 201)
                currentProgress += random
                progress1LiveData.postValue(currentProgress)
                delay(50)
            }

            setStatusLiveDataIfNeeded(RacingJobStatus.COMPLETED)
            if (job2?.isActive == true) job2?.cancel()
        }
    }

    /**
     * A method for start coroutines to increase progress 2 [MutableLiveData]'s value.
     * @param target The target uses for set the maximum value that can be reach.
     */
    private suspend fun startProgress2Job(target: Long) {
        job2 = viewModelScope.launch(Dispatchers.IO) {
            setStatusLiveDataIfNeeded(RacingJobStatus.RUNNING)

            var currentProgress = 0
            while (currentProgress < target && isActive) {
                val random = Random.nextInt(1, 201)
                currentProgress += random
                progress2LiveData.postValue(currentProgress)
                delay(50)
            }

            setStatusLiveDataIfNeeded(RacingJobStatus.COMPLETED)
            if (job1?.isActive == true) job1?.cancel()
        }
    }

    /**
     * A method for set the value of the statusLiveData if needed.
     * @param targetValue The target [RacingJobStatus] to be set into this LiveData.
     */
    private fun setStatusLiveDataIfNeeded(targetValue: RacingJobStatus) {
        if (statusLiveData.value != targetValue) {
            statusLiveData.postValue(targetValue)
        }
    }
}