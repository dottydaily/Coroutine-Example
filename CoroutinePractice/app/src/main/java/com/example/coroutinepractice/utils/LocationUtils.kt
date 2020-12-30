package com.example.coroutinepractice.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.lang.reflect.InvocationTargetException
import kotlin.jvm.Throws
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationUtils {

    /**
     * A method for request the current location.
     * @param context The Application's [Context].
     * @param fusedLocationClient The [FusedLocationProviderClient] to use for requesting the location.
     * @param timeoutMillis The [Long] value for using as a timeout of this request.
     * @return Return [Deferred] of [Location]
     */
    @SuppressLint("MissingPermission")
    @Throws(ResolvableApiException::class)
    suspend fun getCurrentLocationAsync(context: Context,
                                fusedLocationClient: FusedLocationProviderClient,
                                locationRequest: LocationRequest,
                                timeoutMillis: Long
    ): Deferred<Location?> {
        return GlobalScope.async(Dispatchers.IO) {
            var resultLocation: Location? = null
            if (hasAllowLocationPermission(context)) {
                val hasTurnOnSetting = hasLocationSettingTurnOn(context)
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
                } else {
                    throw LocationSettingDisabledException()
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
     * A method for checking if has turn on the location setting or not.
     * @param context The Application's [Context].
     */
    fun hasLocationSettingTurnOn(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            Log.e("COROUTINE-PRACTICES", e.localizedMessage)
        }
        return gpsEnabled && networkEnabled
    }

    /**
     * A method for checking if has enable location setting or not.
     * @param context The Application's [Context].
     * @param locationRequest The [LocationRequest] uses for check location setting.
     * @param onSuccessListener The Listener that will triggered when has turn on location.
     * @param onFailedListener The Listener that will be triggered when has turn off location.
     */
    fun checkLocationSetting(context: Context,
                             locationRequest: LocationRequest,
                             onSuccessListener: (() -> Unit)? = null,
                             onFailedListener: ((Exception) -> Unit)? = null
    ) {
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())
        task.addOnCompleteListener { onSuccessListener?.invoke() }
        task.addOnFailureListener { onFailedListener?.invoke(it) }
    }

    /**
     * A method for calculate the distance between coordinate 1 and 2.
     * @param lat1 The latitude of coordinate 1.
     * @param lon1 The longitude of coordinate 1.
     * @param lat2 The latitude of coordinate 2.
     * @param lon2 The longitude of coordinate 2.
     * @return The distance between these coordinates
     */
    fun calDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double) : Double {
        val r = 6_371_000.0
        val d2r = Math.PI / 180.0
        val rLat1 = lat1 * d2r
        val rLat2 = lat2 * d2r
        val dLat = (lat2 - lat1) * d2r
        val dLon = (lon2 - lon1) * d2r
        val a = ( sin(dLat / 2) * sin(dLat / 2) ) +
                cos(rLat1) * cos (rLat2) * ( sin(dLon / 2) * sin(dLon / 2) )
        return 2 * r * atan2(sqrt(a), sqrt(1 - a))
    }
}

class LocationPermissionDeniedException : Exception("Coarse or Fine location permission hasn't been allowed.")
class LocationSettingDisabledException : Exception("Location setting is off.")