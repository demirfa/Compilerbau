package de.thm.mni.compilerbau.phases._01_scanner;

import de.thm.mni.compilerbau.utils.SplError;
import de.thm.mni.compilerbau.phases._02_03_parser.Sym;
import de.thm.mni.compilerbau.absyn.Position;
import de.thm.mni.compilerbau.table.Identifier;
import de.thm.mni.compilerbau.CommandLineOptions;
import java_cup.runtime.*;

%%


%class Scanner
%public
%line
%column
%cup
%eofval{
    return new java_cup.runtime.Symbol(Sym.EOF, yyline + 1, yycolumn + 1);   //This needs to be specified when using a custom sym class name
%eofval}

%{
    public CommandLineOptions options = null;
  
    private Symbol symbol(int type) {
      return new Symbol(type, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int type, Object value) {
      return new Symbol(type, yyline + 1, yycolumn + 1, value);
    }
%}

  L = [A-Za-z_]
  D = [0-9]
  H = [0-9A-Fa-f]
  ID = {L}({L}|{D})*
  DECNUM = {D}+
  HEXNUM = 0x{H}+

%%

// TODO (assignment 1): The regular expressions for all tokens need to be defined here.

proc {return symbol(Sym.PROC); }
var {return symbol(Sym.VAR); }
array { return symbol(Sym.ARRAY);}
else {return symbol(Sym.ELSE); }
if {return symbol(Sym.IF); }
of {return symbol(Sym.OF); }
ref {return symbol(Sym.REF); }
type {return symbol(Sym.TYPE); }
while {return symbol(Sym.WHILE); }
\( {return symbol (Sym.LPAREN); }
\) {return symbol (Sym.RPAREN); }
\{ {return symbol(Sym.LCURL); }
\: {return symbol(Sym.COLON); }
\, { return symbol(Sym.COMMA); }
\<  {return symbol(Sym.LT); }
\> {return symbol(Sym.GT); }
\<= {return symbol(Sym.LE); }
\>= {return symbol(Sym.GE); }
\[ {return symbol(Sym.LBRACK); }
\] {return symbol(Sym.RBRACK); }
\* {return symbol(Sym.STAR); }
\- {return symbol(Sym.MINUS); }
\/ {return symbol(Sym.SLASH); }
\#  {return symbol(Sym.NE); }
\=  {return symbol(Sym.EQ); }
\; {return symbol(Sym.SEMIC); }
\:= {return symbol(Sym.ASGN); }
{D}+ {return symbol(Sym.INTLIT,Integer.parseInt(yytext())); }
{HEXNUM} {return symbol(Sym.INTLIT, Integer.parseInt(yytext().substring(2),16));}

\+ {return symbol(Sym.PLUS); }
\} {return symbol(Sym.RCURL); }
\/\/.* {}
'.' {return symbol(Sym.INTLIT, (int)yytext().charAt(1));}
'\\n' {return symbol(Sym.INTLIT,10);}
[ \t\n\r] { /* fuer SPACE, TAB und NEWLINE ist nichts zu tun */ }
{ID} {return symbol(Sym.IDENT,new Identifier(yytext())); }

[^]		{throw SplError.IllegalCharacter(new Position(yyline + 1, yycolumn + 1), yytext().charAt(0));}

