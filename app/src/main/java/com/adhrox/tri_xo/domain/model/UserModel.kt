package com.adhrox.tri_xo.domain.model

import com.adhrox.tri_xo.data.network.model.UserData

data class UserModel(
    val uid: String,
    val userName: String,
    val email: String,
    val gamesInfo: MutableMap<String, Int>
)

fun UserData.toModel() = UserModel(uid = uid.orEmpty(), userName = userName.orEmpty(), email = email ?: "Correo no disponible", gamesInfo = gamesInfo?.toMutableMap() ?: mutableMapOf("win" to 0, "tie" to 0, "lose" to 0, "total" to 0))