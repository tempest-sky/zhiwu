package com.zhiwu.app

import android.app.Application
import com.zhiwu.app.data.database.ZhiWuDatabase

/**
 * 值物应用Application类
 */
class ZhiWuApplication : Application() {
    
    val database by lazy { ZhiWuDatabase.getDatabase(this) }
    
    override fun onCreate() {
        super.onCreate()
    }
}