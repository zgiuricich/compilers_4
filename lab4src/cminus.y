
/* 
 * A BYACCJ specification for the Cminus language.
 * Author: Vijay Gehlot
 */
%{
import java.io.*;
import java.util.*;
%}
  
%token ID NUM 
%token VOID INT SEMI PLUS ASSIGN COMMA
%token LPAREN RPAREN LBRACKET RBRACKET LBRACE RBRACE
%token MINUS MULT DIVIDE LT GT LTE GTE EQ NOTEQ
%token PRINT INPUT ELSE IF RETURN WHILE
%token ERROR

%%

program:		{ 
					// TODO enter scope in symbol table
					symtab.enterScope();
					// TODO generate code prologue
					GenCode.genPrologue();
                } 
                declaration_list 
                {
                	if (usesRead) GenCode.genReadMethod();
                	// TODO generate class constructor code
						GenCode.genClassConstructor();
                	// TODO generate epilog
						GenCode.genEpilogue(symtab);
                	// TODO exit symbol table scope
					symtab.exitScope();
                	if (!seenMain) semerror("No main in file"); 
				}
			;

declaration_list:	declaration_list declaration
			|	declaration
			;

declaration:	var_declaration
			|	fun_declaration	
			;

var_declaration:	type_specifier ID SEMI
				{
					String name = $2.sval;
					int rettype = $1.ival;
					int scope = symtab.getScope();
					int FAIL =0;
					// lookup the name in the symbol table
					
					for(int i =0; i <=scope; i++){
						if(symtab.lookup(name)){
								i = scope;
								FAIL=1;
							}
							if(!(i==scope)){
								symtab.decScope();
							}
						}
							
					
					// for(int i=0;i<scope;i++)
					// 	symtab.incScope();
					symtab.setScope(scope);
					if (FAIL == 1)
					{
						// if already there, that's a semantic error
						semerror("Redeclaration of name " + name + " in the current scope");
					}
					else if (rettype == VOID)
					{
						// if it is a VOID type, that's a semantic error
						semerror("Variable name " + name + " cannot be VOID type");
					}
					else
					{
						// ok, not in symbol table, no semantic errors, so insert into symtab
						SymTabRec rec = new VarRec(name, symtab.getScope(), rettype);
						symtab.insert(name, rec);

						// If this is a global variable (scope of 0) then generate code
						if (symtab.getScope() == 0)
						{
							GenCode.genStaticDecl(rec);
						}
					}
				}
			|	type_specifier ID LBRACKET NUM RBRACKET SEMI
				{
					String name = $2.sval;
					int rettype = $1.ival;
					int arraysize = $4.ival;
					
					// lookup the name in the symbol table
					if (symtab.lookup(name))
					{
						// if already there, that's a semantic error
						semerror("Redeclaration of array name " + name + " in the current scope");
					}
					else if (rettype == VOID)
					{
						// if it is a VOID type, that's a semantic error
						semerror("Array variable name " + name + " cannot be VOID type");
					}
					else
					{
						// ok, not in symbol table, no semantic errors, so insert into symtab
						SymTabRec rec = new ArrRec(name, symtab.getScope(), rettype, arraysize);
						symtab.insert(name, rec);
						if (symtab.getScope() == 0)
						{
							GenCode.genStaticDecl(rec);
						}
					}
				}
			;

type_specifier:		INT { $$ = $1; }
			|		VOID { $$ = $1; }
			;

fun_declaration:	type_specifier ID
					{
						int rettype = $1.ival;
						String name = $2.sval;
						
						// create a FunRec for use in symbol table
						FunRec rec = new FunRec(name, symtab.getScope(), rettype, null);
						
						// return the FunRec to the rest of the grammar rule below
						$$ = new ParserVal(rec);
						
						// lookup the function name in symbol table
						if (symtab.lookup(name))
						{
							semerror("Redeclaration of function name " + name + " in the current scope");
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
					LPAREN params RPAREN
					{
						FunRec rec = (FunRec)$3.obj;
						List<SymTabRec> params = (List<SymTabRec>)$5.obj;
						rec.setParams(params);
						// Generate code for beginning of function
						GenCode.genFunBegin(rec);
					}
					compound_stmt
					{
						// remember we entered scope
						firstTime = true;
						// Generate code for end of function
						GenCode.genFunEnd();
					}
			;

params:			param_list { $$ = $1; }
			|	VOID  			{ $$ = new ParserVal(null); }
			|	/* empty */		{ $$ = new ParserVal(null); }
			;

param_list:		param_list COMMA param
				{
					List<SymTabRec> reclist = (List<SymTabRec>)$1.obj;
					SymTabRec rec = (SymTabRec)$3.obj;
					reclist.add(rec);
					$$ = new ParserVal(reclist);
				}
				|	param
				{
					List<SymTabRec> reclist = new ArrayList<SymTabRec>();
					SymTabRec rec = (SymTabRec)$1.obj;
					reclist.add(rec);
					$$ = new ParserVal(reclist);
				}
			;

param:			type_specifier ID
				{
					int vartype = $1.ival;
					String name = $2.sval;
					VarRec rec = new VarRec(name, symtab.getScope(), vartype);
					$$ = new ParserVal(rec);
					if (symtab.lookup(name))
					{
						semerror("Redeclaration of parameter name " + name + " in the current scope");
					}
					else
					{
						symtab.insert(name, rec);
						
					}
				}
			|	type_specifier ID LBRACKET RBRACKET
				{
					int vartype = $1.ival;
					String name = $2.sval;
					ArrRec rec = new ArrRec(name, symtab.getScope(), vartype, -1);
					$$ = new ParserVal(rec);
					if (symtab.lookup(name))
					{
						semerror("Redeclaration of array parameter name " + name + " in the current scope");
					}
					else
					{
						symtab.insert(name, rec);
					}
				}
			;

compound_stmt:	{
					// handle special case of entering scope in function params
					if (firstTime)
						firstTime = false;
					else
						symtab.enterScope();
				}
				LBRACE local_declarations statement_list RBRACE
				{
					symtab.exitScope();
				}
			;

local_declarations:		local_declarations var_declaration
			|	/* empty */
			;

statement_list:		statement_list statement
			|	/* empty */	
			;

statement:		assign_stmt 
			|	compound_stmt
			|	selection_stmt
			|	iteration_stmt
			|	return_stmt
			|	print_stmt
			|	input_stmt
			|	call_stmt
			|	SEMI
			;

call_stmt:  	call SEMI
			;
/*FINISHED SCOPE CHECKS*/
assign_stmt:	ID ASSIGN expression SEMI{								
					String name = $1.sval;
					int scope = symtab.getScope();
					int FAIL=0;
					//checking for above scoop 
					SymTabRec rec = symtab.get(name);
					if(rec == null) {
						for(int i =0; i <= scope; i++){
							if(symtab.lookup(name)){
								rec = symtab.get(name);
								if(rec== null){
									FAIL=1;
									i = scope;
								}
								else if(!rec.isVar()){FAIL=2;i=scope;}
							}
								if(!(i==scope)){
									symtab.decScope();
								}
						}
					}
					// for(int i=0;i<scope;i++)
					// 	symtab.incScope();
					symtab.setScope(scope);
					//checking under scoop
					if(FAIL==1 )
							semerror(" undeclared variable" + name + " in the current scope");
					else if(FAIL==2 )
						semerror(" name "+ name +" the assignment is not a variable or array in scope");
					else {
						GenCode.genStore(rec);
					}
			}
			|	ID LBRACKET expression RBRACKET ASSIGN expression SEMI{
				// fix scope checks
					String name = $1.sval;
					int scope = symtab.getScope();
					int FAIL=0;
					SymTabRec rec = symtab.get(name);
					if(rec == null) {
						for(int i =0; i <=scope; i++){
							if(symtab.lookup(name)){
								rec = symtab.get(name);
								if(rec == null){
									FAIL=1;
									i = scope;
								}
								if(!rec.isArr()){FAIL=2;i=scope;}
							}
							if(!(i==scope)){
								symtab.decScope();
							}
						}
					}
					// for(int i=0;i<scope;i++)
					// 	symtab.incScope();
					symtab.setScope(scope);
					//checking under scoop
					if(FAIL==1 )
						if(FAIL==1)
							semerror(" undeclared array" + name + " in the current scope");
					else if(FAIL==2 )
						semerror(" name "+ name +" the assignment is not a variable or array in scope");
					else {
						GenCode.genStore(rec);
					}
			}
			;

selection_stmt:	IF LPAREN expression RPAREN 
			{
					String label = GenCode.getLabel(); // for false condition
					$$ = new ParserVal(label); // becomes $5 below
					GenCode.genFGoto(label);
			}
			statement 
			{
					String label = GenCode.getLabel();
					$$ = new ParserVal(label); // becomes $7
					GenCode.genGoto(label);
			}
			ELSE 
			{
					// handle the false condition
					String label = $5.sval;
					GenCode.genLabel(label);
			}
			statement
			{
					String label2 = $7.sval;
					GenCode.genLabel(label2);
			}
			;

iteration_stmt:	WHILE LPAREN expression RPAREN statement
			;

print_stmt:		PRINT 
				{
					GenCode.genBeginPrint();
				}
				LPAREN expression RPAREN SEMI 
				{
					GenCode.genEndPrint();
				}
			;
input_stmt:		ID ASSIGN INPUT LPAREN RPAREN SEMI{
				String name = $1.sval;
				int FAIL=0;int PASS=0;
				int scope = symtab.getScope();
				SymTabRec rec=symtab.get(name);
				if(rec == null) {
					for(int i =0; i <= scope; i++){
						if(symtab.lookup(name)){
							rec = symtab.get(name);
							if(rec == null){
								FAIL=1;
								i = scope;
							}
							if(!rec.isVar()){FAIL=2;i=scope;}
						}
						if(!(i==scope)){
							symtab.decScope();
						}
					}
				}
					// for(int i=0;i<scope;i++)
					// 	symtab.incScope();
					symtab.setScope(scope);
					//checking under scoop
					if(FAIL==1 )
						semerror(" undeclared variable" + name + " in the current scope");
					else if(FAIL==2 )
						semerror(" name "+ name +" the assignment is not a variable");
					else{
					usesRead=true;GenCode.genRead(rec);
					}

			}
			;

return_stmt:	RETURN SEMI 
			{

				GenCode.genReturn();
			}
			| 	RETURN expression SEMI
			{
				GenCode.genIReturn();
			}
			;

expression:	  	additive_expression relop additive_expression
				{
					int relop = $2.ival;
					GenCode.genRelOper(relop);

				}
			|	additive_expression
			;

relop:			LTE 
			|	LT
			|	GT
			|	GTE
			|	EQ
			|	NOTEQ
			;

additive_expression:	additive_expression addop term{GenCode.genArithOper($2.ival);}
			|	term
			;

addop:			PLUS
			|	MINUS
			;

term:			term mulop factor{GenCode.genArithOper($2.ival);}
			|	factor
			;

mulop:			MULT
			|	DIVIDE
			;
factor:			LPAREN expression RPAREN
			|	ID
			{
				String name = $1.sval;
				int scope = symtab.getScope();
				int FAIL=0;
				SymTabRec rec =symtab.get(name);
				// fix params
				if(rec == null) {
					for(int i =0; i <= scope; i++){
						if(symtab.lookup(name)){
							rec = symtab.get(name);
							if(rec == null){
								FAIL=1;
								i = scope;
							}
							if(!(rec.isVar()||rec.isArr())){FAIL=2;i=scope;}
						}
						if(!(i==scope)){
							symtab.decScope();
						}
					}
				}
				// for(int i=0;i<scope;i++)
				// 	symtab.incScope();
				symtab.setScope(scope);
				//checking under scoop
				if(FAIL==1 ) {
					semerror(" undeclared variable" + name + " in the current scope");
				}
				else if(FAIL==2 ) {
					semerror(" name "+ name +" the assignment is not a variable or array in scope");
				}
				else {
					GenCode.genLoadVar(rec);
				}
					
					
					
				}
			|	ID LBRACKET expression RBRACKET
			{
				String name = $1.sval;
				int scope = symtab.getScope();
				int FAIL =0;
				SymTabRec rec = symtab.get(name);
				if(rec == null) {
					for(int i =0; i <= scope; i++){
						if(symtab.lookup(name)){
							 rec = symtab.get(name);
							if(rec == null){
								FAIL=1;
								i = scope;
							}
							if(!rec.isArr()){FAIL=2;i=scope;}
						}
						if(!(i==scope)){
							symtab.decScope();
						}
					}
				}
					// for(int i=0;i<scope;i++)
					// 	symtab.incScope();
					symtab.setScope(scope);
					//checking under scoop
					if(FAIL==1 )
							semerror(" undeclared  array variable" + name + " in the current scope");
					else if(FAIL==2 )
							semerror(" name "+ name +" the assignment is not a variable or array in scope");
					else
							GenCode.genLoadArrAddr(rec);
				}
			|	call
			|	NUM {GenCode.genLoadConst($1.ival);}
			;

call:		ID LPAREN args RPAREN{
				String name =$1.sval;
				SymTabRec rec = symtab.get(name);
				
				if( rec == null){
					semerror(name +" is undeclared in the current scope crazy stuff");

				}
				else if(!rec.isFun()){
					semerror("NOT A FUNCTION WHAT ARE YOU DOING");

				}
				else{
					GenCode.genFunCall(rec);
					//its all good stuff
				}

			}
			;

args:			arg_list 
			|	/* empty */
			;

arg_list:		 arg_list COMMA expression
			|	expression
			;

%%

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
		System.out.println();
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