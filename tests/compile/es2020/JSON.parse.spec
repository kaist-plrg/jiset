        1. Let _jsonString_ be ? ToString(_text_).
        1. Parse ! UTF16DecodeString(_jsonString_) as a JSON text as specified in ECMA-404. Throw a *SyntaxError* exception if it is not a valid JSON text as defined in that specification.
        1. Let _scriptString_ be the string-concatenation of *"("*, _jsonString_, and *");"*.
        1. Let _completion_ be the result of parsing and evaluating ! UTF16DecodeString(_scriptString_) as if it was the source text of an ECMAScript |Script|. The extended PropertyDefinitionEvaluation semantics defined in <emu-xref href="#sec-__proto__-property-names-in-object-initializers"></emu-xref> must not be used during the evaluation.
        1. Let _unfiltered_ be _completion_.[[Value]].
        1. Assert: _unfiltered_ is either a String, Number, Boolean, Null, or an Object that is defined by either an |ArrayLiteral| or an |ObjectLiteral|.
        1. If IsCallable(_reviver_) is *true*, then
          1. Let _root_ be OrdinaryObjectCreate(%Object.prototype%).
          1. Let _rootName_ be the empty String.
          1. Perform ! CreateDataPropertyOrThrow(_root_, _rootName_, _unfiltered_).
          1. Return ? InternalizeJSONProperty(_root_, _rootName_, _reviver_).
        1. Else,
          1. Return _unfiltered_.