package kr.ac.kaist.ase.parser

import scala.util.parsing.combinator.Parsers
import scala.util.parsing.input.{ Reader, Position }
import scala.collection.mutable
import scala.language.implicitConversions

trait PackratParsers extends Parsers {
  type Container
  protected def emptyContainer: Container
  class ContainerReader[+T](underlying: Reader[T]) extends Reader[T] { outer =>
    val container: Container = emptyContainer
    private[PackratParsers] val cache = mutable.HashMap.empty[(Parser[_], Position), MemoEntry[_]]

    private[PackratParsers] def getFromCache[T](p: Parser[T]): Option[MemoEntry[T]] = {
      cache.get((p, pos)).asInstanceOf[Option[MemoEntry[T]]]
    }

    private[PackratParsers] def updateCacheAndGet[T](p: Parser[T], w: MemoEntry[T]): MemoEntry[T] = {
      cache.put((p, pos), w)
      w
    }

    private[PackratParsers] val recursionHeads: mutable.HashMap[Position, Head] = mutable.HashMap.empty

    private[PackratParsers] var lrStack: List[LR] = Nil

    override def source: java.lang.CharSequence = underlying.source
    override def offset: Int = underlying.offset

    def first: T = underlying.first
    def rest: Reader[T] = new ContainerReader(underlying.rest) {
      override val container = outer.container
      override private[PackratParsers] val cache = outer.cache
      override private[PackratParsers] val recursionHeads = outer.recursionHeads
      lrStack = outer.lrStack
    }

    def pos: Position = underlying.pos
    def atEnd: Boolean = underlying.atEnd
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

  private def getPosFromResult(r: ParseResult[_]): Position = r.next.pos

  private case class MemoEntry[+T](var r: Either[LR, ParseResult[_]]) {
    def getResult: ParseResult[T] = r match {
      case Left(LR(res, _, _)) => res.asInstanceOf[ParseResult[T]]
      case Right(res) => res.asInstanceOf[ParseResult[T]]
    }
  }

  private case class LR(var seed: ParseResult[_], var rule: Parser[_], var head: Option[Head]) {
    def getPos: Position = getPosFromResult(seed)
  }

  private case class Head(var headParser: Parser[_], var involvedSet: List[Parser[_]], var evalSet: List[Parser[_]]) {
    def getHead = headParser
  }

  abstract class PackratParser[+T] extends super.Parser[T]

  implicit def parser2packrat[T](p: => super.Parser[T]): PackratParser[T] = {
    lazy val q = p
    memo(super.Parser { in => q(in) })
  }

  private def recall(p: super.Parser[_], in: ContainerReader[Elem]): Option[MemoEntry[_]] = {
    val cached = in.getFromCache(p)
    val head = in.recursionHeads.get(in.pos)

    head match {
      case None => cached
      case Some(h @ Head(hp, involved, evalSet)) => {
        if (cached.isEmpty && !(hp :: involved contains p)) {
          return Some(MemoEntry(Right(Failure("dummy ", in))))
        }
        if (evalSet contains p) {
          h.evalSet = h.evalSet.filterNot(_ == p)
          val tempRes = p(in)
          val tempEntry: MemoEntry[_] = cached.get
          tempEntry.r = Right(tempRes)
        }
        cached
      }
    }
  }

  private def setupLR(p: Parser[_], in: ContainerReader[_], recDetect: LR): Unit = {
    if (recDetect.head.isEmpty) recDetect.head = Some(Head(p, Nil, Nil))

    in.lrStack.takeWhile(_.rule != p).foreach { x =>
      x.head = recDetect.head
      recDetect.head.map(h => h.involvedSet = x.rule :: h.involvedSet)
    }
  }

  private def lrAnswer[T](p: Parser[T], in: ContainerReader[Elem], growable: LR): ParseResult[T] = growable match {
    case LR(seed, rule, Some(head)) =>
      if (head.getHead != p) seed.asInstanceOf[ParseResult[T]]
      else {
        in.updateCacheAndGet(p, MemoEntry(Right[LR, ParseResult[T]](seed.asInstanceOf[ParseResult[T]])))
        seed match {
          case f @ Failure(_, _) => f
          case e @ Error(_, _) => e
          case s @ Success(_, _) => grow(p, in, head)
        }
      }
    case _ => throw new Exception("lrAnswer with no head !!")
  }

  def memo[T](p: super.Parser[T]): PackratParser[T] = {
    new PackratParser[T] {
      def apply(in: Input) = {
        val inMem = in.asInstanceOf[ContainerReader[Elem]]

        val m = recall(p, inMem)
        m match {
          case None =>
            val base = LR(Failure("Base Failure", in), p, None)
            inMem.lrStack = base :: inMem.lrStack
            inMem.updateCacheAndGet(p, MemoEntry(Left(base)))
            val tempRes = p(in)
            inMem.lrStack = inMem.lrStack.tail
            base.head match {
              case None =>
                inMem.updateCacheAndGet(p, MemoEntry(Right(tempRes)))
                tempRes
              case s @ Some(_) =>
                base.seed = tempRes
                val res = lrAnswer(p, inMem, base)
                res
            }

          case Some(mEntry) => {
            mEntry match {
              case MemoEntry(Left(recDetect)) => {
                setupLR(p, inMem, recDetect)
                recDetect match { case LR(seed, _, _) => seed.asInstanceOf[ParseResult[T]] }
              }
              case MemoEntry(Right(res: ParseResult[_])) => res.asInstanceOf[ParseResult[T]]
            }
          }
        }
      }
    }
  }

  private def grow[T](p: super.Parser[T], rest: ContainerReader[Elem], head: Head): ParseResult[T] = {
    rest.recursionHeads.put(rest.pos, head)
    val oldRes: ParseResult[T] = rest.getFromCache(p).get match {
      case MemoEntry(Right(x)) => x.asInstanceOf[ParseResult[T]]
      case _ => throw new Exception("impossible match")
    }

    head.evalSet = head.involvedSet
    val tempRes = p(rest); tempRes match {
      case s @ Success(_, _) =>
        if (getPosFromResult(oldRes) < getPosFromResult(tempRes)) {
          rest.updateCacheAndGet(p, MemoEntry(Right(s)))
          grow(p, rest, head)
        } else {
          rest.recursionHeads -= rest.pos
          rest.getFromCache(p).get match {
            case MemoEntry(Right(x: ParseResult[_])) => x.asInstanceOf[ParseResult[T]]
            case _ => throw new Exception("impossible match")
          }
        }
      case f =>
        rest.recursionHeads -= rest.pos
        oldRes
    }
  }
}

