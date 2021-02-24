package kr.ac.kaist.jiset.util

trait StateMonad[+T, S] extends (S => (T, S)) {
  def map[U](f: T => U): StateMonad[U, S] = s => {
    val (v, s0) = this(s)
    (f(v), s0)
  }
  def flatMap[U](f: T => StateMonad[U, S]): StateMonad[U, S] = s => {
    val (v, s0) = this(s)
    f(v)(s0)
  }
  def ^^[U](f: ((T, S)) => (U, S)): StateMonad[U, S] = s => f(this(s))
  def ^^^[U](v: U): StateMonad[U, S] = this ^^ { case (_, s) => (v, s) }
}
object StateMonad {
  implicit def pure[T, S](v: T): StateMonad[T, S] = s => (v, s)
  def join[T, S](list: List[StateMonad[T, S]]): StateMonad[List[T], S] = list match {
    case Nil => Nil
    case hd :: tl => for {
      h <- hd
      t <- join(tl)
    } yield h :: t
  }
}
