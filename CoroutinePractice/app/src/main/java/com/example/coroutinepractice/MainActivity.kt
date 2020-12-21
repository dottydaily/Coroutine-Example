package com.example.coroutinepractice

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.coroutinepractice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    // ViewModel
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            viewModel.runAllProcess()
        }

        // Observe
        viewModel.run {
            job1LiveData.observe(this@MainActivity) {
                binding.job1TextView.run {
                    text = "$it %"

                    if (it == 100) {
                        this.setTextColor(Color.GREEN)
                    }
                }
            }

            job2LiveData.observe(this@MainActivity) {
                binding.job2TextView.run {
                    text = "$it %"

                    if (it == 100) {
                        this.setTextColor(Color.GREEN)
                    }
                }
            }

            job3LiveData.observe(this@MainActivity) {
                binding.job3TextView.run {
                    text = "$it %"

                    if (it == 100) {
                        this.setTextColor(Color.GREEN)
                    }
                }
            }

            statusLiveData.observe(this@MainActivity) { isDone ->
                if (isDone) {
                    binding.root.setBackgroundColor(Color.GREEN)
                }
            }
        }
    }
}