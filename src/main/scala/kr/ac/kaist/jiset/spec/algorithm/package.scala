package kr.ac.kaist.jiset.spec

package object algorithm {
  // pre-defined parameters
  val THIS_PARAM = "this"
  val ARGS_LIST = "argumentsList"
  val NEW_TARGET = "NewTarget"
  val ENV_PARAM = "envRec"
  val OBJ_PARAM = "O"
  val AWAIT_PARAM = "value"
  val COMP_PARAMS = toParams(List("x", "y"), Param.Kind.Normal)
  val REL_COMP_PARAMS = COMP_PARAMS ++ toParams("LeftFirst", Param.Kind.Optional)

  def toParams(
    param: String,
    kind: Param.Kind = Param.Kind.Normal
  ): List[Param] = toParams(List(param), kind)
  def toParams(
    params: List[String],
    kind: Param.Kind
  ): List[Param] = params.map(Param(_, kind))

  // constant
  val OBJECT = "Object"
}
