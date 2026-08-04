package com.khadamatparastari.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.khadamatparastari.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val smsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "دسترسی پیامک فعال شد", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "بدون این دسترسی، درخواست‌های مشتریان به شما اطلاع داده نمی‌شود",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val services = listOf(
            Service(1, "پرستاری در منزل", "مراقبت روزانه یا شبانه از سالمند و بیمار در منزل", "از ۳۰۰,۰۰۰ تومان"),
            Service(2, "تزریقات و پانسمان", "انجام تزریقات، سرم‌تراپی و تعویض پانسمان توسط پرستار", "از ۱۰۰,۰۰۰ تومان"),
            Service(3, "مراقبت از سالمند", "همراهی و مراقبت تخصصی از سالمندان", "از ۲۵۰,۰۰۰ تومان"),
            Service(4, "مراقبت بعد از عمل", "مراقبت‌های پرستاری بعد از جراحی در منزل", "از ۳۵۰,۰۰۰ تومان"),
            Service(5, "فیزیوتراپی در منزل", "خدمات فیزیوتراپی توسط کارشناس در منزل", "از ۲۰۰,۰۰۰ تومان")
        )

        binding.rvServices.layoutManager = LinearLayoutManager(this)
        binding.rvServices.adapter = ServiceAdapter(services) { selectedService ->
            val intent = Intent(this, RequestServiceActivity::class.java)
            intent.putExtra("SERVICE_TITLE", selectedService.title)
            startActivity(intent)
        }

        checkAndRequestSmsPermission()
    }

    private fun checkAndRequestSmsPermission() {
        val alreadyGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) ==
                PackageManager.PERMISSION_GRANTED

        if (alreadyGranted) return

        val canShowRationale = shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)

        if (!canShowRationale && wasPermissionAskedBefore()) {
            // یعنی کاربر قبلاً رد کرده و گزینه «دیگر نشان نده» فعال شده
            showGoToSettingsDialog()
        } else {
            showExplanationDialog()
        }
        markPermissionAsAsked()
    }

    private fun showExplanationDialog() {
        AlertDialog.Builder(this)
            .setTitle("دسترسی ارسال پیامک")
            .setMessage(
                "این دسترسی صرفاً برای اطلاع‌رسانی به شرکت خدمات‌رسان از ثبت درخواست و پرداخت وجه توسط مشتری استفاده می‌شود. " +
                        "بدون این دسترسی، از درخواست‌های مشتریان باخبر نخواهید شد."
            )
            .setCancelable(false)
            .setPositiveButton("اجازه می‌دهم") { _, _ ->
                smsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
            }
            .setNegativeButton("فعلاً نه") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showGoToSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("دسترسی پیامک غیرفعال است")
            .setMessage(
                "برای این‌که از درخواست مشتریان باخبر شوید، لطفاً از تنظیمات گوشی، دسترسی پیامک را برای این برنامه فعال کنید."
            )
            .setCancelable(true)
            .setPositiveButton("رفتن به تنظیمات") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("فعلاً نه") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun wasPermissionAskedBefore(): Boolean {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return prefs.getBoolean("sms_permission_asked", false)
    }

    private fun markPermissionAsAsked() {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("sms_permission_asked", true).apply()
    }
}
