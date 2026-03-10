package com.wantime.wbangapp.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 *  on 2018/1/18.
 * //https://www.jianshu.com/p/91fc8305b41a
 */
class DBSQLiteOpenHelper(
    context: Context,
    name: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        private val varsion = 1
        private val dataPath = "data.db3"
        private var instance: DBSQLiteOpenHelper? = null
        @Synchronized
        fun getInstance(context: Context): DBSQLiteOpenHelper {
            if (instance == null) {
                instance = DBSQLiteOpenHelper(context,dataPath,null,varsion)
            }
            return instance!!
        }

        fun closeDB() {
            if (instance != null) {
                try {
                    val db = instance!!.writableDatabase
                    db.close()
                } catch (e: Exception) { }
                instance = null
            }
        }
    }


    override fun onCreate(db: SQLiteDatabase) {

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }


}