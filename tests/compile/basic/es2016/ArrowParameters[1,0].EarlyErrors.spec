        <li>
          If the <sub>[Yield]</sub> grammar parameter is present on |ArrowParameters|, it is a Syntax Error if the lexical token sequence matched by |CoverParenthesizedExpressionAndArrowParameterList[?Yield]| cannot be parsed with no tokens left over using |ArrowFormalParameters[Yield]| as the goal symbol.
        </li>
        <li>
          If the <sub>[Yield]</sub> grammar parameter is not present on |ArrowParameters|, it is a Syntax Error if the lexical token sequence matched by |CoverParenthesizedExpressionAndArrowParameterList[?Yield]| cannot be parsed with no tokens left over using |ArrowFormalParameters| as the goal symbol.
        </li>
        <li>
          All early errors rules for |ArrowFormalParameters| and its derived productions also apply to CoveredFormalsList of |CoverParenthesizedExpressionAndArrowParameterList[?Yield]|.
        </li>