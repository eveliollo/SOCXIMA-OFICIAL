package com.example

import android.app.Application
import com.example.db.MemoryDatabase

class SocximaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MemoryDatabase.initialize(this)
    }
}
