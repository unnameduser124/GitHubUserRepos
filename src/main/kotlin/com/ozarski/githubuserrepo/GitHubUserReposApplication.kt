package com.ozarski.githubuserrepo

import com.google.gson.Gson
import okhttp3.OkHttpClient
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class GitHubUserReposApplication

@Configuration
class AppConfig{
	@Bean
	fun okHttpClient(): OkHttpClient {
		return OkHttpClient()
	}

	@Bean
	fun gson(): Gson {
		return Gson()
	}
}


fun main(args: Array<String>) {
	runApplication<GitHubUserReposApplication>(*args)
}
