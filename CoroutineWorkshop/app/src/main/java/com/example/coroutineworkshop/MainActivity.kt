package com.example.coroutineworkshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.coroutineworkshop.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    companion object {
        const val MAX_SECOND = 10
        const val DEFAULT_TEXT = "$MAX_SECOND ..."
    }

    private lateinit var viewModel: MainViewModel

    // ViewBinding
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.run {
            showButton.setOnClickListener {
                viewModel.swapTreeAppearance()
            }

            timerSyncButton.run {
                setOnClickListener {
                    text = DEFAULT_TEXT
                    viewModel.count1To10UiThread()
                }
            }

            timerAsyncButton.run {
                setOnClickListener {
                    text = DEFAULT_TEXT
                    viewModel.count1To10Background()
                }
            }

            timerSuspendButton.run {
                setOnClickListener {
                    text = DEFAULT_TEXT
                    viewModel.count1To10UiThreadWithSuspend(this@MainActivity)
                }
            }
        }

        observeTreeVisibility()
        observeButtonText()
    }

    private fun observeTreeVisibility() {
        viewModel.treeAppearanceStatus.observe(this) { isShowing ->
            binding.showButton.run {
                text = if (isShowing) "Hide" else "Show"
            }
            binding.lottieChristmasTree.let { tree ->
                tree.visibility = if (isShowing) View.VISIBLE else View.INVISIBLE

                if (tree.visibility == View.VISIBLE) {
                    Toast.makeText(this@MainActivity,
                        "Show Christmas tree!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeButtonText() {
        viewModel.syncButtonTextLiveData.observe(this) {
            binding.timerSyncButton.text = it
        }
        viewModel.asyncButtonTextLiveData.observe(this) {
            binding.timerAsyncButton.text = it
        }
        viewModel.suspendButtonTextLiveData.observe(this) {
            binding.timerSuspendButton.text = it
        }
    }
}