.field static x I
.field static y I
; >> FUNCTION max <<
.limit stack 32
.limit locals 32
Label0:
Label1:
; >> FUNCTION main <<
.limit stack 32
.limit locals 32
invokestatic Main/myRead()I
invokestatic Main/myRead()I
getstatic Main/x I
getstatic Main/y I
; >> READ METHOD <<
.method public static myRead()I
.throws java/io/IOException

.limit stack 5
.limit locals 2

new java/io/BufferedReader
dup
new java/io/InputStreamReader
dup
getstatic java/lang/System/in Ljava/io/InputStream;
invokenonvirtual java/io/InputStreamReader/<init>(Ljava/io/InputStream;)V
invokenonvirtual java/io/BufferedReader/<init>(Ljava/io/Reader;)V
astore_0
aload_0
invokevirtual java/io/BufferedReader/readLine()Ljava/lang/String;
astore_1
aload_1
invokestatic java/lang/Integer/parseInt(Ljava/lang/String;)I
ireturn
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

