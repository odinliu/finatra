package com.twitter.finatra.http.filters

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.inject.Test
import com.twitter.util.{Await, Future}
import java.util.Locale

class HttpResponseFilterTest extends Test {

  val respFilter = new HttpResponseFilter[Request]
  val rfc7231Regex = """^(?:Mon|Tue|Wed|Thu|Fri|Sat|Sun), \d\d (?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) \d{4} \d\d:\d\d:\d\d GMT$"""

  "test response header" in {
    val originalLocale = Locale.getDefault
    /*
     * Chinese Locale's Date would be formatted as Chinese Character,
     * which must not respect `RFC 7231 Date Format`
     */
    Locale.setDefault(Locale.CHINA)

    val service = Service.mk[Request, Response] { request =>
      val response = Response()
      response.setStatusCode(200)
      response.setContentString("test header")
      Future(response)
    }
    val request = Request()

    try {
      val response = Await.result(respFilter.apply(request, service))

      response.version should equal(request.version)
      response.server should equal(Some("Finatra"))
      response.contentType should equal(Some("application/octet-stream"))
      response.date.getOrElse("") should fullyMatch regex rfc7231Regex
    } finally {
      service.close()
    }

    // reset JVM's locale to the original one
    Locale.setDefault(originalLocale)
  }
}
