        <li>
          It is a Syntax Error if the lexical token sequence matched by |CoverParenthesizedExpressionAndArrowParameterList| cannot be parsed with no tokens left over using |ArrowFormalParameters| as the goal symbol with its <sub>[Yield]</sub> and <sub>[Await]</sub> parameters set to the values used when parsing this |CoverParenthesizedExpressionAndArrowParameterList|.
        </li>
        <li>
          All early error rules for |ArrowFormalParameters| and its derived productions also apply to CoveredFormalsList of |CoverParenthesizedExpressionAndArrowParameterList|.
        </li>