package br.edu.infnet.firebasetest

data class Empresa(
    var id: String? = null,
    var name: String? = null,
    var adress: String? = null,
    var is_approved: Boolean? = null,
    var comments: String? = null,
)