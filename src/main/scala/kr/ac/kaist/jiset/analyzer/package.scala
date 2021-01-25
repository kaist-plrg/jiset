package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.cfg.Node

package object analyzer {
  import domain._
  type Result[T] = (T, AbsState)
}
