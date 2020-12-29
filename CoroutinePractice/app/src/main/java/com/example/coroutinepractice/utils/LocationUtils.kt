package com.example.coroutinepractice.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.*

object LocationUtils {

    /**
     * A method for request the current location.
     * @param context The Application's [Context].
     * @param fusedLocationClient The [FusedLocationProviderClient] to use for requesting the location.
     * @param timeoutMillis The [Long] value for using as a timeout of this request.
     * @return Return [Deferred] of [Location]
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationAsync(context: Context,
                                fusedLocationClient: FusedLocationProviderClient,
                                locationRequest: LocationRequest,
                                timeoutMillis: Long
    ): Deferred<Location?> {
        return GlobalScope.async(Dispatchers.IO) {
            var resultLocation: Location? = null
            if (hasAllowLocationPermission(context)) {
                try {
                    val hasTurnOnSetting = hasLocationSetting(context, locationRequest).await()
                    if (hasTurnOnSetting) {
                        val job = launch(Dispatchers.IO) {
                            fusedLocationClient.getCurrentLocation(
                                LocationRequest.PRIORITY_HIGH_ACCURACY, null
                            ).addOnCompleteListener {
                                resultLocation = it.result
                                this.cancel()
                            }.addOnFailureListener {
                                this.cancel()
                            }

                            var timeout = timeoutMillis
                            while (timeout != 0L && this.isActive) {
                                Log.d("COROUTINE-PRACTICES", "Permission request timeout=${timeout/1000}s")
                                delay(1000L)
                                timeout -= 1000L
                            }
                        }
                        job.join()
                    }
                } catch (e: ResolvableApiException) {
                    Log.e("COROUTINE-PRACTICES", e.localizedMessage)
                    this.cancel()
                    throw e
                }
                resultLocation
            } else {
                throw LocationPermissionDeniedException()
            }
        }
    }

    /**
     * A method for checking if has all of the required location permissions or not.
     * @param context The Application's [Context].
     * @return Return true if already allow both COARSE & FINE location permission. Otherwise, return false.
     */
    fun hasAllowLocationPermission(context: Context): Boolean {
        val hasCoarseLocation = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasFineLocation = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return hasCoarseLocation && hasFineLocation
    }

    /**
     * A method for checking if has enable location setting or not.
     * @param context The Application's [Context].
     * @param locationRequest The [LocationRequest] uses for check location setting.
     */
    suspend fun hasLocationSetting(context: Context, locationRequest: LocationRequest): Deferred<Boolean> {
        var hasTurnOnSetting = false
        return GlobalScope.async(Dispatchers.IO) {
            val settingJob = launch {
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                val client = LocationServices.getSettingsClient(context)
                val task = client.checkLocationSettings(builder.build())

                task.addOnCompleteListener {
                    val exception = it.exception
                    if (exception != null) {
                        Log.d("COROUTINE-PRACTICES", "Throw exception (addOnCompleteListener)")
                        //TODO: This cause the InvocationTargetException (WHY? We still don't know it yet.)
                        throw exception
                    }

                    hasTurnOnSetting = true
                    if (isActive) cancel()
                }
                task.addOnFailureListener {
                    if (isActive) cancel()
                    Log.d("COROUTINE-PRACTICES", "Throw exception (addOnFailureListener)")
                    //TODO: This cause the InvocationTargetException (WHY? We still don't know it yet.)
                    throw it
                }

                var timeout = 5000
                while (timeout > 0L && isActive) {
                    Log.d("COROUTINE-PRACTICES", "Setting request timeout=${timeout/1000}s")
                    delay(1000L)
                    timeout -= 1000
                }
            }
            settingJob.join()
            hasTurnOnSetting
        }
    }
}

class LocationPermissionDeniedException : Exception("Coarse or Fine location permission hasn't been allowed.")