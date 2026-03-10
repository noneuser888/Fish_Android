package com.wantime.wbangapp.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.wantime.wbangapp.app.WBangApp
import org.json.JSONObject

/**
 *  on 2018/1/18.
 */

class SQLHelper private constructor() {
    private val columnName = "columns"

    companion object {
        private var applicationContext: Context? = null
        private var instance: SQLHelper? = null

        fun onInitHelper(applicationContext: Context?) {
            this.applicationContext = applicationContext
        }

        fun getInstance(applicationContext: Context?): SQLHelper {
            if (instance == null && applicationContext != null)
                synchronized(SQLHelper::class.java) {
                    instance = SQLHelper()
                }
            this.applicationContext = applicationContext

            return instance!!
        }

        fun onDestroy() {
            DBSQLiteOpenHelper.closeDB()
            applicationContext = null
        }
    }


    private fun getWritableDatabase(): SQLiteDatabase {
        return DBSQLiteOpenHelper.getInstance(applicationContext!!).writableDatabase
    }

    private fun isTableExist(tableName: String): Boolean {
        var isExist = false
        val createSql = getSQL(tableName)
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getWritableDatabase()
            db.execSQL(createSql)
            if (db.isOpen) {
                cursor = db.query(tableName, null, null, null, null, null, null)
                while (cursor!!.moveToNext()) {
                    isExist = true
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return isExist
    }

    fun getSqlData(tableName: String): JSONObject {
        var tempObject = JSONObject()
        val createSql = getSQL(tableName)
        var db: SQLiteDatabase? = null
        var cursor: Cursor? = null
        try {
            db = getWritableDatabase()
            db.execSQL(createSql)
            if (db.isOpen) {
                cursor = db.query(tableName, null, null, null, null, null, null)
                while (cursor!!.moveToNext()) {
                    val jsonByteArray = cursor.getBlob(cursor.getColumnIndex(columnName))
                    tempObject = JSONObject(String(jsonByteArray))
                }
            }
        } catch (e: Exception) {
            tempObject=JSONObject()
            e.printStackTrace()
        } finally {
            cursor?.close()
        }


        return tempObject
    }

    fun saveSqlData(tableName: String, tempObject: JSONObject?) {
        if (tempObject == null) return
        var db: SQLiteDatabase? = null
        try {
            db = getWritableDatabase()
            if (db.isOpen) {
                val values = ContentValues()
                val data = tempObject.toString().toByteArray()
                values.put(columnName, data)
                if (isTableExist(tableName)) {
                    db.update(tableName, values, null, null)
                } else {
                    db.insert(tableName, null, values)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    /*** 根据标签获得数据库创建SQL  */
    private fun getSQL(tableName: String): String {
        return "create table IF NOT EXISTS  $tableName($columnName BLOB)"
    }
}