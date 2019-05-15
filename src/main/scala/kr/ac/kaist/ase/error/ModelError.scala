package kr.ac.kaist.ase.error

import kr.ac.kaist.ase.Command

sealed abstract class ModelError(msg: String) extends ASEError(msg)

case object ModelNotYetGenerated extends ModelError({
  s"Models are not yet generated. Please generate models using 'sbt gen-model'."
})
