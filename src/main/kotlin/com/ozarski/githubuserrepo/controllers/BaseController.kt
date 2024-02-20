package com.ozarski.githubuserrepo.controllers

import com.google.gson.Gson
import com.ozarski.githubuserrepo.GitHubAPIRequestHandler
import com.ozarski.githubuserrepo.dataclasses.ResultRepo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

data class ErrorResponse(val status: Int, val message: String)

@RestController
class BaseController(private val gitHubAPIRequestHandler: GitHubAPIRequestHandler, private val gson: Gson) {


    @GetMapping("/repo/{username}")
    fun userRepos(
        @PathVariable username: String,
    ): ResponseEntity<Any> {
        val userRepos = gitHubAPIRequestHandler.getCompleteData(username)
        val formattedRepos = userRepos.map { ResultRepo(it) }
        return ResponseEntity.ok(formattedRepos)
    }

    @GetMapping("/graphql/{username}")
    fun userReposGraphQL(
        @PathVariable username: String,
    ): ResponseEntity<Any> {
        val userRepos = gitHubAPIRequestHandler.getReposWithBranchesGraphQL(username)
        return ResponseEntity.ok(userRepos)
    }
}
