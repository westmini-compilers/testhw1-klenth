lexer grammar HW02P03;

/* Your lexer rules go here. In ANTLR, names of lexer rules are always UPPERCASE. */

/* If you are using IDEA, the lexer code will placed in the "gen" directory when generated.
   (Right-click on file, "Generate ANTLR Recognizer," or Ctrl+Shift+G) */

fragment LETTER
    : [a-zA-Z]
    ;

WORD
    : LETTER+
    | LETTER (LETTER | '\'')* LETTER
    ;

HYPHEN
    : '-'
    ;

SQUOTE
    : '\''
    ;

DQUOTE
    : '"'
    ;

SENTENCE_END
    : [.?!‽]
    ;

ORDINAL
    : [0-9]* ('0th' | '1st' | '2nd' | '3rd' | '4th' | '5th' | '6th' | '7th' | '8th' | '9th')
    ;

CARDINAL
    : [0-9]+
    ;

SPACE
    : [ \r\n\t]+
    ;
