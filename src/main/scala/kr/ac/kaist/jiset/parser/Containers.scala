package kr.ac.kaist.jiset.parser

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{ Reader, Position }

trait Containers { this: Parsers =>
  type Container
  def emptyContainer: Container
  class ContainerReader[T](reader: Reader[T]) extends Reader[T] { outer =>
    val container = emptyContainer
    val rev = List[T]()
    override def source = reader.source
    override def offset = reader.offset
    def first: T = reader.first
    def rest: Reader[T] = new ContainerReader(reader.rest) {
      override val container = outer.container
      override val rev = outer.first :: outer.rev
    }
    def pos: Position = reader.pos
    def atEnd: Boolean = reader.atEnd
  }
}
