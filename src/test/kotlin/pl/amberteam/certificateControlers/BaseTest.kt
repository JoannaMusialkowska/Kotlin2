package pl.amberteam.certificateControlers

import io.restassured.RestAssured.*
import org.junit.jupiter.api.BeforeAll

open class BaseTest {
    companion object {
        protected const val API_VERSION = "1"

        @BeforeAll
        @JvmStatic
        fun setup() {
            baseURI = "http://localhost"
            basePath = "/api/rest/v${API_VERSION}"
            port = 9999
                    }

    }
}