          1. Let _product_ be code unit 0x0022 (QUOTATION MARK).
          1. For each code unit _C_ in _value_
            1. If _C_ is 0x0022 (QUOTATION MARK) or 0x005C (REVERSE SOLIDUS), then
              1. Let _product_ be the concatenation of _product_ and code unit 0x005C (REVERSE SOLIDUS).
              1. Let _product_ be the concatenation of _product_ and _C_.
            1. Else if _C_ is 0x0008 (BACKSPACE), 0x000C (FORM FEED), 0x000A (LINE FEED), 0x000D (CARRIAGE RETURN), or 0x0009 (CHARACTER TABULATION), then
              1. Let _product_ be the concatenation of _product_ and code unit 0x005C (REVERSE SOLIDUS).
              1. Let _abbrev_ be the String value corresponding to the value of _C_ as follows:
                <table class="lightweight">
                  <tbody>
                  <tr>
                    <td>
                      BACKSPACE
                    </td>
                    <td>
                      `"b"`
                    </td>
                  </tr>
                  <tr>
                    <td>
                      FORM FEED (FF)
                    </td>
                    <td>
                      `"f"`
                    </td>
                  </tr>
                  <tr>
                    <td>
                      LINE FEED (LF)
                    </td>
                    <td>
                      `"n"`
                    </td>
                  </tr>
                  <tr>
                    <td>
                      CARRIAGE RETURN (CR)
                    </td>
                    <td>
                      `"r"`
                    </td>
                  </tr>
                  <tr>
                    <td>
                      CHARACTER TABULATION
                    </td>
                    <td>
                      `"t"`
                    </td>
                  </tr>
                  </tbody>
                </table>
              1. Let _product_ be the concatenation of _product_ and _abbrev_.
            1. Else if _C_ has a code unit value less than 0x0020 (SPACE), then
              1. Let _product_ be the concatenation of _product_ and code unit 0x005C (REVERSE SOLIDUS).
              1. Let _product_ be the concatenation of _product_ and `"u"`.
              1. Let _hex_ be the string result of converting the numeric code unit value of _C_ to a String of four hexadecimal digits. Alphabetic hexadecimal digits are presented as lowercase Latin letters.
              1. Let _product_ be the concatenation of _product_ and _hex_.
            1. Else,
              1. Let _product_ be the concatenation of _product_ and _C_.
          1. Let _product_ be the concatenation of _product_ and code unit 0x0022 (QUOTATION MARK).
          1. Return _product_.