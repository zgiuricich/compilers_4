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
					
					// lookup the name in the symbol table
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
						// if it is a VOID type, that's a semantic error
						semerror("Variable name " + name + " cannot be VOID type");
					}
					else
					{
						// ok, not in symbol table, no semantic errors, so insert into symtab
						SymTabRec rec = new VarRec(name, symtab.getScope(), rettype);
						symtab.insert(name, rec);
						if(symtab.getScope() == 0) {
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
						// if it is a VOID type, that's a semantic error
						semerror("Array variable name " + name + " cannot be VOID type");
					}
					else
					{
						// ok, not in symbol table, no semantic errors, so insert into symtab
						SymTabRec rec = new ArrRec(name, symtab.getScope(), rettype, arraysize);
						symtab.insert(name, rec);
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
					LPAREN params RPAREN
					{
						FunRec rec = (FunRec)$3.obj;
						List<SymTabRec> params = (List<SymTabRec>)$5.obj;
						rec.setParams(params);
						GenCode.genFunBegin(rec);
					}
					compound_stmt
					{
						// remember we entered scope
						firstTime = true;
						GenCode.genFunEnd();
					}
			;

params:			param_list { $$ = $1; }
			|	VOID	{$$ = new ParserVal(null);}
			|	/* empty */ {$$ = new ParserVal(null);}
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
			|	type_specifier ID LBRACKET RBRACKET
				{
					int vartype = $1.ival;
					String name = $2.sval;
					ArrRec rec = new ArrRec(name, symtab.getScope(), vartype, -1);
					$$ = new ParserVal(rec);
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

assign_stmt:	ID ASSIGN expression SEMI
				{
					String name = $1.sval;
					int expression = $3.ival;
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
			|	ID LBRACKET expression RBRACKET ASSIGN expression SEMI
				{
					String name = $1.sval;
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
			;

selection_stmt:	IF LPAREN expression RPAREN 
				{
					String label = GenCode.getLabel();
					$$ = new ParserVal(label);
					GenCode.genFGoto(label);
				}
				statement
				{
					String label = GenCode.getLabel();
					$$ = new ParserVal(label);
					GenCode.genGoto(label);
				}
				ELSE
				{
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

print_stmt:		PRINT LPAREN expression RPAREN SEMI
			;

input_stmt:		ID ASSIGN INPUT LPAREN RPAREN SEMI
				{
					String name = $1.sval;
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

additive_expression:	additive_expression addop term { GenCode.genArithOper($2.ival);}
			|	term
			;

addop:			PLUS
			|	MINUS
			;

term:			term mulop factor { GenCode.genArithOper($2.ival); }
			|	factor
			;

mulop:			MULT
			|	DIVIDE
			;

factor:			LPAREN expression RPAREN
			|	ID
				{
					String name = $1.sval;
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
			|	ID LBRACKET expression RBRACKET
				{
					String name = $1.sval;
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
			|	call
			|	NUM
			;

call:			ID LPAREN args RPAREN
				{
					String name = $1.sval;
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
