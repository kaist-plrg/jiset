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
              Return *NaN*.
            </td>
          </tr>
          <tr>
            <td>
              Null
            </td>
            <td>
              Return *+0*.
            </td>
          </tr>
          <tr>
            <td>
              Boolean
            </td>
            <td>
              Return 1 if _argument_ is *true*. Return *+0* if _argument_ is *false*.
            </td>
          </tr>
          <tr>
            <td>
              Number
            </td>
            <td>
              Return _argument_ (no conversion).
            </td>
          </tr>
          <tr>
            <td>
              String
            </td>
            <td>
              See grammar and conversion algorithm below.
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
              Object
            </td>
            <td>
              <p>Apply the following steps:</p>
              <emu-alg>
                1. Let _primValue_ be ? ToPrimitive(_argument_, hint Number).
                1. Return ? ToNumber(_primValue_).
              </emu-alg>
            </td>
          </tr>
          </tbody>
        </table>