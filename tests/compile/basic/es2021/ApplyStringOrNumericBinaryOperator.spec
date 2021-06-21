        1. Assert: _opText_ is present in the table in step <emu-xref href="#step-applystringornumericbinaryoperator-operations-table"></emu-xref>.
        1. If _opText_ is `+`, then
          1. [id="step-binary-op-toprimitive-lval"] Let _lprim_ be ? ToPrimitive(_lval_).
          1. [id="step-binary-op-toprimitive-rval"] Let _rprim_ be ? ToPrimitive(_rval_).
          1. [id="step-binary-op-string-check"] If Type(_lprim_) is String or Type(_rprim_) is String, then
            1. Let _lstr_ be ? ToString(_lprim_).
            1. Let _rstr_ be ? ToString(_rprim_).
            1. Return the string-concatenation of _lstr_ and _rstr_.
          1. Set _lval_ to _lprim_.
          1. Set _rval_ to _rprim_.
        1. NOTE: At this point, it must be a numeric operation.
        1. Let _lnum_ be ? ToNumeric(_lval_).
        1. Let _rnum_ be ? ToNumeric(_rval_).
        1. If Type(_lnum_) is different from Type(_rnum_), throw a *TypeError* exception.
        1. Let _T_ be Type(_lnum_).
        1. [id="step-applystringornumericbinaryoperator-operations-table"] Let _operation_ be the abstract operation associated with _opText_ in the following table:
          <figure>
            <table class="lightweight-table">
              <tbody>
                <tr><th> _opText_       </th><th> _operation_             </th></tr>
                <tr><td> `**`           </td><td> _T_::exponentiate       </td></tr>
                <tr><td> `*`            </td><td> _T_::multiply           </td></tr>
                <tr><td> `/`            </td><td> _T_::divide             </td></tr>
                <tr><td> `%`            </td><td> _T_::remainder          </td></tr>
                <tr><td> `+`            </td><td> _T_::add                </td></tr>
                <tr><td> `-`            </td><td> _T_::subtract           </td></tr>
                <tr><td> `<<`     </td><td> _T_::leftShift          </td></tr>
                <tr><td> `>>`     </td><td> _T_::signedRightShift   </td></tr>
                <tr><td> `>>>` </td><td> _T_::unsignedRightShift </td></tr>
                <tr><td> `&`        </td><td> _T_::bitwiseAND         </td></tr>
                <tr><td> `^`            </td><td> _T_::bitwiseXOR         </td></tr>
                <tr><td> `|`            </td><td> _T_::bitwiseOR          </td></tr>
              </tbody>
            </table>
          </figure>
        1. Return ? _operation_(_lnum_, _rnum_).