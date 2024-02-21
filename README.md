# GitHub User Repos

A simple Spring Boot application that retrieves repositories of a user given their username from the GitHub API.

### Prerequisites
- Java 21
- Gradle 8.5
- GitHub personal access token (replace placeholder in GitHubAPIConfig.kt file)

### Endpoints
- `GET /repo/{username}` - retrieves not forked repositories with branches for specified GitHub user
- `GET /graphql/{username}` - retrieves not forked repositories for specified GitHub user using GraphQL query, retrieves only up to 100 branches for each repository
