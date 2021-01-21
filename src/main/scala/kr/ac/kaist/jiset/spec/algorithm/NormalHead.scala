package kr.ac.kaist.jiset.spec.algorithm

// normal algorithm heads
case class NormalHead(
    name: String,
    params: List[Param],
    secId: String
) extends Head
