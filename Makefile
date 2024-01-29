COMMON_LIB:=lib/common/joml-1.10.5.jar:lib/common/lwjgl-glfw.jar:lib/common/lwjgl.jar:lib/common/lwjgl-opengl.jar:lib/common/lwjgl-stb.jar
WINDOWS_LIB:=lib/windows/lwjgl-glfw-natives-windows.jar:lib/windows/lwjgl-natives-windows.jar:lib/windows/lwjgl-opengl-natives-windows.jar:lib/windows/lwjgl-stb-natives-windows.jar
LINUX_LIB:=lib/linux/lwjgl-glfw-natives-linux.jar:lib/linux/lwjgl-natives-linux.jar:lib/linux/lwjgl-opengl-natives-linux.jar:lib/linux/lwjgl-stb-natives-linux.jar

LIBS:=$(COMMON_LIB)
# Here `addsuffix` function is used to concatenate the common lib string with
# the OS-specific lib WITHOUT a space. Using a '+=' placed a space between
# the two strings
ifeq ($(OS),Windows_NT)
	LIBS:=$(addsuffix :$(WINDOWS_LIB),$(LIBS))
else
	LIBS:=$(addsuffix :$(LINUX_LIB),$(LIBS))
endif

SOURCES:=$(wildcard src/*.java)
CLASSES:=$(patsubst src/%.java, classes/%.class, $(SOURCES))

.PHONY: all init setup clean

all: setup $(CLASSES)
	java -cp src:$(LIBS):classes Main

classes/%.class: src/%.java
	javac -cp src:$(LIBS):classes $^ -d classes

init: setup
	javac -cp src:$(LIBS):classes src/*.java -d classes
	java -cp src:$(LIBS):classes Main

setup:
	mkdir -p classes

clean:
	rm -rf classes/*
