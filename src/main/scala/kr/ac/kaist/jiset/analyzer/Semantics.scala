package kr.ac.kaist.jiset.analyzer

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.spec._
import kr.ac.kaist.jiset.spec.algorithm.{ Algo, NormalHead }
import kr.ac.kaist.jiset.util.Useful._
import scala.collection.mutable.{ Queue, Stack }

class Semantics(
  inst: Inst,
  algos: List[Algo],
  initState: State = State(Env(), Heap())
) {
  // assign fid to algos
  val fid2algo = algos.zipWithIndex.map {
    case (algo, i) => i -> algo
  }.toMap
  val algo2fid = algos.map(_.name).zipWithIndex.toMap

  // globals
  var globals: Map[String, Value] = algos.map {
    case Algo(NormalHead(name, params), _, _, _) =>
      name -> Clo(algo2fid(name))
    case _ => ??? // TODO
  }.toMap

  // next
  def next: Option[Inst] = context.next match {
    case None =>
      if (callStacks.isEmpty) None
      else { context = callStacks.pop; next }
    case inst => inst
  }

  // set state of current context
  def setState(st: State) = context.state = st

  // get state of current context
  def getState: State = context.state

  // prepend instrunctions to current context
  def prepend(i: Inst) = context.remains.prepend(i)
  def prependAll(l: List[Inst]) = context.remains.prependAll(l)

  // handle call
  def doCall(fid: Int, args: List[Value], id: Id): State = fid2algo(fid) match {
    case Algo(NormalHead(_, params), _, body, _) =>
      // set init state of new context
      val env =
        if (params.length != args.length) error(s"arity mismatch")
        else Env(params.map(_.name).zip(args).toMap)
      val state = State(env, context.state.heap)
      // set return target of current context
      context.retTarget = Some(id)
      // push current context to call stack
      callStacks.push(context)
      // change current context
      context = new Context(fid, Queue(body), state)
      // return new state
      state
    case _ => ??? // TODO handle BuiltinHead, SyntaxDirectedHead
  }

  // handle return
  def doReturn(v: Value): State =
    if (callStacks.isEmpty) error(s"call stack is empty")
    else {
      // get heap of current context
      val heap = context.state.heap
      // pop context from call stack
      val newContext = callStacks.pop
      // resume context
      context = newContext.doReturn(v, heap)
      // return new state
      context.state
    }

  // call stacks
  private val MAIN_ENTRY = -1
  private var callStacks: Stack[Context] = Stack()
  private case class Context(
    fid: Int,
    remains: Queue[Inst],
    var state: State,
    var retTarget: Option[Id] = None
  ) {
    def next: Option[Inst] =
      if (remains.isEmpty) None else Some(remains.dequeue)
    def doReturn(v: Value, heap: Heap): Context = retTarget match {
      case None => error("return target is empty")
      case Some(Id(name)) => {
        state = State(state.env.define(name, v), heap)
        this
      }
    }
  }
  private var context = new Context(MAIN_ENTRY, Queue(inst), initState)

  // return value
  var ret: Option[Value] = None
}
