package kr.ac.kaist.jiset.web.routes

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

// exec router
object ExecRoute {
  // // ir steps
  // irStep (): Scala_StepResult;
  // irStepOver (): Scala_StepResult;
  // irStepOut (): Scala_StepResult;
  // // spec steps
  // specStep (): Scala_StepResult;
  // specStepOver (): Scala_StepResult;
  // specStepOut (): Scala_StepResult;
  // // spec steps
  // jsStep (): Scala_StepResult;
  // // continue
  // continueAlgo (): Scala_StepResult;

  // root router
  def apply(): Route = {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
    }
  }
}
