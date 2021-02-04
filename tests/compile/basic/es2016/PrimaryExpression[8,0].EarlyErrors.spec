          <li>
            It is a Syntax Error if BodyText of |RegularExpressionLiteral| cannot be recognized using the goal symbol |Pattern| of the ECMAScript RegExp grammar specified in <emu-xref href="#sec-patterns"></emu-xref>.
          </li>
          <li>
            It is a Syntax Error if FlagText of |RegularExpressionLiteral| contains any code points other than `"g"`, `"i"`, `"m"`, `"u"`, or `"y"`, or if it contains the same code point more than once.
          </li>