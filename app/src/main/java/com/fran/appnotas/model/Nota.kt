package com.fran.appnotas.model

data class Nota(
    var id: Int,
    var title: String,
    var content: String,
    var date: String?,
    var color: String?,
    var category: Categoria? // Cambiado a un objeto Categoria nulable
)
