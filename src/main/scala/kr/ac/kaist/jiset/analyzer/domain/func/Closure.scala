package kr.ac.kaist.jiset.analyzer.domain.func

import kr.ac.kaist.jiset.cfg.Function
import kr.ac.kaist.ires.ir.{ Id, Value }

// function closure
case class Closure(func: Function, locals: Map[Id, Value])
