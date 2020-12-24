package com.example.coroutinepractice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
                val job1 = viewModel.job1
                val job2 = viewModel.job2
                val job3 = viewModel.job3

                val hasAnyJobRunning = job1?.isActive == true || job2?.isActive == true || job3?.isActive == true

                if (hasAnyJobRunning) {
                    viewModel.cancelAllJob()
//                    resetStartAllButtonToDefaultUi(true)
                } else {
                    viewModel.runAllJob()
//                    setStartAllButtonToCancelUi()
                }
            }

            startInOrderButton.setOnClickListener {
                val jobs = viewModel.jobsInOrder

                if (jobs == null || jobs.isCompleted) {
                    // Start each job in order
                    viewModel.runAllJobInOrder()
                } else {
                    viewModel.cancelAllJob()
                }
            }

            startButtonJob1.setOnClickListener {
                val job = viewModel.job1

                if (job == null || job.isCompleted) {
                    viewModel.startJob1()
//                    setStartAllButtonToCancelUi()
                } else if (!job.isCompleted) {
                    viewModel.cancelJob1()
//                    resetStartAllButtonToDefaultUi(true)
                }
            }

            startButtonJob2.setOnClickListener {
                val job = viewModel.job2

                if (job == null || job.isCompleted) {
                    viewModel.startJob2()
//                    setStartAllButtonToCancelUi()
                } else if (!job.isCompleted) {
                    viewModel.cancelJob2()
//                    resetStartAllButtonToDefaultUi(true)
                }
            }

            startButtonJob3.setOnClickListener {
                val job = viewModel.job3

                if (job == null || job.isCompleted) {
                    viewModel.startJob3()
//                    setStartAllButtonToCancelUi()
                } else if (!job.isCompleted) {
                    viewModel.cancelJob3()
//                    resetStartAllButtonToDefaultUi(true)
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

                    if (it == 100) {
//                        resetStartAllButtonToDefaultUi()
                    }
                }
            }

            job2LiveData.observe(this@MainActivity) {
                binding.run {
                    updateJobUi(
                        it, viewModel.job2, "Start 2", colorBlue,
                        startButtonJob2, progressBarJob2, job2TextView
                    )

                    if (it == 100) {
//                        resetStartAllButtonToDefaultUi()
                    }
                }
            }

            job3LiveData.observe(this@MainActivity) {
                binding.run {
                    updateJobUi(
                        it, viewModel.job3, "Start 3", colorGreenYellow,
                        startButtonJob3, progressBarJob3, job3TextView
                    )

                    if (it == 100) {
//                        resetStartAllButtonToDefaultUi()
                    }
                }
            }

            statusLiveData.observe(this@MainActivity) { status ->
                binding.run {
                    when (status) {
                        MainViewModel.JobStatus.WAITING -> {
                            resetBottomButtonsToDefaultUi()
                        }
                        MainViewModel.JobStatus.RUNNING -> {
                            if (jobsInOrder?.isActive == true) {
                                startAllButton.visibility = View.INVISIBLE
                                setStartInOrderButtonToCancelUi()
                            } else {
                                startInOrderButton.visibility = View.INVISIBLE
                                setStartAllButtonToCancelUi()
                            }
                        }
                        MainViewModel.JobStatus.COMPLETED -> {
                            resetBottomButtonsToDefaultUi()
                            Toast.makeText(this@MainActivity, "All job is done!", Toast.LENGTH_SHORT).show()
                        }
                    }
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

    private fun resetBottomButtonsToDefaultUi() {
        binding.startAllButton.visibility = View.VISIBLE
        binding.startInOrderButton.visibility = View.VISIBLE
        setStartAllButtonToDefaultUi()
        setStartInOrderButtonToDefaultUi()
    }

    private fun setStartAllButtonToCancelUi() {
        binding.startAllButton.run {
            text = "Cancel All"
            setBackgroundColor(Color.GRAY)
        }
    }

    private fun setStartAllButtonToDefaultUi() {
        binding.startAllButton.run {
            text = "Start All Job"
            setBackgroundColor(getColor(R.color.teal_500))
        }
    }

    private fun setStartInOrderButtonToCancelUi() {
        binding.startInOrderButton.run {
            text = "Cancel Order"
            setBackgroundColor(Color.GRAY)
        }
    }

    private fun setStartInOrderButtonToDefaultUi() {
        binding.startInOrderButton.run {
            text = "Start in Order"
            setBackgroundColor(getColor(R.color.red_200))
        }
    }

    private fun resetStartAllButtonToDefaultUi(forcedDefault: Boolean = false) {
        viewModel.run {
            val job1Percent = job1LiveData.value
            val job2Percent = job2LiveData.value
            val job3Percent = job3LiveData.value

            val job1Done = job1?.isCompleted == true || job1Percent == 100
            val job2Done = job2?.isCompleted == true || job2Percent == 100
            val job3Done = job3?.isCompleted == true || job3Percent == 100

            if (forcedDefault || (job1Done && job2Done && job3Done)) {
                binding.startAllButton.run {
                    text = "Start All Job"
                    setBackgroundColor(getColor(R.color.teal_500))
                }
            }
        }
    }
}