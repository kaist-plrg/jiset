object EmptyFunction extends Algorithm {
  val name: String = "EmptyFunction"
  val length: Int = 0
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""EmptyFunction" () => {}"""), this)
}
