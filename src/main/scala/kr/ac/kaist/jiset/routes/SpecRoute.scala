package kr.ac.kaist.jiset.web.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import kr.ac.kaist.jiset.js
import kr.ac.kaist.jiset.spec.JsonProtocol._
import io.circe.syntax._

// spec router
object SpecRoute {
  // root router
  def apply(): Route = {
    // get spec
    get {
      complete(HttpEntity(ContentTypes.`application/json`, js.spec.asJson.noSpaces))
    }
  }
}
