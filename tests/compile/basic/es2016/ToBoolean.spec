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
              Return *false*.
            </td>
          </tr>
          <tr>
            <td>
              Null
            </td>
            <td>
              Return *false*.
            </td>
          </tr>
          <tr>
            <td>
              Boolean
            </td>
            <td>
              Return _argument_.
            </td>
          </tr>
          <tr>
            <td>
              Number
            </td>
            <td>
              Return *false* if _argument_ is *+0*, *-0*, or *NaN*; otherwise return *true*.
            </td>
          </tr>
          <tr>
            <td>
              String
            </td>
            <td>
              Return *false* if _argument_ is the empty String (its length is zero); otherwise return *true*.
            </td>
          </tr>
          <tr>
            <td>
              Symbol
            </td>
            <td>
              Return *true*.
            </td>
          </tr>
          <tr>
            <td>
              Object
            </td>
            <td>
              Return *true*.
            </td>
          </tr>
          </tbody>
        </table>