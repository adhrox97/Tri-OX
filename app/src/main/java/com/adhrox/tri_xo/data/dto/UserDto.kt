package com.adhrox.tri_xo.data.dto

data class UserDto(
    val uid: String = "",
    val userName: String,
    val email: String? = null,
    val gamesInfo: Map<String, Int> = mapOf("win" to 0, "tie" to 0, "lose" to 0, "total" to 0)
)