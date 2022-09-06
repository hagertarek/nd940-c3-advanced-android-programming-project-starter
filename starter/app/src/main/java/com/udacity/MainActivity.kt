package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager

    private var baseUrl = ""
    private var fileName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        //creates notification channel
        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )

        custom_button.setOnClickListener {
            when (rgDownloadOptions.checkedRadioButtonId) {
                R.id.rbGlide -> {
                    baseUrl = URL_GLIDE
                    fileName = getString(R.string.glide_option)
                }
                R.id.rbLoadApp -> {
                    baseUrl = URL_LOAD_APP
                    fileName = getString(R.string.load_app_option)
                }
                R.id.rbRetrofit -> {
                    baseUrl = URL_RETROFIT
                    fileName = getString(R.string.retrofit_option)
                }
                else -> {
                    etEnterUrl.text.toString().apply {
                        if (URLUtil.isValidUrl(this)) {
                            baseUrl = this
                            fileName = this
                        }
                    }
                }
            }

            if (baseUrl.isNotBlank() || baseUrl.isNotEmpty()) {
                custom_button.buttonState = ButtonState.Clicked
                download()
            } else  {
                Toast.makeText(this, getString(R.string.invalid_url_message), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun createChannel(channelID: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "loading...."

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            custom_button.buttonState = ButtonState.Completed

            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(
                        cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    )
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            notificationManager.sendNotification(
                                fileName,
                                getString(R.string.success_status),
                                applicationContext
                            )
                        }
                        DownloadManager.STATUS_FAILED -> {
                            notificationManager.sendNotification(
                                fileName,
                                getString(R.string.fail_status),
                                applicationContext
                            )
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        custom_button.buttonState = ButtonState.Loading
        val request =
            DownloadManager.Request(Uri.parse(baseUrl))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)
    }

    override fun onResume() {
        super.onResume()
        baseUrl = ""
    }

    companion object {
        private const val URL_LOAD_APP =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val URL_GLIDE =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val URL_RETROFIT =
            "https://github.com/square/retrofit/archive/master.zip"
    }
}

