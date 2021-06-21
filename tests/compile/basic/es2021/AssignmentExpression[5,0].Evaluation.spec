        1. Let _lref_ be the result of evaluating |LeftHandSideExpression|.
        1. [id="step-assignmentexpression-evaluation-compound-getvalue"] Let _lval_ be ? GetValue(_lref_).
        1. Let _rref_ be the result of evaluating |AssignmentExpression|.
        1. Let _rval_ be ? GetValue(_rref_).
        1. Let _assignmentOpText_ be the source text matched by |AssignmentOperator|.
        1. Let _opText_ be the sequence of Unicode code points associated with _assignmentOpText_ in the following table:
          <figure>
            <table class="lightweight-table">
              <tbody>
                <tr><th> _assignmentOpText_ </th><th> _opText_       </th></tr>
                <tr><td> `**=`              </td><td> `**`           </td></tr>
                <tr><td> `*=`               </td><td> `*`            </td></tr>
                <tr><td> `/=`               </td><td> `/`            </td></tr>
                <tr><td> `%=`               </td><td> `%`            </td></tr>
                <tr><td> `+=`               </td><td> `+`            </td></tr>
                <tr><td> `-=`               </td><td> `-`            </td></tr>
                <tr><td> `<<=`        </td><td> `<<`     </td></tr>
                <tr><td> `>>=`        </td><td> `>>`     </td></tr>
                <tr><td> `>>>=`    </td><td> `>>>` </td></tr>
                <tr><td> `&=`           </td><td> `&`        </td></tr>
                <tr><td> `^=`               </td><td> `^`            </td></tr>
                <tr><td> `|=`               </td><td> `|`            </td></tr>
              </tbody>
            </table>
          </figure>
        1. Let _r_ be ApplyStringOrNumericBinaryOperator(_lval_, _opText_, _rval_).
        1. [id="step-assignmentexpression-evaluation-compound-putvalue"] Perform ? PutValue(_lref_, _r_).
        1. Return _r_.