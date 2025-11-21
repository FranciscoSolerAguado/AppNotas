package com.fran.appnotas.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fran.appnotas.model.Nota

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
        val createTable = """
        CREATE TABLE $TABLE_NAME (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_TITULO TEXT NOT NULL,
            $COLUMN_CONTENT TEXT NOT NULL,
            $COLUMN_DATE TEXT,
            $COLUMN_COLOR TEXT,
            $COLUMN_CATEGORY TEXT
        )
    """
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertarNota(nota: Nota): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITULO, nota.title)
            put(COLUMN_CONTENT, nota.content)
            put(COLUMN_DATE, nota.date)
            put(COLUMN_COLOR, nota.color)
            put(COLUMN_CATEGORY, nota.category)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun actualizarNota(nota: Nota): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITULO, nota.title)
            put(COLUMN_CONTENT, nota.content)
            put(COLUMN_DATE, nota.date)
            put(COLUMN_COLOR, nota.color)
            put(COLUMN_CATEGORY, nota.category)
        }
        return db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(nota.id.toString()))
    }

    fun obtenerNotaPorId(id: Int): Nota? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID=?",
            arrayOf(id.toString())
        )

        return if (cursor.moveToFirst()) {
            Nota(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
            )
        } else null
    }

    fun obtenerNotas(): MutableList<Nota> {
        val lista = mutableListOf<Nota>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_DATE DESC", null)

        while (cursor.moveToNext()) {
            lista.add(
                Nota(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
                )
            )
        }
        return lista
    }

    fun eliminarNota(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
    }


}