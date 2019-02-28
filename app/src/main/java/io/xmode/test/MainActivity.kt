package io.xmode.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private companion object {
        const val TAG = "wrx"
        const val HELLO_STRING_LENGTH = 5
        const val PERMISSION_REQUEST_CODE_LOCATION = 100
    }

    private lateinit var textView : TextView

    /** lifecycle callback */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = id_text_view
        setClickableText()

        Log.d(TAG, "on create")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE_LOCATION)
        }
    }

    /** permission request callback */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "permission granted")
                } else {
                    Toast.makeText(applicationContext, R.string.location_permission_failed_toast, Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    /** setup the textview to handle clicks */
    private fun setClickableText() {
        val helloClick = object : ClickableSpan() {
            override fun onClick(view : View?) {
                Toast.makeText(applicationContext, "Task #1 completed.", Toast.LENGTH_SHORT).show()
                LocationFetchReceiver.scheduleAlarm(this@MainActivity)
            }
        }

        val spannableString = SpannableString(textView.text)

        spannableString.setSpan(helloClick, 0, HELLO_STRING_LENGTH,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }
}
