package com.khadamatparastari.app

import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.khadamatparastari.app.databinding.ActivityRequestServiceBinding
import java.text.NumberFormat
import java.util.Locale

class RequestServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestServiceBinding
    private val PRICE_PER_SERVICE = 1_000_000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceTitle = intent.getStringExtra("SERVICE_TITLE") ?: ""
        binding.tvSelectedService.text = "خدمت انتخابی: $serviceTitle"

        val checkBoxes = listOf(
            binding.cbInjection,
            binding.cbSerum,
            binding.cbDressing,
            binding.cbMassage,
            binding.cbSuction,
            binding.cbCatheter
        )

        checkBoxes.forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ ->
                updateTotalPrice(checkBoxes)
            }
        }

        binding.btnSubmit.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val selectedCount = checkBoxes.count { it.isChecked }

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "لطفاً همه فیلدها را پر کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedCount == 0) {
                Toast.makeText(this, "لطفاً حداقل یک خدمت را انتخاب کنید", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "درخواست شما با موفقیت ثبت شد", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun updateTotalPrice(checkBoxes: List<CheckBox>) {
        val selectedCount = checkBoxes.count { it.isChecked }
        val totalPrice = selectedCount * PRICE_PER_SERVICE
        val formattedPrice = NumberFormat.getNumberInstance(Locale("fa", "IR")).format(totalPrice)
        binding.tvTotalPrice.text = "مبلغ قابل پرداخت: $formattedPrice ریال"
    }
}
