# Build Instructions

## Setup
- Create a `classes` directory so that all the `.class` files can be placed into one directory
```
$ mkdir classes
```

## Compilation
- If you have `make` readily available, run this command
```
$ make init
```

- If `make` isn't readily available, run the following by choosing the right one depending on your OS.
### Windows
```
javac -cp src:lib/common/joml-1.10.5.jar:lib/common/lwjgl-glfw.jar:lib/common/lwjgl.jar:lib/common/lwjgl-opengl.jar:lib/common/lwjgl-stb.jar:lib/windows/lwjgl-glfw-natives-windows.jar:lib/windows/lwjgl-natives-windows.jar:lib/windows/lwjgl-opengl-natives-windows.jar:lib/windows/lwjgl-stb-natives-windows.jar:classes src/*.java -d classes
java -cp src:lib/common/joml-1.10.5.jar:lib/common/lwjgl-glfw.jar:lib/common/lwjgl.jar:lib/common/lwjgl-opengl.jar:lib/common/lwjgl-stb.jar:lib/windows/lwjgl-glfw-natives-windows.jar:lib/windows/lwjgl-natives-windows.jar:lib/windows/lwjgl-opengl-natives-windows.jar:lib/windows/lwjgl-stb-natives-windows.jar:classes Main
```

### Linux
```
javac -cp src;lib/common/joml-1.10.5.jar;lib/common/lwjgl-glfw.jar;lib/common/lwjgl.jar;lib/common/lwjgl-opengl.jar;lib/common/lwjgl-stb.jar;lib/linux/lwjgl-glfw-natives-linux.jar;lib/linux/lwjgl-natives-linux.jar;lib/linux/lwjgl-opengl-natives-linux.jar;lib/linux/lwjgl-stb-natives-linux.jar;classes src/*.java -d classes
java -cp src;lib/common/joml-1.10.5.jar;lib/common/lwjgl-glfw.jar;lib/common/lwjgl.jar;lib/common/lwjgl-opengl.jar;lib/common/lwjgl-stb.jar;lib/linux/lwjgl-glfw-natives-linux.jar;lib/linux/lwjgl-natives-linux.jar;lib/linux/lwjgl-opengl-natives-linux.jar;lib/linux/lwjgl-stb-natives-linux.jar;classes Main
```
