package kr.ac.kaist.jiset.ir

trait AllocSite {
  private var asiteOpt: Option[Int] = None
  def setASite(asite: Int): this.type = { asiteOpt = Some(asite); this }
  lazy val asite: Int = asiteOpt.getOrElse(-1)
}
