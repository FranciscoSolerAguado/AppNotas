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
        private const val DATABASE_VERSION = 4 // Versión incrementada para forzar la actualización
        private const val DATABASE_NAME = "db_notas"

        // Tabla Notas
        private const val TABLE_NOTAS = "notas"
        private const val COLUMN_NOTAS_ID = "id"
        private const val COLUMN_NOTAS_TITULO = "titulo"
        private const val COLUMN_NOTAS_CONTENIDO = "contenido"
        private const val COLUMN_NOTAS_FECHA = "fecha"
        private const val COLUMN_NOTAS_COLOR = "color"
        private const val COLUMN_NOTAS_ID_CATEGORIA = "id_categoria"

        // Tabla Categorías
        private const val TABLE_CATEGORIAS = "categorias"
        private const val COLUMN_CATEGORIAS_ID = "id"
        private const val COLUMN_CATEGORIAS_NAME = "name"
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableNotas = """
            CREATE TABLE $TABLE_NOTAS (
                $COLUMN_NOTAS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOTAS_TITULO TEXT,
                $COLUMN_NOTAS_CONTENIDO TEXT,
                $COLUMN_NOTAS_FECHA TEXT,
                $COLUMN_NOTAS_COLOR TEXT,
                $COLUMN_NOTAS_ID_CATEGORIA INTEGER,
                FOREIGN KEY($COLUMN_NOTAS_ID_CATEGORIA) REFERENCES $TABLE_CATEGORIAS($COLUMN_CATEGORIAS_ID) ON DELETE SET NULL
            )
        """
        db?.execSQL(createTableNotas)

        val createTableCategorias = """
            CREATE TABLE $TABLE_CATEGORIAS (
                $COLUMN_CATEGORIAS_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CATEGORIAS_NAME TEXT NOT NULL UNIQUE
            )
        """
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
        val values = ContentValues().apply {
            put(COLUMN_NOTAS_TITULO, nota.title)
            put(COLUMN_NOTAS_CONTENIDO, nota.content)
            put(COLUMN_NOTAS_FECHA, nota.date)
            put(COLUMN_NOTAS_COLOR, nota.color)
            put(COLUMN_NOTAS_ID_CATEGORIA, nota.category?.id)
        }
        val id = db.insert(TABLE_NOTAS, null, values)
        db.close()
        return id
    }

    fun actualizarNota(nota: Nota) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOTAS_TITULO, nota.title)
            put(COLUMN_NOTAS_CONTENIDO, nota.content)
            put(COLUMN_NOTAS_FECHA, nota.date)
            put(COLUMN_NOTAS_COLOR, nota.color)
            put(COLUMN_NOTAS_ID_CATEGORIA, nota.category?.id)
        }
        db.update(TABLE_NOTAS, values, "$COLUMN_NOTAS_ID = ?", arrayOf(nota.id.toString()))
        db.close()
    }

    fun eliminarNota(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NOTAS, "$COLUMN_NOTAS_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    private fun getNotesWithQuery(query: String, selectionArgs: Array<String>?): MutableList<Nota> {
        val lista = mutableListOf<Nota>()
        val db = this.readableDatabase
        db.rawQuery(query, selectionArgs).use { cursor ->
            while (cursor.moveToNext()) {
                val categoria = if (!cursor.isNull(cursor.getColumnIndexOrThrow("category_id"))) {
                    Categoria(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("category_id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("category_name"))
                    )
                } else {
                    null
                }

                val nota = Nota(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_ID)),
                    title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_TITULO)),
                    content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_CONTENIDO)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_FECHA)),
                    color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTAS_COLOR)),
                    category = categoria
                )
                lista.add(nota)
            }
        }
        db.close()
        return lista
    }

    fun obtenerNotas(): MutableList<Nota> {
        val query = """
            SELECT
                n.$COLUMN_NOTAS_ID, n.$COLUMN_NOTAS_TITULO, n.$COLUMN_NOTAS_CONTENIDO, n.$COLUMN_NOTAS_FECHA, n.$COLUMN_NOTAS_COLOR,
                c.$COLUMN_CATEGORIAS_ID AS category_id, c.$COLUMN_CATEGORIAS_NAME AS category_name
            FROM $TABLE_NOTAS n LEFT JOIN $TABLE_CATEGORIAS c ON n.$COLUMN_NOTAS_ID_CATEGORIA = c.$COLUMN_CATEGORIAS_ID
        """
        return getNotesWithQuery(query, null)
    }

    fun obtenerNotasPorCategoria(idCategoria: Int): MutableList<Nota> {
        val query = """
            SELECT
                n.$COLUMN_NOTAS_ID, n.$COLUMN_NOTAS_TITULO, n.$COLUMN_NOTAS_CONTENIDO, n.$COLUMN_NOTAS_FECHA, n.$COLUMN_NOTAS_COLOR,
                c.$COLUMN_CATEGORIAS_ID AS category_id, c.$COLUMN_CATEGORIAS_NAME AS category_name
            FROM $TABLE_NOTAS n LEFT JOIN $TABLE_CATEGORIAS c ON n.$COLUMN_NOTAS_ID_CATEGORIA = c.$COLUMN_CATEGORIAS_ID
            WHERE n.$COLUMN_NOTAS_ID_CATEGORIA = ?
        """
        return getNotesWithQuery(query, arrayOf(idCategoria.toString()))
    }

    fun obtenerNotaPorId(id: Int): Nota? {
        val query = """
            SELECT
                n.$COLUMN_NOTAS_ID, n.$COLUMN_NOTAS_TITULO, n.$COLUMN_NOTAS_CONTENIDO, n.$COLUMN_NOTAS_FECHA, n.$COLUMN_NOTAS_COLOR,
                c.$COLUMN_CATEGORIAS_ID AS category_id, c.$COLUMN_CATEGORIAS_NAME AS category_name
            FROM $TABLE_NOTAS n LEFT JOIN $TABLE_CATEGORIAS c ON n.$COLUMN_NOTAS_ID_CATEGORIA = c.$COLUMN_CATEGORIAS_ID
            WHERE n.$COLUMN_NOTAS_ID = ?
        """
        val notes = getNotesWithQuery(query, arrayOf(id.toString()))
        return if (notes.isNotEmpty()) notes[0] else null
    }

    // --- Métodos para Categorías ---

    fun insertarCategoria(categoria: Categoria): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CATEGORIAS_NAME, categoria.name)
        }
        val id = db.insert(TABLE_CATEGORIAS, null, values)
        db.close()
        return id
    }

    fun obtenerCategorias(): MutableList<Categoria> {
        val lista = mutableListOf<Categoria>()
        val db = this.readableDatabase
        db.rawQuery("SELECT * FROM $TABLE_CATEGORIAS ORDER BY $COLUMN_CATEGORIAS_NAME ASC", null).use { cursor ->
            while (cursor.moveToNext()) {
                lista.add(
                    Categoria(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIAS_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIAS_NAME))
                    )
                )
            }
        }
        db.close()
        return lista
    }

    fun eliminarCategoria(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_CATEGORIAS, "$COLUMN_CATEGORIAS_ID = ?", arrayOf(id.toString()))
        db.close()
    }
}
