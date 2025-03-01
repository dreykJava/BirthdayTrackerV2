package com.example.birthdaytrackerv2.ui.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "BirthdayDatabase.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_NAME = "man_table"
        const val ID = "id"
        const val NAME = "name"
        const val BIRTHDAY_DATE_DAY = "date_day"
        const val BIRTHDAY_DATE_MONTH = "date_month"
        const val BIRTHDAY_DATE_YEAR = "date_year"
    }

    // Создание таблицы
    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = ("CREATE TABLE $TABLE_NAME ("
                + "$ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$NAME TEXT,"
                + "$BIRTHDAY_DATE_DAY TEXT,"
                + "$BIRTHDAY_DATE_MONTH TEXT,"
                + "$BIRTHDAY_DATE_YEAR TEXT)")
        db?.execSQL(createTableQuery)
    }

    // Обновление базы данных (если версия изменилась)
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Метод для добавления данных
    fun insertData(name: String, date_day: String, date_month: String, date_year: String?): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(NAME, name)
            put(BIRTHDAY_DATE_DAY, date_day)
            put(BIRTHDAY_DATE_MONTH, date_month)
            put(BIRTHDAY_DATE_YEAR, date_year)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    // Метод для чтения данных
    fun readData(): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    //TODO Сделать возможность редактирования записей

    // Метод для обновления данных
    fun updateData(id: Int, name: String, date_day: String, date_month: String, date_year: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(NAME, name)
            put(BIRTHDAY_DATE_DAY, date_day)
            put(BIRTHDAY_DATE_MONTH, date_month)
            put(BIRTHDAY_DATE_YEAR, date_year)
        }
        return db.update(TABLE_NAME, values, "$ID=?", arrayOf(id.toString()))
    }

    // Метод для удаления данных
    fun deleteData(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$ID=?", arrayOf(id.toString()))
    }
}