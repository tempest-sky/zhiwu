package com.zhiwu.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zhiwu.app.data.dao.CategoryDao
import com.zhiwu.app.data.dao.ItemDao
import com.zhiwu.app.data.dao.TagDao
import com.zhiwu.app.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 值物应用数据库
 */
@Database(
    entities = [
        Item::class,
        Category::class,
        Tag::class,
        ItemTagCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ZhiWuDatabase : RoomDatabase() {
    
    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    
    companion object {
        @Volatile
        private var INSTANCE: ZhiWuDatabase? = null
        
        /** 预设分类 */
        private val PRESET_CATEGORIES = listOf(
            Category(name = "电子产品", icon = "devices", isPreset = true, sortOrder = 0),
            Category(name = "服饰鞋包", icon = "checkroom", isPreset = true, sortOrder = 1),
            Category(name = "家居用品", icon = "home", isPreset = true, sortOrder = 2),
            Category(name = "食品饮料", icon = "restaurant", isPreset = true, sortOrder = 3),
            Category(name = "学习办公", icon = "school", isPreset = true, sortOrder = 4),
            Category(name = "其他", icon = "more_horiz", isPreset = true, sortOrder = 5)
        )
        
        fun getDatabase(context: Context): ZhiWuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ZhiWuDatabase::class.java,
                    "zhiwu_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 首次创建数据库时插入预设分类
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.categoryDao()?.insertCategories(PRESET_CATEGORIES)
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}