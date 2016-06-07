package com.twitter.finatra.http.tests.integration.startup

import com.twitter.finatra.http.internal.server.BaseHttpServer
import com.twitter.finatra.http.modules.ResponseBuilderModule
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.Test

class BaseHttpServerStartupIntegrationTest extends Test {

  "BaseHttpServer startup" in {
    val server = new EmbeddedHttpServer(
      twitterServer = new BaseHttpServer {
        override val modules = Seq(ResponseBuilderModule)
      })

    server.start()
    server.assertHealthy()
    server.close()
  }

  "BaseHttpServer startup with only an http external port and no admin port" in {
    val server = new EmbeddedHttpServer(
      twitterServer = new BaseHttpServer {
        override val disableAdminHttpServer = true
        override val modules = Seq(ResponseBuilderModule)
      })

    server.start()
    server.close()
  }
}
