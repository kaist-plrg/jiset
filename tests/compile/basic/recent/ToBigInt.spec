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
                Return `1n` if _prim_ is *true* and `0n` if _prim_ is *false*.
              </td>
            </tr>
            <tr>
              <td>
                BigInt
              </td>
              <td>
                Return _prim_.
              </td>
            </tr>
            <tr>
              <td>
                Number
              </td>
              <td>
                Throw a *TypeError* exception.
              </td>
            </tr>
            <tr>
              <td>
                String
              </td>
              <td>
                <emu-alg>
                  1. Let _n_ be ! StringToBigInt(_prim_).
                  1. If _n_ is *NaN*, throw a *SyntaxError* exception.
                  1. Return _n_.
                </emu-alg>
              </td>
            </tr>
            <tr>
              <td>
                Symbol
              </td>
              <td>
                Throw a *TypeError* exception.
              </td>
            </tr>
          </tbody>
        </table>