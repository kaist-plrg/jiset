package kr.ac.kaist.jiset.util
class StateMonad[S] {
  // result type
  trait Result[+T] extends (S => (T, S)) {
    // foreach function
    def foreach[U](f: T => U): Result[Unit] = s => {
      val (_, s0) = this(s)
      ((), s0)
    }

    // map function
    def map[U](f: T => U): Result[U] = s => {
      val (v, s0) = this(s)
      (f(v), s0)
    }

    // flat map function
    def flatMap[U](f: T => Result[U]): Result[U] = s => {
      val (v, s0) = this(s)
      f(v)(s0)
    }

    // XXX remove this method after refactoring
    @deprecated("this method will be removed")
    def ~[U](monad: Result[U]): Result[(T, U)] =
      for { v0 <- this; v1 <- monad } yield (v0, v1)
    def ^^[U](f: ((T, S)) => (U, S)): Result[U] = s => f(this(s))
    def ^^^[U](v: U): Result[U] = this ^^ { case (_, s) => (v, s) }
  }

  // convert function to result type
  def id[T](f: S => (T, S)): Result[T] = s => f(s)

  // update type
  type Updater = S => S

  // get the current state
  def get: Result[S] = s => (s, s)

  // get a property of the current state
  def gets[A](f: S => A): Result[A] = s => (f(s), s)

  // modify the current state
  implicit def modify(f: Updater): Result[Unit] = s => ((), f(s))

  // conversion to updater
  implicit def toUpdater(m: Result[_]): Updater = s => m(s)._2

  // list of updaters to list of results
  implicit def listModify(list: List[Updater]): List[Result[Unit]] = list.map(modify)

  // pur a new state as the current state
  def put(s: S): Result[Unit] = _ => ((), s)

  // create a state moand with a value
  implicit def pure[T](v: T): Result[T] = s => (v, s)

  // join a list of state monads to a state monad of lists
  def join[T](
    iter: Iterable[Result[T]]
  ): Result[List[T]] = {
    if (iter.isEmpty) Nil
    else for {
      h <- iter.head
      t <- join(iter.tail)
    } yield h :: t
  }
}
