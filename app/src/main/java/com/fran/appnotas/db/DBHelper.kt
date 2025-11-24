package com.fran.appnotas.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.fran.appnotas.model.Categoria
import com.fran.appnotas.model.Nota

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 2 // Versión incrementada
        private const val DATABASE_NAME = "db_notas"

        // Tabla Notas
        private const val TABLE_NOTAS = "notas"
        private const val COLUMN_NOTAS_ID = "id"
        private const val COLUMN_NOTAS_TITULO = "titulo"
        private const val COLUMN_NOTAS_CONTENIDO = "contenido"
        private const val COLUMN_NOTAS_FECHA = "fecha"
        private const val COLUMN_NOTAS_COLOR = "color"
        private const val COLUMN_NOTAS_CATEGORY = "categoria"

        // Tabla Categorías
        private const val TABLE_CATEGORIAS = "categorias"
        private const val COLUMN_CATEGORIAS_ID = "id"
        private const val COLUMN_CATEGORIAS_NOMBRE = "nombre"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableNotas = "CREATE TABLE $TABLE_NOTAS ( " +
                "$COLUMN_NOTAS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NOTAS_TITULO TEXT, " +
                "$COLUMN_NOTAS_CONTENIDO TEXT, " +
                "$COLUMN_NOTAS_FECHA TEXT, " +
                "$COLUMN_NOTAS_COLOR TEXT, " +
                "$COLUMN_NOTAS_CATEGORY TEXT)"
        db?.execSQL(createTableNotas)

        val createTableCategorias = "CREATE TABLE $TABLE_CATEGORIAS ( " +
                "$COLUMN_CATEGORIAS_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_CATEGORIAS_NOMBRE TEXT NOT NULL UNIQUE)"
        db?.execSQL(createTableCategorias)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NOTAS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIAS")
        onCreate(db)
    }

    // --- Métodos para Notas ---

    fun insertarNota(nota: Nota): Long {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NOTAS_TITULO, nota.title)
        values.put(COLUMN_NOTAS_CONTENIDO, nota.content)
        values.put(COLUMN_NOTAS_FECHA, nota.date)
        values.put(COLUMN_NOTAS_COLOR, nota.color)
        values.put(COLUMN_NOTAS_CATEGORY, nota.category)

        val id = db.insert(TABLE_NOTAS, null, values)
        db.close()
        return id
    }

    fun actualizarNota(nota: Nota) {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NOTAS_TITULO, nota.title)
        values.put(COLUMN_NOTAS_CONTENIDO, nota.content)
        values.put(COLUMN_NOTAS_FECHA, nota.date)
        values.put(COLUMN_NOTAS_COLOR, nota.color)
        values.put(COLUMN_NOTAS_CATEGORY, nota.category)

        db.update(TABLE_NOTAS, values, "$COLUMN_NOTAS_ID = ?", arrayOf(nota.id.toString()))
        db.close()
    }

    fun eliminarNota(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NOTAS, "$COLUMN_NOTAS_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    fun obtenerNotas(): MutableList<Nota> {
        val lista = mutableListOf<Nota>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_NOTAS", null)

        if (cursor!!.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_ID))
                val titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_TITULO))
                val contenido = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_CONTENIDO))
                val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_FECHA))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_COLOR))
                val categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_CATEGORY))

                lista.add(Nota(id, titulo, contenido, fecha, color, categoria))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun obtenerNotaPorId(id: Int): Nota? {
        val db = this.readableDatabase
        val cursor: Cursor? =
            db.rawQuery("SELECT * FROM $TABLE_NOTAS WHERE $COLUMN_NOTAS_ID = ?", arrayOf(id.toString()))

        var nota: Nota? = null

        if (cursor!!.moveToFirst()) {
            val notaId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_ID))
            val titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_TITULO))
            val contenido = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_CONTENIDO))
            val fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_FECHA))
            val color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_COLOR))
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_CATEGORY))

            nota = Nota(notaId, titulo, contenido, fecha, color, categoria)
        }

        cursor.close()
        db.close()
        return nota
    }

    // --- Métodos para Categorías ---

    fun insertarCategoria(categoria: Categoria): Long {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_CATEGORIAS_NOMBRE, categoria.nombre)
        val id = db.insert(TABLE_CATEGORIAS, null, values)
        db.close()
        return id
    }

    fun obtenerCategorias(): MutableList<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_CATEGORIAS", null)

        if (cursor!!.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIAS_ID))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIAS_NOMBRE))
                lista.add(Categoria(id, nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    fun eliminarCategoria(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_CATEGORIAS, "$COLUMN_CATEGORIAS_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}
