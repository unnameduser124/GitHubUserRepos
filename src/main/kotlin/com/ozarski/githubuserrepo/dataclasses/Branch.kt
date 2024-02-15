package com.ozarski.github_api.dataclasses

data class Branch(
    val name: String,
    val commit: Commit
) {
    data class Commit(val sha: String, val url: String)
}