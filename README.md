# fernflowers

> Why do I write this?

When decompiling with Jetbrains' fernflower, the program freezes for a particular file or directory and cannot continue. In addition, the CPU usage is not high enough during decompiling, which makes decompilation slow.

So I developed this program based on fernflower to optimize these problems, with the following features

- multi-thread, 
- progress bar
- breakpoint continue
- failure record

## Usage

**java 17 is required**

```
usage: java -jar fernflowers.jar [args]
 -h              help
 -i <arg>        input, the directory to be scanned.
 -o <arg>        output, the directory to saved result.
 -thread <arg>   thread, default: 20
 -unzip          unzip the jar file
```

After running for a period of time, almost all files are decomcompiled successfully, and the progress bar remains stuck, indicating that some jar packages have been decomcompiled incorrectly. You only need to terminate the program by `Ctrl + C`, and the program will automatically print out the wrong jar package path.

## Reference

1. [JetBrains/intellij-community/plugins/java-decompiler/engine](https://github.com/JetBrains/intellij-community/tree/master/plugins/java-decompiler/engine)