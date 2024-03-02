@echo off

set "COMMON_LIB=lib/common/joml_1.10.5.jar;lib/common/lwjgl-glfw.jar;lib/common/lwjgl.jar;lib/common/lwjgl-opengl.jar;lib/common/lwjgl-stb.jar"
set "WINDOWS_LIB=lib/windows/lwjgl-glfw-natives-windows.jar;lib/windows/lwjgl-natives-windows.jar;lib/windows/lwjgl-opengl-natives-windows.jar;lib/windows/lwjgl-stb-natives-windows.jar"
set "LIBS=src;%COMMON_LIB%;%WINDOWS_LIB%;classes"

javac -cp %LIBS% src/*.java -d classes
if %ERRORLEVEL% EQU 0 java -cp %LIBS% Main
