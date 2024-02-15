package com.ozarski.github_api.dataclasses

import com.google.gson.annotations.Expose

data class Repo(
    val name: String,
    val fork: Boolean,
    val owner: Owner,
    val branches: MutableList<Branch> = mutableListOf()
)