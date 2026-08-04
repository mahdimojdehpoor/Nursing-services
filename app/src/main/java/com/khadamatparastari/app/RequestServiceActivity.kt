package com.khadamatparastari.app

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.SmsManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.khadamatparastari.app.databinding.ActivityRequestServiceBinding
import java.text.NumberFormat
import java.util.Locale

class RequestServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestServiceBinding
    private val PRICE_PER_SERVICE = 1_000_000
    private val OWNER_PHONE_NUMBER = "09307674048"
    private val CARD_NUMBER = "5859831144131066"

    private var pendingSmsBody: String? = null

    private val requestSmsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingSmsBody?.let { sendSms(it) }
            } else {
                AlertDialog.Builder(this)
                    .setTitle("دسترسی پیامک لازم است")
                    .setMessage("برای ثبت و اطلاع‌رسانی درخواست، دسترسی پیامک باید از تنظیمات گوشی فعال شود.")
                    .setPositiveButton("رفتن به تنظیمات") { _, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", packageName, null)
                        startActivity(intent)
                    }
                    .setNegativeButton("بستن", null)
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceTitle = intent.getStringExtra("SERVICE_TITLE") ?: ""
        binding.tvSelectedService.text = "خدمت انتخابی: $serviceTitle"

        val checkBoxes = listOf(
            binding.cbInjection to "تزریقات",
            binding.cbSerum to "سرم درمانی",
            binding.cbDressing to "پانسمان",
            binding.cbMassage to "ماساژ",
            binding.cbSuction to "ساکشن",
            binding.cbCatheter to "سوند گذاری"
        )

        checkBoxes.forEach { (checkBox, _) ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                updateTotalPrice(checkBoxes.map { it.first })
            }
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val customerPhone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val selectedServices = checkBoxes.filter { it.first.isChecked }.map { it.second }

            if (name.isEmpty() || customerPhone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "لطفاً همه فیلدها را پر کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedServices.isEmpty()) {
                Toast.makeText(this, "لطفاً حداقل یک خدمت را انتخاب کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val totalPrice = selectedServices.size * PRICE_PER_SERVICE
            val formattedPrice = NumberFormat.getNumberInstance(Locale("fa", "IR")).format(totalPrice)

            val smsBody = buildString {
                append("درخواست جدید خدمات پرستاری\n")
                append("نام: $name\n")
                append("شماره تماس مشتری: $customerPhone\n")
                append("آدرس: $address\n")
                append("خدمات: ${selectedServices.joinToString("، ")}\n")
                append("مبلغ قابل پرداخت: $formattedPrice ریال")
            }

            sendSmsWithPermissionCheck(smsBody)
        }

        binding.tvContactPhone.setOnClickListener { copyToClipboard("شماره تماس", OWNER_PHONE_NUMBER) }
        binding.tvCardNumber.setOnClickListener { copyToClipboard("شماره کارت", CARD_NUMBER) }
    }

    private fun copyToClipboard(label: String, value: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
        Toast.makeText(this, "$label کپی شد", Toast.LENGTH_SHORT).show()
    }

    private fun sendSmsWithPermissionCheck(body: String) {
        pendingSmsBody = body
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            sendSms(body)
        } else {
            requestSmsPermissionLauncher.launch(Manifest.permission.SEND_SMS)
        }
    }

    private fun sendSms(body: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val parts = smsManager.divideMessage(body)
            smsManager.sendMultipartTextMessage(OWNER_PHONE_NUMBER, null, parts, null, null)
            Toast.makeText(this, "درخواست شما با موفقیت ارسال شد", Toast.LENGTH_LONG).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "ارسال پیامک ناموفق بود، دوباره تلاش کنید", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateTotalPrice(checkBoxes: List<CheckBox>) {
        val selectedCount = checkBoxes.count { it.isChecked }
        val totalPrice = selectedCount * PRICE_PER_SERVICE
        val formattedPrice = NumberFormat.getNumberInstance(Locale("fa", "IR")).format(totalPrice)
        binding.tvTotalPrice.text = "مبلغ قابل پرداخت: $formattedPrice ریال"
    }
}
