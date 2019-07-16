package kr.ac.kaist.ase.model

import scala.util.matching.Regex
import scala.util.parsing.combinator._
import scala.util.parsing.input._

object ESValueParser extends RegexParsers {
  // parsing
  def parseString(str: String): String = parseAll(SV.StringLiteral, str).get
  def parseNumber(str: String): Double = parseAll(MV.NumericLiteral, str).get
  def str2num(str: String): Double = parseAll(MV.StringNumericLiteral, str) match {
    case Success(n, _) => n
    case _ => Double.NaN
  }

  // String Value
  object SV {
    lazy val StringLiteral: S = (
      // The SV of StringLiteral::"" is the empty code unit sequence.
      "\"\"" ^^^ "" |||
      // The SV of StringLiteral::'' is the empty code unit sequence.
      "''" ^^^ "" |||
      // The SV of StringLiteral::"DoubleStringCharacters" is the SV of DoubleStringCharacters.
      "\"" ~> SV.DoubleStringCharacters <~ "\"" |||
      // The SV of StringLiteral::'SingleStringCharacters' is the SV of SingleStringCharacters.
      "'" ~> SV.SingleStringCharacters <~ "'"
    )
    lazy val DoubleStringCharacters: S = (
      // The SV of DoubleStringCharacters::DoubleStringCharacter is a sequence of up to two code units that is the SV of DoubleStringCharacter.
      SV.DoubleStringCharacter |||
      // The SV of DoubleStringCharacters::DoubleStringCharacterDoubleStringCharacters is a sequence of up to two code units that is the SV of DoubleStringCharacter followed by the code units of the SV of DoubleStringCharacters in order.
      seq(SV.DoubleStringCharacter, SV.DoubleStringCharacters)
    )
    lazy val SingleStringCharacters: S = (
      // The SV of SingleStringCharacters::SingleStringCharacter is a sequence of up to two code units that is the SV of SingleStringCharacter.
      SV.SingleStringCharacter |||
      // The SV of SingleStringCharacters::SingleStringCharacterSingleStringCharacters is a sequence of up to two code units that is the SV of SingleStringCharacter followed by the code units of the SV of SingleStringCharacters in order.
      seq(SV.SingleStringCharacter, SV.SingleStringCharacters)
    )
    lazy val DoubleStringCharacter: S = (
      // The SV of DoubleStringCharacter::SourceCharacterbut not one of " or \ or LineTerminator is the UTF16Encoding of the code point value of SourceCharacter.
      notChars("\"" | "\\" | Predef.LineTerminator) |||
      // The SV of DoubleStringCharacter::\EscapeSequence is the SV of the EscapeSequence.
      "\\" ~> SV.EscapeSequence |||
      // The SV of DoubleStringCharacter::LineContinuation is the empty code unit sequence.
      Predef.LineContinuation ^^^ ""
    )
    lazy val SingleStringCharacter: S = (
      // The SV of SingleStringCharacter::SourceCharacterbut not one of ' or \ or LineTerminator is the UTF16Encoding of the code point value of SourceCharacter.
      notChars("'" | "\\" | Predef.LineTerminator) |||
      // The SV of SingleStringCharacter::\EscapeSequence is the SV of the EscapeSequence.
      "\\" ~> SV.EscapeSequence |||
      // The SV of SingleStringCharacter::LineContinuation is the empty code unit sequence.
      Predef.LineContinuation ^^^ ""
    )
    lazy val EscapeSequence: S = (
      // The SV of EscapeSequence::CharacterEscapeSequence is the SV of the CharacterEscapeSequence.
      SV.CharacterEscapeSequence |||
      // The SV of EscapeSequence::0 is the code unit 0x0000 (NULL).
      "0" ^^^ "\u0000" |||
      // The SV of EscapeSequence::HexEscapeSequence is the SV of the HexEscapeSequence.
      SV.HexEscapeSequence |||
      // The SV of EscapeSequence::UnicodeEscapeSequence is the SV of the UnicodeEscapeSequence.
      SV.UnicodeEscapeSequence
    )
    lazy val CharacterEscapeSequence: S = (
      // The SV of CharacterEscapeSequence::SingleEscapeCharacter is the code unit whose value is determined by the SingleEscapeCharacter according to Table 34.
      Predef.SingleEscapeCharacter ^^ {
        case "b" => "\u0008"
        case "t" => "\u0009"
        case "n" => "\u000a"
        case "v" => "\u000b"
        case "f" => "\u000c"
        case "r" => "\u000d"
        case s => s
      } |||
      // The SV of CharacterEscapeSequence::NonEscapeCharacter is the SV of the NonEscapeCharacter.
      SV.NonEscapeCharacter
    )
    lazy val NonEscapeCharacter: S = (
      // The SV of NonEscapeCharacter::SourceCharacterbut not one of EscapeCharacter or LineTerminator is the UTF16Encoding of the code point value of SourceCharacter.
      notChars(Predef.EscapeCharacter | Predef.LineTerminator)
    )
    lazy val HexEscapeSequence: S = (
      // The SV of HexEscapeSequence::xHexDigitHexDigit is the code unit whose value is (16 times the MV of the first HexDigit) plus the MV of the second HexDigit.
      "x" ~> MV.HexDigit ~ MV.HexDigit ^^ {
        case x ~ y => Character.toChars(16 * x.toInt + y.toInt).mkString
      }
    )
    lazy val UnicodeEscapeSequence: S = (
      // The SV of UnicodeEscapeSequence::uHex4Digits is the SV of Hex4Digits.
      "u" ~> SV.Hex4Digits |||
      // The SV of UnicodeEscapeSequence::u{CodePoint} is the UTF16Encoding of the MV of CodePoint(HexDigits).
      "u{" ~> MV.HexDigits <~ "}" ^^ { case n => Character.toChars(n.toInt).mkString }
    )
    lazy val Hex4Digits: S = (
      // The SV of Hex4Digits::HexDigitHexDigitHexDigitHexDigit is the code unit whose value is (0x1000 times the MV of the first HexDigit) plus (0x100 times the MV of the second HexDigit) plus (0x10 times the MV of the third HexDigit) plus the MV of the fourth HexDigit.
      MV.HexDigit ~ MV.HexDigit ~ MV.HexDigit ~ MV.HexDigit ^^ {
        case a ~ b ~ c ~ d => Character.toChars(
          a.toInt * 0x1000 +
            b.toInt * 0x100 +
            c.toInt * 0x10 +
            d.toInt * 0x1
        ).mkString
      }
    )
  }

  // Mathematical Value
  object MV {
    lazy val NumericLiteral: D = (
      // The MV of NumericLiteral::DecimalLiteral is the MV of DecimalLiteral.
      MV.DecimalLiteral |||
      // The MV of NumericLiteral::BinaryIntegerLiteral is the MV of BinaryIntegerLiteral.
      MV.BinaryIntegerLiteral |||
      // The MV of NumericLiteral::OctalIntegerLiteral is the MV of OctalIntegerLiteral.
      MV.OctalIntegerLiteral |||
      // The MV of NumericLiteral::HexIntegerLiteral is the MV of HexIntegerLiteral.
      MV.HexIntegerLiteral
    )
    lazy val DecimalLiteral: D = Predef.DecimalLiteral ^^ { _.toDouble }
    lazy val HexDigit: D = (
      "[0-9]".r ^^ { _.toDouble } |||
      "[a-fA-F]".r ^^ { s => s"0x${s}p0".toDouble }
    )
    lazy val BinaryIntegerLiteral: D = (
      // The MV of BinaryIntegerLiteral::0bBinaryDigits is the MV of BinaryDigits.
      "0b" ~> MV.BinaryDigits |||
      // The MV of BinaryIntegerLiteral::0BBinaryDigits is the MV of BinaryDigits.
      "0B" ~> MV.BinaryDigits
    )
    lazy val BinaryDigits: D = rep1("0" | "1") ^^ {
      case list => (0.0 /: list) {
        case (x, s) => x * 2 + s.toInt
      }
    }
    lazy val OctalIntegerLiteral: D = (
      // The MV of OctalIntegerLiteral::0oOctalDigits is the MV of OctalDigits.
      "0o" ~> MV.OctalDigits |||
      // The MV of OctalIntegerLiteral::0OOctalDigits is the MV of OctalDigits.
      "0O" ~> MV.OctalDigits
    )
    lazy val OctalDigits: D = rep1("[0-7]".r) ^^ {
      case list => (0.0 /: list) {
        case (x, s) => x * 8 + s.toInt
      }
    }
    lazy val HexIntegerLiteral: D = (
      // The MV of HexIntegerLiteral::0xHexDigits is the MV of HexDigits.
      "0x" ~> MV.HexDigits |||
      // The MV of HexIntegerLiteral::0XHexDigits is the MV of HexDigits.
      "0X" ~> MV.HexDigits
    )
    lazy val HexDigits: D = rep1(MV.HexDigit) ^^ {
      case list => (0.0 /: list) {
        case (x, s) => x * 16 + s.toInt
      }
    }
    lazy val StringNumericLiteral: D = (
      // The MV of StringNumericLiteral:::[empty] is 0.
      "" ^^^ 0.0 |||
      // The MV of StringNumericLiteral:::StrWhiteSpace is 0.
      Predef.StrWhiteSpace ^^^ 0.0 |||
      // The MV of StringNumericLiteral:::StrWhiteSpaceStrNumericLiteralStrWhiteSpace is the MV of StrNumericLiteral, no matter whether white space is present or not.
      sOpt(Predef.StrWhiteSpace) ~> MV.StrNumericLiteral <~ sOpt(Predef.StrWhiteSpace)
    )
    lazy val StrNumericLiteral: D = (
      // The MV of StrNumericLiteral:::StrDecimalLiteral is the MV of StrDecimalLiteral.
      MV.StrDecimalLiteral |||
      // The MV of StrNumericLiteral:::BinaryIntegerLiteral is the MV of BinaryIntegerLiteral.
      MV.BinaryIntegerLiteral |||
      // The MV of StrNumericLiteral:::OctalIntegerLiteral is the MV of OctalIntegerLiteral.
      MV.OctalIntegerLiteral |||
      // The MV of StrNumericLiteral:::HexIntegerLiteral is the MV of HexIntegerLiteral.
      MV.HexIntegerLiteral
    )
    lazy val StrDecimalLiteral: D = Predef.StrDecimalLiteral ^^ { _.toDouble }
  }

  // Template Value
  object TV {
  }

  // Template Raw Value
  object TRV {
  }

  // types
  type S = Parser[String]
  type D = Parser[Double]

  // predefined parsers
  object Predef {
    lazy val SourceCharacter: S = "(?s).".r
    lazy val ZWNJ: Parser[String] = "\u200C"
    lazy val ZWJ: Parser[String] = "\u200D"
    lazy val ZWNBSP: Parser[String] = "\uFEFF"
    lazy val TAB: Parser[String] = "\u0009"
    lazy val VT: Parser[String] = "\u000B"
    lazy val FF: Parser[String] = "\u000C"
    lazy val SP: Parser[String] = "\u0020"
    lazy val NBSP: Parser[String] = "\u00A0"
    lazy val USP: Parser[String] = "[\u1680\u2000\u2001\u2002\u2003\u2004\u2005\u2006\u2007\u2008\u2009\u200A\u202F\u205F\u3000]".r
    lazy val LF: S = "\u000A"
    lazy val CR: S = "\u000D"
    lazy val LS: S = "\u2028"
    lazy val PS: S = "\u2029"
    lazy val WhiteSpace: Parser[String] = TAB | VT | FF | SP | NBSP | ZWNBSP | USP
    lazy val LineTerminator: S = LF | CR | LS | PS
    lazy val LineTerminatorSequence: Parser[String] = LF | CR <~ not(LF) | LS | PS | seq(CR, LF)
    lazy val LineContinuation: S = "\\" ~> LineTerminatorSequence
    lazy val SingleEscapeCharacter: S = """['"\\bfnrtv]""".r
    lazy val EscapeCharacter: S = (
      SingleEscapeCharacter |||
      DecimalDigit |||
      "x" |||
      "u"
    )
    lazy val DecimalLiteral: S = (
      seq(DecimalIntegerLiteral, ".", sOpt(DecimalDigits), sOpt(ExponentPart)) |||
      seq(".", DecimalDigits, sOpt(ExponentPart)) |||
      seq(DecimalIntegerLiteral, sOpt(ExponentPart))
    )
    lazy val StrDecimalLiteral: S = (
      StrUnsignedDecimalLiteral |||
      seq("+", StrUnsignedDecimalLiteral) |||
      seq("-", StrUnsignedDecimalLiteral)
    )
    lazy val StrUnsignedDecimalLiteral: S = (
      "Infinity" |||
      seq(DecimalDigits, ".", sOpt(DecimalDigits), sOpt(ExponentPart)) |||
      seq(".", DecimalDigits, sOpt(ExponentPart)) |||
      seq(DecimalDigits, sOpt(ExponentPart))
    )
    lazy val DecimalIntegerLiteral: S = (
      "0" |||
      seq(NonZeroDigit, sOpt(DecimalDigits))
    )
    lazy val DecimalDigits: S = (
      DecimalDigit |||
      seq(DecimalDigit, DecimalDigits)
    )
    lazy val DecimalDigit: S = "[0-9]".r
    lazy val NonZeroDigit: S = "[1-9]".r
    lazy val ExponentPart: S = (
      seq(ExponentIndicator, SignedInteger)
    )
    lazy val ExponentIndicator: S = "[eE]".r
    lazy val SignedInteger: S = (
      DecimalDigits |||
      seq("+", DecimalDigits) |||
      seq("-", DecimalDigits)
    )
    lazy val StrWhiteSpace: S = (
      seq(StrWhiteSpaceChar, sOpt(StrWhiteSpace))
    )
    lazy val StrWhiteSpaceChar: S = (
      WhiteSpace |||
      LineTerminator
    )
  }

  // sequences
  def sOpt(s: => S): S = opt(s) ^^ {
    case Some(s) => s
    case None => ""
  }
  def seq(x0: S, x1: => S): S = x0 ~ x1 ^^ { case x ~ y => x + y }
  def seq(x0: S, x1: => S, x2: => S): S = x0 ~ x1 ~ x2 ^^ { case x ~ y ~ z => x + y + z }
  def seq(x0: S, x1: => S, x2: => S, x3: => S): S = x0 ~ x1 ~ x2 ~ x3 ^^ { case x ~ y ~ z ~ a => x + y + z + a }

  // filtering out source characters
  def notChars(cond: S): S = Predef.SourceCharacter.filter(s => parseAll(cond, s).isEmpty)

  // not skip white spaces
  override def skipWhitespace = false
}
