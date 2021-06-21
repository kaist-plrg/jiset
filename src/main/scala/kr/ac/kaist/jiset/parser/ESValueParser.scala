package kr.ac.kaist.jiset.parser

import kr.ac.kaist.jiset.js.ast._
import kr.ac.kaist.jiset.ir._
import kr.ac.kaist.jiset.error._
import kr.ac.kaist.jiset.util.Useful._
import kr.ac.kaist.jiset.parser.UnicodeRegex

import scala.util.matching.Regex
import scala.util.parsing.combinator._
import scala.util.parsing.input._

object ESValueParser extends RegexParsers with UnicodeRegex {
  // parsing
  def parseIdentifier(str: String): String = get("SV.IdentifierName", SV.IdentifierName, str)
  def parseString(str: String): String = get("SV.StringLiteral", SV.StringLiteral, str)
  def parseNumber(str: String): Double = get("MV.NumericLiteral", MV.NumericLiteral, str)
  def parseTVNoSubstitutionTemplate(str: String): String = get("TV.NoSubstitutionTemplate", TV.NoSubstitutionTemplate, str)
  def parseTRVNoSubstitutionTemplate(str: String): String = get("TRV.NoSubstitutionTemplate", TRV.NoSubstitutionTemplate, str)
  def parseTVTemplateHead(str: String): String = get("TV.TemplateHead", TV.TemplateHead, str)
  def parseTRVTemplateHead(str: String): String = get("TRV.TemplateHead", TRV.TemplateHead, str)
  def parseTVTemplateMiddle(str: String): String = get("TV.TemplateMiddle", TV.TemplateMiddle, str)
  def parseTRVTemplateMiddle(str: String): String = get("TRV.TemplateMiddle", TRV.TemplateMiddle, str)
  def parseTVTemplateTail(str: String): String = get("TV.TemplateTail", TV.TemplateTail, str)
  def parseTRVTemplateTail(str: String): String = get("TRV.TemplateTail", TRV.TemplateTail, str)
  def str2num(str: String): Double = parseAll(MV.StringNumericLiteral, str) match {
    case Success(n, _) => n
    case _ => Double.NaN
  }
  private def get[T](name: String, rule: Parser[T], str: String): T = parseAll(rule, str) match {
    case Success(res, _) => res
    case f => throw ParseFailed(name + "\n" + f.toString)
  }

  // String Value
  object SV {
    lazy val IdentifierName: S = (
      seq(SV.IdentifierStart, rep(SV.IdentifierPart) ^^ { case l => l.foldLeft("")(_ + _) })
    )
    lazy val IdentifierStart: S = (
      IDStart |||
      "$" |||
      "_" |||
      "\\" ~> SV.UnicodeEscapeSequence
    )
    lazy val IdentifierPart: S = (
      IDContinue |||
      "$" |||
      "\\" ~> SV.UnicodeEscapeSequence |||
      Predef.ZWNJ |||
      Predef.ZWJ
    )
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
      // The SV of DoubleStringCharacter::<LS> is the code unit 0x2028 (LINE SEPARATOR).
      Predef.LS |||
      // The SV of DoubleStringCharacter::<PS> is the code unit 0x2029 (PARAGRAPH SEPARATOR).
      Predef.PS |||
      // The SV of DoubleStringCharacter::\EscapeSequence is the SV of the EscapeSequence.
      "\\" ~> SV.EscapeSequence |||
      // The SV of DoubleStringCharacter::LineContinuation is the empty code unit sequence.
      Predef.LineContinuation ^^^ ""
    )
    lazy val SingleStringCharacter: S = (
      // The SV of SingleStringCharacter::SourceCharacterbut not one of ' or \ or LineTerminator is the UTF16Encoding of the code point value of SourceCharacter.
      notChars("'" | "\\" | Predef.LineTerminator) |||
      // The SV of SingleStringCharacter::<LS> is the code unit 0x2028 (LINE SEPARATOR).
      Predef.LS |||
      // The SV of SingleStringCharacter::<PS> is the code unit 0x2029 (PARAGRAPH SEPARATOR).
      Predef.PS |||
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
      "u{" ~> MV.CodePoint <~ "}" ^^ { case n => Character.toChars(n.toInt).mkString }
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
      case list => list.foldLeft(0.0) {
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
      case list => list.foldLeft(0.0) {
        case (x, s) => x * 8 + s.toInt
      }
    }
    lazy val HexIntegerLiteral: D = (
      // The MV of HexIntegerLiteral::0xHexDigits is the MV of HexDigits.
      "0x" ~> MV.HexDigits |||
      // The MV of HexIntegerLiteral::0XHexDigits is the MV of HexDigits.
      "0X" ~> MV.HexDigits
    )
    lazy val CodePoint: D = MV.HexDigits.filter(_ <= 0x10ffff)
    lazy val HexDigits: D = rep1(MV.HexDigit) ^^ {
      case list => list.foldLeft(0.0) {
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
    lazy val NoSubstitutionTemplate: S = (
      // The TV of NoSubstitutionTemplate::`` is the empty code unit sequence.
      "``" ^^^ "" |||
      // The TV of NoSubstitutionTemplate::`TemplateCharacters` is the TV of TemplateCharacters.
      "`" ~> TV.TemplateCharacters <~ "`"
    )
    lazy val TemplateHead: S = (
      // The TV of TemplateHead::`${ is the empty code unit sequence.
      "`${" ^^^ "" |||
      // The TV of TemplateHead::`TemplateCharacters${ is the TV of TemplateCharacters.
      "`" ~> TV.TemplateCharacters <~ "${"
    )
    lazy val TemplateMiddle: S = (
      // The TV of TemplateMiddle::}${ is the empty code unit sequence.
      "}${" ^^^ "" |||
      // The TV of TemplateMiddle::}TemplateCharacters${ is the TV of TemplateCharacters.
      "}" ~> TV.TemplateCharacters <~ "${"
    )
    lazy val TemplateTail: S = (
      // The TV of TemplateTail::}TemplateCharacters` is the TV of TemplateCharacters.
      "}" ~> TV.TemplateCharacters <~ "`" |||
      // The TV of TemplateTail::}` is the empty code unit sequence.
      "}`" ^^^ ""
    )
    lazy val TemplateCharacter: S = (
      // The TV of TemplateCharacter::$ is the code unit 0x0024 (DOLLAR SIGN).
      "$" <~ not("{") |||
      // The TV of TemplateCharacter::LineContinuation is the TV of LineContinuation.
      TV.LineContinuation |||
      // The TV of TemplateCharacter::LineTerminatorSequence is the TRV of LineTerminatorSequence.
      TRV.LineTerminatorSequence |||
      // The TV of TemplateCharacter::SourceCharacterbut not one of ` or \ or $ or LineTerminator is the UTF16Encoding of the code point value of SourceCharacter.
      notChars("`" | "\\" | "$" | Predef.LineTerminator) |||
      // The TV of TemplateCharacter::\EscapeSequence is the SV of EscapeSequence.
      "\\" ~> SV.EscapeSequence
    )
    lazy val TemplateCharacters: S = (
      // The TV of TemplateCharacters::TemplateCharacter is the TV of TemplateCharacter.
      TV.TemplateCharacter |||
      // XXX The TV of TemplateCharacter::\NotEscapeSequence is undefined.
      // XXX The TV of TemplateCharacters::TemplateCharacterTemplateCharacters is undefined if either the TV of TemplateCharacter is undefined or the TV of TemplateCharacters is undefined.
      // Otherwise, it is a sequence consisting of the code units of the TV of TemplateCharacter followed by the code units of the TV of TemplateCharacters.
      seq(TemplateCharacter, TemplateCharacters)
    )
    lazy val LineContinuation: S = (
      // The TV of LineContinuation::\LineTerminatorSequence is the empty code unit sequence.
      "\\" ~ Predef.LineTerminatorSequence ^^^ ""
    )
  }

  // Template Raw Value
  object TRV {
    lazy val CharacterEscapeSequence: S = (
      // The TRV of CharacterEscapeSequence::NonEscapeCharacter is the SV of the NonEscapeCharacter.
      SV.NonEscapeCharacter |||
      // The TRV of CharacterEscapeSequence::SingleEscapeCharacter is the TRV of the SingleEscapeCharacter.
      TRV.SingleEscapeCharacter
    )
    lazy val DecimalDigit: S = (
      // The TRV of DecimalDigit::one of0123456789 is the SV of the SourceCharacter that is that single code point.
      "[0-9]".r
    )
    lazy val EscapeSequence: S = (
      // The TRV of EscapeSequence::0 is the code unit 0x0030 (DIGIT ZERO).
      "0" |||
      // The TRV of EscapeSequence::CharacterEscapeSequence is the TRV of the CharacterEscapeSequence.
      TRV.CharacterEscapeSequence |||
      // The TRV of EscapeSequence::HexEscapeSequence is the TRV of the HexEscapeSequence.
      TRV.HexEscapeSequence |||
      // The TRV of EscapeSequence::UnicodeEscapeSequence is the TRV of the UnicodeEscapeSequence.
      TRV.UnicodeEscapeSequence
    )
    lazy val Hex4Digits: S = (
      // The TRV of Hex4Digits::HexDigitHexDigitHexDigitHexDigit is the sequence consisting of the TRV of the first HexDigit followed by the TRV of the second HexDigit followed by the TRV of the third HexDigit followed by the TRV of the fourth HexDigit.
      seq(TRV.HexDigit, TRV.HexDigit, TRV.HexDigit, TRV.HexDigit)
    )
    lazy val NotCodePoint: S = Predef.NotCodePoint
    lazy val CodePoint: S = Predef.CodePoint
    lazy val HexDigits: S = (
      // The TRV of HexDigits::HexDigit is the TRV of HexDigit.
      TRV.HexDigit |||
      // The TRV of HexDigits::HexDigitsHexDigit is the sequence consisting of TRV of HexDigits followed by TRV of HexDigit.
      seq(TRV.HexDigit, TRV.HexDigits)
    )
    lazy val HexEscapeSequence: S = (
      // The TRV of HexEscapeSequence::xHexDigitHexDigit is the sequence consisting of the code unit 0x0078 (LATIN SMALL LETTER X) followed by TRV of the first HexDigit followed by the TRV of the second HexDigit.
      "x" ~> seq(TRV.HexDigit, TRV.HexDigit)
    )
    lazy val LineContinuation: S = (
      // The TRV of LineContinuation::\LineTerminatorSequence is the sequence consisting of the code unit 0x005C (REVERSE SOLIDUS) followed by the code units of TRV of LineTerminatorSequence.
      seq("\\", LineTerminatorSequence)
    )
    lazy val LineTerminatorSequence: S = (
      // The TRV of LineTerminatorSequence::<CR> is the code unit 0x000A (LINE FEED).
      Predef.CR ^^^ "\u000a" |||
      // The TRV of LineTerminatorSequence::<CR><LF> is the sequence consisting of the code unit 0x000A (LINE FEED).
      Predef.CR ~> Predef.LF |||
      // The TRV of LineTerminatorSequence::<LF> is the code unit 0x000A (LINE FEED).
      Predef.LF |||
      // The TRV of LineTerminatorSequence::<LS> is the code unit 0x2028 (LINE SEPARATOR).
      Predef.LS |||
      // The TRV of LineTerminatorSequence::<PS> is the code unit 0x2029 (PARAGRAPH SEPARATOR).
      Predef.PS
    )
    lazy val NoSubstitutionTemplate: S = (
      // The TRV of NoSubstitutionTemplate::`TemplateCharacters` is the TRV of TemplateCharacters.
      "`" ~> TRV.TemplateCharacters <~ "`" |||
      // The TRV of NoSubstitutionTemplate::`` is the empty code unit sequence.
      "``" ^^^ ""
    )
    lazy val NotEscapeSequence: S = (
      // The TRV of NotEscapeSequence::0DecimalDigit is the sequence consisting of the code unit 0x0030 (DIGIT ZERO) followed by the code units of the TRV of DecimalDigit.
      seq("0", TRV.DecimalDigit) |||
      // The TRV of NotEscapeSequence::uHexDigitHexDigitHexDigit[lookahead ∉ HexDigit] is the sequence consisting of the code unit 0x0075 (LATIN SMALL LETTER U) followed by the code units of the TRV of the first HexDigit followed by the code units of the TRV of the second HexDigit followed by the code units of the TRV of the third HexDigit.
      seq("u", TRV.HexDigit, TRV.HexDigit, TRV.HexDigit) <~ not(Predef.HexDigit) |||
      // The TRV of NotEscapeSequence::uHexDigitHexDigit[lookahead ∉ HexDigit] is the sequence consisting of the code unit 0x0075 (LATIN SMALL LETTER U) followed by the code units of the TRV of the first HexDigit followed by the code units of the TRV of the second HexDigit.
      seq("u", TRV.HexDigit, TRV.HexDigit) <~ not(Predef.HexDigit) |||
      // The TRV of NotEscapeSequence::uHexDigit[lookahead ∉ HexDigit] is the sequence consisting of the code unit 0x0075 (LATIN SMALL LETTER U) followed by the code units of the TRV of HexDigit.
      seq("u", TRV.HexDigit) <~ not(Predef.HexDigit) |||
      // The TRV of NotEscapeSequence::u[lookahead ∉ HexDigit][lookahead ≠ {] is the code unit 0x0075 (LATIN SMALL LETTER U).
      "u" <~ not(Predef.HexDigit) <~ not("{") |||
      // The TRV of NotEscapeSequence::u{CodePoint[lookahead ∉ HexDigit][lookahead ≠ }] is the sequence consisting of the code unit 0x0075 (LATIN SMALL LETTER U) followed by the code unit 0x007B (LEFT CURLY BRACKET) followed by the code units of the TRV of CodePoint.
      seq("u{", TRV.CodePoint) <~ not(Predef.HexDigit) <~ not("}") |||
      // The TRV of NotEscapeSequence::u{NotCodePoint[lookahead ∉ HexDigit] is the sequence consisting of the code unit 0x0075 (LATIN SMALL LETTER U) followed by the code unit 0x007B (LEFT CURLY BRACKET) followed by the code units of the TRV of NotCodePoint.
      seq("u{", TRV.NotCodePoint) <~ not(Predef.HexDigit) |||
      // The TRV of NotEscapeSequence::u{[lookahead ∉ HexDigit] is the sequence consisting of the code unit 0x0075 (LATIN SMALL LETTER U) followed by the code unit 0x007B (LEFT CURLY BRACKET).
      "u{" <~ not(Predef.HexDigit) |||
      // The TRV of NotEscapeSequence::xHexDigit[lookahead ∉ HexDigit] is the sequence consisting of the code unit 0x0078 (LATIN SMALL LETTER X) followed by the code units of the TRV of HexDigit.
      seq("x", TRV.HexDigit) <~ not(HexDigit) |||
      // The TRV of NotEscapeSequence::x[lookahead ∉ HexDigit] is the code unit 0x0078 (LATIN SMALL LETTER X).
      "x[" <~ not(HexDigit)
    )
    lazy val SingleEscapeCharacter: S = (
      // The TRV of SingleEscapeCharacter::one of'"\bfnrtv is the SV of the SourceCharacter that is that single code point.
      Predef.SingleEscapeCharacter
    )
    lazy val TemplateCharacter: S = (
      // The TRV of TemplateCharacter::$ is the code unit 0x0024 (DOLLAR SIGN).
      "$" <~ not("{") |||
      // The TRV of TemplateCharacter::LineContinuation is the TRV of LineContinuation.
      TRV.LineContinuation |||
      // The TRV of TemplateCharacter::LineTerminatorSequence is the TRV of LineTerminatorSequence.
      TRV.LineTerminatorSequence |||
      // The TRV of TemplateCharacter::SourceCharacterbut not one of ` or \ or $ or LineTerminator is the UTF16Encoding of the code point value of SourceCharacter.
      notChars("`" | "\\" | "$" | Predef.LineTerminator) |||
      // The TRV of TemplateCharacter::\EscapeSequence is the sequence consisting of the code unit 0x005C (REVERSE SOLIDUS) followed by the code units of TRV of EscapeSequence.
      seq("\\", TRV.EscapeSequence) |||
      // The TRV of TemplateCharacter::\NotEscapeSequence is the sequence consisting of the code unit 0x005C (REVERSE SOLIDUS) followed by the code units of TRV of NotEscapeSequence.
      seq("\\", TRV.NotEscapeSequence)
    )
    lazy val TemplateCharacters: S = (
      // The TRV of TemplateCharacters::TemplateCharacter is the TRV of TemplateCharacter.
      TRV.TemplateCharacter |||
      // The TRV of TemplateCharacters::TemplateCharacterTemplateCharacters is a sequence consisting of the code units of the TRV of TemplateCharacter followed by the code units of the TRV of TemplateCharacters.
      seq(TRV.TemplateCharacter, TRV.TemplateCharacters)
    )
    lazy val TemplateHead: S = (
      // The TRV of TemplateHead::`${ is the empty code unit sequence.
      "`${" ^^^ "" |||
      // The TRV of TemplateHead::`TemplateCharacters${ is the TRV of TemplateCharacters.
      "`" ~> TRV.TemplateCharacters <~ "${"
    )
    lazy val TemplateMiddle: S = (
      // The TRV of TemplateMiddle::}${ is the empty code unit sequence.
      "}${" ^^^ "" |||
      // The TRV of TemplateMiddle::}TemplateCharacters${ is the TRV of TemplateCharacters.
      "}" ~> TRV.TemplateCharacters <~ "${"
    )
    lazy val TemplateTail: S = (
      // The TRV of TemplateTail::}TemplateCharacters` is the TRV of TemplateCharacters.
      "}" ~> TRV.TemplateCharacters <~ "`" |||
      // The TRV of TemplateTail::}` is the empty code unit sequence.
      "}`" ^^^ ""
    )
    lazy val UnicodeEscapeSequence: S = (
      // The TRV of UnicodeEscapeSequence::uHex4Digits is the sequence consisting of the code unit 0x0075 (LATIN SMALL LETTER U) followed by TRV of Hex4Digits.
      seq("u", TRV.Hex4Digits) |||
      // The TRV of UnicodeEscapeSequence::u{CodePoint} is the sequence consisting of the code unit 0x0075 (LATIN SMALL LETTER U) followed by the code unit 0x007B (LEFT CURLY BRACKET) followed by TRV of CodePoint followed by the code unit 0x007D (RIGHT CURLY BRACKET).
      seq("u{", TRV.CodePoint, "}")
    )
    lazy val HexDigit: S = (
      // The TRV of a HexDigit is the SV of the SourceCharacter that is that HexDigit.
      Predef.HexDigit
    )
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
    lazy val HexDigits: S = seq(HexDigit, sOpt(HexDigits))
    lazy val HexDigit: S = "[0-9a-fA-F]".r
    lazy val CodePoint: S = HexDigits.filter(s => parseAll(MV.HexDigits, s).get <= 0x10ffff)
    lazy val NotCodePoint: S = HexDigits.filter(s => parseAll(MV.HexDigits, s).get > 0x10ffff)
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
