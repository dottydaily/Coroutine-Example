package com.example.coroutineworkshop

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChristmasViewModel: ViewModel() {
    companion object {
        const val defaultShowText = "Show"
        const val defaultSyncText = "Sync - 10 seconds"
        const val defaultAsyncText = "Async - 10 seconds"
        const val defaultSuspendText = "Suspend - 10 second."
    }

    enum class ButtonEnum { SHOW, SYNC, ASYNC, SUSPEND }

    // LiveData for update each button.
    val treeAppearanceStatus = MutableLiveData<Boolean>().apply { value = false }
    fun swapTreeAppearance() {
        val current = treeAppearanceStatus.value
        treeAppearanceStatus.postValue(current?.not() ?: false)
    }

    private val _syncButtonTextLiveData = MutableLiveData<String>().apply { value = defaultSyncText }
    val syncButtonTextLiveData: LiveData<String>
        get() = _syncButtonTextLiveData

    private val _asyncButtonTextLiveData = MutableLiveData<String>().apply { value = defaultAsyncText }
    val asyncButtonTextLiveData: LiveData<String>
        get() = _asyncButtonTextLiveData

    private val _suspendButtonTextLiveData = MutableLiveData<String>().apply { value = defaultSuspendText }
    val suspendButtonTextLiveData: LiveData<String>
        get() = _suspendButtonTextLiveData

    private fun count1To10(enum: ButtonEnum, liveData: MutableLiveData<String>) {
        val prefix = when (enum) {
            ButtonEnum.SYNC -> { "Sync" }
            ButtonEnum.ASYNC -> { "Async" }
            ButtonEnum.SUSPEND -> { "Suspend" }
            else -> { "Else" }
        }

        for (step in ChristmasActivity.MAX_SECOND-1 downTo 0) {
            Thread.sleep(1000)

            liveData.postValue("$step ...")
            Log.d("COUNTDOWN", "$prefix -> Step $step")
        }
        treeAppearanceStatus.postValue(false)
        liveData.postValue("$prefix 10 seconds")
    }

    private suspend fun count1To10Suspend(enum: ButtonEnum,
                                          liveData: MutableLiveData<String>): Int {
        var count = 0
        return withContext(Dispatchers.IO) {
            count1To10(enum, liveData)
            count = ChristmasActivity.MAX_SECOND
            count
        }
    }

    fun count1To10UiThread() {
        count1To10(ButtonEnum.SYNC, _syncButtonTextLiveData)
    }

    fun count1To10UiThreadWithSuspend(context: Context) {
        viewModelScope.launch {
            val total = count1To10Suspend(ButtonEnum.SUSPEND, _suspendButtonTextLiveData)

            if (total == ChristmasActivity.MAX_SECOND) {
                Toast.makeText(context,
                    "Completed countdown with suspend function", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun count1To10Background() {
        viewModelScope.launch(Dispatchers.IO) {
            count1To10(ButtonEnum.ASYNC, _asyncButtonTextLiveData)
        }
    }
}