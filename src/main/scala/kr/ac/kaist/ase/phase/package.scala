package kr.ac.kaist.ase

import kr.ac.kaist.ase.util.OptionKind

package object phase {
  type Regex = scala.util.matching.Regex
  type ArgRegex[PhaseConfig <: Config] = (Regex, Regex, (PhaseConfig, String) => Unit)
  type PhaseOption[PhaseConfig <: Config] = (String, OptionKind[PhaseConfig], String)
}
