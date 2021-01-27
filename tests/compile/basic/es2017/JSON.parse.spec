        1. Let _JText_ be ? ToString(_text_).
        1. Parse _JText_ interpreted as UTF-16 encoded Unicode points (<emu-xref href="#sec-ecmascript-language-types-string-type"></emu-xref>) as a JSON text as specified in ECMA-404. Throw a *SyntaxError* exception if _JText_ is not a valid JSON text as defined in that specification.
        1. Let _scriptText_ be the result of concatenating `"("`, _JText_, and `");"`.
        1. Let _completion_ be the result of parsing and evaluating _scriptText_ as if it was the source text of an ECMAScript |Script|, but using the alternative definition of |DoubleStringCharacter| provided below. The extended PropertyDefinitionEvaluation semantics defined in <emu-xref href="#sec-__proto__-property-names-in-object-initializers"></emu-xref> must not be used during the evaluation.
        1. Let _unfiltered_ be _completion_.[[Value]].
        1. Assert: _unfiltered_ is either a String, Number, Boolean, Null, or an Object that is defined by either an |ArrayLiteral| or an |ObjectLiteral|.
        1. If IsCallable(_reviver_) is *true*, then
          1. Let _root_ be ObjectCreate(%ObjectPrototype%).
          1. Let _rootName_ be the empty String.
          1. Let _status_ be CreateDataProperty(_root_, _rootName_, _unfiltered_).
          1. Assert: _status_ is *true*.
          1. Return ? InternalizeJSONProperty(_root_, _rootName_).
        1. Else,
          1. Return _unfiltered_.