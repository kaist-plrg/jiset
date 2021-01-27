          1. Let _ps_ be SourceText of |UnicodePropertyName|.
          1. Let _p_ be ! UnicodeMatchProperty(_ps_).
          1. Assert: _p_ is a Unicode property name or property alias listed in the “Property name and aliases” column of <emu-xref href="#table-nonbinary-unicode-properties"></emu-xref>.
          1. Let _vs_ be SourceText of |UnicodePropertyValue|.
          1. Let _v_ be ! UnicodeMatchPropertyValue(_p_, _vs_).
          1. Return the CharSet containing all Unicode code points whose character database definition includes the property _p_ with value _v_.