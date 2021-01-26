          1. Let _O_ be ? RequireObjectCoercible(*this* value).
          1. Let _S_ be ? ToString(_O_).
          1. If _form_ is not present or _form_ is *undefined*, set _form_ to `"NFC"`.
          1. Let _f_ be ? ToString(_form_).
          1. If _f_ is not one of `"NFC"`, `"NFD"`, `"NFKC"`, or `"NFKD"`, throw a *RangeError* exception.
          1. Let _ns_ be the String value that is the result of normalizing _S_ into the normalization form named by _f_ as specified in <a href="https://unicode.org/reports/tr15/">https://unicode.org/reports/tr15/</a>.
          1. Return _ns_.