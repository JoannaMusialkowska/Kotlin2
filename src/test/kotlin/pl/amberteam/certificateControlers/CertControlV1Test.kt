package pl.amberteam.certificateControlers

import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import pl.amberteam.trainingControler.TrainingModel
import kotlin.test.assertEquals

class CertControlV1Test: BaseTest() {
//wykorzystując setup z BaseTest uruchamiam GET dla wszystkich certyfikatów V1
    @Test
    @DisplayName ("GET /certificates/all")
    fun getAllCertificates(){
        Given {
            given().log().all()
        } When {
            get("/certificates/all")
        } Then {
            log().all()
            statusCode( 200)
        }
    }
//wykorzystując BaseTest uruchamiam POST jednego nowego certyfikatu
    @Test
    @DisplayName ("POST /certificate")
    fun makeNewCertificate(){
        val bodyString = "{\n" +
                "  \"id\": 6,\n" +
                "  \"organization\": \"ATT\",\n" +
                "  \"name\": \"Asia\",\n" +
                "  \"period\": \"zawsze\",\n" +
                "  \"trade\": \"psychologia sportu\"\n" +
                "}"
        val certificateId = Given {
            contentType(ContentType.JSON)
            body(bodyString)
            log().body().log().method()

        } When {
            post("/certificate")
        } Then {
            statusCode(201)
            log().body()
        } Extract {
            response().jsonPath().getString("id")
        }

       val responseCertificate = Given {
            param("id", certificateId)
        } When {
            get("/certificate")
        } Then {
            statusCode (200)
            log().ifValidationFails()
        } Extract {
            response()
        }
    //sprawdzam, czy poprawnie dodało się ORGANIZATION
        val organizationOnResponse = responseCertificate.jsonPath().getString("organization")
        assertEquals("ATT", organizationOnResponse)

    //sprawdzam, czy poprawnie dodało się NAME
        val nameOnResponse = responseCertificate.jsonPath().getString("name")
        assertEquals("Asia", nameOnResponse)

    //sprawdzam, czy poprawnie dodało się PERIOD
        val periodOnResponse = responseCertificate.jsonPath().getString("period")
        assertEquals("zawsze", periodOnResponse)

    //sprawdzam, czy poprawnie dodało się TRADE
        val tradeOnResponse = responseCertificate.jsonPath().getString("trade")
        assertEquals("psychologia sportu", tradeOnResponse)
    }

    @Test
    @DisplayName("PUT /certificate/{id}")
    fun changeCertificate(){
        val bodyString = "{\n" +
                "  \"id\": 6,\n" +
                "  \"organization\": \"ATT\",\n" +
                "  \"name\": \"Asia MG\",\n" +
                "  \"period\": \"zawsze albo i nie\",\n" +
                "  \"trade\": \"teoria wszystkiego\"\n" +
                "}"


        val trainingId = Given {
            contentType(ContentType.JSON)
            body(bodyString)

        } When {
            post("/certificate")
        } Then {
            statusCode(201)
            log().body()
        } Extract {
            response().jsonPath().getString("id")
        }

        val response = Given {
            pathParam("id", trainingId)
            contentType(ContentType.JSON)
            body(bodyString)
        } When {
            put("/certificate/{id}")
        } Then {
            statusCode(201)
            log().ifValidationFails()
        }


        val responseCertificate = Given {
            param("id", trainingId)
        } When {
            get("/training")
        } Then {
            statusCode (200)
            log().body()
        } Extract {
            response()
        }


        //sprawdzam, czy poprawnie zmieniło się NAME
        val nameOnResponse = responseCertificate.jsonPath().getString("name")
        assertEquals("Asia MG", nameOnResponse)

        //sprawdzam, czy poprawnie zmieniło się PERIOD
        val periodOnResponse = responseCertificate.jsonPath().getString("period")
        assertEquals("zawsze albo i nie", periodOnResponse)

        //sprawdzam, czy poprawnie zmieniło się TRADE
        val tradeOnResponse = responseCertificate.jsonPath().getString("trade")
        assertEquals("teoria wszystkiego", tradeOnResponse)
    }

    @Test
    @DisplayName ("DELETE /certificate/{id}")
    fun deleteCertificate(){
        val bodyString = "{\n" +
                "  \"id\": 6,\n" +
                "  \"organization\": \"ATT\",\n" +
                "  \"name\": \"Asia MG\",\n" +
                "  \"period\": \"zawsze albo i nie\",\n" +
                "  \"trade\": \"teoria wszystkiego\"\n" +
                "}"

        val expectedMsg = "Brak szkolenia o id: "

     val certificateId = Given {
         contentType(ContentType.JSON)
         body(bodyString)
     } When {
         post("/certificate")
     } Then {
         statusCode(201)
         log().body()
     } Extract {
         response().jsonPath().getString("id")
     }

        Given {
            pathParam("id", certificateId)
        } When{
            delete("certificate/{id}")
        } Then {
            log().body()
            statusCode(200)
        }

//        Given {
//          pathParam("id", trainingId)
//    } When{
//         delete("training/{id}")
//     } Then {
//         log().body()
//         statusCode(404)
//   }

     val responseMsg = Given{
            param("id", certificateId)
        } When {
            get ("/training")
        } Then {
            statusCode(404)
            log().body()
        } Extract {
         response().jsonPath().getString("message")
     }
        assertEquals(expectedMsg+certificateId, responseMsg )
        print(expectedMsg+certificateId)

    }
}