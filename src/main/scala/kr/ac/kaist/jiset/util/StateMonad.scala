package kr.ac.kaist.jiset.util

// state monads
trait StateMonad[+T, S] extends (S => (T, S)) {
  // map function
  def map[U](f: T => U): StateMonad[U, S] = s => {
    val (v, s0) = this(s)
    (f(v), s0)
  }

  // flat map function
  def flatMap[U](f: T => StateMonad[U, S]): StateMonad[U, S] = s => {
    val (v, s0) = this(s)
    f(v)(s0)
  }

  // XXX remove this method after refactoring
  @deprecated("this method will be removed")
  def ^^[U](f: ((T, S)) => (U, S)): StateMonad[U, S] = s => f(this(s))
  def ^^^[U](v: U): StateMonad[U, S] = this ^^ { case (_, s) => (v, s) }
}
object StateMonad {
  // get the current state
  def get[S]: StateMonad[S, S] = s => (s, s)

  // get a property of the current state
  def gets[A, S](f: S => A): StateMonad[A, S] = s => (f(s), s)

  // modify the current state
  def modify[S](f: S => S): StateMonad[Unit, S] = s => ((), f(s))

  // pur a new state as the current state
  def put[S](s: S): StateMonad[Unit, S] = _ => ((), s)

  // create a state moand with a value
  implicit def pure[T, S](v: T): StateMonad[T, S] = s => (v, s)

  // join a list of state monads to a state monad of lists
  def join[T, S](list: List[StateMonad[T, S]]): StateMonad[List[T], S] = list match {
    case Nil => Nil
    case hd :: tl => for {
      h <- hd
      t <- join(tl)
    } yield h :: t
  }
}
