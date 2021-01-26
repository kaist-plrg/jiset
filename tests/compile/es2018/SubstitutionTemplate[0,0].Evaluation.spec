          1. Let _head_ be the TV of |TemplateHead| as defined in <emu-xref href="#sec-template-literal-lexical-components"></emu-xref>.
          1. Let _sub_ be the result of evaluating |Expression|.
          1. ReturnIfAbrupt(_sub_).
          1. Let _middle_ be ? ToString(_sub_).
          1. Let _tail_ be the result of evaluating |TemplateSpans|.
          1. ReturnIfAbrupt(_tail_).
          1. Return the string-concatenation of _head_, _middle_, and _tail_.