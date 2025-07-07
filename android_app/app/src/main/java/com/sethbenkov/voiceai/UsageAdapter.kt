package com.sethbenkov.voiceai

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sethbenkov.voiceai.database.Usage
import com.sethbenkov.voiceai.databinding.ItemUsageBinding

class UsageAdapter : ListAdapter<Usage, UsageAdapter.UsageViewHolder>(UsageDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageViewHolder {
        val binding = ItemUsageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UsageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UsageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UsageViewHolder(private val binding: ItemUsageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(usage: Usage) {
            binding.promptTokensText.text = "Prompt: ${usage.promptTokens}"
            binding.completionTokensText.text = "Completion: ${usage.completionTokens}"
            binding.totalTokensText.text = "Total: ${usage.totalTokens}"
            binding.timestampText.text = java.text.DateFormat.getDateTimeInstance().format(usage.timestamp)
        }
    }

    class UsageDiffCallback : DiffUtil.ItemCallback<Usage>() {
        override fun areItemsTheSame(oldItem: Usage, newItem: Usage): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Usage, newItem: Usage): Boolean = oldItem == newItem
    }
} 