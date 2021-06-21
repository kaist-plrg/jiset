          1. Let _head_ be the result of evaluating |TemplateMiddleList|.
          1. ReturnIfAbrupt(_head_).
          1. Let _tail_ be the TV of |TemplateTail| as defined in <emu-xref href="#sec-template-literal-lexical-components"></emu-xref>.
          1. Return the string-concatenation of _head_ and _tail_.