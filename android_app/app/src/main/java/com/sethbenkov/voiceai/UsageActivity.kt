package com.sethbenkov.voiceai

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sethbenkov.voiceai.databinding.ActivityUsageBinding

class UsageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsageBinding
    private val viewModel: UsageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.usageRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = UsageAdapter()
        binding.usageRecyclerView.adapter = adapter

        viewModel.usageList.observe(this) { usageList ->
            adapter.submitList(usageList)
        }
    }
} 