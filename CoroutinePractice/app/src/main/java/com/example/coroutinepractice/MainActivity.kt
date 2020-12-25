package com.example.coroutinepractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coroutinepractice.databinding.ActivityMainBinding
import com.example.coroutinepractice.handle_job.ProgressActivity
import com.example.coroutineworkshop.ChristmasActivity

class MainActivity : AppCompatActivity() {

    // ViewBinding's object.
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.run {
            coroutineScopeButton.setOnClickListener { launchActivity(ChristmasActivity::class.java) }
            handleJobButton.setOnClickListener { launchActivity(ProgressActivity::class.java) }
        }
    }

    ///////////////////
    // Helper method //
    ///////////////////

    /**
     * A method to launch the target [Activity].
     * @param activityClass The target class of [Activity].
     */
    private fun launchActivity(activityClass: Class<*>) {
        val intent = Intent(this@MainActivity, activityClass)
        startActivity(intent)
    }
}