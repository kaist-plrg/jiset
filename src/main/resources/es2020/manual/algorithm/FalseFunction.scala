object FalseFunction extends Algorithm {
  val name: String = "FalseFunction"
  val length: Int = 0
  val lang: Boolean = false
  val func: Func = FixUIdWalker(parseFunc(""""FalseFunction" () => return false"""), this)
}
