package com.example.coroutinepractice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.coroutinepractice.databinding.ActivityMainBinding
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // ViewModel
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            startAllButton.setOnClickListener {
                viewModel.runAllJob()
            }

            cancelAllButton.setOnClickListener {
                viewModel.cancelAllJob()
            }

            startButtonJob1.setOnClickListener {
                val job = viewModel.job1

                if (job == null) {
                    viewModel.startJob1()
                } else if (!job.isCompleted) {
                    viewModel.cancelJob1()
                }
            }

            startButtonJob2.setOnClickListener {
                val job = viewModel.job2

                if (job == null) {
                    viewModel.startJob2()
                } else if (!job.isCompleted) {
                    viewModel.cancelJob2()
                }
            }

            startButtonJob3.setOnClickListener {
                val job = viewModel.job3

                if (job == null) {
                    viewModel.startJob3()
                } else if (!job.isCompleted) {
                    viewModel.cancelJob3()
                }
            }
        }

        // Observe
        viewModel.run {
            val colorRed = Color.RED
            val colorBlue = getColor(R.color.colorBlue)
            val colorGreenYellow = getColor(R.color.colorGreenYellow)

            job1LiveData.observe(this@MainActivity) {
                binding.run {
                    updateJobUi(
                        it, viewModel.job1, "Start 1", colorRed,
                        startButtonJob1, progressBarJob1, job1TextView
                    )
                }
            }

            job2LiveData.observe(this@MainActivity) {
                binding.run {
                    updateJobUi(
                        it, viewModel.job2, "Start 2", colorBlue,
                        startButtonJob2, progressBarJob2, job2TextView
                    )
                }
            }

            job3LiveData.observe(this@MainActivity) {
                binding.run {
                    updateJobUi(
                        it, viewModel.job3, "Start 3", colorGreenYellow,
                        startButtonJob3, progressBarJob3, job3TextView
                    )
                }
            }

            statusLiveData.observe(this@MainActivity) { isDone ->
                if (isDone) {
                    Toast.makeText(this@MainActivity, "All job is done!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateJobUi(percent: Int, job: Job?, mainText: String, mainColor: Int,
                            button: Button, progressBar: ProgressBar, percentTextView: TextView) {
        val colorDarkGray = Color.GRAY

        percentTextView.text = "$percent %"
        progressBar.progress = percent

        if (percent == 100 || percent == 0) {
            button.setBackgroundColor(mainColor)
            button.text = mainText
            percentTextView.setTextColor(
                if (percent == 100) mainColor else colorDarkGray
            )
        } else if (job?.isActive == true) {
            button.setBackgroundColor(colorDarkGray)
            button.text = "Cancel"
        }
    }
}