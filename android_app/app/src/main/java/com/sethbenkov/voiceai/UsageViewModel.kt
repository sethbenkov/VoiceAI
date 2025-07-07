package com.sethbenkov.voiceai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import com.sethbenkov.voiceai.database.AppDatabase

class UsageViewModel(application: Application) : AndroidViewModel(application) {
    private val usageDao = AppDatabase.getDatabase(application).usageDao()
    val usageList = usageDao.getAllUsage().asLiveData()
} 