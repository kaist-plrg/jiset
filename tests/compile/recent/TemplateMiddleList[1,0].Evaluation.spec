          1. Let _rest_ be the result of evaluating |TemplateMiddleList|.
          1. ReturnIfAbrupt(_rest_).
          1. Let _middle_ be the TV of |TemplateMiddle| as defined in <emu-xref href="#sec-template-literal-lexical-components"></emu-xref>.
          1. Let _subRef_ be the result of evaluating |Expression|.
          1. Let _sub_ be ? GetValue(_subRef_).
          1. Let _last_ be ? ToString(_sub_).
          1. Return the string-concatenation of _rest_, _middle_, and _last_.