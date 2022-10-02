package com.example.ugd.Activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.ugd.PushNotification.NotificationReceiver
import com.example.ugd.R
import com.example.ugd.databinding.ActivityRegisterBinding
import com.example.ugd.room.User
import com.example.ugd.room.UserDB

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val CHANNEL_ID = "channel_01"
    private val notificationId = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val db by lazy { UserDB(this) }
        val userDao = db.userDao()

        supportActionBar?.hide()

        createChannel()

        var inputUsername = binding.etUsername
        var inputPassword = binding.etPassword
        var inputEmail = binding.etEmail
        var inputNoTelp = binding.etPhone
        var inputTanggal = binding.etTanggal

        binding.btnSignUp.setOnClickListener(View.OnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            val mBundle = Bundle()

            val username: String = inputUsername.getText().toString()
            val password: String = inputPassword.getText().toString()
            val email: String = inputEmail.getText().toString()
            val tanggal: String = inputTanggal.getText().toString()
            val telfon: String = inputNoTelp.getText().toString()
            var checkRegister = false

            mBundle.putString("username", inputUsername.text.toString())
            mBundle.putString("email", inputEmail.text.toString())
            mBundle.putString("password", inputPassword.text.toString())
            mBundle.putString("Tanggallahir", inputTanggal.text.toString())
            mBundle.putString("NoHandphone",inputTanggal.text.toString())

            if(username.isEmpty()){
                inputUsername.setError("Username must be filled with text")
                checkRegister = false
            }
            else if(password.isEmpty()){
                inputPassword.setError("Password must be filled with text")
                checkRegister = false
            }
            else if(email.isEmpty()){
                inputEmail.setError("Email must be filled with text")
                checkRegister = false
            }
            else if(tanggal.isEmpty()){
                inputTanggal.setError("Tanggal must be filled with text")
                checkRegister = false
            }
            else if(telfon.isEmpty()){
                inputNoTelp.setError("No Telp must be filled with text")
                checkRegister = false
            }

            if(!username.isEmpty() && !tanggal.isEmpty() && !email.isEmpty() && !telfon.isEmpty()&& !password.isEmpty()) {
                checkRegister = true
            }

            if(!checkRegister){
                return@OnClickListener
            }

            val user = User(0, username, email, telfon, tanggal, password)
            userDao.addUser(user)

            sendNotification()
            intent.putExtra("Register", mBundle)
            startActivity(intent)
        })
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE)  as NotificationManager

            notificationManager.createNotificationChannel(channel1)
        }
    }

    private fun sendNotification() {

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val broadcastIntent: Intent = Intent(this, NotificationReceiver:: class.java)
        broadcastIntent.putExtra("toastMessage", "Hi " + binding.etUsername.setText("Berhasil Sign Up").toString())
        val actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val bigPictureBitmap = ContextCompat.getDrawable(this, R.drawable.profile)?.toBitmap()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setSound(defaultSoundUri)
            .setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(bigPictureBitmap)
            )
            .setContentTitle(binding?.etUsername?.text.toString())
            .setContentText("Berhasil Sign Up Account")
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setColor(Color.BLUE)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_reply_24, "Reply", actionIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)){
            notify(notificationId, builder.build())
        }
    }
}