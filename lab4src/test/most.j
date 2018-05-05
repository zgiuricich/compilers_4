.source stdin
.class  synchronized Main
.super  java/lang/Object

.field static aNumber1 I
.field static aNumber2 I
.field static anArray [I
.field static aVariable I
.field static anotherArray [I
; >> FUNCTION function1 <<
.method static function1()V
.limit stack 32
.limit locals 32
return
.end method

; >> FUNCTION function2 <<
.method static function2()V
.limit stack 32
.limit locals 32
ldc 5
istore 0
iload 0
ldc 8
imul
ldc 3
idiv
ldc 1
iadd
istore 1
iload 1
return
.end method

; >> FUNCTION readAValue <<
.method static readAValue()I
.limit stack 32
.limit locals 32
invokestatic Main/myRead()I
istore 2
iload 2
ireturn
return
.end method

; >> FUNCTION combinevalues <<
.method static combinevalues(II)I
.limit stack 32
.limit locals 32
iload 3
iload 4
imul
ldc 3
idiv
iload 3
isub
iload 4
iadd
istore 5
iload 5
ireturn
return
.end method

; >> FUNCTION manyRelationalOps <<
.method static manyRelationalOps(I)V
.limit stack 32
.limit locals 32
iload 6
ldc 10
if_icmplt Label1
iconst_0
goto Label0
Label1:
iconst_1
Label0:
ifeq Label2
ldc 0
goto Label3
Label2:
ldc 10
Label3:
iload 6
ldc 20
if_icmple Label5
iconst_0
goto Label4
Label5:
iconst_1
Label4:
ifeq Label6
ldc 20
goto Label7
Label6:
ldc 0
Label7:
iload 6
ldc 30
if_icmpeq Label9
iconst_0
goto Label8
Label9:
iconst_1
Label8:
ifeq Label10
ldc 30
goto Label11
Label10:
ldc 0
Label11:
iload 6
ldc 40
if_icmpne Label13
iconst_0
goto Label12
Label13:
iconst_1
Label12:
ifeq Label14
ldc 0
goto Label15
Label14:
ldc 40
Label15:
iload 6
ldc 50
if_icmpge Label17
iconst_0
goto Label16
Label17:
iconst_1
Label16:
ifeq Label18
ldc 50
goto Label19
Label18:
ldc 0
Label19:
iload 6
ldc 60
if_icmpgt Label21
iconst_0
goto Label20
Label21:
iconst_1
Label20:
ifeq Label22
ldc 60
goto Label23
Label22:
ldc 0
Label23:
iload 6
ldc 5
if_icmpgt Label25
iconst_0
goto Label24
Label25:
iconst_1
Label24:
ifeq Label26
iload 6
ldc 10
if_icmplt Label28
iconst_0
goto Label27
Label28:
iconst_1
Label27:
ifeq Label29
ldc 7
goto Label30
Label29:
ldc 15
Label30:
goto Label31
Label26:
iload 6
ldc 2
if_icmpgt Label33
iconst_0
goto Label32
Label33:
iconst_1
Label32:
ifeq Label34
ldc 3
goto Label35
Label34:
ldc 0
Label35:
Label31:
return
.end method

; >> FUNCTION sumarray <<
.method static sumarray(I[I)I
.limit stack 32
.limit locals 32
ldc 0
istore 11
ldc 0
istore 9
iload 11
iload 7
if_icmplt Label37
iconst_0
goto Label36
Label37:
iconst_1
Label36:
iload 11
aload 8
istore 12
iload 12
iload 9
iload 12
iadd
istore 9
iload 11
ldc 1
iadd
istore 11
iload 9
iload 7
ldc 3
imul
if_icmpgt Label39
iconst_0
goto Label38
Label39:
iconst_1
Label38:
ifeq Label40
iload 9
goto Label41
Label40:
ldc 99999999
ldc 0
istore 9
Label41:
iload 9
ireturn
return
.end method

; >> FUNCTION main <<
.method public static main([Ljava/lang/String;)V
.throws java/io/IOException
.limit stack 32
.limit locals 32
ldc 0
istore 13
invokestatic Main/readAValue()I
istore 14
iload 13
ldc 10
if_icmplt Label43
iconst_0
goto Label42
Label43:
iconst_1
Label42:
iload 13
iload 14
iload 13
ldc 1
iadd
istore 13
ldc 10
