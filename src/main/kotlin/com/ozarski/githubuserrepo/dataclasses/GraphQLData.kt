package com.ozarski.githubuserrepo.dataclasses


data class GraphQLResponse(val data: GraphQLData)
data class GraphQLData(val user: GraphQLDataUser)

data class GraphQLDataUser(
    val login: String,
    val repositories: GraphQLDataRepositories
)

data class GraphQLDataPageInfo(
    val hasNextPage: Boolean,
    val endCursor: String
)

data class GraphQLDataRepositories(
    val nodes: List<GraphQLDataRepoNode>,
    val pageInfo: GraphQLDataPageInfo
) {
    data class GraphQLDataRepoNode(
        val name: String,
        val refs: GraphQLDataDefaultBranchRef,
        val owner: GraphQLDataOwner
    ) {
        data class GraphQLDataDefaultBranchRef(
            val nodes: List<GraphQLDataBranchNode>
        )
    }
}

data class GraphQLDataOwner(
    val login: String
)

data class GraphQLDataBranchNode(
    val name: String,
    val target: GraphQLDataBranchTarget
) {
    data class GraphQLDataBranchTarget(
        val oid: String
    )
}
