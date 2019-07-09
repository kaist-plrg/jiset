package kr.ac.kaist.ase.model

object ModelHelper {

  def flattenStList(s: StatementList): List[StatementListItem] = s match {
    case StatementList0(x0, _) => List(x0)
    case StatementList1(x0, x1, _) => flattenStList(x0) :+ x1
  }
  def flattenStatement(s: Script) = s match {
    case Script0(Some(ScriptBody0(stlist, _)), _) =>
      flattenStList(stlist)
    case _ => List()
  }
  def mergeStatement(l: List[StatementListItem]): Script = Script0(l match {
    case a :: rest => Some(ScriptBody0(rest.foldLeft[StatementList](StatementList0(a, List(false, false, false))) {
      case (x, y) => StatementList1(x, y, List(false, false, false))
    }, List(false, false, false)))
    case Nil => None
  }, List(false, false, false))
}

