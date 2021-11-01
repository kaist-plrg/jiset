package kr.ac.kaist.jiset.editor.analyzer.domain

import kr.ac.kaist.jiset.cfg._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.js.ast.AST
import kr.ac.kaist.jiset.spec.algorithm.Algo
import kr.ac.kaist.jiset.util.Appender
import kr.ac.kaist.jiset.util.Appender._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.js.ast.AbsAST

// basic abstract values
object BasicValue extends Domain {
  lazy val Bot = BaseBot
  lazy val Top = BaseTop
  // abstraction functions
  def isSyntacticView(ast: AST): Boolean = ast match {
    case _: AbsAST => true
    case _ => ast.children.collect { case ASTVal(ast) => ast }.exists(isSyntacticView(_))
  }
  def apply(v: Value): Elem = v match {
    case ASTVal(ast) if isSyntacticView(ast) => BaseSyntacticView(ast)
    case _ => BaseElem(v)
  }
  val absent: Elem = BaseElem(Absent)
  val undef: Elem = BaseElem(Undef)
  val nullv: Elem = BaseElem(Null)

  // constructors

  // extractors

  // appender
  implicit val app: App[Elem] = (app, elem) => {

    elem match {
      case BaseTop => app >> "T"
      case BaseBot => app >> "⊥"
      case BaseElem(value) => app >> value.toString
      case BaseSyntacticView(ast) => app >> ast.toString
    }

  }

  // elements
  sealed trait Elem extends ElemTrait {
    // partial order
    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Bot) => false
      case (_, Top) => true
      case (Top, _) => false
      case (BaseElem(v1), BaseElem(v2)) => v1 == v2
      case (BaseSyntacticView(ast1), BaseSyntacticView(ast2)) => true // TODO
      case (_, _) => false
    }
    // join operator
    def ⊔(that: Elem): Elem = (this, that) match {
      case (Bot, _) => that
      case (_, Bot) => this
      case (Top, _) => Top
      case (_, Top) => Top
      case (BaseElem(v1), BaseElem(v2)) => if (v1 == v2) BaseElem(v1) else Top
      case (BaseSyntacticView(ast1), BaseSyntacticView(ast2)) => BaseSyntacticView(ast1) // TODO
      case (_, _) => Top

    }
    // meet operator
    def ⊓(that: Elem): Elem = (this, that) match {
      case (Bot, _) => Bot
      case (_, Bot) => Bot
      case (Top, _) => that
      case (_, Top) => this
      case (BaseElem(v1), BaseElem(v2)) => if (v1 == v2) BaseElem(v1) else Bot
      case (BaseSyntacticView(ast1), BaseSyntacticView(ast2)) => BaseSyntacticView(ast1) // TODO
      case (_, _) => Bot
    }
    // get single value
    def getSingle: Flat[Value] = this match {
      case BaseTop => FlatTop
      case BaseBot => FlatBot
      case BaseElem(value) => FlatElem(value)
      case BaseSyntacticView(ast) => FlatTop
    }

    def getSingleAST: Flat[AST] = this match {
      case BaseTop => FlatTop
      case BaseBot => FlatBot
      case BaseElem(ASTVal(v)) => FlatElem(v)
      case BaseSyntacticView(ast: AbsAST) => FlatTop
      case BaseSyntacticView(ast) => FlatElem(ast)
      case BaseElem(_) => FlatTop
    }

    // escape completion
    def escaped: Elem = this match {
      case BaseElem(value) => value match {
        case CompValue(_, value, _) => BaseElem(value)
        case _ => this
      }
      case _ => this
    }

    // singleton checks
    def isSingle: Boolean = getSingle match {
      case FlatElem(_) => true
      case _ => false
    }

    // check completion
    def isCompletion: Elem = {
      var b: Elem = BaseBot
      this match {
        case BaseTop => b ⊔= BaseTop
        case BaseBot => ()
        case BaseElem(value) => value match {
          case CompValue(_, _, _) => b ⊔= BaseElem(Bool(true))
          case _ => b ⊔= BaseElem(Bool(false))
        }
        case BaseSyntacticView(ast) => b ⊔= BaseElem(Bool(false))
      }
      b
    }

    // abstract equality
    def =^=(that: Elem): Elem = (this.getSingle, that.getSingle) match {
      case (FlatBot, _) | (_, FlatBot) => BaseBot
      case (FlatElem(l), FlatElem(r)) => BaseElem(Bool(l == r))
      case _ => BaseTop
    }

    // check abrupt completion
    def isAbruptCompletion: Elem = {
      var b: Elem = BaseBot
      this match {
        case BaseTop => b ⊔= BaseTop
        case BaseBot => ()
        case BaseElem(value) => value match {
          case CompValue(Const(ty), _, _) if ty != "normal" => b ⊔= BaseElem(Bool(true))
          case _ => b ⊔= BaseElem(Bool(false))
        }
        case BaseSyntacticView(ast) => false
      }
      b
    }

    // wrap completion
    def wrapCompletion(targetOpt: Option[String]): Elem = wrapCompletion("normal", targetOpt)
    def wrapCompletion(ty: String, targetOpt: Option[String]): Elem = this match {
      case BaseTop => BaseTop
      case BaseBot => BaseBot
      case BaseElem(value: PureValue) => BaseElem(CompValue(Const(ty), value, targetOpt))
      case BaseElem(_) => BaseTop
      case BaseSyntacticView(ast) => BaseTop
    }

    // check absents
    def isAbsent: Elem = {
      var b: Elem = BaseBot
      this match {
        case BaseTop => b ⊔= BaseTop
        case BaseBot => ()
        case BaseElem(Absent) => b ⊔= BaseElem(Bool(true))
        case _ => b ⊔= BaseElem(Bool(false))
      }
      b
    }

    def removeAbsent: Elem = this match {
      case BaseElem(Absent) => BaseBot
      case _ => this
    }
  }
  case object BaseTop extends Elem
  case object BaseBot extends Elem
  case class BaseElem(value: Value) extends Elem
  case class BaseSyntacticView(ast: AST) extends Elem

}
