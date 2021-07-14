package kr.ac.kaist.jiset.util

import kr.ac.kaist.jiset._

object Stat {
  private var algoNameCounter: Map[String, Int] = Map()
  def algoNames: Map[String, Int] = algoNameCounter
  def touchAlgo(name: String): Unit =
    algoNameCounter += name -> (algoNameCounter.getOrElse(name, 0) + 1)

  // conversion to string
  override def toString: String =
    algoNameCounter.map { case (name, k) => s"$k - $name" }.mkString(LINE_SEP)
}
