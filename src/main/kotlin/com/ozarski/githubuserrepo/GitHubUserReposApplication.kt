package com.ozarski.githubuserrepo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitHubUserReposApplication

fun main(args: Array<String>) {
	runApplication<GitHubUserReposApplication>(*args)
}
