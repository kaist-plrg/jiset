package kr.ac.kaist.jiset.analyzer

import scala.collection.mutable.{ Stack, Queue }

// worklist
trait Worklist[T] {
  def push(x: T): Unit
  def isEmpty: Boolean
  def pop: T
  def head: T
  def has(x: T): Boolean
}

// stack-based worklist
class StackWorklist[T] extends Worklist[T] {
  private var set: Set[T] = Set()
  private var stack: Stack[T] = Stack()
  def push(x: T): Unit = if (!set.contains(x)) { stack.push(x); set += x; }
  def isEmpty: Boolean = stack.isEmpty
  def pop: T = { val x = stack.pop; set -= x; x }
  def head: T = stack.head
  def has(x: T): Boolean = set.contains(x)
}

// queue-based worklist
class QueueWorklist[T] extends Worklist[T] {
  private var set: Set[T] = Set()
  private var queue: Queue[T] = Queue()
  def push(x: T): Unit = if (!set.contains(x)) { queue.enqueue(x); set += x; }
  def isEmpty: Boolean = queue.isEmpty
  def pop: T = { val x = queue.dequeue; set -= x; x }
  def head: T = queue.head
  def has(x: T): Boolean = set.contains(x)
}
