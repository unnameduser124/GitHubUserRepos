package com.ozarski.githubuserrepo.dataclasses

import com.ozarski.github_api.dataclasses.Branch

data class ResultBranch(val name: String, val lastCommitSha: String) {
    constructor(branch: Branch): this(branch.name, branch.commit.sha)
}
