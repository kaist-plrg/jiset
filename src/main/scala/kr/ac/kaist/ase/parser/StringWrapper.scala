package kr.ac.kaist.ase.parser

case class StringWrapper(id: String, data: String) {
  override def toString: String = data
}

