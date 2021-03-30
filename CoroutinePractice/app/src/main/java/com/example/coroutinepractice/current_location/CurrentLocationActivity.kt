package com.example.coroutinepractice.current_location

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.coroutinepractice.databinding.ActivityCurrentLocationBinding
import com.example.coroutinepractice.utils.AlertDialogUtils
import com.example.coroutinepractice.utils.LocationUtils
import com.example.coroutinepractice.utils.LocationPermissionDeniedException
import com.example.coroutinepractice.utils.LocationSettingDisabledException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class CurrentLocationActivity : AppCompatActivity() {

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 101
        const val LOCATION_SETTING_REQUEST_CODE = 102
    }

    // ViewBinding of this page.
    private lateinit var binding: ActivityCurrentLocationBinding

    // ViewModel for this Activity.
    private val viewModel by viewModels<CurrentLocationViewModel>()

    // FusedLocationClient
    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    // LocationRequest
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000
        fastestInterval = 5000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            setShowing(true)

            this.lifecycleScope.launch {
                try {
                    val result = LocationUtils.getCurrentLocationAsync(
                        this@CurrentLocationActivity, fusedLocationClient,
                        locationRequest, 5000).await()
                    result.let {
                        val lat = it?.latitude
                        val lon = it?.longitude
                        binding.curlocValueTextView.text = "$lat, $lon"
                    }
                    setShowing(false)
                } catch (e: LocationPermissionDeniedException) {
                    ActivityCompat.requestPermissions(this@CurrentLocationActivity,
                        arrayOf(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ), LOCATION_PERMISSION_REQUEST_CODE
                    )
                    Log.e("COROUTINE-PRACTICES", e.localizedMessage)
                    setShowing(false)
                } catch (e: LocationSettingDisabledException) {
                    LocationUtils.checkLocationSetting(
                        this@CurrentLocationActivity, locationRequest,
                        onFailedListener = {
                            if (it is ResolvableApiException) {
                                it.startResolutionForResult(
                                        this@CurrentLocationActivity, LOCATION_SETTING_REQUEST_CODE)
                            } else {
                                setShowing(false)
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                binding.startButton.performClick()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //TODO: Show alert and go to setting to enable location permission.
                    AlertDialogUtils.handleNeverAskLocationAgain(this)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LOCATION_SETTING_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                binding.startButton.performClick()
            } else {
                setShowing(false)
            }
        }
    }

    private fun setShowing(isShowing: Boolean) {
        if (isShowing) {
            binding.startButton.visibility = View.INVISIBLE
            binding.loadingProgressBar.visibility = View.VISIBLE
        } else {
            binding.startButton.visibility = View.VISIBLE
            binding.loadingProgressBar.visibility = View.INVISIBLE
        }
    }
}