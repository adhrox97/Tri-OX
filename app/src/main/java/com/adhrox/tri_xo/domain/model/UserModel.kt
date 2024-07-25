package com.adhrox.tri_xo.domain.model

import com.adhrox.tri_xo.data.network.model.UserData

data class UserModel(
    val uid: String,
    val userName: String
)

fun UserData.toModel() = UserModel(uid = uid.orEmpty(), userName = userName.orEmpty())