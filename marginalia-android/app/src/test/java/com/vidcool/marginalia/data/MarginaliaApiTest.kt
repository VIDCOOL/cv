package com.vidcool.marginalia.data

import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MarginaliaApiTest {
    private lateinit var server: MockWebServer
    private lateinit var api: MarginaliaApi

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        api = MarginaliaApi(
            client = OkHttpClient(),
            baseUrl = server.url("/").toString().removeSuffix("/"),
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun search_parsesResultsAndSendsApiKeyHeader() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(
                    """
                    {"results":[{"url":"https://example.com","title":"Example","description":"Desc"}],"pages":2}
                    """.trimIndent(),
                ),
        )

        val response = api.search(apiKey = "test-key", query = "hello", count = 10, page = 1, nsfw = 0)

        assertEquals(1, response.results.size)
        assertEquals("Example", response.results.first().title)
        assertEquals(2, response.pages)

        val request = server.takeRequest()
        assertEquals("test-key", request.getHeader("API-Key"))
        assertTrue(request.path!!.contains("query=hello"))
    }

    @Test(expected = MarginaliaApiException::class)
    fun search_maps429ToFriendlyError() {
        server.enqueue(MockResponse().setResponseCode(429).setBody("Daily Limit Exceeded"))
        api.search(apiKey = "public", query = "x", count = 5, page = 1, nsfw = 0)
    }

    @Test
    fun websiteSearchUrl_encodesQuery() {
        val url = api.websiteSearchUrl("hello world")
        assertTrue(url.startsWith("https://marginalia-search.com/search?q="))
        assertTrue(url.contains("hello+world") || url.contains("hello%20world"))
    }
}
