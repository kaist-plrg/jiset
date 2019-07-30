package kr.ac.kaist.ase.parser

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{ Reader, Position }
import scala.collection.mutable
import scala.language.implicitConversions

trait PackratParsers extends Parsers {
  type Container
  protected def emptyContainer: Container

  class PackratMemoized extends Memoized[ContainerReader[Elem], ParseResult] {
    def failure(msg: String, i: ContainerReader[Elem]): ParseResult[Nothing] = Failure(msg, i)
    def isSuccess(result: ParseResult[_]): Boolean = result.successful
    def isProminent(old: ParseResult[_], temp: ParseResult[_]): Boolean = {
      old.next.pos < temp.next.pos
    }
    type Case[T] = Parser[T]
  }

  class ContainerReader[+T](underlying: Reader[T]) extends Reader[T] { outer =>
    val container: Container = emptyContainer
    private[PackratParsers] val memoized = new PackratMemoized
    override def source: java.lang.CharSequence = underlying.source
    override def offset: Int = underlying.offset
    def first: T = underlying.first
    def copy: Reader[T] = new ContainerReader(underlying) {
      override val container = outer.container
      override private[PackratParsers] val memoized = outer.memoized
    }
    def rest: Reader[T] = new ContainerReader(underlying.rest) {
      override val container = outer.container
      override private[PackratParsers] val memoized = outer.memoized
    }
    def pos: Position = underlying.pos
    def atEnd: Boolean = underlying.atEnd
    override def toString: String = pos.longString
  }

  override def phrase[T](p: Parser[T]) = {
    val q = super.phrase(p)
    new PackratParser[T] {
      def apply(in: Input) = in match {
        case in: ContainerReader[_] => q(in)
        case in => q(new ContainerReader(in))
      }
    }
  }

  abstract class PackratParser[+T] extends super.Parser[T]

  implicit def parser2packrat[T](p: => super.Parser[T]): PackratParser[T] = {
    lazy val q = p
    memo(super.Parser { in => q(in) })
  }

  def memo[T](p: super.Parser[T]): PackratParser[T] = {
    new PackratParser[T] {
      def apply(in: Input) = {
        val inMem = in.asInstanceOf[ContainerReader[Elem]]
        val res = inMem.memoized.memo(p, inMem)
        res
      }
    }
  }
}

