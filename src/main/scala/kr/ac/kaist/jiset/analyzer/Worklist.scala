package kr.ac.kaist.jiset.analyzer

import scala.collection.mutable.{ Stack, Queue }

// worklist
trait Worklist[T] {
  def all: Set[T]
  def +=(x: T): Unit
  def next: Option[T]
  def headOption: Option[T]
  def isEmpty: Boolean = all.isEmpty
  def has(x: T): Boolean = all contains x
}

// stack-based worklist
class StackWorklist[T](init: Iterable[T]) extends Worklist[T] {
  private var stack = new Stack[T]()
  private var set = Set[T]()
  init.foreach(this += _)
  def all = set
  def +=(x: T) = if (!set.contains(x)) { stack.push(x); set += x; }
  def next =
    if (isEmpty) None
    else { val x = stack.pop; set -= x; Some(x) }
  def headOption: Option[T] = stack.headOption
}

// queue-based worklist
class QueueWorklist[T](init: Iterable[T]) extends Worklist[T] {
  private var queue = Queue[T]()
  private var set = Set[T]()
  init.foreach(this += _)
  def all = set
  def +=(x: T): Unit = if (!set.contains(x)) { queue.enqueue(x); set += x; }
  def next =
    if (isEmpty) None
    else { val x = queue.dequeue; set -= x; Some(x) }
  def headOption: Option[T] = queue.lastOption
}
