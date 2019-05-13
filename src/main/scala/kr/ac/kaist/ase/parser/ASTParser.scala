package kr.ac.kaist.ase.parser

import kr.ac.kaist.ase.node.ast

object ASTParser extends ASTParsers {
  lazy val SourceCharacter: Parser[String] =
    seq(Unicode, subSourceCharacter) |||
      STR_MISMATCH
  lazy val subSourceCharacter: Parser[String] =
    STR_MATCH
  lazy val InputElementDiv: Parser[String] =
    seq(WhiteSpace, subInputElementDiv) |||
      seq(LineTerminator, subInputElementDiv) |||
      seq(Comment, subInputElementDiv) |||
      seq(CommonToken, subInputElementDiv) |||
      seq(DivPunctuator, subInputElementDiv) |||
      seq(RightBracePunctuator, subInputElementDiv) |||
      STR_MISMATCH
  lazy val subInputElementDiv: Parser[String] =
    STR_MATCH
  lazy val InputElementRegExp: Parser[String] =
    seq(WhiteSpace, subInputElementRegExp) |||
      seq(LineTerminator, subInputElementRegExp) |||
      seq(Comment, subInputElementRegExp) |||
      seq(CommonToken, subInputElementRegExp) |||
      seq(RightBracePunctuator, subInputElementRegExp) |||
      seq(RegularExpressionLiteral, subInputElementRegExp) |||
      STR_MISMATCH
  lazy val subInputElementRegExp: Parser[String] =
    STR_MATCH
  lazy val InputElementRegExpOrTemplateTail: Parser[String] =
    seq(WhiteSpace, subInputElementRegExpOrTemplateTail) |||
      seq(LineTerminator, subInputElementRegExpOrTemplateTail) |||
      seq(Comment, subInputElementRegExpOrTemplateTail) |||
      seq(CommonToken, subInputElementRegExpOrTemplateTail) |||
      seq(RegularExpressionLiteral, subInputElementRegExpOrTemplateTail) |||
      seq(TemplateSubstitutionTail, subInputElementRegExpOrTemplateTail) |||
      STR_MISMATCH
  lazy val subInputElementRegExpOrTemplateTail: Parser[String] =
    STR_MATCH
  lazy val InputElementTemplateTail: Parser[String] =
    seq(WhiteSpace, subInputElementTemplateTail) |||
      seq(LineTerminator, subInputElementTemplateTail) |||
      seq(Comment, subInputElementTemplateTail) |||
      seq(CommonToken, subInputElementTemplateTail) |||
      seq(DivPunctuator, subInputElementTemplateTail) |||
      seq(TemplateSubstitutionTail, subInputElementTemplateTail) |||
      STR_MISMATCH
  lazy val subInputElementTemplateTail: Parser[String] =
    STR_MATCH
  lazy val MultiLineComment: Parser[String] =
    seq("/*", strOpt(MultiLineCommentChars), "*/", subMultiLineComment) |||
      STR_MISMATCH
  lazy val subMultiLineComment: Parser[String] =
    STR_MATCH
  lazy val MultiLineCommentChars: Parser[String] =
    seq(MultiLineNotAsteriskChar, strOpt(MultiLineCommentChars), subMultiLineCommentChars) |||
      seq("*", strOpt(PostAsteriskCommentChars), subMultiLineCommentChars) |||
      STR_MISMATCH
  lazy val subMultiLineCommentChars: Parser[String] =
    STR_MATCH
  lazy val PostAsteriskCommentChars: Parser[String] =
    seq(MultiLineNotForwardSlashOrAsteriskChar, strOpt(MultiLineCommentChars), subPostAsteriskCommentChars) |||
      seq("*", strOpt(PostAsteriskCommentChars), subPostAsteriskCommentChars) |||
      STR_MISMATCH
  lazy val subPostAsteriskCommentChars: Parser[String] =
    STR_MATCH
  lazy val MultiLineNotAsteriskChar: Parser[String] =
    seq((SourceCharacter \ "*"), subMultiLineNotAsteriskChar) |||
      STR_MISMATCH
  lazy val subMultiLineNotAsteriskChar: Parser[String] =
    STR_MATCH
  lazy val MultiLineNotForwardSlashOrAsteriskChar: Parser[String] =
    seq((SourceCharacter \ "/" ||| "*"), subMultiLineNotForwardSlashOrAsteriskChar) |||
      STR_MISMATCH
  lazy val subMultiLineNotForwardSlashOrAsteriskChar: Parser[String] =
    STR_MATCH
  lazy val SingleLineComment: Parser[String] =
    seq("//", strOpt(SingleLineCommentChars), subSingleLineComment) |||
      STR_MISMATCH
  lazy val subSingleLineComment: Parser[String] =
    STR_MATCH
  lazy val SingleLineCommentChars: Parser[String] =
    seq(SingleLineCommentChar, strOpt(SingleLineCommentChars), subSingleLineCommentChars) |||
      STR_MISMATCH
  lazy val subSingleLineCommentChars: Parser[String] =
    STR_MATCH
  lazy val SingleLineCommentChar: Parser[String] =
    seq((SourceCharacter \ LineTerminator), subSingleLineCommentChar) |||
      STR_MISMATCH
  lazy val subSingleLineCommentChar: Parser[String] =
    STR_MATCH
  lazy val CommonToken: Parser[String] =
    seq(IdentifierName, subCommonToken) |||
      seq(Punctuator, subCommonToken) |||
      seq(NumericLiteral, subCommonToken) |||
      seq(StringLiteral, subCommonToken) |||
      seq(Template, subCommonToken) |||
      STR_MISMATCH
  lazy val subCommonToken: Parser[String] =
    STR_MATCH
  lazy val IdentifierName: Parser[String] =
    seq(IdentifierStart, subIdentifierName) |||
      STR_MISMATCH
  lazy val subIdentifierName: Parser[String] =
    seq(IdentifierPart, subIdentifierName) |||
      STR_MATCH
  lazy val IdentifierStart: Parser[String] =
    seq(UnicodeIDStart, subIdentifierStart) |||
      seq("$", subIdentifierStart) |||
      seq("_", subIdentifierStart) |||
      seq("\\", UnicodeEscapeSequence, subIdentifierStart) |||
      STR_MISMATCH
  lazy val subIdentifierStart: Parser[String] =
    STR_MATCH
  lazy val IdentifierPart: Parser[String] =
    seq(UnicodeIDContinue, subIdentifierPart) |||
      seq("$", subIdentifierPart) |||
      seq("\\", UnicodeEscapeSequence, subIdentifierPart) |||
      seq(ZWNJ, ZWJ, subIdentifierPart) |||
      STR_MISMATCH
  lazy val subIdentifierPart: Parser[String] =
    STR_MATCH
  lazy val UnicodeIDStart: Parser[String] =
    seq(IDStart, subUnicodeIDStart) |||
      STR_MISMATCH
  lazy val subUnicodeIDStart: Parser[String] =
    STR_MATCH
  lazy val UnicodeIDContinue: Parser[String] =
    seq(IDContinue, subUnicodeIDContinue) |||
      STR_MISMATCH
  lazy val subUnicodeIDContinue: Parser[String] =
    STR_MATCH
  lazy val ReservedWord: Parser[String] =
    seq(Keyword, subReservedWord) |||
      seq(FutureReservedWord, subReservedWord) |||
      seq(NullLiteral, subReservedWord) |||
      seq(BooleanLiteral, subReservedWord) |||
      STR_MISMATCH
  lazy val subReservedWord: Parser[String] =
    STR_MATCH
  lazy val Keyword: Parser[String] =
    seq("await", subKeyword) |||
      seq("break", subKeyword) |||
      seq("case", subKeyword) |||
      seq("catch", subKeyword) |||
      seq("class", subKeyword) |||
      seq("const", subKeyword) |||
      seq("continue", subKeyword) |||
      seq("debugger", subKeyword) |||
      seq("default", subKeyword) |||
      seq("delete", subKeyword) |||
      seq("do", subKeyword) |||
      seq("else", subKeyword) |||
      seq("export", subKeyword) |||
      seq("extends", subKeyword) |||
      seq("finally", subKeyword) |||
      seq("for", subKeyword) |||
      seq("function", subKeyword) |||
      seq("if", subKeyword) |||
      seq("import", subKeyword) |||
      seq("in", subKeyword) |||
      seq("instanceof", subKeyword) |||
      seq("new", subKeyword) |||
      seq("return", subKeyword) |||
      seq("super", subKeyword) |||
      seq("switch", subKeyword) |||
      seq("this", subKeyword) |||
      seq("throw", subKeyword) |||
      seq("try", subKeyword) |||
      seq("typeof", subKeyword) |||
      seq("var", subKeyword) |||
      seq("void", subKeyword) |||
      seq("while", subKeyword) |||
      seq("with", subKeyword) |||
      seq("yield", subKeyword) |||
      STR_MISMATCH
  lazy val subKeyword: Parser[String] =
    STR_MATCH
  lazy val FutureReservedWord: Parser[String] =
    seq("enum", subFutureReservedWord) |||
      STR_MISMATCH
  lazy val subFutureReservedWord: Parser[String] =
    STR_MATCH
  lazy val Punctuator: Parser[String] =
    seq("{", subPunctuator) |||
      seq("(", subPunctuator) |||
      seq(")", subPunctuator) |||
      seq("[", subPunctuator) |||
      seq("]", subPunctuator) |||
      seq(".", subPunctuator) |||
      seq("...", subPunctuator) |||
      seq(";", subPunctuator) |||
      seq(",", subPunctuator) |||
      seq("<", subPunctuator) |||
      seq(">", subPunctuator) |||
      seq("<=", subPunctuator) |||
      seq(">=", subPunctuator) |||
      seq("==", subPunctuator) |||
      seq("!=", subPunctuator) |||
      seq("===", subPunctuator) |||
      seq("!==", subPunctuator) |||
      seq("+", subPunctuator) |||
      seq("-", subPunctuator) |||
      seq("*", subPunctuator) |||
      seq("%", subPunctuator) |||
      seq("**", subPunctuator) |||
      seq("++", subPunctuator) |||
      seq("--", subPunctuator) |||
      seq("<<", subPunctuator) |||
      seq(">>", subPunctuator) |||
      seq(">>>", subPunctuator) |||
      seq("&", subPunctuator) |||
      seq("|", subPunctuator) |||
      seq("^", subPunctuator) |||
      seq("!", subPunctuator) |||
      seq("~", subPunctuator) |||
      seq("&&", subPunctuator) |||
      seq("||", subPunctuator) |||
      seq("?", subPunctuator) |||
      seq(":", subPunctuator) |||
      seq("=", subPunctuator) |||
      seq("+=", subPunctuator) |||
      seq("-=", subPunctuator) |||
      seq("*=", subPunctuator) |||
      seq("%=", subPunctuator) |||
      seq("**=", subPunctuator) |||
      seq("<<=", subPunctuator) |||
      seq(">>=", subPunctuator) |||
      seq(">>>=", subPunctuator) |||
      seq("&=", subPunctuator) |||
      seq("|=", subPunctuator) |||
      seq("^=", subPunctuator) |||
      seq("=>", subPunctuator) |||
      STR_MISMATCH
  lazy val subPunctuator: Parser[String] =
    STR_MATCH
  lazy val DivPunctuator: Parser[String] =
    seq("/", subDivPunctuator) |||
      seq("/=", subDivPunctuator) |||
      STR_MISMATCH
  lazy val subDivPunctuator: Parser[String] =
    STR_MATCH
  lazy val RightBracePunctuator: Parser[String] =
    seq("}", subRightBracePunctuator) |||
      STR_MISMATCH
  lazy val subRightBracePunctuator: Parser[String] =
    STR_MATCH
  lazy val NullLiteral: Parser[String] =
    seq("null", subNullLiteral) |||
      STR_MISMATCH
  lazy val subNullLiteral: Parser[String] =
    STR_MATCH
  lazy val BooleanLiteral: Parser[String] =
    seq("true", subBooleanLiteral) |||
      seq("false", subBooleanLiteral) |||
      STR_MISMATCH
  lazy val subBooleanLiteral: Parser[String] =
    STR_MATCH
  lazy val NumericLiteral: Parser[String] =
    seq(DecimalLiteral, subNumericLiteral) |||
      seq(BinaryIntegerLiteral, subNumericLiteral) |||
      seq(OctalIntegerLiteral, subNumericLiteral) |||
      seq(HexIntegerLiteral, subNumericLiteral) |||
      STR_MISMATCH
  lazy val subNumericLiteral: Parser[String] =
    STR_MATCH
  lazy val DecimalLiteral: Parser[String] =
    seq(DecimalIntegerLiteral, ".", strOpt(DecimalDigits), strOpt(ExponentPart), subDecimalLiteral) |||
      seq(".", DecimalDigits, strOpt(ExponentPart), subDecimalLiteral) |||
      seq(DecimalIntegerLiteral, strOpt(ExponentPart), subDecimalLiteral) |||
      STR_MISMATCH
  lazy val subDecimalLiteral: Parser[String] =
    STR_MATCH
  lazy val DecimalIntegerLiteral: Parser[String] =
    seq("0", subDecimalIntegerLiteral) |||
      seq(NonZeroDigit, strOpt(DecimalDigits), subDecimalIntegerLiteral) |||
      STR_MISMATCH
  lazy val subDecimalIntegerLiteral: Parser[String] =
    STR_MATCH
  lazy val DecimalDigits: Parser[String] =
    seq(DecimalDigit, subDecimalDigits) |||
      STR_MISMATCH
  lazy val subDecimalDigits: Parser[String] =
    seq(DecimalDigit, subDecimalDigits) |||
      STR_MATCH
  lazy val DecimalDigit: Parser[String] =
    seq("0", subDecimalDigit) |||
      seq("1", subDecimalDigit) |||
      seq("2", subDecimalDigit) |||
      seq("3", subDecimalDigit) |||
      seq("4", subDecimalDigit) |||
      seq("5", subDecimalDigit) |||
      seq("6", subDecimalDigit) |||
      seq("7", subDecimalDigit) |||
      seq("8", subDecimalDigit) |||
      seq("9", subDecimalDigit) |||
      STR_MISMATCH
  lazy val subDecimalDigit: Parser[String] =
    STR_MATCH
  lazy val NonZeroDigit: Parser[String] =
    seq("1", subNonZeroDigit) |||
      seq("2", subNonZeroDigit) |||
      seq("3", subNonZeroDigit) |||
      seq("4", subNonZeroDigit) |||
      seq("5", subNonZeroDigit) |||
      seq("6", subNonZeroDigit) |||
      seq("7", subNonZeroDigit) |||
      seq("8", subNonZeroDigit) |||
      seq("9", subNonZeroDigit) |||
      STR_MISMATCH
  lazy val subNonZeroDigit: Parser[String] =
    STR_MATCH
  lazy val ExponentPart: Parser[String] =
    seq(ExponentIndicator, SignedInteger, subExponentPart) |||
      STR_MISMATCH
  lazy val subExponentPart: Parser[String] =
    STR_MATCH
  lazy val ExponentIndicator: Parser[String] =
    seq("e", subExponentIndicator) |||
      seq("E", subExponentIndicator) |||
      STR_MISMATCH
  lazy val subExponentIndicator: Parser[String] =
    STR_MATCH
  lazy val SignedInteger: Parser[String] =
    seq(DecimalDigits, subSignedInteger) |||
      seq("+", DecimalDigits, subSignedInteger) |||
      seq("-", DecimalDigits, subSignedInteger) |||
      STR_MISMATCH
  lazy val subSignedInteger: Parser[String] =
    STR_MATCH
  lazy val BinaryIntegerLiteral: Parser[String] =
    seq("0b", BinaryDigits, subBinaryIntegerLiteral) |||
      seq("0B", BinaryDigits, subBinaryIntegerLiteral) |||
      STR_MISMATCH
  lazy val subBinaryIntegerLiteral: Parser[String] =
    STR_MATCH
  lazy val BinaryDigits: Parser[String] =
    seq(BinaryDigit, subBinaryDigits) |||
      STR_MISMATCH
  lazy val subBinaryDigits: Parser[String] =
    seq(BinaryDigit, subBinaryDigits) |||
      STR_MATCH
  lazy val BinaryDigit: Parser[String] =
    seq("0", subBinaryDigit) |||
      seq("1", subBinaryDigit) |||
      STR_MISMATCH
  lazy val subBinaryDigit: Parser[String] =
    STR_MATCH
  lazy val OctalIntegerLiteral: Parser[String] =
    seq("0o", OctalDigits, subOctalIntegerLiteral) |||
      seq("0O", OctalDigits, subOctalIntegerLiteral) |||
      STR_MISMATCH
  lazy val subOctalIntegerLiteral: Parser[String] =
    STR_MATCH
  lazy val OctalDigits: Parser[String] =
    seq(OctalDigit, subOctalDigits) |||
      STR_MISMATCH
  lazy val subOctalDigits: Parser[String] =
    seq(OctalDigit, subOctalDigits) |||
      STR_MATCH
  lazy val OctalDigit: Parser[String] =
    seq("0", subOctalDigit) |||
      seq("1", subOctalDigit) |||
      seq("2", subOctalDigit) |||
      seq("3", subOctalDigit) |||
      seq("4", subOctalDigit) |||
      seq("5", subOctalDigit) |||
      seq("6", subOctalDigit) |||
      seq("7", subOctalDigit) |||
      STR_MISMATCH
  lazy val subOctalDigit: Parser[String] =
    STR_MATCH
  lazy val HexIntegerLiteral: Parser[String] =
    seq("0x", HexDigits, subHexIntegerLiteral) |||
      seq("0X", HexDigits, subHexIntegerLiteral) |||
      STR_MISMATCH
  lazy val subHexIntegerLiteral: Parser[String] =
    STR_MATCH
  lazy val HexDigits: Parser[String] =
    seq(HexDigit, subHexDigits) |||
      STR_MISMATCH
  lazy val subHexDigits: Parser[String] =
    seq(HexDigit, subHexDigits) |||
      STR_MATCH
  lazy val HexDigit: Parser[String] =
    seq("0", subHexDigit) |||
      seq("1", subHexDigit) |||
      seq("2", subHexDigit) |||
      seq("3", subHexDigit) |||
      seq("4", subHexDigit) |||
      seq("5", subHexDigit) |||
      seq("6", subHexDigit) |||
      seq("7", subHexDigit) |||
      seq("8", subHexDigit) |||
      seq("9", subHexDigit) |||
      seq("a", subHexDigit) |||
      seq("b", subHexDigit) |||
      seq("c", subHexDigit) |||
      seq("d", subHexDigit) |||
      seq("e", subHexDigit) |||
      seq("f", subHexDigit) |||
      seq("A", subHexDigit) |||
      seq("B", subHexDigit) |||
      seq("C", subHexDigit) |||
      seq("D", subHexDigit) |||
      seq("E", subHexDigit) |||
      seq("F", subHexDigit) |||
      STR_MISMATCH
  lazy val subHexDigit: Parser[String] =
    STR_MATCH
  lazy val StringLiteral: Parser[String] =
    seq("\"", strOpt(DoubleStringCharacters), "\"", subStringLiteral) |||
      seq("'", strOpt(SingleStringCharacters), "'", subStringLiteral) |||
      STR_MISMATCH
  lazy val subStringLiteral: Parser[String] =
    STR_MATCH
  lazy val DoubleStringCharacters: Parser[String] =
    seq(DoubleStringCharacter, strOpt(DoubleStringCharacters), subDoubleStringCharacters) |||
      STR_MISMATCH
  lazy val subDoubleStringCharacters: Parser[String] =
    STR_MATCH
  lazy val SingleStringCharacters: Parser[String] =
    seq(SingleStringCharacter, strOpt(SingleStringCharacters), subSingleStringCharacters) |||
      STR_MISMATCH
  lazy val subSingleStringCharacters: Parser[String] =
    STR_MATCH
  lazy val DoubleStringCharacter: Parser[String] =
    seq((SourceCharacter \ "\"" ||| "\\" ||| LineTerminator), subDoubleStringCharacter) |||
      seq("\\", EscapeSequence, subDoubleStringCharacter) |||
      seq(LineContinuation, subDoubleStringCharacter) |||
      STR_MISMATCH
  lazy val subDoubleStringCharacter: Parser[String] =
    STR_MATCH
  lazy val SingleStringCharacter: Parser[String] =
    seq((SourceCharacter \ "'" ||| "\\" ||| LineTerminator), subSingleStringCharacter) |||
      seq("\\", EscapeSequence, subSingleStringCharacter) |||
      seq(LineContinuation, subSingleStringCharacter) |||
      STR_MISMATCH
  lazy val subSingleStringCharacter: Parser[String] =
    STR_MATCH
  lazy val LineContinuation: Parser[String] =
    seq("\\", LineTerminatorSequence, subLineContinuation) |||
      STR_MISMATCH
  lazy val subLineContinuation: Parser[String] =
    STR_MATCH
  lazy val EscapeSequence: Parser[String] =
    seq(CharacterEscapeSequence, subEscapeSequence) |||
      seq("0", "" <~ -(seq(DecimalDigit)), subEscapeSequence) |||
      seq(HexEscapeSequence, subEscapeSequence) |||
      seq(UnicodeEscapeSequence, subEscapeSequence) |||
      STR_MISMATCH
  lazy val subEscapeSequence: Parser[String] =
    STR_MATCH
  lazy val CharacterEscapeSequence: Parser[String] =
    seq(SingleEscapeCharacter, subCharacterEscapeSequence) |||
      seq(NonEscapeCharacter, subCharacterEscapeSequence) |||
      STR_MISMATCH
  lazy val subCharacterEscapeSequence: Parser[String] =
    STR_MATCH
  lazy val SingleEscapeCharacter: Parser[String] =
    seq("'", subSingleEscapeCharacter) |||
      seq("\"", subSingleEscapeCharacter) |||
      seq("\\", subSingleEscapeCharacter) |||
      seq("b", subSingleEscapeCharacter) |||
      seq("f", subSingleEscapeCharacter) |||
      seq("n", subSingleEscapeCharacter) |||
      seq("r", subSingleEscapeCharacter) |||
      seq("t", subSingleEscapeCharacter) |||
      seq("v", subSingleEscapeCharacter) |||
      STR_MISMATCH
  lazy val subSingleEscapeCharacter: Parser[String] =
    STR_MATCH
  lazy val NonEscapeCharacter: Parser[String] =
    seq((SourceCharacter \ EscapeCharacter ||| LineTerminator), subNonEscapeCharacter) |||
      STR_MISMATCH
  lazy val subNonEscapeCharacter: Parser[String] =
    STR_MATCH
  lazy val EscapeCharacter: Parser[String] =
    seq(SingleEscapeCharacter, subEscapeCharacter) |||
      seq(DecimalDigit, subEscapeCharacter) |||
      seq("x", subEscapeCharacter) |||
      seq("u", subEscapeCharacter) |||
      STR_MISMATCH
  lazy val subEscapeCharacter: Parser[String] =
    STR_MATCH
  lazy val HexEscapeSequence: Parser[String] =
    seq("x", HexDigit, HexDigit, subHexEscapeSequence) |||
      STR_MISMATCH
  lazy val subHexEscapeSequence: Parser[String] =
    STR_MATCH
  lazy val UnicodeEscapeSequence: Parser[String] =
    seq("u", Hex4Digits, subUnicodeEscapeSequence) |||
      seq("u{", CodePoint, "}", subUnicodeEscapeSequence) |||
      STR_MISMATCH
  lazy val subUnicodeEscapeSequence: Parser[String] =
    STR_MATCH
  lazy val Hex4Digits: Parser[String] =
    seq(HexDigit, HexDigit, HexDigit, HexDigit, subHex4Digits) |||
      STR_MISMATCH
  lazy val subHex4Digits: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionLiteral: Parser[String] =
    seq("/", RegularExpressionBody, "/", RegularExpressionFlags, subRegularExpressionLiteral) |||
      STR_MISMATCH
  lazy val subRegularExpressionLiteral: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionBody: Parser[String] =
    seq(RegularExpressionFirstChar, RegularExpressionChars, subRegularExpressionBody) |||
      STR_MISMATCH
  lazy val subRegularExpressionBody: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionChars: Parser[String] =
    seq(STR_MATCH, subRegularExpressionChars) |||
      STR_MISMATCH
  lazy val subRegularExpressionChars: Parser[String] =
    seq(RegularExpressionChar, subRegularExpressionChars) |||
      STR_MATCH
  lazy val RegularExpressionFirstChar: Parser[String] =
    seq((RegularExpressionNonTerminator \ "*" ||| "\\" ||| "/" ||| "["), subRegularExpressionFirstChar) |||
      seq(RegularExpressionBackslashSequence, subRegularExpressionFirstChar) |||
      seq(RegularExpressionClass, subRegularExpressionFirstChar) |||
      STR_MISMATCH
  lazy val subRegularExpressionFirstChar: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionChar: Parser[String] =
    seq((RegularExpressionNonTerminator \ "\\" ||| "/" ||| "["), subRegularExpressionChar) |||
      seq(RegularExpressionBackslashSequence, subRegularExpressionChar) |||
      seq(RegularExpressionClass, subRegularExpressionChar) |||
      STR_MISMATCH
  lazy val subRegularExpressionChar: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionBackslashSequence: Parser[String] =
    seq("\\", RegularExpressionNonTerminator, subRegularExpressionBackslashSequence) |||
      STR_MISMATCH
  lazy val subRegularExpressionBackslashSequence: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionNonTerminator: Parser[String] =
    seq((SourceCharacter \ LineTerminator), subRegularExpressionNonTerminator) |||
      STR_MISMATCH
  lazy val subRegularExpressionNonTerminator: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionClass: Parser[String] =
    seq("[", RegularExpressionClassChars, "]", subRegularExpressionClass) |||
      STR_MISMATCH
  lazy val subRegularExpressionClass: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionClassChars: Parser[String] =
    seq(STR_MATCH, subRegularExpressionClassChars) |||
      STR_MISMATCH
  lazy val subRegularExpressionClassChars: Parser[String] =
    seq(RegularExpressionClassChar, subRegularExpressionClassChars) |||
      STR_MATCH
  lazy val RegularExpressionClassChar: Parser[String] =
    seq((RegularExpressionNonTerminator \ "]" ||| "\\"), subRegularExpressionClassChar) |||
      seq(RegularExpressionBackslashSequence, subRegularExpressionClassChar) |||
      STR_MISMATCH
  lazy val subRegularExpressionClassChar: Parser[String] =
    STR_MATCH
  lazy val RegularExpressionFlags: Parser[String] =
    seq(STR_MATCH, subRegularExpressionFlags) |||
      STR_MISMATCH
  lazy val subRegularExpressionFlags: Parser[String] =
    seq(IdentifierPart, subRegularExpressionFlags) |||
      STR_MATCH
  lazy val Template: Parser[String] =
    seq(NoSubstitutionTemplate, subTemplate) |||
      seq(TemplateHead, subTemplate) |||
      STR_MISMATCH
  lazy val subTemplate: Parser[String] =
    STR_MATCH
  lazy val NoSubstitutionTemplate: Parser[String] =
    seq("`", strOpt(TemplateCharacters), "`", subNoSubstitutionTemplate) |||
      STR_MISMATCH
  lazy val subNoSubstitutionTemplate: Parser[String] =
    STR_MATCH
  lazy val TemplateHead: Parser[String] =
    seq("`", strOpt(TemplateCharacters), "${", subTemplateHead) |||
      STR_MISMATCH
  lazy val subTemplateHead: Parser[String] =
    STR_MATCH
  lazy val TemplateSubstitutionTail: Parser[String] =
    seq(TemplateMiddle, subTemplateSubstitutionTail) |||
      seq(TemplateTail, subTemplateSubstitutionTail) |||
      STR_MISMATCH
  lazy val subTemplateSubstitutionTail: Parser[String] =
    STR_MATCH
  lazy val TemplateMiddle: Parser[String] =
    seq("}", strOpt(TemplateCharacters), "${", subTemplateMiddle) |||
      STR_MISMATCH
  lazy val subTemplateMiddle: Parser[String] =
    STR_MATCH
  lazy val TemplateTail: Parser[String] =
    seq("}", strOpt(TemplateCharacters), "`", subTemplateTail) |||
      STR_MISMATCH
  lazy val subTemplateTail: Parser[String] =
    STR_MATCH
  lazy val TemplateCharacters: Parser[String] =
    seq(TemplateCharacter, strOpt(TemplateCharacters), subTemplateCharacters) |||
      STR_MISMATCH
  lazy val subTemplateCharacters: Parser[String] =
    STR_MATCH
  lazy val TemplateCharacter: Parser[String] =
    seq("$", "" <~ -(seq("{")), subTemplateCharacter) |||
      seq("\\", EscapeSequence, subTemplateCharacter) |||
      seq("\\", NotEscapeSequence, subTemplateCharacter) |||
      seq(LineContinuation, subTemplateCharacter) |||
      seq(LineTerminatorSequence, subTemplateCharacter) |||
      seq((SourceCharacter \ "`" ||| "\\" ||| "$" ||| LineTerminator), subTemplateCharacter) |||
      STR_MISMATCH
  lazy val subTemplateCharacter: Parser[String] =
    STR_MATCH
  lazy val NotEscapeSequence: Parser[String] =
    seq("0", DecimalDigit, subNotEscapeSequence) |||
      seq((DecimalDigit \ "0"), subNotEscapeSequence) |||
      seq("x", "" <~ -(seq(HexDigit)), subNotEscapeSequence) |||
      seq("x", HexDigit, "" <~ -(seq(HexDigit)), subNotEscapeSequence) |||
      seq("u", "" <~ -(seq(HexDigit)), "" <~ -(seq("{")), subNotEscapeSequence) |||
      seq("u", HexDigit, "" <~ -(seq(HexDigit)), subNotEscapeSequence) |||
      seq("u", HexDigit, HexDigit, "" <~ -(seq(HexDigit)), subNotEscapeSequence) |||
      seq("u", HexDigit, HexDigit, HexDigit, "" <~ -(seq(HexDigit)), subNotEscapeSequence) |||
      seq("u", "{", "" <~ -(seq(HexDigit)), subNotEscapeSequence) |||
      seq("u", "{", NotCodePoint, "" <~ -(seq(HexDigit)), subNotEscapeSequence) |||
      seq("u", "{", CodePoint, "" <~ -(seq(HexDigit)), "" <~ -(seq("}")), subNotEscapeSequence) |||
      STR_MISMATCH
  lazy val subNotEscapeSequence: Parser[String] =
    STR_MATCH
  lazy val NotCodePoint: Parser[String] =
    seq(HexDigits, subNotCodePoint) |||
      STR_MISMATCH
  lazy val subNotCodePoint: Parser[String] =
    STR_MATCH
  lazy val CodePoint: Parser[String] =
    seq(HexDigits, subCodePoint) |||
      STR_MISMATCH
  lazy val subCodePoint: Parser[String] =
    STR_MATCH
  lazy val IdentifierReference: P2[ast.IdentifierReference] = memo {
    case (pYield, pAwait) =>
      MATCH ~ Identifier ~ subIdentifierReference(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.IdentifierReference0(x0)) } |
        (if (!pYield) (MATCH <~ term("yield")) ~ subIdentifierReference(pYield, pAwait) ^^ { case _ ~ y => y(ast.IdentifierReference1) } else MISMATCH) |
        (if (!pAwait) (MATCH <~ term("await")) ~ subIdentifierReference(pYield, pAwait) ^^ { case _ ~ y => y(ast.IdentifierReference2) } else MISMATCH) |
        MISMATCH
  }
  lazy val subIdentifierReference: R2[ast.IdentifierReference] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val BindingIdentifier: P2[ast.BindingIdentifier] = memo {
    case (pYield, pAwait) =>
      MATCH ~ Identifier ~ subBindingIdentifier(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingIdentifier0(x0)) } |
        (MATCH <~ term("yield")) ~ subBindingIdentifier(pYield, pAwait) ^^ { case _ ~ y => y(ast.BindingIdentifier1) } |
        (MATCH <~ term("await")) ~ subBindingIdentifier(pYield, pAwait) ^^ { case _ ~ y => y(ast.BindingIdentifier2) } |
        MISMATCH
  }
  lazy val subBindingIdentifier: R2[ast.BindingIdentifier] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val Identifier: P0[ast.Identifier] = {
    MATCH ~ term("""(IdentifierName \ ReservedWord)""", (IdentifierName \ ReservedWord)) ~ subIdentifier ^^ { case _ ~ x0 ~ y => y(ast.Identifier0(x0)) } |
      MISMATCH
  }
  lazy val subIdentifier: R0[ast.Identifier] = {
    MATCH ^^^ { x => x }
  }
  lazy val AsyncArrowBindingIdentifier: P1[ast.AsyncArrowBindingIdentifier] = memo {
    case (pYield) =>
      MATCH ~ BindingIdentifier(pYield, true) ~ subAsyncArrowBindingIdentifier(pYield) ^^ { case _ ~ x0 ~ y => y(ast.AsyncArrowBindingIdentifier0(x0)) } |
        MISMATCH
  }
  lazy val subAsyncArrowBindingIdentifier: R1[ast.AsyncArrowBindingIdentifier] = memo {
    case (pYield) =>
      MATCH ^^^ { x => x }
  }
  lazy val LabelIdentifier: P2[ast.LabelIdentifier] = memo {
    case (pYield, pAwait) =>
      MATCH ~ Identifier ~ subLabelIdentifier(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.LabelIdentifier0(x0)) } |
        (if (!pYield) (MATCH <~ term("yield")) ~ subLabelIdentifier(pYield, pAwait) ^^ { case _ ~ y => y(ast.LabelIdentifier1) } else MISMATCH) |
        (if (!pAwait) (MATCH <~ term("await")) ~ subLabelIdentifier(pYield, pAwait) ^^ { case _ ~ y => y(ast.LabelIdentifier2) } else MISMATCH) |
        MISMATCH
  }
  lazy val subLabelIdentifier: R2[ast.LabelIdentifier] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val PrimaryExpression: P2[ast.PrimaryExpression] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("this")) ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ y => y(ast.PrimaryExpression0) } |
        MATCH ~ IdentifierReference(pYield, pAwait) ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression1(x0)) } |
        MATCH ~ Literal ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression2(x0)) } |
        MATCH ~ ArrayLiteral(pYield, pAwait) ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression3(x0)) } |
        MATCH ~ ObjectLiteral(pYield, pAwait) ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression4(x0)) } |
        MATCH ~ FunctionExpression ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression5(x0)) } |
        MATCH ~ ClassExpression(pYield, pAwait) ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression6(x0)) } |
        MATCH ~ GeneratorExpression ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression7(x0)) } |
        MATCH ~ AsyncFunctionExpression ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression8(x0)) } |
        MATCH ~ AsyncGeneratorExpression ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression9(x0)) } |
        MATCH ~ term("RegularExpressionLiteral", RegularExpressionLiteral) ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression10(x0)) } |
        MATCH ~ TemplateLiteral(pYield, pAwait, false) ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression11(x0)) } |
        MATCH ~ CoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ~ subPrimaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PrimaryExpression12(x0)) } |
        MISMATCH
  }
  lazy val subPrimaryExpression: R2[ast.PrimaryExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val CoverParenthesizedExpressionAndArrowParameterList: P2[ast.CoverParenthesizedExpressionAndArrowParameterList] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ subCoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.CoverParenthesizedExpressionAndArrowParameterList0(x0)) } |
        (((MATCH <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(",")) <~ term(")")) ~ subCoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.CoverParenthesizedExpressionAndArrowParameterList1(x0)) } |
        ((MATCH <~ term("(")) <~ term(")")) ~ subCoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ^^ { case _ ~ y => y(ast.CoverParenthesizedExpressionAndArrowParameterList2) } |
        (((MATCH <~ term("(")) <~ term("...")) ~ BindingIdentifier(pYield, pAwait) <~ term(")")) ~ subCoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.CoverParenthesizedExpressionAndArrowParameterList3(x0)) } |
        (((MATCH <~ term("(")) <~ term("...")) ~ BindingPattern(pYield, pAwait) <~ term(")")) ~ subCoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.CoverParenthesizedExpressionAndArrowParameterList4(x0)) } |
        ((((MATCH <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(",")) <~ term("...")) ~ BindingIdentifier(pYield, pAwait) <~ term(")")) ~ subCoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.CoverParenthesizedExpressionAndArrowParameterList5(x0, x1)) } |
        ((((MATCH <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(",")) <~ term("...")) ~ BindingPattern(pYield, pAwait) <~ term(")")) ~ subCoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.CoverParenthesizedExpressionAndArrowParameterList6(x0, x1)) } |
        MISMATCH
  }
  lazy val subCoverParenthesizedExpressionAndArrowParameterList: R2[ast.CoverParenthesizedExpressionAndArrowParameterList] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ParenthesizedExpression: P2[ast.ParenthesizedExpression] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ subParenthesizedExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ParenthesizedExpression0(x0)) } |
        MISMATCH
  }
  lazy val subParenthesizedExpression: R2[ast.ParenthesizedExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val Literal: P0[ast.Literal] = {
    MATCH ~ term("NullLiteral", NullLiteral) ~ subLiteral ^^ { case _ ~ x0 ~ y => y(ast.Literal0(x0)) } |
      MATCH ~ term("BooleanLiteral", BooleanLiteral) ~ subLiteral ^^ { case _ ~ x0 ~ y => y(ast.Literal1(x0)) } |
      MATCH ~ term("NumericLiteral", NumericLiteral) ~ subLiteral ^^ { case _ ~ x0 ~ y => y(ast.Literal2(x0)) } |
      MATCH ~ term("StringLiteral", StringLiteral) ~ subLiteral ^^ { case _ ~ x0 ~ y => y(ast.Literal3(x0)) } |
      MISMATCH
  }
  lazy val subLiteral: R0[ast.Literal] = {
    MATCH ^^^ { x => x }
  }
  lazy val ArrayLiteral: P2[ast.ArrayLiteral] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("[")) ~ opt(Elision) <~ term("]")) ~ subArrayLiteral(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArrayLiteral0(x0)) } |
        ((MATCH <~ term("[")) ~ ElementList(pYield, pAwait) <~ term("]")) ~ subArrayLiteral(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArrayLiteral1(x0)) } |
        (((MATCH <~ term("[")) ~ ElementList(pYield, pAwait) <~ term(",")) ~ opt(Elision) <~ term("]")) ~ subArrayLiteral(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ArrayLiteral2(x0, x1)) } |
        MISMATCH
  }
  lazy val subArrayLiteral: R2[ast.ArrayLiteral] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ElementList: P2[ast.ElementList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ opt(Elision) ~ AssignmentExpression(true, pYield, pAwait) ~ subElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ElementList0(x0, x1)) } |
        MATCH ~ opt(Elision) ~ SpreadElement(pYield, pAwait) ~ subElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ElementList1(x0, x1)) } |
        MISMATCH
  }
  lazy val subElementList: R2[ast.ElementList] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term(",")) ~ opt(Elision) ~ AssignmentExpression(true, pYield, pAwait) ~ subElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => ((x: ast.ElementList) => y(ast.ElementList2(x, x0, x1))) } |
        (MATCH <~ term(",")) ~ opt(Elision) ~ SpreadElement(pYield, pAwait) ~ subElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => ((x: ast.ElementList) => y(ast.ElementList3(x, x0, x1))) } |
        MATCH ^^^ { x => x }
  }
  lazy val Elision: P0[ast.Elision] = {
    (MATCH <~ term(",")) ~ subElision ^^ { case _ ~ y => y(ast.Elision0) } |
      MISMATCH
  }
  lazy val subElision: R0[ast.Elision] = {
    (MATCH <~ term(",")) ~ subElision ^^ { case _ ~ y => ((x: ast.Elision) => y(ast.Elision1(x))) } |
      MATCH ^^^ { x => x }
  }
  lazy val SpreadElement: P2[ast.SpreadElement] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("...")) ~ AssignmentExpression(true, pYield, pAwait) ~ subSpreadElement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.SpreadElement0(x0)) } |
        MISMATCH
  }
  lazy val subSpreadElement: R2[ast.SpreadElement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ObjectLiteral: P2[ast.ObjectLiteral] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("{")) <~ term("}")) ~ subObjectLiteral(pYield, pAwait) ^^ { case _ ~ y => y(ast.ObjectLiteral0) } |
        ((MATCH <~ term("{")) ~ PropertyDefinitionList(pYield, pAwait) <~ term("}")) ~ subObjectLiteral(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ObjectLiteral1(x0)) } |
        (((MATCH <~ term("{")) ~ PropertyDefinitionList(pYield, pAwait) <~ term(",")) <~ term("}")) ~ subObjectLiteral(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ObjectLiteral2(x0)) } |
        MISMATCH
  }
  lazy val subObjectLiteral: R2[ast.ObjectLiteral] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val PropertyDefinitionList: P2[ast.PropertyDefinitionList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ PropertyDefinition(pYield, pAwait) ~ subPropertyDefinitionList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PropertyDefinitionList0(x0)) } |
        MISMATCH
  }
  lazy val subPropertyDefinitionList: R2[ast.PropertyDefinitionList] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term(",")) ~ PropertyDefinition(pYield, pAwait) ~ subPropertyDefinitionList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.PropertyDefinitionList) => y(ast.PropertyDefinitionList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val PropertyDefinition: P2[ast.PropertyDefinition] = memo {
    case (pYield, pAwait) =>
      MATCH ~ IdentifierReference(pYield, pAwait) ~ subPropertyDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PropertyDefinition0(x0)) } |
        MATCH ~ CoverInitializedName(pYield, pAwait) ~ subPropertyDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PropertyDefinition1(x0)) } |
        (MATCH ~ PropertyName(pYield, pAwait) <~ term(":")) ~ AssignmentExpression(true, pYield, pAwait) ~ subPropertyDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.PropertyDefinition2(x0, x1)) } |
        MATCH ~ MethodDefinition(pYield, pAwait) ~ subPropertyDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PropertyDefinition3(x0)) } |
        (MATCH <~ term("...")) ~ AssignmentExpression(true, pYield, pAwait) ~ subPropertyDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PropertyDefinition4(x0)) } |
        MISMATCH
  }
  lazy val subPropertyDefinition: R2[ast.PropertyDefinition] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val PropertyName: P2[ast.PropertyName] = memo {
    case (pYield, pAwait) =>
      MATCH ~ LiteralPropertyName ~ subPropertyName(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PropertyName0(x0)) } |
        MATCH ~ ComputedPropertyName(pYield, pAwait) ~ subPropertyName(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.PropertyName1(x0)) } |
        MISMATCH
  }
  lazy val subPropertyName: R2[ast.PropertyName] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val LiteralPropertyName: P0[ast.LiteralPropertyName] = {
    MATCH ~ term("IdentifierName", IdentifierName) ~ subLiteralPropertyName ^^ { case _ ~ x0 ~ y => y(ast.LiteralPropertyName0(x0)) } |
      MATCH ~ term("StringLiteral", StringLiteral) ~ subLiteralPropertyName ^^ { case _ ~ x0 ~ y => y(ast.LiteralPropertyName1(x0)) } |
      MATCH ~ term("NumericLiteral", NumericLiteral) ~ subLiteralPropertyName ^^ { case _ ~ x0 ~ y => y(ast.LiteralPropertyName2(x0)) } |
      MISMATCH
  }
  lazy val subLiteralPropertyName: R0[ast.LiteralPropertyName] = {
    MATCH ^^^ { x => x }
  }
  lazy val ComputedPropertyName: P2[ast.ComputedPropertyName] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("[")) ~ AssignmentExpression(true, pYield, pAwait) <~ term("]")) ~ subComputedPropertyName(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ComputedPropertyName0(x0)) } |
        MISMATCH
  }
  lazy val subComputedPropertyName: R2[ast.ComputedPropertyName] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val CoverInitializedName: P2[ast.CoverInitializedName] = memo {
    case (pYield, pAwait) =>
      MATCH ~ IdentifierReference(pYield, pAwait) ~ Initializer(true, pYield, pAwait) ~ subCoverInitializedName(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.CoverInitializedName0(x0, x1)) } |
        MISMATCH
  }
  lazy val subCoverInitializedName: R2[ast.CoverInitializedName] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val Initializer: P3[ast.Initializer] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term("=")) ~ AssignmentExpression(pIn, pYield, pAwait) ~ subInitializer(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.Initializer0(x0)) } |
        MISMATCH
  }
  lazy val subInitializer: R3[ast.Initializer] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val TemplateLiteral: P3[ast.TemplateLiteral] = memo {
    case (pYield, pAwait, pTagged) =>
      MATCH ~ term("NoSubstitutionTemplate", NoSubstitutionTemplate) ~ subTemplateLiteral(pYield, pAwait, pTagged) ^^ { case _ ~ x0 ~ y => y(ast.TemplateLiteral0(x0)) } |
        MATCH ~ SubstitutionTemplate(pYield, pAwait, pTagged) ~ subTemplateLiteral(pYield, pAwait, pTagged) ^^ { case _ ~ x0 ~ y => y(ast.TemplateLiteral1(x0)) } |
        MISMATCH
  }
  lazy val subTemplateLiteral: R3[ast.TemplateLiteral] = memo {
    case (pYield, pAwait, pTagged) =>
      MATCH ^^^ { x => x }
  }
  lazy val TemplateSpans: P3[ast.TemplateSpans] = memo {
    case (pYield, pAwait, pTagged) =>
      MATCH ~ term("TemplateTail", TemplateTail) ~ subTemplateSpans(pYield, pAwait, pTagged) ^^ { case _ ~ x0 ~ y => y(ast.TemplateSpans0(x0)) } |
        MATCH ~ TemplateMiddleList(pYield, pAwait, pTagged) ~ term("TemplateTail", TemplateTail) ~ subTemplateSpans(pYield, pAwait, pTagged) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.TemplateSpans1(x0, x1)) } |
        MISMATCH
  }
  lazy val subTemplateSpans: R3[ast.TemplateSpans] = memo {
    case (pYield, pAwait, pTagged) =>
      MATCH ^^^ { x => x }
  }
  lazy val TemplateMiddleList: P3[ast.TemplateMiddleList] = memo {
    case (pYield, pAwait, pTagged) =>
      MATCH ~ term("TemplateMiddle", TemplateMiddle) ~ Expression(true, pYield, pAwait) ~ subTemplateMiddleList(pYield, pAwait, pTagged) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.TemplateMiddleList0(x0, x1)) } |
        MISMATCH
  }
  lazy val subTemplateMiddleList: R3[ast.TemplateMiddleList] = memo {
    case (pYield, pAwait, pTagged) =>
      MATCH ~ term("TemplateMiddle", TemplateMiddle) ~ Expression(true, pYield, pAwait) ~ subTemplateMiddleList(pYield, pAwait, pTagged) ^^ { case _ ~ x0 ~ x1 ~ y => ((x: ast.TemplateMiddleList) => y(ast.TemplateMiddleList1(x, x0, x1))) } |
        MATCH ^^^ { x => x }
  }
  lazy val MemberExpression: P2[ast.MemberExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ PrimaryExpression(pYield, pAwait) ~ subMemberExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.MemberExpression0(x0)) } |
        MATCH ~ SuperProperty(pYield, pAwait) ~ subMemberExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.MemberExpression4(x0)) } |
        MATCH ~ MetaProperty ~ subMemberExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.MemberExpression5(x0)) } |
        (MATCH <~ term("new")) ~ MemberExpression(pYield, pAwait) ~ Arguments(pYield, pAwait) ~ subMemberExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.MemberExpression6(x0, x1)) } |
        MISMATCH
  }
  lazy val subMemberExpression: R2[ast.MemberExpression] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("[")) ~ Expression(true, pYield, pAwait) <~ term("]")) ~ subMemberExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.MemberExpression) => y(ast.MemberExpression1(x, x0))) } |
        (MATCH <~ term(".")) ~ term("IdentifierName", IdentifierName) ~ subMemberExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.MemberExpression) => y(ast.MemberExpression2(x, x0))) } |
        MATCH ~ TemplateLiteral(pYield, pAwait, true) ~ subMemberExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.MemberExpression) => y(ast.MemberExpression3(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val SuperProperty: P2[ast.SuperProperty] = memo {
    case (pYield, pAwait) =>
      (((MATCH <~ term("super")) <~ term("[")) ~ Expression(true, pYield, pAwait) <~ term("]")) ~ subSuperProperty(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.SuperProperty0(x0)) } |
        ((MATCH <~ term("super")) <~ term(".")) ~ term("IdentifierName", IdentifierName) ~ subSuperProperty(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.SuperProperty1(x0)) } |
        MISMATCH
  }
  lazy val subSuperProperty: R2[ast.SuperProperty] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val MetaProperty: P0[ast.MetaProperty] = {
    MATCH ~ NewTarget ~ subMetaProperty ^^ { case _ ~ x0 ~ y => y(ast.MetaProperty0(x0)) } |
      MISMATCH
  }
  lazy val subMetaProperty: R0[ast.MetaProperty] = {
    MATCH ^^^ { x => x }
  }
  lazy val NewTarget: P0[ast.NewTarget] = {
    (((MATCH <~ term("new")) <~ term(".")) <~ term("target")) ~ subNewTarget ^^ { case _ ~ y => y(ast.NewTarget0) } |
      MISMATCH
  }
  lazy val subNewTarget: R0[ast.NewTarget] = {
    MATCH ^^^ { x => x }
  }
  lazy val NewExpression: P2[ast.NewExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ MemberExpression(pYield, pAwait) ~ subNewExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.NewExpression0(x0)) } |
        (MATCH <~ term("new")) ~ NewExpression(pYield, pAwait) ~ subNewExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.NewExpression1(x0)) } |
        MISMATCH
  }
  lazy val subNewExpression: R2[ast.NewExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val CallExpression: P2[ast.CallExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ CoverCallExpressionAndAsyncArrowHead(pYield, pAwait) ~ subCallExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.CallExpression0(x0)) } |
        MATCH ~ SuperCall(pYield, pAwait) ~ subCallExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.CallExpression1(x0)) } |
        MISMATCH
  }
  lazy val subCallExpression: R2[ast.CallExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ Arguments(pYield, pAwait) ~ subCallExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.CallExpression) => y(ast.CallExpression2(x, x0))) } |
        ((MATCH <~ term("[")) ~ Expression(true, pYield, pAwait) <~ term("]")) ~ subCallExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.CallExpression) => y(ast.CallExpression3(x, x0))) } |
        (MATCH <~ term(".")) ~ term("IdentifierName", IdentifierName) ~ subCallExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.CallExpression) => y(ast.CallExpression4(x, x0))) } |
        MATCH ~ TemplateLiteral(pYield, pAwait, true) ~ subCallExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.CallExpression) => y(ast.CallExpression5(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val CoverCallExpressionAndAsyncArrowHead: P2[ast.CoverCallExpressionAndAsyncArrowHead] = memo {
    case (pYield, pAwait) =>
      MATCH ~ MemberExpression(pYield, pAwait) ~ Arguments(pYield, pAwait) ~ subCoverCallExpressionAndAsyncArrowHead(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.CoverCallExpressionAndAsyncArrowHead0(x0, x1)) } |
        MISMATCH
  }
  lazy val subCoverCallExpressionAndAsyncArrowHead: R2[ast.CoverCallExpressionAndAsyncArrowHead] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val CallMemberExpression: P2[ast.CallMemberExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ MemberExpression(pYield, pAwait) ~ Arguments(pYield, pAwait) ~ subCallMemberExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.CallMemberExpression0(x0, x1)) } |
        MISMATCH
  }
  lazy val subCallMemberExpression: R2[ast.CallMemberExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val SuperCall: P2[ast.SuperCall] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("super")) ~ Arguments(pYield, pAwait) ~ subSuperCall(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.SuperCall0(x0)) } |
        MISMATCH
  }
  lazy val subSuperCall: R2[ast.SuperCall] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val Arguments: P2[ast.Arguments] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("(")) <~ term(")")) ~ subArguments(pYield, pAwait) ^^ { case _ ~ y => y(ast.Arguments0) } |
        ((MATCH <~ term("(")) ~ ArgumentList(pYield, pAwait) <~ term(")")) ~ subArguments(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.Arguments1(x0)) } |
        (((MATCH <~ term("(")) ~ ArgumentList(pYield, pAwait) <~ term(",")) <~ term(")")) ~ subArguments(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.Arguments2(x0)) } |
        MISMATCH
  }
  lazy val subArguments: R2[ast.Arguments] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ArgumentList: P2[ast.ArgumentList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ AssignmentExpression(true, pYield, pAwait) ~ subArgumentList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArgumentList0(x0)) } |
        (MATCH <~ term("...")) ~ AssignmentExpression(true, pYield, pAwait) ~ subArgumentList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArgumentList1(x0)) } |
        MISMATCH
  }
  lazy val subArgumentList: R2[ast.ArgumentList] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term(",")) ~ AssignmentExpression(true, pYield, pAwait) ~ subArgumentList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.ArgumentList) => y(ast.ArgumentList2(x, x0))) } |
        ((MATCH <~ term(",")) <~ term("...")) ~ AssignmentExpression(true, pYield, pAwait) ~ subArgumentList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.ArgumentList) => y(ast.ArgumentList3(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val LeftHandSideExpression: P2[ast.LeftHandSideExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ NewExpression(pYield, pAwait) ~ subLeftHandSideExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.LeftHandSideExpression0(x0)) } |
        MATCH ~ CallExpression(pYield, pAwait) ~ subLeftHandSideExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.LeftHandSideExpression1(x0)) } |
        MISMATCH
  }
  lazy val subLeftHandSideExpression: R2[ast.LeftHandSideExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val UpdateExpression: P2[ast.UpdateExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ LeftHandSideExpression(pYield, pAwait) ~ subUpdateExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UpdateExpression0(x0)) } |
        ((MATCH ~ LeftHandSideExpression(pYield, pAwait) <~ NoLineTerminator) <~ term("++")) ~ subUpdateExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UpdateExpression1(x0)) } |
        ((MATCH ~ LeftHandSideExpression(pYield, pAwait) <~ NoLineTerminator) <~ term("--")) ~ subUpdateExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UpdateExpression2(x0)) } |
        (MATCH <~ term("++")) ~ UnaryExpression(pYield, pAwait) ~ subUpdateExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UpdateExpression3(x0)) } |
        (MATCH <~ term("--")) ~ UnaryExpression(pYield, pAwait) ~ subUpdateExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UpdateExpression4(x0)) } |
        MISMATCH
  }
  lazy val subUpdateExpression: R2[ast.UpdateExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val UnaryExpression: P2[ast.UnaryExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ UpdateExpression(pYield, pAwait) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression0(x0)) } |
        (MATCH <~ term("delete")) ~ UnaryExpression(pYield, pAwait) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression1(x0)) } |
        (MATCH <~ term("void")) ~ UnaryExpression(pYield, pAwait) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression2(x0)) } |
        (MATCH <~ term("typeof")) ~ UnaryExpression(pYield, pAwait) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression3(x0)) } |
        (MATCH <~ term("+")) ~ UnaryExpression(pYield, pAwait) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression4(x0)) } |
        (MATCH <~ term("-")) ~ UnaryExpression(pYield, pAwait) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression5(x0)) } |
        (MATCH <~ term("~")) ~ UnaryExpression(pYield, pAwait) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression6(x0)) } |
        (MATCH <~ term("!")) ~ UnaryExpression(pYield, pAwait) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression7(x0)) } |
        (if (pAwait) MATCH ~ AwaitExpression(pYield) ~ subUnaryExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UnaryExpression8(x0)) } else MISMATCH) |
        MISMATCH
  }
  lazy val subUnaryExpression: R2[ast.UnaryExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ExponentiationExpression: P2[ast.ExponentiationExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ UnaryExpression(pYield, pAwait) ~ subExponentiationExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ExponentiationExpression0(x0)) } |
        (MATCH ~ UpdateExpression(pYield, pAwait) <~ term("**")) ~ ExponentiationExpression(pYield, pAwait) ~ subExponentiationExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ExponentiationExpression1(x0, x1)) } |
        MISMATCH
  }
  lazy val subExponentiationExpression: R2[ast.ExponentiationExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val MultiplicativeExpression: P2[ast.MultiplicativeExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ ExponentiationExpression(pYield, pAwait) ~ subMultiplicativeExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.MultiplicativeExpression0(x0)) } |
        MISMATCH
  }
  lazy val subMultiplicativeExpression: R2[ast.MultiplicativeExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ MultiplicativeOperator ~ ExponentiationExpression(pYield, pAwait) ~ subMultiplicativeExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => ((x: ast.MultiplicativeExpression) => y(ast.MultiplicativeExpression1(x, x0, x1))) } |
        MATCH ^^^ { x => x }
  }
  lazy val MultiplicativeOperator: P0[ast.MultiplicativeOperator] = {
    (MATCH <~ term("*")) ~ subMultiplicativeOperator ^^ { case _ ~ y => y(ast.MultiplicativeOperator0) } |
      (MATCH <~ term("/")) ~ subMultiplicativeOperator ^^ { case _ ~ y => y(ast.MultiplicativeOperator1) } |
      (MATCH <~ term("%")) ~ subMultiplicativeOperator ^^ { case _ ~ y => y(ast.MultiplicativeOperator2) } |
      MISMATCH
  }
  lazy val subMultiplicativeOperator: R0[ast.MultiplicativeOperator] = {
    MATCH ^^^ { x => x }
  }
  lazy val AdditiveExpression: P2[ast.AdditiveExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ MultiplicativeExpression(pYield, pAwait) ~ subAdditiveExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AdditiveExpression0(x0)) } |
        MISMATCH
  }
  lazy val subAdditiveExpression: R2[ast.AdditiveExpression] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("+")) ~ MultiplicativeExpression(pYield, pAwait) ~ subAdditiveExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.AdditiveExpression) => y(ast.AdditiveExpression1(x, x0))) } |
        (MATCH <~ term("-")) ~ MultiplicativeExpression(pYield, pAwait) ~ subAdditiveExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.AdditiveExpression) => y(ast.AdditiveExpression2(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val ShiftExpression: P2[ast.ShiftExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ~ AdditiveExpression(pYield, pAwait) ~ subShiftExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ShiftExpression0(x0)) } |
        MISMATCH
  }
  lazy val subShiftExpression: R2[ast.ShiftExpression] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("<<")) ~ AdditiveExpression(pYield, pAwait) ~ subShiftExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.ShiftExpression) => y(ast.ShiftExpression1(x, x0))) } |
        (MATCH <~ term(">>")) ~ AdditiveExpression(pYield, pAwait) ~ subShiftExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.ShiftExpression) => y(ast.ShiftExpression2(x, x0))) } |
        (MATCH <~ term(">>>")) ~ AdditiveExpression(pYield, pAwait) ~ subShiftExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.ShiftExpression) => y(ast.ShiftExpression3(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val RelationalExpression: P3[ast.RelationalExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ ShiftExpression(pYield, pAwait) ~ subRelationalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.RelationalExpression0(x0)) } |
        MISMATCH
  }
  lazy val subRelationalExpression: R3[ast.RelationalExpression] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term("<")) ~ ShiftExpression(pYield, pAwait) ~ subRelationalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.RelationalExpression) => y(ast.RelationalExpression1(x, x0))) } |
        (MATCH <~ term(">")) ~ ShiftExpression(pYield, pAwait) ~ subRelationalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.RelationalExpression) => y(ast.RelationalExpression2(x, x0))) } |
        (MATCH <~ term("<=")) ~ ShiftExpression(pYield, pAwait) ~ subRelationalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.RelationalExpression) => y(ast.RelationalExpression3(x, x0))) } |
        (MATCH <~ term(">=")) ~ ShiftExpression(pYield, pAwait) ~ subRelationalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.RelationalExpression) => y(ast.RelationalExpression4(x, x0))) } |
        (MATCH <~ term("instanceof")) ~ ShiftExpression(pYield, pAwait) ~ subRelationalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.RelationalExpression) => y(ast.RelationalExpression5(x, x0))) } |
        (if (pIn) (MATCH <~ term("in")) ~ ShiftExpression(pYield, pAwait) ~ subRelationalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.RelationalExpression) => y(ast.RelationalExpression6(x, x0))) } else MISMATCH) |
        MATCH ^^^ { x => x }
  }
  lazy val EqualityExpression: P3[ast.EqualityExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ RelationalExpression(pIn, pYield, pAwait) ~ subEqualityExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.EqualityExpression0(x0)) } |
        MISMATCH
  }
  lazy val subEqualityExpression: R3[ast.EqualityExpression] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term("==")) ~ RelationalExpression(pIn, pYield, pAwait) ~ subEqualityExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.EqualityExpression) => y(ast.EqualityExpression1(x, x0))) } |
        (MATCH <~ term("!=")) ~ RelationalExpression(pIn, pYield, pAwait) ~ subEqualityExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.EqualityExpression) => y(ast.EqualityExpression2(x, x0))) } |
        (MATCH <~ term("===")) ~ RelationalExpression(pIn, pYield, pAwait) ~ subEqualityExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.EqualityExpression) => y(ast.EqualityExpression3(x, x0))) } |
        (MATCH <~ term("!==")) ~ RelationalExpression(pIn, pYield, pAwait) ~ subEqualityExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.EqualityExpression) => y(ast.EqualityExpression4(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val BitwiseANDExpression: P3[ast.BitwiseANDExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ EqualityExpression(pIn, pYield, pAwait) ~ subBitwiseANDExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BitwiseANDExpression0(x0)) } |
        MISMATCH
  }
  lazy val subBitwiseANDExpression: R3[ast.BitwiseANDExpression] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term("&")) ~ EqualityExpression(pIn, pYield, pAwait) ~ subBitwiseANDExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.BitwiseANDExpression) => y(ast.BitwiseANDExpression1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val BitwiseXORExpression: P3[ast.BitwiseXORExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ BitwiseANDExpression(pIn, pYield, pAwait) ~ subBitwiseXORExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BitwiseXORExpression0(x0)) } |
        MISMATCH
  }
  lazy val subBitwiseXORExpression: R3[ast.BitwiseXORExpression] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term("^")) ~ BitwiseANDExpression(pIn, pYield, pAwait) ~ subBitwiseXORExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.BitwiseXORExpression) => y(ast.BitwiseXORExpression1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val BitwiseORExpression: P3[ast.BitwiseORExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ BitwiseXORExpression(pIn, pYield, pAwait) ~ subBitwiseORExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BitwiseORExpression0(x0)) } |
        MISMATCH
  }
  lazy val subBitwiseORExpression: R3[ast.BitwiseORExpression] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term("|")) ~ BitwiseXORExpression(pIn, pYield, pAwait) ~ subBitwiseORExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.BitwiseORExpression) => y(ast.BitwiseORExpression1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val LogicalANDExpression: P3[ast.LogicalANDExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ BitwiseORExpression(pIn, pYield, pAwait) ~ subLogicalANDExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.LogicalANDExpression0(x0)) } |
        MISMATCH
  }
  lazy val subLogicalANDExpression: R3[ast.LogicalANDExpression] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term("&&")) ~ BitwiseORExpression(pIn, pYield, pAwait) ~ subLogicalANDExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.LogicalANDExpression) => y(ast.LogicalANDExpression1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val LogicalORExpression: P3[ast.LogicalORExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ LogicalANDExpression(pIn, pYield, pAwait) ~ subLogicalORExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.LogicalORExpression0(x0)) } |
        MISMATCH
  }
  lazy val subLogicalORExpression: R3[ast.LogicalORExpression] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term("||")) ~ LogicalANDExpression(pIn, pYield, pAwait) ~ subLogicalORExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.LogicalORExpression) => y(ast.LogicalORExpression1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val ConditionalExpression: P3[ast.ConditionalExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ LogicalORExpression(pIn, pYield, pAwait) ~ subConditionalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ConditionalExpression0(x0)) } |
        ((MATCH ~ LogicalORExpression(pIn, pYield, pAwait) <~ term("?")) ~ AssignmentExpression(true, pYield, pAwait) <~ term(":")) ~ AssignmentExpression(pIn, pYield, pAwait) ~ subConditionalExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.ConditionalExpression1(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subConditionalExpression: R3[ast.ConditionalExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AssignmentExpression: P3[ast.AssignmentExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ ConditionalExpression(pIn, pYield, pAwait) ~ subAssignmentExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentExpression0(x0)) } |
        (if (pYield) MATCH ~ YieldExpression(pIn, pAwait) ~ subAssignmentExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentExpression1(x0)) } else MISMATCH) |
        MATCH ~ ArrowFunction(pIn, pYield, pAwait) ~ subAssignmentExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentExpression2(x0)) } |
        MATCH ~ AsyncArrowFunction(pIn, pYield, pAwait) ~ subAssignmentExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentExpression3(x0)) } |
        (MATCH ~ LeftHandSideExpression(pYield, pAwait) <~ term("=")) ~ AssignmentExpression(pIn, pYield, pAwait) ~ subAssignmentExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AssignmentExpression4(x0, x1)) } |
        MATCH ~ LeftHandSideExpression(pYield, pAwait) ~ AssignmentOperator ~ AssignmentExpression(pIn, pYield, pAwait) ~ subAssignmentExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.AssignmentExpression5(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subAssignmentExpression: R3[ast.AssignmentExpression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AssignmentPattern: P2[ast.AssignmentPattern] = memo {
    case (pYield, pAwait) =>
      MATCH ~ ObjectAssignmentPattern(pYield, pAwait) ~ subAssignmentPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentPattern0(x0)) } |
        MATCH ~ ArrayAssignmentPattern(pYield, pAwait) ~ subAssignmentPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentPattern1(x0)) } |
        MISMATCH
  }
  lazy val subAssignmentPattern: R2[ast.AssignmentPattern] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ObjectAssignmentPattern: P2[ast.ObjectAssignmentPattern] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("{")) <~ term("}")) ~ subObjectAssignmentPattern(pYield, pAwait) ^^ { case _ ~ y => y(ast.ObjectAssignmentPattern0) } |
        ((MATCH <~ term("{")) ~ AssignmentRestProperty(pYield, pAwait) <~ term("}")) ~ subObjectAssignmentPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ObjectAssignmentPattern1(x0)) } |
        ((MATCH <~ term("{")) ~ AssignmentPropertyList(pYield, pAwait) <~ term("}")) ~ subObjectAssignmentPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ObjectAssignmentPattern2(x0)) } |
        (((MATCH <~ term("{")) ~ AssignmentPropertyList(pYield, pAwait) <~ term(",")) ~ opt(AssignmentRestProperty(pYield, pAwait)) <~ term("}")) ~ subObjectAssignmentPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ObjectAssignmentPattern3(x0, x1)) } |
        MISMATCH
  }
  lazy val subObjectAssignmentPattern: R2[ast.ObjectAssignmentPattern] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ArrayAssignmentPattern: P2[ast.ArrayAssignmentPattern] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("[")) ~ opt(Elision) ~ opt(AssignmentRestElement(pYield, pAwait)) <~ term("]")) ~ subArrayAssignmentPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ArrayAssignmentPattern0(x0, x1)) } |
        ((MATCH <~ term("[")) ~ AssignmentElementList(pYield, pAwait) <~ term("]")) ~ subArrayAssignmentPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArrayAssignmentPattern1(x0)) } |
        (((MATCH <~ term("[")) ~ AssignmentElementList(pYield, pAwait) <~ term(",")) ~ opt(Elision) ~ opt(AssignmentRestElement(pYield, pAwait)) <~ term("]")) ~ subArrayAssignmentPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.ArrayAssignmentPattern2(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subArrayAssignmentPattern: R2[ast.ArrayAssignmentPattern] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AssignmentPropertyList: P2[ast.AssignmentPropertyList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ AssignmentProperty(pYield, pAwait) ~ subAssignmentPropertyList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentPropertyList0(x0)) } |
        MISMATCH
  }
  lazy val subAssignmentPropertyList: R2[ast.AssignmentPropertyList] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term(",")) ~ AssignmentProperty(pYield, pAwait) ~ subAssignmentPropertyList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.AssignmentPropertyList) => y(ast.AssignmentPropertyList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val AssignmentElementList: P2[ast.AssignmentElementList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ AssignmentElisionElement(pYield, pAwait) ~ subAssignmentElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentElementList0(x0)) } |
        MISMATCH
  }
  lazy val subAssignmentElementList: R2[ast.AssignmentElementList] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term(",")) ~ AssignmentElisionElement(pYield, pAwait) ~ subAssignmentElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.AssignmentElementList) => y(ast.AssignmentElementList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val AssignmentElisionElement: P2[ast.AssignmentElisionElement] = memo {
    case (pYield, pAwait) =>
      MATCH ~ opt(Elision) ~ AssignmentElement(pYield, pAwait) ~ subAssignmentElisionElement(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AssignmentElisionElement0(x0, x1)) } |
        MISMATCH
  }
  lazy val subAssignmentElisionElement: R2[ast.AssignmentElisionElement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AssignmentProperty: P2[ast.AssignmentProperty] = memo {
    case (pYield, pAwait) =>
      MATCH ~ IdentifierReference(pYield, pAwait) ~ opt(Initializer(true, pYield, pAwait)) ~ subAssignmentProperty(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AssignmentProperty0(x0, x1)) } |
        (MATCH ~ PropertyName(pYield, pAwait) <~ term(":")) ~ AssignmentElement(pYield, pAwait) ~ subAssignmentProperty(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AssignmentProperty1(x0, x1)) } |
        MISMATCH
  }
  lazy val subAssignmentProperty: R2[ast.AssignmentProperty] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AssignmentElement: P2[ast.AssignmentElement] = memo {
    case (pYield, pAwait) =>
      MATCH ~ DestructuringAssignmentTarget(pYield, pAwait) ~ opt(Initializer(true, pYield, pAwait)) ~ subAssignmentElement(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AssignmentElement0(x0, x1)) } |
        MISMATCH
  }
  lazy val subAssignmentElement: R2[ast.AssignmentElement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AssignmentRestElement: P2[ast.AssignmentRestElement] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("...")) ~ DestructuringAssignmentTarget(pYield, pAwait) ~ subAssignmentRestElement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentRestElement0(x0)) } |
        MISMATCH
  }
  lazy val subAssignmentRestElement: R2[ast.AssignmentRestElement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val DestructuringAssignmentTarget: P2[ast.DestructuringAssignmentTarget] = memo {
    case (pYield, pAwait) =>
      MATCH ~ LeftHandSideExpression(pYield, pAwait) ~ subDestructuringAssignmentTarget(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.DestructuringAssignmentTarget0(x0)) } |
        MISMATCH
  }
  lazy val subDestructuringAssignmentTarget: R2[ast.DestructuringAssignmentTarget] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AssignmentOperator: P0[ast.AssignmentOperator] = {
    (MATCH <~ term("*=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator0) } |
      (MATCH <~ term("/=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator1) } |
      (MATCH <~ term("%=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator2) } |
      (MATCH <~ term("+=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator3) } |
      (MATCH <~ term("-=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator4) } |
      (MATCH <~ term("<<=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator5) } |
      (MATCH <~ term(">>=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator6) } |
      (MATCH <~ term(">>>=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator7) } |
      (MATCH <~ term("&=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator8) } |
      (MATCH <~ term("^=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator9) } |
      (MATCH <~ term("|=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator10) } |
      (MATCH <~ term("**=")) ~ subAssignmentOperator ^^ { case _ ~ y => y(ast.AssignmentOperator11) } |
      MISMATCH
  }
  lazy val subAssignmentOperator: R0[ast.AssignmentOperator] = {
    MATCH ^^^ { x => x }
  }
  lazy val Expression: P3[ast.Expression] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ AssignmentExpression(pIn, pYield, pAwait) ~ subExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.Expression0(x0)) } |
        MISMATCH
  }
  lazy val subExpression: R3[ast.Expression] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term(",")) ~ AssignmentExpression(pIn, pYield, pAwait) ~ subExpression(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.Expression) => y(ast.Expression1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val Statement: P3[ast.Statement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ BlockStatement(pYield, pAwait, pReturn) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement0(x0)) } |
        MATCH ~ VariableStatement(pYield, pAwait) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement1(x0)) } |
        MATCH ~ EmptyStatement ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement2(x0)) } |
        MATCH ~ ExpressionStatement(pYield, pAwait) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement3(x0)) } |
        MATCH ~ IfStatement(pYield, pAwait, pReturn) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement4(x0)) } |
        MATCH ~ BreakableStatement(pYield, pAwait, pReturn) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement5(x0)) } |
        MATCH ~ ContinueStatement(pYield, pAwait) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement6(x0)) } |
        MATCH ~ BreakStatement(pYield, pAwait) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement7(x0)) } |
        (if (pReturn) MATCH ~ ReturnStatement(pYield, pAwait) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement8(x0)) } else MISMATCH) |
        MATCH ~ WithStatement(pYield, pAwait, pReturn) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement9(x0)) } |
        MATCH ~ LabelledStatement(pYield, pAwait, pReturn) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement10(x0)) } |
        MATCH ~ ThrowStatement(pYield, pAwait) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement11(x0)) } |
        MATCH ~ TryStatement(pYield, pAwait, pReturn) ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement12(x0)) } |
        MATCH ~ DebuggerStatement ~ subStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Statement13(x0)) } |
        MISMATCH
  }
  lazy val subStatement: R3[ast.Statement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val Declaration: P2[ast.Declaration] = memo {
    case (pYield, pAwait) =>
      MATCH ~ HoistableDeclaration(pYield, pAwait, false) ~ subDeclaration(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.Declaration0(x0)) } |
        MATCH ~ ClassDeclaration(pYield, pAwait, false) ~ subDeclaration(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.Declaration1(x0)) } |
        MATCH ~ LexicalDeclaration(true, pYield, pAwait) ~ subDeclaration(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.Declaration2(x0)) } |
        MISMATCH
  }
  lazy val subDeclaration: R2[ast.Declaration] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val HoistableDeclaration: P3[ast.HoistableDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      MATCH ~ FunctionDeclaration(pYield, pAwait, pDefault) ~ subHoistableDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ y => y(ast.HoistableDeclaration0(x0)) } |
        MATCH ~ GeneratorDeclaration(pYield, pAwait, pDefault) ~ subHoistableDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ y => y(ast.HoistableDeclaration1(x0)) } |
        MATCH ~ AsyncFunctionDeclaration(pYield, pAwait, pDefault) ~ subHoistableDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ y => y(ast.HoistableDeclaration2(x0)) } |
        MATCH ~ AsyncGeneratorDeclaration(pYield, pAwait, pDefault) ~ subHoistableDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ y => y(ast.HoistableDeclaration3(x0)) } |
        MISMATCH
  }
  lazy val subHoistableDeclaration: R3[ast.HoistableDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      MATCH ^^^ { x => x }
  }
  lazy val BreakableStatement: P3[ast.BreakableStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ IterationStatement(pYield, pAwait, pReturn) ~ subBreakableStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.BreakableStatement0(x0)) } |
        MATCH ~ SwitchStatement(pYield, pAwait, pReturn) ~ subBreakableStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.BreakableStatement1(x0)) } |
        MISMATCH
  }
  lazy val subBreakableStatement: R3[ast.BreakableStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val BlockStatement: P3[ast.BlockStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ Block(pYield, pAwait, pReturn) ~ subBlockStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.BlockStatement0(x0)) } |
        MISMATCH
  }
  lazy val subBlockStatement: R3[ast.BlockStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val Block: P3[ast.Block] = memo {
    case (pYield, pAwait, pReturn) =>
      ((MATCH <~ term("{")) ~ opt(StatementList(pYield, pAwait, pReturn)) <~ term("}")) ~ subBlock(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Block0(x0)) } |
        MISMATCH
  }
  lazy val subBlock: R3[ast.Block] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val StatementList: P3[ast.StatementList] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ StatementListItem(pYield, pAwait, pReturn) ~ subStatementList(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.StatementList0(x0)) } |
        MISMATCH
  }
  lazy val subStatementList: R3[ast.StatementList] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ StatementListItem(pYield, pAwait, pReturn) ~ subStatementList(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => ((x: ast.StatementList) => y(ast.StatementList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val StatementListItem: P3[ast.StatementListItem] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ Statement(pYield, pAwait, pReturn) ~ subStatementListItem(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.StatementListItem0(x0)) } |
        MATCH ~ Declaration(pYield, pAwait) ~ subStatementListItem(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.StatementListItem1(x0)) } |
        MISMATCH
  }
  lazy val subStatementListItem: R3[ast.StatementListItem] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val LexicalDeclaration: P3[ast.LexicalDeclaration] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH ~ LetOrConst ~ BindingList(pIn, pYield, pAwait) <~ term(";")) ~ subLexicalDeclaration(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.LexicalDeclaration0(x0, x1)) } |
        MISMATCH
  }
  lazy val subLexicalDeclaration: R3[ast.LexicalDeclaration] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val LetOrConst: P0[ast.LetOrConst] = {
    (MATCH <~ term("let")) ~ subLetOrConst ^^ { case _ ~ y => y(ast.LetOrConst0) } |
      (MATCH <~ term("const")) ~ subLetOrConst ^^ { case _ ~ y => y(ast.LetOrConst1) } |
      MISMATCH
  }
  lazy val subLetOrConst: R0[ast.LetOrConst] = {
    MATCH ^^^ { x => x }
  }
  lazy val BindingList: P3[ast.BindingList] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ LexicalBinding(pIn, pYield, pAwait) ~ subBindingList(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingList0(x0)) } |
        MISMATCH
  }
  lazy val subBindingList: R3[ast.BindingList] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term(",")) ~ LexicalBinding(pIn, pYield, pAwait) ~ subBindingList(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.BindingList) => y(ast.BindingList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val LexicalBinding: P3[ast.LexicalBinding] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ BindingIdentifier(pYield, pAwait) ~ opt(Initializer(pIn, pYield, pAwait)) ~ subLexicalBinding(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.LexicalBinding0(x0, x1)) } |
        MATCH ~ BindingPattern(pYield, pAwait) ~ Initializer(pIn, pYield, pAwait) ~ subLexicalBinding(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.LexicalBinding1(x0, x1)) } |
        MISMATCH
  }
  lazy val subLexicalBinding: R3[ast.LexicalBinding] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val VariableStatement: P2[ast.VariableStatement] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("var")) ~ VariableDeclarationList(true, pYield, pAwait) <~ term(";")) ~ subVariableStatement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.VariableStatement0(x0)) } |
        MISMATCH
  }
  lazy val subVariableStatement: R2[ast.VariableStatement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val VariableDeclarationList: P3[ast.VariableDeclarationList] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ VariableDeclaration(pIn, pYield, pAwait) ~ subVariableDeclarationList(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.VariableDeclarationList0(x0)) } |
        MISMATCH
  }
  lazy val subVariableDeclarationList: R3[ast.VariableDeclarationList] = memo {
    case (pIn, pYield, pAwait) =>
      (MATCH <~ term(",")) ~ VariableDeclaration(pIn, pYield, pAwait) ~ subVariableDeclarationList(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.VariableDeclarationList) => y(ast.VariableDeclarationList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val VariableDeclaration: P3[ast.VariableDeclaration] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ~ BindingIdentifier(pYield, pAwait) ~ opt(Initializer(pIn, pYield, pAwait)) ~ subVariableDeclaration(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.VariableDeclaration0(x0, x1)) } |
        MATCH ~ BindingPattern(pYield, pAwait) ~ Initializer(pIn, pYield, pAwait) ~ subVariableDeclaration(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.VariableDeclaration1(x0, x1)) } |
        MISMATCH
  }
  lazy val subVariableDeclaration: R3[ast.VariableDeclaration] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val BindingPattern: P2[ast.BindingPattern] = memo {
    case (pYield, pAwait) =>
      MATCH ~ ObjectBindingPattern(pYield, pAwait) ~ subBindingPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingPattern0(x0)) } |
        MATCH ~ ArrayBindingPattern(pYield, pAwait) ~ subBindingPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingPattern1(x0)) } |
        MISMATCH
  }
  lazy val subBindingPattern: R2[ast.BindingPattern] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ObjectBindingPattern: P2[ast.ObjectBindingPattern] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("{")) <~ term("}")) ~ subObjectBindingPattern(pYield, pAwait) ^^ { case _ ~ y => y(ast.ObjectBindingPattern0) } |
        ((MATCH <~ term("{")) ~ BindingRestProperty(pYield, pAwait) <~ term("}")) ~ subObjectBindingPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ObjectBindingPattern1(x0)) } |
        ((MATCH <~ term("{")) ~ BindingPropertyList(pYield, pAwait) <~ term("}")) ~ subObjectBindingPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ObjectBindingPattern2(x0)) } |
        (((MATCH <~ term("{")) ~ BindingPropertyList(pYield, pAwait) <~ term(",")) ~ opt(BindingRestProperty(pYield, pAwait)) <~ term("}")) ~ subObjectBindingPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ObjectBindingPattern3(x0, x1)) } |
        MISMATCH
  }
  lazy val subObjectBindingPattern: R2[ast.ObjectBindingPattern] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ArrayBindingPattern: P2[ast.ArrayBindingPattern] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("[")) ~ opt(Elision) ~ opt(BindingRestElement(pYield, pAwait)) <~ term("]")) ~ subArrayBindingPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ArrayBindingPattern0(x0, x1)) } |
        ((MATCH <~ term("[")) ~ BindingElementList(pYield, pAwait) <~ term("]")) ~ subArrayBindingPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArrayBindingPattern1(x0)) } |
        (((MATCH <~ term("[")) ~ BindingElementList(pYield, pAwait) <~ term(",")) ~ opt(Elision) ~ opt(BindingRestElement(pYield, pAwait)) <~ term("]")) ~ subArrayBindingPattern(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.ArrayBindingPattern2(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subArrayBindingPattern: R2[ast.ArrayBindingPattern] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val BindingPropertyList: P2[ast.BindingPropertyList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ BindingProperty(pYield, pAwait) ~ subBindingPropertyList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingPropertyList0(x0)) } |
        MISMATCH
  }
  lazy val subBindingPropertyList: R2[ast.BindingPropertyList] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term(",")) ~ BindingProperty(pYield, pAwait) ~ subBindingPropertyList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.BindingPropertyList) => y(ast.BindingPropertyList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val BindingElementList: P2[ast.BindingElementList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ BindingElisionElement(pYield, pAwait) ~ subBindingElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingElementList0(x0)) } |
        MISMATCH
  }
  lazy val subBindingElementList: R2[ast.BindingElementList] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term(",")) ~ BindingElisionElement(pYield, pAwait) ~ subBindingElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.BindingElementList) => y(ast.BindingElementList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val BindingElisionElement: P2[ast.BindingElisionElement] = memo {
    case (pYield, pAwait) =>
      MATCH ~ opt(Elision) ~ BindingElement(pYield, pAwait) ~ subBindingElisionElement(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.BindingElisionElement0(x0, x1)) } |
        MISMATCH
  }
  lazy val subBindingElisionElement: R2[ast.BindingElisionElement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val BindingProperty: P2[ast.BindingProperty] = memo {
    case (pYield, pAwait) =>
      MATCH ~ SingleNameBinding(pYield, pAwait) ~ subBindingProperty(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingProperty0(x0)) } |
        (MATCH ~ PropertyName(pYield, pAwait) <~ term(":")) ~ BindingElement(pYield, pAwait) ~ subBindingProperty(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.BindingProperty1(x0, x1)) } |
        MISMATCH
  }
  lazy val subBindingProperty: R2[ast.BindingProperty] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val BindingElement: P2[ast.BindingElement] = memo {
    case (pYield, pAwait) =>
      MATCH ~ SingleNameBinding(pYield, pAwait) ~ subBindingElement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingElement0(x0)) } |
        MATCH ~ BindingPattern(pYield, pAwait) ~ opt(Initializer(true, pYield, pAwait)) ~ subBindingElement(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.BindingElement1(x0, x1)) } |
        MISMATCH
  }
  lazy val subBindingElement: R2[ast.BindingElement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val SingleNameBinding: P2[ast.SingleNameBinding] = memo {
    case (pYield, pAwait) =>
      MATCH ~ BindingIdentifier(pYield, pAwait) ~ opt(Initializer(true, pYield, pAwait)) ~ subSingleNameBinding(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.SingleNameBinding0(x0, x1)) } |
        MISMATCH
  }
  lazy val subSingleNameBinding: R2[ast.SingleNameBinding] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val BindingRestElement: P2[ast.BindingRestElement] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("...")) ~ BindingIdentifier(pYield, pAwait) ~ subBindingRestElement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingRestElement0(x0)) } |
        (MATCH <~ term("...")) ~ BindingPattern(pYield, pAwait) ~ subBindingRestElement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingRestElement1(x0)) } |
        MISMATCH
  }
  lazy val subBindingRestElement: R2[ast.BindingRestElement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val EmptyStatement: P0[ast.EmptyStatement] = {
    (MATCH <~ term(";")) ~ subEmptyStatement ^^ { case _ ~ y => y(ast.EmptyStatement0) } |
      MISMATCH
  }
  lazy val subEmptyStatement: R0[ast.EmptyStatement] = {
    MATCH ^^^ { x => x }
  }
  lazy val ExpressionStatement: P2[ast.ExpressionStatement] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ -term("", seq("{") | seq("function") | seq("async", strNoLineTerminator, "function") | seq("class") | seq("let", "["))) ~ Expression(true, pYield, pAwait) <~ term(";")) ~ subExpressionStatement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ExpressionStatement0(x0)) } |
        MISMATCH
  }
  lazy val subExpressionStatement: R2[ast.ExpressionStatement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val IfStatement: P3[ast.IfStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      ((((MATCH <~ term("if")) <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) <~ term("else")) ~ Statement(pYield, pAwait, pReturn) ~ subIfStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IfStatement0(x0, x1, x2)) } |
        (((MATCH <~ term("if")) <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIfStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.IfStatement1(x0, x1)) } |
        MISMATCH
  }
  lazy val subIfStatement: R3[ast.IfStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val IterationStatement: P3[ast.IterationStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      (((((MATCH <~ term("do")) ~ Statement(pYield, pAwait, pReturn) <~ term("while")) <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(")")) <~ term(";")) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.IterationStatement0(x0, x1)) } |
        (((MATCH <~ term("while")) <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.IterationStatement1(x0, x1)) } |
        ((((((MATCH <~ term("for")) <~ term("(")) <~ -term("", seq("let", "["))) ~ opt(Expression(false, pYield, pAwait)) <~ term(";")) ~ opt(Expression(true, pYield, pAwait)) <~ term(";")) ~ opt(Expression(true, pYield, pAwait)) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ x3 ~ y => y(ast.IterationStatement2(x0, x1, x2, x3)) } |
        ((((((MATCH <~ term("for")) <~ term("(")) <~ term("var")) ~ VariableDeclarationList(false, pYield, pAwait) <~ term(";")) ~ opt(Expression(true, pYield, pAwait)) <~ term(";")) ~ opt(Expression(true, pYield, pAwait)) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ x3 ~ y => y(ast.IterationStatement3(x0, x1, x2, x3)) } |
        ((((MATCH <~ term("for")) <~ term("(")) ~ LexicalDeclaration(false, pYield, pAwait) ~ opt(Expression(true, pYield, pAwait)) <~ term(";")) ~ opt(Expression(true, pYield, pAwait)) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ x3 ~ y => y(ast.IterationStatement4(x0, x1, x2, x3)) } |
        (((((MATCH <~ term("for")) <~ term("(")) <~ -term("", seq("let", "["))) ~ LeftHandSideExpression(pYield, pAwait) <~ term("in")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement5(x0, x1, x2)) } |
        (((((MATCH <~ term("for")) <~ term("(")) <~ term("var")) ~ ForBinding(pYield, pAwait) <~ term("in")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement6(x0, x1, x2)) } |
        ((((MATCH <~ term("for")) <~ term("(")) ~ ForDeclaration(pYield, pAwait) <~ term("in")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement7(x0, x1, x2)) } |
        (((((MATCH <~ term("for")) <~ term("(")) <~ -term("", seq("let"))) ~ LeftHandSideExpression(pYield, pAwait) <~ term("of")) ~ AssignmentExpression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement8(x0, x1, x2)) } |
        (((((MATCH <~ term("for")) <~ term("(")) <~ term("var")) ~ ForBinding(pYield, pAwait) <~ term("of")) ~ AssignmentExpression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement9(x0, x1, x2)) } |
        ((((MATCH <~ term("for")) <~ term("(")) ~ ForDeclaration(pYield, pAwait) <~ term("of")) ~ AssignmentExpression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement10(x0, x1, x2)) } |
        (if (pAwait) ((((((MATCH <~ term("for")) <~ term("await")) <~ term("(")) <~ -term("", seq("let"))) ~ LeftHandSideExpression(pYield, pAwait) <~ term("of")) ~ AssignmentExpression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement11(x0, x1, x2)) } else MISMATCH) |
        (if (pAwait) ((((((MATCH <~ term("for")) <~ term("await")) <~ term("(")) <~ term("var")) ~ ForBinding(pYield, pAwait) <~ term("of")) ~ AssignmentExpression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement12(x0, x1, x2)) } else MISMATCH) |
        (if (pAwait) (((((MATCH <~ term("for")) <~ term("await")) <~ term("(")) ~ ForDeclaration(pYield, pAwait) <~ term("of")) ~ AssignmentExpression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subIterationStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.IterationStatement13(x0, x1, x2)) } else MISMATCH) |
        MISMATCH
  }
  lazy val subIterationStatement: R3[ast.IterationStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val ForDeclaration: P2[ast.ForDeclaration] = memo {
    case (pYield, pAwait) =>
      MATCH ~ LetOrConst ~ ForBinding(pYield, pAwait) ~ subForDeclaration(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ForDeclaration0(x0, x1)) } |
        MISMATCH
  }
  lazy val subForDeclaration: R2[ast.ForDeclaration] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ForBinding: P2[ast.ForBinding] = memo {
    case (pYield, pAwait) =>
      MATCH ~ BindingIdentifier(pYield, pAwait) ~ subForBinding(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ForBinding0(x0)) } |
        MATCH ~ BindingPattern(pYield, pAwait) ~ subForBinding(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ForBinding1(x0)) } |
        MISMATCH
  }
  lazy val subForBinding: R2[ast.ForBinding] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ContinueStatement: P2[ast.ContinueStatement] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("continue")) <~ term(";")) ~ subContinueStatement(pYield, pAwait) ^^ { case _ ~ y => y(ast.ContinueStatement0) } |
        (((MATCH <~ term("continue")) <~ NoLineTerminator) ~ LabelIdentifier(pYield, pAwait) <~ term(";")) ~ subContinueStatement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ContinueStatement1(x0)) } |
        MISMATCH
  }
  lazy val subContinueStatement: R2[ast.ContinueStatement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val BreakStatement: P2[ast.BreakStatement] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("break")) <~ term(";")) ~ subBreakStatement(pYield, pAwait) ^^ { case _ ~ y => y(ast.BreakStatement0) } |
        (((MATCH <~ term("break")) <~ NoLineTerminator) ~ LabelIdentifier(pYield, pAwait) <~ term(";")) ~ subBreakStatement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BreakStatement1(x0)) } |
        MISMATCH
  }
  lazy val subBreakStatement: R2[ast.BreakStatement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ReturnStatement: P2[ast.ReturnStatement] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("return")) <~ term(";")) ~ subReturnStatement(pYield, pAwait) ^^ { case _ ~ y => y(ast.ReturnStatement0) } |
        (((MATCH <~ term("return")) <~ NoLineTerminator) ~ Expression(true, pYield, pAwait) <~ term(";")) ~ subReturnStatement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ReturnStatement1(x0)) } |
        MISMATCH
  }
  lazy val subReturnStatement: R2[ast.ReturnStatement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val WithStatement: P3[ast.WithStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      (((MATCH <~ term("with")) <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ Statement(pYield, pAwait, pReturn) ~ subWithStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.WithStatement0(x0, x1)) } |
        MISMATCH
  }
  lazy val subWithStatement: R3[ast.WithStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val SwitchStatement: P3[ast.SwitchStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      (((MATCH <~ term("switch")) <~ term("(")) ~ Expression(true, pYield, pAwait) <~ term(")")) ~ CaseBlock(pYield, pAwait, pReturn) ~ subSwitchStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.SwitchStatement0(x0, x1)) } |
        MISMATCH
  }
  lazy val subSwitchStatement: R3[ast.SwitchStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val CaseBlock: P3[ast.CaseBlock] = memo {
    case (pYield, pAwait, pReturn) =>
      ((MATCH <~ term("{")) ~ opt(CaseClauses(pYield, pAwait, pReturn)) <~ term("}")) ~ subCaseBlock(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.CaseBlock0(x0)) } |
        ((MATCH <~ term("{")) ~ opt(CaseClauses(pYield, pAwait, pReturn)) ~ DefaultClause(pYield, pAwait, pReturn) ~ opt(CaseClauses(pYield, pAwait, pReturn)) <~ term("}")) ~ subCaseBlock(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.CaseBlock1(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subCaseBlock: R3[ast.CaseBlock] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val CaseClauses: P3[ast.CaseClauses] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ CaseClause(pYield, pAwait, pReturn) ~ subCaseClauses(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.CaseClauses0(x0)) } |
        MISMATCH
  }
  lazy val subCaseClauses: R3[ast.CaseClauses] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ CaseClause(pYield, pAwait, pReturn) ~ subCaseClauses(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => ((x: ast.CaseClauses) => y(ast.CaseClauses1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val CaseClause: P3[ast.CaseClause] = memo {
    case (pYield, pAwait, pReturn) =>
      ((MATCH <~ term("case")) ~ Expression(true, pYield, pAwait) <~ term(":")) ~ opt(StatementList(pYield, pAwait, pReturn)) ~ subCaseClause(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.CaseClause0(x0, x1)) } |
        MISMATCH
  }
  lazy val subCaseClause: R3[ast.CaseClause] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val DefaultClause: P3[ast.DefaultClause] = memo {
    case (pYield, pAwait, pReturn) =>
      ((MATCH <~ term("default")) <~ term(":")) ~ opt(StatementList(pYield, pAwait, pReturn)) ~ subDefaultClause(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.DefaultClause0(x0)) } |
        MISMATCH
  }
  lazy val subDefaultClause: R3[ast.DefaultClause] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val LabelledStatement: P3[ast.LabelledStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      (MATCH ~ LabelIdentifier(pYield, pAwait) <~ term(":")) ~ LabelledItem(pYield, pAwait, pReturn) ~ subLabelledStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.LabelledStatement0(x0, x1)) } |
        MISMATCH
  }
  lazy val subLabelledStatement: R3[ast.LabelledStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val LabelledItem: P3[ast.LabelledItem] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ~ Statement(pYield, pAwait, pReturn) ~ subLabelledItem(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.LabelledItem0(x0)) } |
        MATCH ~ FunctionDeclaration(pYield, pAwait, false) ~ subLabelledItem(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.LabelledItem1(x0)) } |
        MISMATCH
  }
  lazy val subLabelledItem: R3[ast.LabelledItem] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val ThrowStatement: P2[ast.ThrowStatement] = memo {
    case (pYield, pAwait) =>
      (((MATCH <~ term("throw")) <~ NoLineTerminator) ~ Expression(true, pYield, pAwait) <~ term(";")) ~ subThrowStatement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ThrowStatement0(x0)) } |
        MISMATCH
  }
  lazy val subThrowStatement: R2[ast.ThrowStatement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val TryStatement: P3[ast.TryStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      (MATCH <~ term("try")) ~ Block(pYield, pAwait, pReturn) ~ Catch(pYield, pAwait, pReturn) ~ subTryStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.TryStatement0(x0, x1)) } |
        (MATCH <~ term("try")) ~ Block(pYield, pAwait, pReturn) ~ Finally(pYield, pAwait, pReturn) ~ subTryStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.TryStatement1(x0, x1)) } |
        (MATCH <~ term("try")) ~ Block(pYield, pAwait, pReturn) ~ Catch(pYield, pAwait, pReturn) ~ Finally(pYield, pAwait, pReturn) ~ subTryStatement(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.TryStatement2(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subTryStatement: R3[ast.TryStatement] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val Catch: P3[ast.Catch] = memo {
    case (pYield, pAwait, pReturn) =>
      (((MATCH <~ term("catch")) <~ term("(")) ~ CatchParameter(pYield, pAwait) <~ term(")")) ~ Block(pYield, pAwait, pReturn) ~ subCatch(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.Catch0(x0, x1)) } |
        MISMATCH
  }
  lazy val subCatch: R3[ast.Catch] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val Finally: P3[ast.Finally] = memo {
    case (pYield, pAwait, pReturn) =>
      (MATCH <~ term("finally")) ~ Block(pYield, pAwait, pReturn) ~ subFinally(pYield, pAwait, pReturn) ^^ { case _ ~ x0 ~ y => y(ast.Finally0(x0)) } |
        MISMATCH
  }
  lazy val subFinally: R3[ast.Finally] = memo {
    case (pYield, pAwait, pReturn) =>
      MATCH ^^^ { x => x }
  }
  lazy val CatchParameter: P2[ast.CatchParameter] = memo {
    case (pYield, pAwait) =>
      MATCH ~ BindingIdentifier(pYield, pAwait) ~ subCatchParameter(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.CatchParameter0(x0)) } |
        MATCH ~ BindingPattern(pYield, pAwait) ~ subCatchParameter(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.CatchParameter1(x0)) } |
        MISMATCH
  }
  lazy val subCatchParameter: R2[ast.CatchParameter] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val DebuggerStatement: P0[ast.DebuggerStatement] = {
    ((MATCH <~ term("debugger")) <~ term(";")) ~ subDebuggerStatement ^^ { case _ ~ y => y(ast.DebuggerStatement0) } |
      MISMATCH
  }
  lazy val subDebuggerStatement: R0[ast.DebuggerStatement] = {
    MATCH ^^^ { x => x }
  }
  lazy val FunctionDeclaration: P3[ast.FunctionDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      (((((MATCH <~ term("function")) ~ BindingIdentifier(pYield, pAwait) <~ term("(")) ~ FormalParameters(false, false) <~ term(")")) <~ term("{")) ~ FunctionBody(false, false) <~ term("}")) ~ subFunctionDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.FunctionDeclaration0(x0, x1, x2)) } |
        (if (pDefault) (((((MATCH <~ term("function")) <~ term("(")) ~ FormalParameters(false, false) <~ term(")")) <~ term("{")) ~ FunctionBody(false, false) <~ term("}")) ~ subFunctionDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.FunctionDeclaration1(x0, x1)) } else MISMATCH) |
        MISMATCH
  }
  lazy val subFunctionDeclaration: R3[ast.FunctionDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      MATCH ^^^ { x => x }
  }
  lazy val FunctionExpression: P0[ast.FunctionExpression] = {
    (((((MATCH <~ term("function")) ~ opt(BindingIdentifier(false, false)) <~ term("(")) ~ FormalParameters(false, false) <~ term(")")) <~ term("{")) ~ FunctionBody(false, false) <~ term("}")) ~ subFunctionExpression ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.FunctionExpression0(x0, x1, x2)) } |
      MISMATCH
  }
  lazy val subFunctionExpression: R0[ast.FunctionExpression] = {
    MATCH ^^^ { x => x }
  }
  lazy val UniqueFormalParameters: P2[ast.UniqueFormalParameters] = memo {
    case (pYield, pAwait) =>
      MATCH ~ FormalParameters(pYield, pAwait) ~ subUniqueFormalParameters(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.UniqueFormalParameters0(x0)) } |
        MISMATCH
  }
  lazy val subUniqueFormalParameters: R2[ast.UniqueFormalParameters] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val FormalParameters: P2[ast.FormalParameters] = memo {
    case (pYield, pAwait) =>
      MATCH ~ MATCH ~ subFormalParameters(pYield, pAwait) ^^ { case _ ~ y => y(ast.FormalParameters0) } |
        MATCH ~ FunctionRestParameter(pYield, pAwait) ~ subFormalParameters(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.FormalParameters1(x0)) } |
        MATCH ~ FormalParameterList(pYield, pAwait) ~ subFormalParameters(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.FormalParameters2(x0)) } |
        (MATCH ~ FormalParameterList(pYield, pAwait) <~ term(",")) ~ subFormalParameters(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.FormalParameters3(x0)) } |
        (MATCH ~ FormalParameterList(pYield, pAwait) <~ term(",")) ~ FunctionRestParameter(pYield, pAwait) ~ subFormalParameters(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.FormalParameters4(x0, x1)) } |
        MISMATCH
  }
  lazy val subFormalParameters: R2[ast.FormalParameters] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val FormalParameterList: P2[ast.FormalParameterList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ FormalParameter(pYield, pAwait) ~ subFormalParameterList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.FormalParameterList0(x0)) } |
        MISMATCH
  }
  lazy val subFormalParameterList: R2[ast.FormalParameterList] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term(",")) ~ FormalParameter(pYield, pAwait) ~ subFormalParameterList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.FormalParameterList) => y(ast.FormalParameterList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val FunctionRestParameter: P2[ast.FunctionRestParameter] = memo {
    case (pYield, pAwait) =>
      MATCH ~ BindingRestElement(pYield, pAwait) ~ subFunctionRestParameter(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.FunctionRestParameter0(x0)) } |
        MISMATCH
  }
  lazy val subFunctionRestParameter: R2[ast.FunctionRestParameter] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val FormalParameter: P2[ast.FormalParameter] = memo {
    case (pYield, pAwait) =>
      MATCH ~ BindingElement(pYield, pAwait) ~ subFormalParameter(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.FormalParameter0(x0)) } |
        MISMATCH
  }
  lazy val subFormalParameter: R2[ast.FormalParameter] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val FunctionBody: P2[ast.FunctionBody] = memo {
    case (pYield, pAwait) =>
      MATCH ~ FunctionStatementList(pYield, pAwait) ~ subFunctionBody(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.FunctionBody0(x0)) } |
        MISMATCH
  }
  lazy val subFunctionBody: R2[ast.FunctionBody] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val FunctionStatementList: P2[ast.FunctionStatementList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ opt(StatementList(pYield, pAwait, true)) ~ subFunctionStatementList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.FunctionStatementList0(x0)) } |
        MISMATCH
  }
  lazy val subFunctionStatementList: R2[ast.FunctionStatementList] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ArrowFunction: P3[ast.ArrowFunction] = memo {
    case (pIn, pYield, pAwait) =>
      ((MATCH ~ ArrowParameters(pYield, pAwait) <~ NoLineTerminator) <~ term("=>")) ~ ConciseBody(pIn) ~ subArrowFunction(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ArrowFunction0(x0, x1)) } |
        MISMATCH
  }
  lazy val subArrowFunction: R3[ast.ArrowFunction] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ArrowParameters: P2[ast.ArrowParameters] = memo {
    case (pYield, pAwait) =>
      MATCH ~ BindingIdentifier(pYield, pAwait) ~ subArrowParameters(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArrowParameters0(x0)) } |
        MATCH ~ CoverParenthesizedExpressionAndArrowParameterList(pYield, pAwait) ~ subArrowParameters(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArrowParameters1(x0)) } |
        MISMATCH
  }
  lazy val subArrowParameters: R2[ast.ArrowParameters] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ConciseBody: P1[ast.ConciseBody] = memo {
    case (pIn) =>
      (MATCH <~ -term("", seq("{"))) ~ AssignmentExpression(pIn, false, false) ~ subConciseBody(pIn) ^^ { case _ ~ x0 ~ y => y(ast.ConciseBody0(x0)) } |
        ((MATCH <~ term("{")) ~ FunctionBody(false, false) <~ term("}")) ~ subConciseBody(pIn) ^^ { case _ ~ x0 ~ y => y(ast.ConciseBody1(x0)) } |
        MISMATCH
  }
  lazy val subConciseBody: R1[ast.ConciseBody] = memo {
    case (pIn) =>
      MATCH ^^^ { x => x }
  }
  lazy val ArrowFormalParameters: P2[ast.ArrowFormalParameters] = memo {
    case (pYield, pAwait) =>
      ((MATCH <~ term("(")) ~ UniqueFormalParameters(pYield, pAwait) <~ term(")")) ~ subArrowFormalParameters(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ArrowFormalParameters0(x0)) } |
        MISMATCH
  }
  lazy val subArrowFormalParameters: R2[ast.ArrowFormalParameters] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AsyncArrowFunction: P3[ast.AsyncArrowFunction] = memo {
    case (pIn, pYield, pAwait) =>
      ((((MATCH <~ term("async")) <~ NoLineTerminator) ~ AsyncArrowBindingIdentifier(pYield) <~ NoLineTerminator) <~ term("=>")) ~ AsyncConciseBody(pIn) ~ subAsyncArrowFunction(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AsyncArrowFunction0(x0, x1)) } |
        ((MATCH ~ CoverCallExpressionAndAsyncArrowHead(pYield, pAwait) <~ NoLineTerminator) <~ term("=>")) ~ AsyncConciseBody(pIn) ~ subAsyncArrowFunction(pIn, pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AsyncArrowFunction1(x0, x1)) } |
        MISMATCH
  }
  lazy val subAsyncArrowFunction: R3[ast.AsyncArrowFunction] = memo {
    case (pIn, pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AsyncConciseBody: P1[ast.AsyncConciseBody] = memo {
    case (pIn) =>
      (MATCH <~ -term("", seq("{"))) ~ AssignmentExpression(pIn, false, true) ~ subAsyncConciseBody(pIn) ^^ { case _ ~ x0 ~ y => y(ast.AsyncConciseBody0(x0)) } |
        ((MATCH <~ term("{")) ~ AsyncFunctionBody <~ term("}")) ~ subAsyncConciseBody(pIn) ^^ { case _ ~ x0 ~ y => y(ast.AsyncConciseBody1(x0)) } |
        MISMATCH
  }
  lazy val subAsyncConciseBody: R1[ast.AsyncConciseBody] = memo {
    case (pIn) =>
      MATCH ^^^ { x => x }
  }
  lazy val AsyncArrowHead: P0[ast.AsyncArrowHead] = {
    ((MATCH <~ term("async")) <~ NoLineTerminator) ~ ArrowFormalParameters(false, true) ~ subAsyncArrowHead ^^ { case _ ~ x0 ~ y => y(ast.AsyncArrowHead0(x0)) } |
      MISMATCH
  }
  lazy val subAsyncArrowHead: R0[ast.AsyncArrowHead] = {
    MATCH ^^^ { x => x }
  }
  lazy val MethodDefinition: P2[ast.MethodDefinition] = memo {
    case (pYield, pAwait) =>
      ((((MATCH ~ PropertyName(pYield, pAwait) <~ term("(")) ~ UniqueFormalParameters(false, false) <~ term(")")) <~ term("{")) ~ FunctionBody(false, false) <~ term("}")) ~ subMethodDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.MethodDefinition0(x0, x1, x2)) } |
        MATCH ~ GeneratorMethod(pYield, pAwait) ~ subMethodDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.MethodDefinition1(x0)) } |
        MATCH ~ AsyncMethod(pYield, pAwait) ~ subMethodDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.MethodDefinition2(x0)) } |
        MATCH ~ AsyncGeneratorMethod(pYield, pAwait) ~ subMethodDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.MethodDefinition3(x0)) } |
        (((((MATCH <~ term("get")) ~ PropertyName(pYield, pAwait) <~ term("(")) <~ term(")")) <~ term("{")) ~ FunctionBody(false, false) <~ term("}")) ~ subMethodDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.MethodDefinition4(x0, x1)) } |
        (((((MATCH <~ term("set")) ~ PropertyName(pYield, pAwait) <~ term("(")) ~ PropertySetParameterList <~ term(")")) <~ term("{")) ~ FunctionBody(false, false) <~ term("}")) ~ subMethodDefinition(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.MethodDefinition5(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subMethodDefinition: R2[ast.MethodDefinition] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val PropertySetParameterList: P0[ast.PropertySetParameterList] = {
    MATCH ~ FormalParameter(false, false) ~ subPropertySetParameterList ^^ { case _ ~ x0 ~ y => y(ast.PropertySetParameterList0(x0)) } |
      MISMATCH
  }
  lazy val subPropertySetParameterList: R0[ast.PropertySetParameterList] = {
    MATCH ^^^ { x => x }
  }
  lazy val GeneratorMethod: P2[ast.GeneratorMethod] = memo {
    case (pYield, pAwait) =>
      (((((MATCH <~ term("*")) ~ PropertyName(pYield, pAwait) <~ term("(")) ~ UniqueFormalParameters(true, false) <~ term(")")) <~ term("{")) ~ GeneratorBody <~ term("}")) ~ subGeneratorMethod(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.GeneratorMethod0(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subGeneratorMethod: R2[ast.GeneratorMethod] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val GeneratorDeclaration: P3[ast.GeneratorDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      ((((((MATCH <~ term("function")) <~ term("*")) ~ BindingIdentifier(pYield, pAwait) <~ term("(")) ~ FormalParameters(true, false) <~ term(")")) <~ term("{")) ~ GeneratorBody <~ term("}")) ~ subGeneratorDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.GeneratorDeclaration0(x0, x1, x2)) } |
        (if (pDefault) ((((((MATCH <~ term("function")) <~ term("*")) <~ term("(")) ~ FormalParameters(true, false) <~ term(")")) <~ term("{")) ~ GeneratorBody <~ term("}")) ~ subGeneratorDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.GeneratorDeclaration1(x0, x1)) } else MISMATCH) |
        MISMATCH
  }
  lazy val subGeneratorDeclaration: R3[ast.GeneratorDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      MATCH ^^^ { x => x }
  }
  lazy val GeneratorExpression: P0[ast.GeneratorExpression] = {
    ((((((MATCH <~ term("function")) <~ term("*")) ~ opt(BindingIdentifier(true, false)) <~ term("(")) ~ FormalParameters(true, false) <~ term(")")) <~ term("{")) ~ GeneratorBody <~ term("}")) ~ subGeneratorExpression ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.GeneratorExpression0(x0, x1, x2)) } |
      MISMATCH
  }
  lazy val subGeneratorExpression: R0[ast.GeneratorExpression] = {
    MATCH ^^^ { x => x }
  }
  lazy val GeneratorBody: P0[ast.GeneratorBody] = {
    MATCH ~ FunctionBody(true, false) ~ subGeneratorBody ^^ { case _ ~ x0 ~ y => y(ast.GeneratorBody0(x0)) } |
      MISMATCH
  }
  lazy val subGeneratorBody: R0[ast.GeneratorBody] = {
    MATCH ^^^ { x => x }
  }
  lazy val YieldExpression: P2[ast.YieldExpression] = memo {
    case (pIn, pAwait) =>
      (MATCH <~ term("yield")) ~ subYieldExpression(pIn, pAwait) ^^ { case _ ~ y => y(ast.YieldExpression0) } |
        ((MATCH <~ term("yield")) <~ NoLineTerminator) ~ AssignmentExpression(pIn, true, pAwait) ~ subYieldExpression(pIn, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.YieldExpression1(x0)) } |
        (((MATCH <~ term("yield")) <~ NoLineTerminator) <~ term("*")) ~ AssignmentExpression(pIn, true, pAwait) ~ subYieldExpression(pIn, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.YieldExpression2(x0)) } |
        MISMATCH
  }
  lazy val subYieldExpression: R2[ast.YieldExpression] = memo {
    case (pIn, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AsyncMethod: P2[ast.AsyncMethod] = memo {
    case (pYield, pAwait) =>
      ((((((MATCH <~ term("async")) <~ NoLineTerminator) ~ PropertyName(pYield, pAwait) <~ term("(")) ~ UniqueFormalParameters(false, true) <~ term(")")) <~ term("{")) ~ AsyncFunctionBody <~ term("}")) ~ subAsyncMethod(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.AsyncMethod0(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subAsyncMethod: R2[ast.AsyncMethod] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AsyncFunctionDeclaration: P3[ast.AsyncFunctionDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      (((((((MATCH <~ term("async")) <~ NoLineTerminator) <~ term("function")) ~ BindingIdentifier(pYield, pAwait) <~ term("(")) ~ FormalParameters(false, true) <~ term(")")) <~ term("{")) ~ AsyncFunctionBody <~ term("}")) ~ subAsyncFunctionDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.AsyncFunctionDeclaration0(x0, x1, x2)) } |
        (if (pDefault) (((((((MATCH <~ term("async")) <~ NoLineTerminator) <~ term("function")) <~ term("(")) ~ FormalParameters(false, true) <~ term(")")) <~ term("{")) ~ AsyncFunctionBody <~ term("}")) ~ subAsyncFunctionDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AsyncFunctionDeclaration1(x0, x1)) } else MISMATCH) |
        MISMATCH
  }
  lazy val subAsyncFunctionDeclaration: R3[ast.AsyncFunctionDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      MATCH ^^^ { x => x }
  }
  lazy val AsyncFunctionExpression: P0[ast.AsyncFunctionExpression] = {
    (((((((MATCH <~ term("async")) <~ NoLineTerminator) <~ term("function")) <~ term("(")) ~ FormalParameters(false, true) <~ term(")")) <~ term("{")) ~ AsyncFunctionBody <~ term("}")) ~ subAsyncFunctionExpression ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AsyncFunctionExpression0(x0, x1)) } |
      (((((((MATCH <~ term("async")) <~ NoLineTerminator) <~ term("function")) ~ BindingIdentifier(false, true) <~ term("(")) ~ FormalParameters(false, true) <~ term(")")) <~ term("{")) ~ AsyncFunctionBody <~ term("}")) ~ subAsyncFunctionExpression ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.AsyncFunctionExpression1(x0, x1, x2)) } |
      MISMATCH
  }
  lazy val subAsyncFunctionExpression: R0[ast.AsyncFunctionExpression] = {
    MATCH ^^^ { x => x }
  }
  lazy val AsyncFunctionBody: P0[ast.AsyncFunctionBody] = {
    MATCH ~ FunctionBody(false, true) ~ subAsyncFunctionBody ^^ { case _ ~ x0 ~ y => y(ast.AsyncFunctionBody0(x0)) } |
      MISMATCH
  }
  lazy val subAsyncFunctionBody: R0[ast.AsyncFunctionBody] = {
    MATCH ^^^ { x => x }
  }
  lazy val AwaitExpression: P1[ast.AwaitExpression] = memo {
    case (pYield) =>
      (MATCH <~ term("await")) ~ UnaryExpression(pYield, true) ~ subAwaitExpression(pYield) ^^ { case _ ~ x0 ~ y => y(ast.AwaitExpression0(x0)) } |
        MISMATCH
  }
  lazy val subAwaitExpression: R1[ast.AwaitExpression] = memo {
    case (pYield) =>
      MATCH ^^^ { x => x }
  }
  lazy val ClassDeclaration: P3[ast.ClassDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      (MATCH <~ term("class")) ~ BindingIdentifier(pYield, pAwait) ~ ClassTail(pYield, pAwait) ~ subClassDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ClassDeclaration0(x0, x1)) } |
        (if (pDefault) (MATCH <~ term("class")) ~ ClassTail(pYield, pAwait) ~ subClassDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ y => y(ast.ClassDeclaration1(x0)) } else MISMATCH) |
        MISMATCH
  }
  lazy val subClassDeclaration: R3[ast.ClassDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      MATCH ^^^ { x => x }
  }
  lazy val ClassExpression: P2[ast.ClassExpression] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("class")) ~ opt(BindingIdentifier(pYield, pAwait)) ~ ClassTail(pYield, pAwait) ~ subClassExpression(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ClassExpression0(x0, x1)) } |
        MISMATCH
  }
  lazy val subClassExpression: R2[ast.ClassExpression] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ClassTail: P2[ast.ClassTail] = memo {
    case (pYield, pAwait) =>
      ((MATCH ~ opt(ClassHeritage(pYield, pAwait)) <~ term("{")) ~ opt(ClassBody(pYield, pAwait)) <~ term("}")) ~ subClassTail(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ClassTail0(x0, x1)) } |
        MISMATCH
  }
  lazy val subClassTail: R2[ast.ClassTail] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ClassHeritage: P2[ast.ClassHeritage] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("extends")) ~ LeftHandSideExpression(pYield, pAwait) ~ subClassHeritage(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ClassHeritage0(x0)) } |
        MISMATCH
  }
  lazy val subClassHeritage: R2[ast.ClassHeritage] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ClassBody: P2[ast.ClassBody] = memo {
    case (pYield, pAwait) =>
      MATCH ~ ClassElementList(pYield, pAwait) ~ subClassBody(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ClassBody0(x0)) } |
        MISMATCH
  }
  lazy val subClassBody: R2[ast.ClassBody] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val ClassElementList: P2[ast.ClassElementList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ ClassElement(pYield, pAwait) ~ subClassElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ClassElementList0(x0)) } |
        MISMATCH
  }
  lazy val subClassElementList: R2[ast.ClassElementList] = memo {
    case (pYield, pAwait) =>
      MATCH ~ ClassElement(pYield, pAwait) ~ subClassElementList(pYield, pAwait) ^^ { case _ ~ x0 ~ y => ((x: ast.ClassElementList) => y(ast.ClassElementList1(x, x0))) } |
        MATCH ^^^ { x => x }
  }
  lazy val ClassElement: P2[ast.ClassElement] = memo {
    case (pYield, pAwait) =>
      MATCH ~ MethodDefinition(pYield, pAwait) ~ subClassElement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ClassElement0(x0)) } |
        (MATCH <~ term("static")) ~ MethodDefinition(pYield, pAwait) ~ subClassElement(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.ClassElement1(x0)) } |
        (MATCH <~ term(";")) ~ subClassElement(pYield, pAwait) ^^ { case _ ~ y => y(ast.ClassElement2) } |
        MISMATCH
  }
  lazy val subClassElement: R2[ast.ClassElement] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val Script: P0[ast.Script] = {
    MATCH ~ opt(ScriptBody) ~ subScript ^^ { case _ ~ x0 ~ y => y(ast.Script0(x0)) } |
      MISMATCH
  }
  lazy val subScript: R0[ast.Script] = {
    MATCH ^^^ { x => x }
  }
  lazy val ScriptBody: P0[ast.ScriptBody] = {
    MATCH ~ StatementList(false, false, false) ~ subScriptBody ^^ { case _ ~ x0 ~ y => y(ast.ScriptBody0(x0)) } |
      MISMATCH
  }
  lazy val subScriptBody: R0[ast.ScriptBody] = {
    MATCH ^^^ { x => x }
  }
  lazy val Module: P0[ast.Module] = {
    MATCH ~ opt(ModuleBody) ~ subModule ^^ { case _ ~ x0 ~ y => y(ast.Module0(x0)) } |
      MISMATCH
  }
  lazy val subModule: R0[ast.Module] = {
    MATCH ^^^ { x => x }
  }
  lazy val ModuleBody: P0[ast.ModuleBody] = {
    MATCH ~ ModuleItemList ~ subModuleBody ^^ { case _ ~ x0 ~ y => y(ast.ModuleBody0(x0)) } |
      MISMATCH
  }
  lazy val subModuleBody: R0[ast.ModuleBody] = {
    MATCH ^^^ { x => x }
  }
  lazy val ModuleItemList: P0[ast.ModuleItemList] = {
    MATCH ~ ModuleItem ~ subModuleItemList ^^ { case _ ~ x0 ~ y => y(ast.ModuleItemList0(x0)) } |
      MISMATCH
  }
  lazy val subModuleItemList: R0[ast.ModuleItemList] = {
    MATCH ~ ModuleItem ~ subModuleItemList ^^ { case _ ~ x0 ~ y => ((x: ast.ModuleItemList) => y(ast.ModuleItemList1(x, x0))) } |
      MATCH ^^^ { x => x }
  }
  lazy val ModuleItem: P0[ast.ModuleItem] = {
    MATCH ~ ImportDeclaration ~ subModuleItem ^^ { case _ ~ x0 ~ y => y(ast.ModuleItem0(x0)) } |
      MATCH ~ ExportDeclaration ~ subModuleItem ^^ { case _ ~ x0 ~ y => y(ast.ModuleItem1(x0)) } |
      MATCH ~ StatementListItem(false, false, false) ~ subModuleItem ^^ { case _ ~ x0 ~ y => y(ast.ModuleItem2(x0)) } |
      MISMATCH
  }
  lazy val subModuleItem: R0[ast.ModuleItem] = {
    MATCH ^^^ { x => x }
  }
  lazy val ImportDeclaration: P0[ast.ImportDeclaration] = {
    ((MATCH <~ term("import")) ~ ImportClause ~ FromClause <~ term(";")) ~ subImportDeclaration ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ImportDeclaration0(x0, x1)) } |
      ((MATCH <~ term("import")) ~ ModuleSpecifier <~ term(";")) ~ subImportDeclaration ^^ { case _ ~ x0 ~ y => y(ast.ImportDeclaration1(x0)) } |
      MISMATCH
  }
  lazy val subImportDeclaration: R0[ast.ImportDeclaration] = {
    MATCH ^^^ { x => x }
  }
  lazy val ImportClause: P0[ast.ImportClause] = {
    MATCH ~ ImportedDefaultBinding ~ subImportClause ^^ { case _ ~ x0 ~ y => y(ast.ImportClause0(x0)) } |
      MATCH ~ NameSpaceImport ~ subImportClause ^^ { case _ ~ x0 ~ y => y(ast.ImportClause1(x0)) } |
      MATCH ~ NamedImports ~ subImportClause ^^ { case _ ~ x0 ~ y => y(ast.ImportClause2(x0)) } |
      (MATCH ~ ImportedDefaultBinding <~ term(",")) ~ NameSpaceImport ~ subImportClause ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ImportClause3(x0, x1)) } |
      (MATCH ~ ImportedDefaultBinding <~ term(",")) ~ NamedImports ~ subImportClause ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ImportClause4(x0, x1)) } |
      MISMATCH
  }
  lazy val subImportClause: R0[ast.ImportClause] = {
    MATCH ^^^ { x => x }
  }
  lazy val ImportedDefaultBinding: P0[ast.ImportedDefaultBinding] = {
    MATCH ~ ImportedBinding ~ subImportedDefaultBinding ^^ { case _ ~ x0 ~ y => y(ast.ImportedDefaultBinding0(x0)) } |
      MISMATCH
  }
  lazy val subImportedDefaultBinding: R0[ast.ImportedDefaultBinding] = {
    MATCH ^^^ { x => x }
  }
  lazy val NameSpaceImport: P0[ast.NameSpaceImport] = {
    ((MATCH <~ term("*")) <~ term("as")) ~ ImportedBinding ~ subNameSpaceImport ^^ { case _ ~ x0 ~ y => y(ast.NameSpaceImport0(x0)) } |
      MISMATCH
  }
  lazy val subNameSpaceImport: R0[ast.NameSpaceImport] = {
    MATCH ^^^ { x => x }
  }
  lazy val NamedImports: P0[ast.NamedImports] = {
    ((MATCH <~ term("{")) <~ term("}")) ~ subNamedImports ^^ { case _ ~ y => y(ast.NamedImports0) } |
      ((MATCH <~ term("{")) ~ ImportsList <~ term("}")) ~ subNamedImports ^^ { case _ ~ x0 ~ y => y(ast.NamedImports1(x0)) } |
      (((MATCH <~ term("{")) ~ ImportsList <~ term(",")) <~ term("}")) ~ subNamedImports ^^ { case _ ~ x0 ~ y => y(ast.NamedImports2(x0)) } |
      MISMATCH
  }
  lazy val subNamedImports: R0[ast.NamedImports] = {
    MATCH ^^^ { x => x }
  }
  lazy val FromClause: P0[ast.FromClause] = {
    (MATCH <~ term("from")) ~ ModuleSpecifier ~ subFromClause ^^ { case _ ~ x0 ~ y => y(ast.FromClause0(x0)) } |
      MISMATCH
  }
  lazy val subFromClause: R0[ast.FromClause] = {
    MATCH ^^^ { x => x }
  }
  lazy val ImportsList: P0[ast.ImportsList] = {
    MATCH ~ ImportSpecifier ~ subImportsList ^^ { case _ ~ x0 ~ y => y(ast.ImportsList0(x0)) } |
      MISMATCH
  }
  lazy val subImportsList: R0[ast.ImportsList] = {
    (MATCH <~ term(",")) ~ ImportSpecifier ~ subImportsList ^^ { case _ ~ x0 ~ y => ((x: ast.ImportsList) => y(ast.ImportsList1(x, x0))) } |
      MATCH ^^^ { x => x }
  }
  lazy val ImportSpecifier: P0[ast.ImportSpecifier] = {
    MATCH ~ ImportedBinding ~ subImportSpecifier ^^ { case _ ~ x0 ~ y => y(ast.ImportSpecifier0(x0)) } |
      (MATCH ~ term("IdentifierName", IdentifierName) <~ term("as")) ~ ImportedBinding ~ subImportSpecifier ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ImportSpecifier1(x0, x1)) } |
      MISMATCH
  }
  lazy val subImportSpecifier: R0[ast.ImportSpecifier] = {
    MATCH ^^^ { x => x }
  }
  lazy val ModuleSpecifier: P0[ast.ModuleSpecifier] = {
    MATCH ~ term("StringLiteral", StringLiteral) ~ subModuleSpecifier ^^ { case _ ~ x0 ~ y => y(ast.ModuleSpecifier0(x0)) } |
      MISMATCH
  }
  lazy val subModuleSpecifier: R0[ast.ModuleSpecifier] = {
    MATCH ^^^ { x => x }
  }
  lazy val ImportedBinding: P0[ast.ImportedBinding] = {
    MATCH ~ BindingIdentifier(false, false) ~ subImportedBinding ^^ { case _ ~ x0 ~ y => y(ast.ImportedBinding0(x0)) } |
      MISMATCH
  }
  lazy val subImportedBinding: R0[ast.ImportedBinding] = {
    MATCH ^^^ { x => x }
  }
  lazy val ExportDeclaration: P0[ast.ExportDeclaration] = {
    (((MATCH <~ term("export")) <~ term("*")) ~ FromClause <~ term(";")) ~ subExportDeclaration ^^ { case _ ~ x0 ~ y => y(ast.ExportDeclaration0(x0)) } |
      ((MATCH <~ term("export")) ~ ExportClause ~ FromClause <~ term(";")) ~ subExportDeclaration ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ExportDeclaration1(x0, x1)) } |
      ((MATCH <~ term("export")) ~ ExportClause <~ term(";")) ~ subExportDeclaration ^^ { case _ ~ x0 ~ y => y(ast.ExportDeclaration2(x0)) } |
      (MATCH <~ term("export")) ~ VariableStatement(false, false) ~ subExportDeclaration ^^ { case _ ~ x0 ~ y => y(ast.ExportDeclaration3(x0)) } |
      (MATCH <~ term("export")) ~ Declaration(false, false) ~ subExportDeclaration ^^ { case _ ~ x0 ~ y => y(ast.ExportDeclaration4(x0)) } |
      ((MATCH <~ term("export")) <~ term("default")) ~ HoistableDeclaration(false, false, true) ~ subExportDeclaration ^^ { case _ ~ x0 ~ y => y(ast.ExportDeclaration5(x0)) } |
      ((MATCH <~ term("export")) <~ term("default")) ~ ClassDeclaration(false, false, true) ~ subExportDeclaration ^^ { case _ ~ x0 ~ y => y(ast.ExportDeclaration6(x0)) } |
      ((((MATCH <~ term("export")) <~ term("default")) <~ -term("", seq("function") | seq("async", strNoLineTerminator, "function") | seq("class"))) ~ AssignmentExpression(true, false, false) <~ term(";")) ~ subExportDeclaration ^^ { case _ ~ x0 ~ y => y(ast.ExportDeclaration7(x0)) } |
      MISMATCH
  }
  lazy val subExportDeclaration: R0[ast.ExportDeclaration] = {
    MATCH ^^^ { x => x }
  }
  lazy val ExportClause: P0[ast.ExportClause] = {
    ((MATCH <~ term("{")) <~ term("}")) ~ subExportClause ^^ { case _ ~ y => y(ast.ExportClause0) } |
      ((MATCH <~ term("{")) ~ ExportsList <~ term("}")) ~ subExportClause ^^ { case _ ~ x0 ~ y => y(ast.ExportClause1(x0)) } |
      (((MATCH <~ term("{")) ~ ExportsList <~ term(",")) <~ term("}")) ~ subExportClause ^^ { case _ ~ x0 ~ y => y(ast.ExportClause2(x0)) } |
      MISMATCH
  }
  lazy val subExportClause: R0[ast.ExportClause] = {
    MATCH ^^^ { x => x }
  }
  lazy val ExportsList: P0[ast.ExportsList] = {
    MATCH ~ ExportSpecifier ~ subExportsList ^^ { case _ ~ x0 ~ y => y(ast.ExportsList0(x0)) } |
      MISMATCH
  }
  lazy val subExportsList: R0[ast.ExportsList] = {
    (MATCH <~ term(",")) ~ ExportSpecifier ~ subExportsList ^^ { case _ ~ x0 ~ y => ((x: ast.ExportsList) => y(ast.ExportsList1(x, x0))) } |
      MATCH ^^^ { x => x }
  }
  lazy val ExportSpecifier: P0[ast.ExportSpecifier] = {
    MATCH ~ term("IdentifierName", IdentifierName) ~ subExportSpecifier ^^ { case _ ~ x0 ~ y => y(ast.ExportSpecifier0(x0)) } |
      (MATCH ~ term("IdentifierName", IdentifierName) <~ term("as")) ~ term("IdentifierName", IdentifierName) ~ subExportSpecifier ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.ExportSpecifier1(x0, x1)) } |
      MISMATCH
  }
  lazy val subExportSpecifier: R0[ast.ExportSpecifier] = {
    MATCH ^^^ { x => x }
  }
  lazy val AsyncGeneratorMethod: P2[ast.AsyncGeneratorMethod] = memo {
    case (pYield, pAwait) =>
      (((((((MATCH <~ term("async")) <~ NoLineTerminator) <~ term("*")) ~ PropertyName(pYield, pAwait) <~ term("(")) ~ UniqueFormalParameters(true, true) <~ term(")")) <~ term("{")) ~ AsyncGeneratorBody <~ term("}")) ~ subAsyncGeneratorMethod(pYield, pAwait) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.AsyncGeneratorMethod0(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subAsyncGeneratorMethod: R2[ast.AsyncGeneratorMethod] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val AsyncGeneratorDeclaration: P3[ast.AsyncGeneratorDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      ((((((((MATCH <~ term("async")) <~ NoLineTerminator) <~ term("function")) <~ term("*")) ~ BindingIdentifier(pYield, pAwait) <~ term("(")) ~ FormalParameters(true, true) <~ term(")")) <~ term("{")) ~ AsyncGeneratorBody <~ term("}")) ~ subAsyncGeneratorDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.AsyncGeneratorDeclaration0(x0, x1, x2)) } |
        (if (pDefault) ((((((((MATCH <~ term("async")) <~ NoLineTerminator) <~ term("function")) <~ term("*")) <~ term("(")) ~ FormalParameters(true, true) <~ term(")")) <~ term("{")) ~ AsyncGeneratorBody <~ term("}")) ~ subAsyncGeneratorDeclaration(pYield, pAwait, pDefault) ^^ { case _ ~ x0 ~ x1 ~ y => y(ast.AsyncGeneratorDeclaration1(x0, x1)) } else MISMATCH) |
        MISMATCH
  }
  lazy val subAsyncGeneratorDeclaration: R3[ast.AsyncGeneratorDeclaration] = memo {
    case (pYield, pAwait, pDefault) =>
      MATCH ^^^ { x => x }
  }
  lazy val AsyncGeneratorExpression: P0[ast.AsyncGeneratorExpression] = {
    ((((((((MATCH <~ term("async")) <~ NoLineTerminator) <~ term("function")) <~ term("*")) ~ opt(BindingIdentifier(true, true)) <~ term("(")) ~ FormalParameters(true, true) <~ term(")")) <~ term("{")) ~ AsyncGeneratorBody <~ term("}")) ~ subAsyncGeneratorExpression ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.AsyncGeneratorExpression0(x0, x1, x2)) } |
      MISMATCH
  }
  lazy val subAsyncGeneratorExpression: R0[ast.AsyncGeneratorExpression] = {
    MATCH ^^^ { x => x }
  }
  lazy val AsyncGeneratorBody: P0[ast.AsyncGeneratorBody] = {
    MATCH ~ FunctionBody(true, true) ~ subAsyncGeneratorBody ^^ { case _ ~ x0 ~ y => y(ast.AsyncGeneratorBody0(x0)) } |
      MISMATCH
  }
  lazy val subAsyncGeneratorBody: R0[ast.AsyncGeneratorBody] = {
    MATCH ^^^ { x => x }
  }
  lazy val AssignmentRestProperty: P2[ast.AssignmentRestProperty] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("...")) ~ DestructuringAssignmentTarget(pYield, pAwait) ~ subAssignmentRestProperty(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.AssignmentRestProperty0(x0)) } |
        MISMATCH
  }
  lazy val subAssignmentRestProperty: R2[ast.AssignmentRestProperty] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
  lazy val SubstitutionTemplate: P3[ast.SubstitutionTemplate] = memo {
    case (pYield, pAwait, pTagged) =>
      MATCH ~ term("TemplateHead", TemplateHead) ~ Expression(true, pYield, pAwait) ~ TemplateSpans(pYield, pAwait, pTagged) ~ subSubstitutionTemplate(pYield, pAwait, pTagged) ^^ { case _ ~ x0 ~ x1 ~ x2 ~ y => y(ast.SubstitutionTemplate0(x0, x1, x2)) } |
        MISMATCH
  }
  lazy val subSubstitutionTemplate: R3[ast.SubstitutionTemplate] = memo {
    case (pYield, pAwait, pTagged) =>
      MATCH ^^^ { x => x }
  }
  lazy val BindingRestProperty: P2[ast.BindingRestProperty] = memo {
    case (pYield, pAwait) =>
      (MATCH <~ term("...")) ~ BindingIdentifier(pYield, pAwait) ~ subBindingRestProperty(pYield, pAwait) ^^ { case _ ~ x0 ~ y => y(ast.BindingRestProperty0(x0)) } |
        MISMATCH
  }
  lazy val subBindingRestProperty: R2[ast.BindingRestProperty] = memo {
    case (pYield, pAwait) =>
      MATCH ^^^ { x => x }
  }
}