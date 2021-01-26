package kr.ac.kaist.jiset.analyzer

import domain.AbsState

case class Result[T](elem: T, st: AbsState)
