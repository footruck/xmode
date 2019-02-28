package io.xmode.test

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

class LocationFetchReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "wrx"
        const val ACTION_GET_LOCATION = "ACTION_GET_LOCATION"
        fun getStartIntent(context : Context) : Intent{
            val intent = Intent(context, LocationFetchReceiver::class.java)
            intent.action = ACTION_GET_LOCATION
            return intent
        }

        fun scheduleAlarm(context : Context) {
            Log.d(TAG, "scheduling alarm")
            val alarmService = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = getStartIntent(context)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            alarmService.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), if(BuildConfig.DEBUG) 1000 * 60 else AlarmManager.INTERVAL_HOUR, pendingIntent)
        }
    }

    /** broadcast receiving method for boot completed and alarmmanager event */
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "received $intent")
        if (intent.action == ACTION_GET_LOCATION) {
            getLocation(context)
        } else {
            scheduleAlarm(context)
        }
    }

    /** get location from fused location service */
    private fun getLocation(context : Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Please grant location permission to fetch location", Toast.LENGTH_SHORT).show()
            return
        }

        /**
         * use fused location service (superset for Android location system service)
         * for auto choosing the best source available and reduced power consumption when in background
         * */
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnCompleteListener { task : Task<Location> ->
            if (task.isSuccessful) {
                Log.d(TAG, "got location ${task.result}")
                Toast.makeText(context, "Location ${task.result}", Toast.LENGTH_SHORT).show()
                //TODO: (1) use shared preference (2) use database for storing last known location
            } else {
                Log.e(TAG, "Failed to get location", task.exception)
            }
        }
    }
}
