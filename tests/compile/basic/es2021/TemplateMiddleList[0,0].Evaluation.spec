          1. Let _head_ be the TV of |TemplateMiddle| as defined in <emu-xref href="#sec-template-literal-lexical-components"></emu-xref>.
          1. Let _subRef_ be the result of evaluating |Expression|.
          1. Let _sub_ be ? GetValue(_subRef_).
          1. Let _middle_ be ? ToString(_sub_).
          1. Return the string-concatenation of _head_ and _middle_.