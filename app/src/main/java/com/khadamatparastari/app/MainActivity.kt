package com.khadamatparastari.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.khadamatparastari.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
    }
}
