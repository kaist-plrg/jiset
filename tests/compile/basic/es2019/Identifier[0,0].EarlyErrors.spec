        <li>
          It is a Syntax Error if this phrase is contained in strict mode code and the StringValue of |IdentifierName| is: `"implements"`, `"interface"`, `"let"`, `"package"`, `"private"`, `"protected"`, `"public"`, `"static"`, or `"yield"`.
        </li>
        <li>
          It is a Syntax Error if the goal symbol of the syntactic grammar is |Module| and the StringValue of |IdentifierName| is `"await"`.
        </li>
        <li>
          It is a Syntax Error if StringValue of |IdentifierName| is the same String value as the StringValue of any |ReservedWord| except for `yield` or `await`.
        </li>