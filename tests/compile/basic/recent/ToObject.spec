        <table>
          <tbody>
          <tr>
            <th>
              Argument Type
            </th>
            <th>
              Result
            </th>
          </tr>
          <tr>
            <td>
              Undefined
            </td>
            <td>
              Throw a *TypeError* exception.
            </td>
          </tr>
          <tr>
            <td>
              Null
            </td>
            <td>
              Throw a *TypeError* exception.
            </td>
          </tr>
          <tr>
            <td>
              Boolean
            </td>
            <td>
              Return a new Boolean object whose [[BooleanData]] internal slot is set to _argument_. See <emu-xref href="#sec-boolean-objects"></emu-xref> for a description of Boolean objects.
            </td>
          </tr>
          <tr>
            <td>
              Number
            </td>
            <td>
              Return a new Number object whose [[NumberData]] internal slot is set to _argument_. See <emu-xref href="#sec-number-objects"></emu-xref> for a description of Number objects.
            </td>
          </tr>
          <tr>
            <td>
              String
            </td>
            <td>
              Return a new String object whose [[StringData]] internal slot is set to _argument_. See <emu-xref href="#sec-string-objects"></emu-xref> for a description of String objects.
            </td>
          </tr>
          <tr>
            <td>
              Symbol
            </td>
            <td>
              Return a new Symbol object whose [[SymbolData]] internal slot is set to _argument_. See <emu-xref href="#sec-symbol-objects"></emu-xref> for a description of Symbol objects.
            </td>
          </tr>
          <tr>
            <td>
              BigInt
            </td>
            <td>
              Return a new BigInt object whose [[BigIntData]] internal slot is set to _argument_. See <emu-xref href="#sec-bigint-objects"></emu-xref> for a description of BigInt objects.
            </td>
          </tr>
          <tr>
            <td>
              Object
            </td>
            <td>
              Return _argument_.
            </td>
          </tr>
          </tbody>
        </table>