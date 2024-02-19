package com.ozarski.githubuserrepo.dataclasses


data class GraphQLResponse(val data: GraphQLData)
data class GraphQLData(val user: GraphQLDataUser)

data class GraphQLDataUser(
    val login: String,
    val repositories: GraphQLDataRepositories
)

data class GraphQLDataRepositories(
    val nodes: List<GraphQLDataRepoNode>
) {
    data class GraphQLDataRepoNode(
        val name: String,
        val refs: GraphQLDataDefaultBranchRef
    ) {

        data class GraphQLDataDefaultBranchRef(
            val nodes: List<GraphQLDataBranchNode>
        )
    }
}

data class GraphQLDataBranchNode(
    val name: String,
    val target: GraphQLDataBranchTarget
) {
    data class GraphQLDataBranchTarget(
        val oid: String
    )
}
