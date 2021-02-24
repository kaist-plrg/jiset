package kr.ac.kaist.jiset.util

trait StateUpdator[+T, S] extends (S => (T, S)) {
  def map[U](f: T => U): StateUpdator[U, S] = s => {
    val (v, s0) = this(s)
    (f(v), s0)
  }
  def flatMap[U](f: T => StateUpdator[U, S]): StateUpdator[U, S] = s => {
    val (v, s0) = this(s)
    f(v)(s0)
  }
  def >>(f: S => S): StateUpdator[T, S] = s => {
    val (v, s0) = this(s)
    (v, f(s0))
  }
}
