package kr.ac.kaist.ase.parser

import scala.collection.mutable
import scala.language.higherKinds

abstract class Memoized[Input, Result[_]] {
  def failure(msg: String, i: Input): Result[Nothing]
  def isSuccess(result: Result[_]): Boolean
  def isProminent(old: Result[_], temp: Result[_]): Boolean

  type Case[T] <: Input => Result[T]

  case class MemoEntry[+T](
    var r: Either[LR, Result[_]]
  )

  case class LR(
    var seed: Result[_],
    var rule: Case[_],
    var head: Option[Head]
  )

  case class Head(
    var headCase: Case[_],
    var involvedSet: List[Case[_]],
    var evalSet: List[Case[_]]
  )

  val cache: mutable.Map[(Case[_], Input), MemoEntry[_]] = mutable.Map.empty

  def getFromCache[T](c: Case[T], i: Input): Option[MemoEntry[T]] =
    cache.get((c, i)).asInstanceOf[Option[MemoEntry[T]]]

  def updateCacheAndGet[T](c: Case[T], i: Input, w: MemoEntry[T]): MemoEntry[T] = {
    cache.put((c, i), w)
    w
  }

  val recursionHeads: mutable.HashMap[Input, Head] = mutable.HashMap.empty

  var lrStack: List[LR] = Nil

  def recall(c: Case[_], i: Input): Option[MemoEntry[_]] = {
    val cached = getFromCache(c, i)
    val head = recursionHeads.get(i)

    head match {
      case None => cached
      case Some(h @ Head(hp, involved, evalSet)) => {
        if (cached.isEmpty && !(hp :: involved contains c)) {
          return Some(MemoEntry(Right(failure("dummy", i))))
        }
        if (evalSet contains c) {
          h.evalSet = h.evalSet.filterNot(_ == c)
          val tempRes = c(i)
          val tempEntry: MemoEntry[_] = cached.get
          tempEntry.r = Right(tempRes)
        }
        cached
      }
    }
  }

  def setupLR(c: Case[_], i: Input, recDetect: LR): Unit = {
    if (recDetect.head.isEmpty) recDetect.head = Some(Head(c, Nil, Nil))

    lrStack.takeWhile(_.rule != c).foreach { x =>
      x.head = recDetect.head
      recDetect.head.map(h => h.involvedSet = x.rule :: h.involvedSet)
    }
  }

  def lrAnswer[T](c: Case[T], i: Input, growable: LR): Result[T] = growable match {
    case LR(seed, rule, Some(head)) =>
      if (head.headCase != c) seed.asInstanceOf[Result[T]]
      else {
        updateCacheAndGet(c, i, MemoEntry(Right[LR, Result[T]](seed.asInstanceOf[Result[T]])))
        if (isSuccess(seed)) grow(c, i, head)
        else seed.asInstanceOf[Result[T]]
      }
    case _ => throw new Exception("lrAnswer with no head !!")
  }

  def grow[T](c: Case[T], i: Input, head: Head): Result[T] = {
    recursionHeads.put(i, head)
    val oldRes: Result[T] = getFromCache(c, i).get match {
      case MemoEntry(Right(x)) => x.asInstanceOf[Result[T]]
      case _ => throw new Exception("impossible match")
    }

    head.evalSet = head.involvedSet
    val tempRes = c(i)
    if (isSuccess(tempRes)) {
      if (isProminent(oldRes, tempRes)) {
        updateCacheAndGet(c, i, MemoEntry(Right(tempRes)))
        grow(c, i, head)
      } else {
        recursionHeads -= i
        getFromCache(c, i).get match {
          case MemoEntry(Right(x: Result[_])) => x.asInstanceOf[Result[T]]
          case _ => throw new Exception("impossible match")
        }
      }
    } else {
      recursionHeads -= i
      oldRes
    }
  }

  def memo[T](c: Case[T], i: Input): Result[T] = recall(c, i) match {
    case None =>
      val base = LR(failure("Base Failure", i), c, None)
      lrStack = base :: lrStack
      updateCacheAndGet(c, i, MemoEntry(Left(base)))
      val tempRes = c(i)
      lrStack = lrStack.tail
      base.head match {
        case None =>
          updateCacheAndGet(c, i, MemoEntry(Right(tempRes)))
          tempRes
        case s @ Some(_) =>
          base.seed = tempRes
          lrAnswer(c, i, base)
      }

    case Some(mEntry) => {
      mEntry match {
        case MemoEntry(Left(recDetect)) => {
          setupLR(c, i, recDetect)
          recDetect match { case LR(seed, _, _) => seed.asInstanceOf[Result[T]] }
        }
        case MemoEntry(Right(res: Result[_])) => res.asInstanceOf[Result[T]]
      }
    }
  }
}
