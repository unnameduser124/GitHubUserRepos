package com.ozarski.github_api.dataclasses

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class Repo(
    val name: String,
    val fork: Boolean,
    val owner: Owner,
    var branches: MutableList<Branch>
)