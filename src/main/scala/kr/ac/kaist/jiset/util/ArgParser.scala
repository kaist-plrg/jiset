package kr.ac.kaist.jiset.util

import scala.util.parsing.combinator._
import scala.io.Source
import kr.ac.kaist.jiset.{ JISET, Command, JISETConfig }
import kr.ac.kaist.jiset.phase.{ PhaseOption, Config }
import kr.ac.kaist.jiset.error._
import spray.json._

// Argument parser by using Scala RegexParsers.
class ArgParser(cmd: Command[_], jisetConfig: JISETConfig) extends RegexParsers {
  var ruleList: List[Parser[Unit]] = Nil

  var optNameSet: Set[String] = Set()

  addRule(jisetConfig, "", JISET.options)

  def addRule[PhaseConfig <: Config](
    config: PhaseConfig,
    prefix: String,
    options: List[PhaseOption[PhaseConfig]]
  ): Unit = {
    options.foreach {
      case (opt, kind, _) =>
        val optName = prefix + (if (prefix == "") "" else ":") + opt
        optNameSet(optName) match {
          case true => throw OptAlreadyExistError(optName)
          case false =>
            optNameSet += optName
            kind.argRegexList(optName).reverseIterator.foreach {
              case (optRegex, argRegex, fun) =>
                val cur: Parser[Unit] = (optRegex) ~> (argRegex) ^^ { fun(config, _) }
                ruleList ::= cur
            }
        }
    }
  }

  // Parsing arguments.
  def apply(args: List[String]): Unit = {
    var jsonArgs: List[String] = Nil
    val str = ".*".r ^^ { s => s }

    // add arguments from JSON
    def addArg(prefix: String, value: (String, JsValue)): Unit = value match {
      case (opt, JsBoolean(true)) => jsonArgs ::= s"-$prefix$opt"
      case (opt, JsBoolean(false)) =>
      case (opt, JsNumber(num)) => jsonArgs ::= s"-$prefix$opt=$num"
      case (opt, JsString(str)) if !str.isEmpty => jsonArgs ::= s"-$prefix$opt=$str"
      // TODO case (opt, JsArray(lst)) =>
      case (opt, jsValue) => NoSupportError(jsValue.toString)
    }

    // setting options using a JSON file.
    lazy val json: Parser[Unit] = ("-config=" ~> str) ^^ {
      case fileName => {
        Source.fromFile(fileName)("UTF-8").mkString.parseJson match {
          case (obj: JsObject) => obj.fields.foreach {
            case (phase, value: JsObject) => {
              if (JISET.phases.map(_.name).contains(phase))
                value.fields.foreach(addArg(s"$phase:", _))
              else throw NoPhaseError(phase)
            }
            case ("file", JsArray(lst)) => lst.foreach {
              case JsString(fileName) => jsonArgs ::= fileName
              case value => throw NoFileName(value.toString)
            }
            case ("file", value) => throw NoFileList(value.toString)
            case pair => addArg("", pair)
          }
          case value => throw NoObjError(value.toString)
        }
      }
    }

    // no option error
    lazy val optError: Parser[Unit] = ("-" ~> "[^=]+".r <~ "=") ~ str ^^ {
      case o ~ s => throw NoOptError(o, cmd)
    }
    lazy val simpleOptError: Parser[Unit] = ("-" ~> str) ^^ {
      o => throw NoOptError(o, cmd)
    }

    // a filename list
    lazy val fileName: Parser[Unit] = str ^^ {
      s => jisetConfig.args = s :: jisetConfig.args
    }

    // Generate a parser.
    val parser: Parser[Unit] = phrase(json) | ruleList.foldRight(
      phrase(optError) | phrase(simpleOptError) | phrase(fileName)
    ) { case (rule, prev) => phrase(rule) | prev }

    args.foreach(arg => {
      parse(parser, arg).get
      jsonArgs.foreach(parse(parser, _).get)
      jsonArgs = Nil
    })

    jisetConfig.args = jisetConfig.args.reverse
  }
}
