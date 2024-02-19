package com.ozarski.githubuserrepo.dataclasses

import com.ozarski.github_api.dataclasses.Repo

data class ResultRepo(val name: String, val ownerLogin: String, val branches: List<ResultBranch>) {
    constructor(repo: Repo): this(repo.name, repo.owner.login, repo.branches.map { ResultBranch(it) })
}