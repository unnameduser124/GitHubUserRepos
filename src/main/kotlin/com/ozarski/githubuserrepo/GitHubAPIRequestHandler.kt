package com.ozarski.githubuserrepo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ozarski.github_api.dataclasses.Branch
import com.ozarski.github_api.dataclasses.Repo
import com.ozarski.githubuserrepo.dataclasses.GraphQLDataUser
import com.ozarski.githubuserrepo.dataclasses.GraphQLResponse
import com.ozarski.githubuserrepo.dataclasses.ResultRepo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Service


@Service
class GitHubAPIRequestHandler(private val okHttpClient: OkHttpClient, private val gson: Gson) {


    fun getRepos(user: String, page: Int = 1, pageSize: Int = DEFAULT_PAGE_SIZE): List<Repo> {
        val url = "https://api.github.com/users/$user/repos?per_page=$pageSize&page=$page&type=all"

        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${GitHubAPIConfig.PERSONAL_ACCESS_TOKEN}")
            .build()

        val call = okHttpClient.newCall(request)
        val response = call.execute()
        if (response.code != 200) {
            println("Error: ${response.code} ${response.message}")
            return emptyList()
        }
        val json = response.body?.string()
        val itemType = object : TypeToken<List<Repo>>() {}.type
        return gson.fromJson(json, itemType)
    }

    fun getBranches(user: String, repoName: String, page: Int = 1, pageSize: Int = DEFAULT_PAGE_SIZE): List<Branch>? {
        val url = "https://api.github.com/repos/$user/$repoName/branches?page=$page&per_page=$pageSize"

        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .addHeader("Authorization", "Bearer ${GitHubAPIConfig.PERSONAL_ACCESS_TOKEN}")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (response.code != 200) {
            println("Error: ${response.code} ${response.message}")
            return emptyList()
        }
        val json = response.body?.string()
        val itemType = object : TypeToken<List<Branch>>() {}.type
        return gson.fromJson(json, itemType)
    }

    fun getReposWithBranches(repos: List<Repo>): List<Repo> {
        for (repo in repos) {
            val branches = mutableListOf<Branch>()
            var page = 1
            var responseSize = 0
            do{
                with(getBranches(repo.owner.login, repo.name, page = page)) {
                    if (!this.isNullOrEmpty()){
                        responseSize = this.size
                        branches.addAll(this)
                    }
                    else{
                        responseSize = 0
                    }
                }
                page++
            }
            while (responseSize == DEFAULT_PAGE_SIZE)
            repo.branches = branches
        }
        return repos
    }

    fun getCompleteData(username: String): List<Repo> {
        val repos = mutableListOf<Repo>()
        var page = 1

        //var tempRepos = getNotForkedRepos(username, page = page)
        var tempRepos = getRepos(username, page = page)

        while (tempRepos.isNotEmpty()) {
            page++
            repos.addAll(tempRepos)
            if (tempRepos.size < DEFAULT_PAGE_SIZE) break
            tempRepos = getRepos(username, page = page)
        }
        val filteredRepos = repos.filter { !it.fork }

        return getReposWithBranches(filteredRepos)
    }

    //solution using GraphQL API (same result data but less requests)
    fun parseGraphQLResponse(response: String): GraphQLDataUser? {
        if(response.isEmpty()) return null
        val type = object : TypeToken<GraphQLResponse>() {}.type
        val data = gson.fromJson<GraphQLResponse>(response, type)
        return data.data.user
    }

    fun getReposWithBranchesGraphQL(username: String): List<ResultRepo> {
        var after = "null"
        var queryWithPaging =
            """{ "query": "query{ user(login: \"$username\") { repositories(isFork: false, first: $DEFAULT_PAGE_SIZE, after: $after) { nodes { name owner { login } refs(first: $DEFAULT_PAGE_SIZE, refPrefix:\"refs/heads/\") { nodes { name target{ oid } } } } pageInfo{ hasNextPage endCursor } } } }" }"""
        var response = executeGraphQLQuery(queryWithPaging)
        val repoList = mutableListOf<ResultRepo>()
        var parsedResponse = parseGraphQLResponse(response) ?: return emptyList()
        repoList.addAll(parsedResponse.repositories.nodes.map { ResultRepo(it) })

        while (parsedResponse.repositories.pageInfo.hasNextPage) {
            after = """\"${parsedResponse.repositories.pageInfo.endCursor}\""""
            queryWithPaging =
                """{ "query": "query{ user(login: \"$username\") { repositories(isFork: false, first: $DEFAULT_PAGE_SIZE, after: $after) { nodes { name owner { login } refs(first: $DEFAULT_PAGE_SIZE, refPrefix:\"refs/heads/\") { nodes { name target{ oid } } } } pageInfo{ hasNextPage endCursor } } } }" }"""
            response = executeGraphQLQuery(queryWithPaging)
            parsedResponse = parseGraphQLResponse(response) ?: break
            repoList.addAll(parsedResponse.repositories.nodes.map { ResultRepo(it) })
        }
        return repoList
    }

    fun executeGraphQLQuery(query: String): String {
        val requestBody = query.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.github.com/graphql")
            .header("Authorization", "Bearer ${GitHubAPIConfig.PERSONAL_ACCESS_TOKEN}")
            .post(requestBody)
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (response.code != 200) {
            println("Error: ${response.code} ${response.message}")
            return ""
        }
        return response.body?.string() ?: ""
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 100
    }

}
