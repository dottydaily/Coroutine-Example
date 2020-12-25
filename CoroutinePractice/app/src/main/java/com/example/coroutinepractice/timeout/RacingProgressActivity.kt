package com.example.coroutinepractice.timeout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.coroutinepractice.databinding.ActivityRacingProgressBinding

class RacingProgressActivity : AppCompatActivity() {
    companion object {
        const val TIMEOUT = 2500L
        const val TARGET = 10000L
    }

    // ViewBinding's object.
    private lateinit var binding: ActivityRacingProgressBinding

    // The ViewModel of this Activity
    private val viewModel by viewModels<RacingProgressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRacingProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.progress1LiveData.observe(this) {
            var progress1Percent = ((it.toDouble() / TARGET) * 100).toInt()
            progress1Percent = if (progress1Percent > 100) 100 else progress1Percent
            binding.progress1Bar.progress = progress1Percent
            binding.progress1ValueTextView.text = "$progress1Percent%"
        }

        viewModel.progress2LiveData.observe(this) {
            var progress2Percent = ((it.toDouble() / TARGET) * 100).toInt()
            progress2Percent = if (progress2Percent > 100) 100 else progress2Percent
            binding.progress2Bar.progress = progress2Percent
            binding.progress2ValueTextView.text = "$progress2Percent%"
        }

        viewModel.statusLiveData.observe(this) { status ->
            binding.statusTextView.text = when (status) {
                RacingProgressViewModel.RacingJobStatus.READY -> { "READY" }
                RacingProgressViewModel.RacingJobStatus.RUNNING -> { "RUNNING" }
                RacingProgressViewModel.RacingJobStatus.COMPLETED -> { "COMPLETED" }
                RacingProgressViewModel.RacingJobStatus.TIMEOUT -> { "TIMEOUT" }
            }
        }

        binding.startButton.setOnClickListener {
            viewModel.start(TIMEOUT, TARGET)
        }
    }
}