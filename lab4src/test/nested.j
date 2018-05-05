.source stdin
.class  synchronized Main
.super  java/lang/Object

.field static a I
; >> FUNCTION f <<
.method static f(I)V
.limit stack 32
.limit locals 32
ldc 6
istore 1
ldc 7
istore 2
iload 2
iload 1
iadd
iload 0
iadd
getstatic Main/a I
iadd
istore 3
return
.end method

; >> FUNCTION main <<
.method public static main([Ljava/lang/String;)V
.throws java/io/IOException
.limit stack 32
.limit locals 32
ldc 5
putstatic Main/a I
getstatic java/lang/System/out Ljava/io/PrintStream;
getstatic Main/a I
invokevirtual java/io/PrintStream/println(I)V
return
.end method

; >> CONSTRUCTOR <<
.method <init>()V
.limit stack 1
.limit locals 1
aload_0
invokenonvirtual java/lang/Object/<init>()V
return
.end method

; >> CLASS INIT FOR GLOBAL ARRAYS <<
.method static <clinit>()V
.limit stack 1
.limit locals 0
return
.end method

