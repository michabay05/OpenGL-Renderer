MATH_LIB=lib/joml-1.10.5.jar
GLFW_LIB=lib/lwjgl-glfw-natives-linux.jar:lib/lwjgl-glfw.jar
LWGL_LIB=lib/lwjgl.jar:lib/lwjgl-natives-linux.jar
OPENGL_LIB=lib/lwjgl-opengl.jar:lib/lwjgl-opengl-natives-linux.jar
STB_LIB=lib/lwjgl-stb.jar:lib/lwjgl-stb-natives-linux.jar
LIBS=$(MATH_LIB):$(GLFW_LIB):$(LWGL_LIB):$(OPENGL_LIB):$(STB_LIB)

SOURCES = $(wildcard src/*.java)
CLASSES = $(patsubst src/%.java, classes/%.class, $(SOURCES))

all: $(CLASSES)
	java -cp src:$(LIBS):classes Main

classes/%.class: src/%.java
	javac -cp src:$(LIBS):classes $^ -d classes

init:
	javac -cp src:$(LIBS):classes src/*.java -d classes
	java -cp src:$(LIBS):classes Main
