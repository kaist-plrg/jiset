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
              Return *"undefined"*.
            </td>
          </tr>
          <tr>
            <td>
              Null
            </td>
            <td>
              Return *"null"*.
            </td>
          </tr>
          <tr>
            <td>
              Boolean
            </td>
            <td>
              <p>If _argument_ is *true*, return *"true"*.</p>
              <p>If _argument_ is *false*, return *"false"*.</p>
            </td>
          </tr>
          <tr>
            <td>
              Number
            </td>
            <td>
              Return ! Number::toString(_argument_).
            </td>
          </tr>
          <tr>
            <td>
              String
            </td>
            <td>
              Return _argument_.
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
          <tr>
            <td>
              BigInt
            </td>
            <td>
              Return ! BigInt::toString(_argument_).
            </td>
          </tr>
          <tr>
            <td>
              Object
            </td>
            <td>
              <p>Apply the following steps:</p>
              <emu-alg>
                1. Let _primValue_ be ? ToPrimitive(_argument_, hint String).
                1. Return ? ToString(_primValue_).
              </emu-alg>
            </td>
          </tr>
          </tbody>
        </table>