package kr.ac.kaist.ase

sealed trait Command

case class ExitCmd() extends Command
case class NopCmd() extends Command
case class AddCmd(s: String, r: CFGRule) extends Command
case class ßFindCmd(l: String) extends Command
case class FindFirstCmd() extends Command
