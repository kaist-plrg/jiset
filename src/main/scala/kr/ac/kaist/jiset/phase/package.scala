package kr.ac.kaist.jiset

import kr.ac.kaist.jiset.util.OptionKind

package object phase {
  type Regex = scala.util.matching.Regex
  type ArgRegex[PhaseConfig <: Config] = (Regex, Regex, (PhaseConfig, String) => Unit)
  type PhaseOption[PhaseConfig <: Config] = (String, OptionKind[PhaseConfig], String)
}
