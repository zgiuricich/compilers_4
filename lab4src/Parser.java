//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 6 "cminus.y"
import java.io.*;
import java.util.*;
//#line 20 "Parser.java"




public class Parser
             implements ParserTokens
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    2,    0,    1,    1,    3,    3,    4,    4,    6,    6,
    7,   10,    5,    8,    8,    8,   11,   11,   12,   12,
   13,    9,   14,   14,   15,   15,   16,   16,   16,   16,
   16,   16,   16,   16,   16,   23,   17,   17,   26,   27,
   28,   18,   19,   21,   22,   20,   20,   25,   25,   30,
   30,   30,   30,   30,   30,   29,   29,   31,   31,   32,
   32,   33,   33,   34,   34,   34,   34,   34,   24,   35,
   35,   36,   36,
};
final static short yylen[] = {                            2,
    0,    2,    2,    1,    1,    1,    3,    6,    1,    1,
    0,    0,    8,    1,    1,    0,    3,    1,    2,    4,
    0,    5,    2,    0,    2,    0,    1,    1,    1,    1,
    1,    1,    1,    1,    1,    2,    4,    7,    0,    0,
    0,   10,    5,    5,    6,    2,    3,    3,    1,    1,
    1,    1,    1,    1,    1,    3,    1,    1,    1,    3,
    1,    1,    1,    3,    1,    4,    1,    1,    4,    1,
    0,    3,    1,
};
final static short yydefred[] = {                         1,
    0,    0,   10,    9,    0,    4,    5,    6,    0,    3,
    0,    7,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   18,    8,    0,   12,    0,    0,   21,   17,   20,
   13,    0,   24,    0,   23,    0,    0,    0,    0,   35,
   22,    0,    0,    0,    0,   28,   25,   27,   29,   30,
   31,   32,   33,   34,    0,    0,    0,    0,    0,    0,
    0,   68,   46,    0,   67,    0,    0,    0,   61,    0,
   36,    0,    0,   73,    0,    0,    0,    0,    0,    0,
    0,   47,   58,   59,   51,   52,   50,   53,   54,   55,
    0,    0,   62,   63,    0,    0,    0,   37,   69,    0,
    0,    0,   39,    0,   64,    0,    0,   60,    0,    0,
   72,    0,   44,    0,   66,   43,   45,    0,   40,   38,
    0,   41,    0,   42,
};
final static short yydgoto[] = {                          1,
    5,    2,    6,    7,    8,    9,   14,   20,   46,   28,
   21,   22,   32,   34,   37,   47,   48,   49,   50,   51,
   52,   53,   54,   65,   66,  114,  121,  123,   67,   91,
   92,   68,   95,   69,   75,   76,
};
final static short yysindex[] = {                         0,
    0, -144,    0,    0, -144,    0,    0,    0, -249,    0,
 -223,    0, -247, -234, -231, -131, -240,    0, -215, -208,
 -193,    0,    0, -194,    0, -144, -189,    0,    0,    0,
    0, -188,    0, -144,    0, -173, -244, -223, -165,    0,
    0, -178, -169, -134, -162,    0,    0,    0,    0,    0,
    0,    0,    0,    0, -155, -230, -132, -132, -132, -132,
 -197,    0,    0, -132,    0, -148, -157, -126,    0, -132,
    0, -135, -127,    0, -130, -124, -120, -122, -117, -132,
 -116,    0,    0,    0,    0,    0,    0,    0,    0,    0,
 -132, -132,    0,    0, -132, -115, -114,    0,    0, -132,
 -121, -108,    0, -113,    0, -255, -126,    0, -237, -107,
    0, -132,    0, -237,    0,    0,    0, -105,    0,    0,
 -125,    0, -237,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,  158,    0,    0,    0,    0,    0,
 -106,    0,    0,    0,    0, -104,    0, -243,    0,    0,
 -103,    0,    0, -211,    0,    0,    0,    0,    0,    0,
    0,    0,    0, -251,    0,    0, -109,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0, -102,    0,    0,    0,
 -212,    0,    0,    0,    0,    0, -129, -186,    0,    0,
    0,    0,    0,    0,    0, -101,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0, -123, -167,    0, -109,    0,
    0,    0,    0, -109,    0,    0,    0,    0,    0,    0,
    0,    0, -109,    0,
};
final static short yygindex[] = {                         0,
    0,    0,  156,  132,    0,   -4,    0,    0,  139,    0,
    0,  142,    0,    0,    0,  -40,    0,    0,    0,    0,
    0,    0,    0,  -37,  -55,    0,    0,    0,   78,    0,
    0,   79,    0,   75,    0,    0,
};
final static int YYTABLESIZE=171;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         55,
   73,   74,   77,   78,   79,   26,   83,   11,   81,   26,
   15,   19,   39,   10,   96,   84,   40,   26,   26,   39,
   23,   19,   15,   40,  104,   41,   61,   62,   26,   36,
   16,   26,   26,   26,   64,   42,   17,   12,   43,   44,
   45,   24,   42,   13,  111,   43,   44,   45,   65,   65,
   72,   65,   19,   65,   19,   65,  118,   25,   65,   65,
   65,   65,   65,   65,   65,   65,   65,   57,  116,   80,
   26,   55,   27,  119,   57,   57,   55,   57,   30,   57,
   33,   57,  124,   38,   57,   55,   59,   57,   57,   57,
   57,   57,   57,   56,   56,   60,   56,   56,   56,   57,
   56,   58,   70,   56,   83,   71,   56,   56,   56,   56,
   56,   56,   82,   84,    3,    4,   85,   86,   87,   88,
   89,   90,   61,   62,   61,   62,   63,   18,    4,   97,
   64,   49,   64,   98,   49,   99,   49,   48,   49,  100,
   48,  112,   48,  102,   48,   93,   94,  101,  103,  105,
  109,  110,  113,  117,  115,  120,  122,    2,   11,   21,
   10,   16,   14,   71,   70,   35,   31,   29,  106,  108,
  107,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         37,
   56,   57,   58,   59,   60,  257,  262,  257,   64,  261,
  258,   16,  257,  257,   70,  271,  261,  269,  270,  257,
  261,   26,  266,  261,   80,  270,  257,  258,  280,   34,
  265,  283,  284,  285,  265,  280,  268,  261,  283,  284,
  285,  257,  280,  267,  100,  283,  284,  285,  261,  262,
  281,  264,  264,  266,  266,  268,  112,  266,  271,  272,
  273,  274,  275,  276,  277,  278,  279,  265,  109,  267,
  264,  109,  267,  114,  261,  262,  114,  264,  268,  266,
  269,  268,  123,  257,  271,  123,  265,  274,  275,  276,
  277,  278,  279,  261,  262,  265,  264,  263,  266,  265,
  268,  267,  265,  271,  262,  261,  274,  275,  276,  277,
  278,  279,  261,  271,  259,  260,  274,  275,  276,  277,
  278,  279,  257,  258,  257,  258,  261,  259,  260,  265,
  265,  261,  265,  261,  264,  266,  266,  261,  268,  264,
  264,  263,  266,  266,  268,  272,  273,  268,  266,  266,
  266,  266,  261,  261,  268,  261,  282,    0,  265,  269,
    5,  266,  266,  266,  266,   34,   28,   26,   91,   95,
   92,
};
}
final static short YYFINAL=1;
final static short YYMAXTOKEN=286;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,"ID","NUM","VOID","INT","SEMI","PLUS","ASSIGN","COMMA","LPAREN",
"RPAREN","LBRACKET","RBRACKET","LBRACE","RBRACE","MINUS","MULT","DIVIDE","LT",
"GT","LTE","GTE","EQ","NOTEQ","PRINT","INPUT","ELSE","IF","RETURN","WHILE",
"ERROR",
};
final static String yyrule[] = {
"$accept : program",
"$$1 :",
"program : $$1 declaration_list",
"declaration_list : declaration_list declaration",
"declaration_list : declaration",
"declaration : var_declaration",
"declaration : fun_declaration",
"var_declaration : type_specifier ID SEMI",
"var_declaration : type_specifier ID LBRACKET NUM RBRACKET SEMI",
"type_specifier : INT",
"type_specifier : VOID",
"$$2 :",
"$$3 :",
"fun_declaration : type_specifier ID $$2 LPAREN params RPAREN $$3 compound_stmt",
"params : param_list",
"params : VOID",
"params :",
"param_list : param_list COMMA param",
"param_list : param",
"param : type_specifier ID",
"param : type_specifier ID LBRACKET RBRACKET",
"$$4 :",
"compound_stmt : $$4 LBRACE local_declarations statement_list RBRACE",
"local_declarations : local_declarations var_declaration",
"local_declarations :",
"statement_list : statement_list statement",
"statement_list :",
"statement : assign_stmt",
"statement : compound_stmt",
"statement : selection_stmt",
"statement : iteration_stmt",
"statement : return_stmt",
"statement : print_stmt",
"statement : input_stmt",
"statement : call_stmt",
"statement : SEMI",
"call_stmt : call SEMI",
"assign_stmt : ID ASSIGN expression SEMI",
"assign_stmt : ID LBRACKET expression RBRACKET ASSIGN expression SEMI",
"$$5 :",
"$$6 :",
"$$7 :",
"selection_stmt : IF LPAREN expression RPAREN $$5 statement $$6 ELSE $$7 statement",
"iteration_stmt : WHILE LPAREN expression RPAREN statement",
"print_stmt : PRINT LPAREN expression RPAREN SEMI",
"input_stmt : ID ASSIGN INPUT LPAREN RPAREN SEMI",
"return_stmt : RETURN SEMI",
"return_stmt : RETURN expression SEMI",
"expression : additive_expression relop additive_expression",
"expression : additive_expression",
"relop : LTE",
"relop : LT",
"relop : GT",
"relop : GTE",
"relop : EQ",
"relop : NOTEQ",
"additive_expression : additive_expression addop term",
"additive_expression : term",
"addop : PLUS",
"addop : MINUS",
"term : term mulop factor",
"term : factor",
"mulop : MULT",
"mulop : DIVIDE",
"factor : LPAREN expression RPAREN",
"factor : ID",
"factor : ID LBRACKET expression RBRACKET",
"factor : call",
"factor : NUM",
"call : ID LPAREN args RPAREN",
"args : arg_list",
"args :",
"arg_list : arg_list COMMA expression",
"arg_list : expression",
};

//#line 540 "cminus.y"

/* reference to the lexer object */
private static Yylex lexer;

/* The symbol table */
public final SymTab<SymTabRec> symtab = new SymTab<SymTabRec>();

/* To check if main has been encountered and is the last declaration */
private boolean seenMain = false;

/* To take care of nuance associated with params and decls in compound stsmt */
private boolean firstTime = true;

/* To gen boilerplate code for read only if input was encountered  */
private boolean usesRead = false;

/* Interface to the lexer */
private int yylex()
{
    int retVal = -1;
    try
	{
		retVal = lexer.yylex();
    }
	catch (IOException e)
	{
		System.err.println("IO Error:" + e);
    }
    return retVal;
}
	
/* error reporting */
public void yyerror (String error)
{
    System.err.println("Parse Error : " + error + " at line " + 
		lexer.getLine() + " column " + 
		lexer.getCol() + ". Got: " + lexer.yytext());
}

/* For semantic errors */
public void semerror (String error)
{
    System.err.println("Semantic Error : " + error + " at line " + 
		lexer.getLine() + " column " + 
		lexer.getCol());
}

/* constructor taking in File Input */
public Parser (Reader r)
{
	lexer = new Yylex(r, this);
}

/* This is how to invoke the parser

public static void main (String [] args) throws IOException
{
	Parser yyparser = new Parser(new FileReader(args[0]));
	yyparser.yyparse();
}

*/
//#line 410 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 19 "cminus.y"
{ 
					/* TODO enter scope in symbol table*/
					symtab.enterScope();
					/* TODO generate code prologue*/
					GenCode.genPrologue();
                }
break;
case 2:
//#line 26 "cminus.y"
{
                	if (usesRead) GenCode.genReadMethod();
                	/* TODO generate class constructor code*/
					GenCode.genClassConstructor();
                	/* TODO generate epilog*/
					GenCode.genEpilogue(symtab);
                	/* TODO exit symbol table scope*/
					symtab.exitScope();
                	if (!seenMain) semerror("No main in file"); 
				}
break;
case 7:
//#line 47 "cminus.y"
{
					String name = val_peek(1).sval;
					int rettype = val_peek(2).ival;
					
					/* lookup the name in the symbol table*/
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(exists) {
						semerror("Redeclaration of variable " + name);
					}
					else if (rettype == VOID)
					{
						/* if it is a VOID type, that's a semantic error*/
						semerror("Variable name " + name + " cannot be VOID type");
					}
					else
					{
						/* ok, not in symbol table, no semantic errors, so insert into symtab*/
						SymTabRec rec = new VarRec(name, symtab.getScope(), rettype);
						symtab.insert(name, rec);
						if(symtab.getScope() == 0) {
							GenCode.genStaticDecl(rec);
						}
					}
				}
break;
case 8:
//#line 82 "cminus.y"
{
					String name = val_peek(4).sval;
					int rettype = val_peek(5).ival;
					int arraysize = val_peek(2).ival;
					
					/* lookup the name in the symbol table*/
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(exists) {
						semerror("Redeclaration of variable " + name);
					}
					else if (rettype == VOID)
					{
						/* if it is a VOID type, that's a semantic error*/
						semerror("Array variable name " + name + " cannot be VOID type");
					}
					else
					{
						/* ok, not in symbol table, no semantic errors, so insert into symtab*/
						SymTabRec rec = new ArrRec(name, symtab.getScope(), rettype, arraysize);
						symtab.insert(name, rec);
					}
				}
break;
case 9:
//#line 116 "cminus.y"
{ yyval = val_peek(0); }
break;
case 10:
//#line 117 "cminus.y"
{ yyval = val_peek(0); }
break;
case 11:
//#line 121 "cminus.y"
{
						int rettype = val_peek(1).ival;
						String name = val_peek(0).sval;
						
						/* create a FunRec for use in symbol table*/
						FunRec rec = new FunRec(name, symtab.getScope(), rettype, null);
						
						/* return the FunRec to the rest of the grammar rule below*/
						yyval = new ParserVal(rec);
						
						/* lookup the function name in symbol table*/
						int scope = symtab.getScope();
						boolean exists = symtab.lookup(name);
						for (int i = scope; i > 0; --i) {
							symtab.exitScope();
							if(symtab.lookup(name)) {
								exists = true;
							}
						}
						for (int i = 0; i < scope; ++i) {
							symtab.enterScope();
						}
						if(exists) {
							semerror("Redeclaration of function " + name);
						}
						else if (seenMain)
						{
							semerror("Function " + name + " declared after main");
						}
						else
						{
							symtab.insert(name, rec);
							if (name.equals("main"))
							{
								seenMain = true;
							}
						}
						
						symtab.enterScope();
					}
break;
case 12:
//#line 162 "cminus.y"
{
						FunRec rec = (FunRec)val_peek(3).obj;
						List<SymTabRec> params = (List<SymTabRec>)val_peek(1).obj;
						rec.setParams(params);
						GenCode.genFunBegin(rec);
					}
break;
case 13:
//#line 169 "cminus.y"
{
						/* remember we entered scope*/
						firstTime = true;
						GenCode.genFunEnd();
					}
break;
case 14:
//#line 176 "cminus.y"
{ yyval = val_peek(0); }
break;
case 15:
//#line 177 "cminus.y"
{yyval = new ParserVal(null);}
break;
case 16:
//#line 178 "cminus.y"
{yyval = new ParserVal(null);}
break;
case 17:
//#line 182 "cminus.y"
{
					List<SymTabRec> reclist = (List<SymTabRec>)val_peek(2).obj;
					SymTabRec rec = (SymTabRec)val_peek(0).obj;
					reclist.add(rec);
					yyval = new ParserVal(reclist);
				}
break;
case 18:
//#line 189 "cminus.y"
{
					List<SymTabRec> reclist = new ArrayList<SymTabRec>();
					SymTabRec rec = (SymTabRec)val_peek(0).obj;
					reclist.add(rec);
					yyval = new ParserVal(reclist);
				}
break;
case 19:
//#line 198 "cminus.y"
{
					int vartype = val_peek(1).ival;
					String name = val_peek(0).sval;
					VarRec rec = new VarRec(name, symtab.getScope(), vartype);
					yyval = new ParserVal(rec);
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(exists) {
						semerror("Redeclaration of variable " + name);
					}
					else
					{
						symtab.insert(name, rec);
					}
				}
break;
case 20:
//#line 223 "cminus.y"
{
					int vartype = val_peek(3).ival;
					String name = val_peek(2).sval;
					ArrRec rec = new ArrRec(name, symtab.getScope(), vartype, -1);
					yyval = new ParserVal(rec);
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(exists) {
						semerror("Redeclaration of variable " + name);
					}
					else
					{
						symtab.insert(name, rec);
					}
				}
break;
case 21:
//#line 249 "cminus.y"
{
					/* handle special case of entering scope in function params*/
					if (firstTime)
						firstTime = false;
					else
						symtab.enterScope();
				}
break;
case 22:
//#line 257 "cminus.y"
{
					symtab.exitScope();
				}
break;
case 37:
//#line 285 "cminus.y"
{
					String name = val_peek(3).sval;
					int expression = val_peek(1).ival;
					int scope = symtab.getScope();
					int varType = 0;
					SymTabRec rec = symtab.get(name);
					if(rec != null) {
						varType = rec.type;
					}
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
							varType = symtab.get(name).type;
							rec = symtab.get(name);
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(!exists) {
						semerror("Variable " + name + " does not exist in the current scope");
					}
					else {
						if(varType != ParserTokens.INT) {
							semerror("Variable " + name + " is not of type INT and cannot be assigned");
						}
						else {
							GenCode.genLoadVar(rec);
						}
					}
				}
break;
case 38:
//#line 319 "cminus.y"
{
					String name = val_peek(6).sval;
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					SymTabRec rec = symtab.get(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
							rec = symtab.get(name);
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(!exists) {
						semerror("Variable " + name + " does not exist in the current scope");
					}
					else {
						GenCode.genLoadVar(rec);
					}
				}
break;
case 39:
//#line 344 "cminus.y"
{
					String label = GenCode.getLabel();
					yyval = new ParserVal(label);
					GenCode.genFGoto(label);
				}
break;
case 40:
//#line 350 "cminus.y"
{
					String label = GenCode.getLabel();
					yyval = new ParserVal(label);
					GenCode.genGoto(label);
				}
break;
case 41:
//#line 356 "cminus.y"
{
					String label = val_peek(3).sval;
					GenCode.genLabel(label);
				}
break;
case 42:
//#line 361 "cminus.y"
{
					String label2 = val_peek(3).sval;
					GenCode.genLabel(label2);
				}
break;
case 45:
//#line 374 "cminus.y"
{
					String name = val_peek(5).sval;
					SymTabRec rec = symtab.get(name);
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
							rec = symtab.get(name);
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(rec == null) {
						semerror("Undeclared variable " + name + " in input statement");
					}
					else if (!rec.isVar()) {
						semerror("Name " + name + " in input statement is not a variable");
					}
					else {
						usesRead = true;
						GenCode.genRead(rec);
					}
				}
break;
case 46:
//#line 403 "cminus.y"
{
					GenCode.genReturn();
				}
break;
case 47:
//#line 407 "cminus.y"
{
					GenCode.genIReturn();
				}
break;
case 48:
//#line 413 "cminus.y"
{
					int relop = val_peek(1).ival;
					GenCode.genRelOper(relop);
				}
break;
case 60:
//#line 436 "cminus.y"
{ GenCode.genArithOper(val_peek(1).ival); }
break;
case 65:
//#line 446 "cminus.y"
{
					String name = val_peek(0).sval;
					SymTabRec rec = symtab.get(name);
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
							rec = symtab.get(name);
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(rec == null) {
						semerror("Undeclared variable " + name + " in the current scope");
					}
					else if (!rec.isVar() || rec.isArr()) {
						semerror("Name " + name + " is not a variable or array in current scope");
					}
					else {
						if(rec.isVar()) {
							GenCode.genLoadVar(rec);
						}
						else {
							GenCode.genLoadArrAddr(rec);
						}
					}
				}
break;
case 66:
//#line 477 "cminus.y"
{
					String name = val_peek(3).sval;
					SymTabRec rec = symtab.get(name);
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
							rec = symtab.get(name);
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(rec == null) {
						semerror("Undeclared array variable " + name + " in current scope");
					}
					else if(! rec.isArr()) {
						semerror("Name " + name + " is not an array in current scope");
					}
					else {
						GenCode.genLoadArrAddr(rec);
					}
				}
break;
case 69:
//#line 507 "cminus.y"
{
					String name = val_peek(3).sval;
					SymTabRec rec = symtab.get(name);
					int scope = symtab.getScope();
					boolean exists = symtab.lookup(name);
					for (int i = scope; i > 0; --i) {
						symtab.exitScope();
						if(symtab.lookup(name)) {
							exists = true;
							rec = symtab.get(name);
						}
					}
					for (int i = 0; i < scope; ++i) {
						symtab.enterScope();
					}
					if(rec == null) {
						semerror("Undeclared function " + name + " in the current scope");
					}
					else if(!rec.isFun()) {
						semerror("Name " + name + " is not a function in current scope");
					}
				}
break;
//#line 1050 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
