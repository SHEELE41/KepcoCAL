package com.mevius.kepcocal.utils

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ActivityContext
import net.daum.mf.map.api.MapView
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationHelper @Inject constructor(@ActivityContext private val activity: Activity) {
    private val permissionRequestCode = 1001
    private val isLocationApiOnRequestCode = 1002
    fun isLocationPermission(){
        if (checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {   // If permission is granted
            // Check Location API State is ON
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }  // ACCESS_FINE_LOCATION
            val lsqBuilder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client: SettingsClient = LocationServices.getSettingsClient(activity)
            val task: Task<LocationSettingsResponse> =
                client.checkLocationSettings(lsqBuilder.build())

            // If State is ON
            task.addOnSuccessListener {
//                if (it.locationSettingsStates.isLocationPresent) {
//                    // Start Tracking
//                    mapView.currentLocationTrackingMode =
//                        MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading
//                }
            }

            // If State is OFF
            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        exception.startResolutionForResult(
                            activity,
                            isLocationApiOnRequestCode
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        } else {    // If permission isn't granted
            // RequestPermissions (show dialog)
            requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                permissionRequestCode
            )
        }
    }


}