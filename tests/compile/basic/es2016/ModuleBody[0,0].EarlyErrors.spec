          <li>
            It is a Syntax Error if the LexicallyDeclaredNames of |ModuleItemList| contains any duplicate entries.
          </li>
          <li>
            It is a Syntax Error if any element of the LexicallyDeclaredNames of |ModuleItemList| also occurs in the VarDeclaredNames of |ModuleItemList|.
          </li>
          <li>
            It is a Syntax Error if the ExportedNames of |ModuleItemList| contains any duplicate entries.
          </li>
          <li>
            It is a Syntax Error if any element of the ExportedBindings of |ModuleItemList| does not also occur in either the VarDeclaredNames of |ModuleItemList|, or the LexicallyDeclaredNames of |ModuleItemList|.
          </li>
          <li>
            It is a Syntax Error if |ModuleItemList| contains `super`.
          </li>
          <li>
            It is a Syntax Error if |ModuleItemList| contains |NewTarget|.
          </li>
          <li>
            It is a Syntax Error if ContainsDuplicateLabels of |ModuleItemList| with argument « » is *true*.
          </li>
          <li>
            It is a Syntax Error if ContainsUndefinedBreakTarget of |ModuleItemList| with argument « » is *true*.
          </li>
          <li>
            It is a Syntax Error if ContainsUndefinedContinueTarget of |ModuleItemList| with arguments « » and « » is *true*.
          </li>