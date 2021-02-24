package kr.ac.kaist.jiset.util

trait StateUpdater[+T, S] extends (S => (T, S)) {
  def map[U](f: T => U): StateUpdater[U, S] = s => {
    val (v, s0) = this(s)
    (f(v), s0)
  }
  def flatMap[U](f: T => StateUpdater[U, S]): StateUpdater[U, S] = s => {
    val (v, s0) = this(s)
    f(v)(s0)
  }
  def >>(f: S => S): StateUpdater[T, S] = s => {
    val (v, s0) = this(s)
    (v, f(s0))
  }
}
