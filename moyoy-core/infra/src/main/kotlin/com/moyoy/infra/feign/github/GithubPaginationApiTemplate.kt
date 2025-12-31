package com.moyoy.infra.feign.github

import org.springframework.http.ResponseEntity

class GithubPaginationApiTemplate {
    companion object {
        private const val RATE_LIMIT_REMAINING_HEADER = "X-RateLimit-Remaining"
        private const val RATE_LIMIT_THRESHOLD = 1000
    }

    fun <T> fetchAll(
        perPage: Int,
        fetcher: (page: Int) -> ResponseEntity<List<T>>
    ): List<T> {
        val allData = mutableListOf<T>()
        var currentPage = 1

        while (true) {
            val response = fetcher(currentPage)
            val body = response.body ?: break

            allData.addAll(body)

            if (body.size < perPage) break

            val remaining = response.headers.getFirst(RATE_LIMIT_REMAINING_HEADER)?.toIntOrNull() ?: 0
            if (remaining <= RATE_LIMIT_THRESHOLD) break

            currentPage++
        }
        return allData
    }
}
