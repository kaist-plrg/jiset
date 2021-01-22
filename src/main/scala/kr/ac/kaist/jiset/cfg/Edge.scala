package kr.ac.kaist.jiset.cfg

import kr.ac.kaist.ires.ir._

// CFG edges
sealed abstract class Edge
case object NormalEdge extends Edge
case class CondEdge(pass: Boolean) extends Edge
