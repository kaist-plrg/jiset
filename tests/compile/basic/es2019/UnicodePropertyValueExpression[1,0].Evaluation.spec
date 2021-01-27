          1. Let _s_ be SourceText of |LoneUnicodePropertyNameOrValue|.
          1. If ! UnicodeMatchPropertyValue(`"General_Category"`, _s_) is identical to a List of Unicode code points that is the name of a Unicode general category or general category alias listed in the “Property value and aliases” column of <emu-xref href="#table-unicode-general-category-values"></emu-xref>, then
            1. Return the CharSet containing all Unicode code points whose character database definition includes the property “General_Category” with value _s_.
          1. Let _p_ be ! UnicodeMatchProperty(_s_).
          1. Assert: _p_ is a binary Unicode property or binary property alias listed in the “Property name and aliases” column of <emu-xref href="#table-binary-unicode-properties"></emu-xref>.
          1. Return the CharSet containing all Unicode code points whose character database definition includes the property _p_ with value “True”.