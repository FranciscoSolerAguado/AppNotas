package com.fran.appnotas.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "appnotas.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "notas"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITULO = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_COLOR = "color"
        private const val COLUMN_CATEGORY = "category"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(
        db: SQLiteDatabase?, oldVersion: Int, newVersion: Int
    ) {
        TODO("Not yet implemented")
    }
}