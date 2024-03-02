import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.util.ArrayList;
import org.joml.Vector2f;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

class Main {
    static long window;
    static boolean wireframe = false;

    static final int INITIAL_SCREEN_WIDTH = 800;
    static final int INITIAL_SCREEN_HEIGHT = 600;

    static Renderer rend;

    public static void main(String[] args) {
        Logger.Info("Starting LWJGL " + Version.getVersion() + "!");

		init();
        Logger.Info("Intializations complete");
        Logger.Divider();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
        Logger.Info("Window successfully terminated");
		glfwSetErrorCallback(null).free();
    }

    static void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Setup a key callback. It will be called every time a key is pressed, repeated or released.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            processInput(key, action);
		});

        glfwSetFramebufferSizeCallback(window, (window, w, h) -> {
            rend.SetWidth(w);
            rend.SetHeight(h);
            glViewport(0, 0, w, h);
            Logger.Info("New resize (" + w + ", " + h + ")");
        });

        rend.Init();

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!glfwWindowShouldClose(window)) {

            rend.Begin();
            rend.ClearBackground(Color.DARKGRAY);
            rend.DrawRect(100, 100, 200, 200, Color.SKYBLUE);
            rend.End();

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
    }

    static void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(INITIAL_SCREEN_WIDTH, INITIAL_SCREEN_HEIGHT, "LWJGL Test", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");
        Logger.Info("Window: Intialized successfully with dimensions (" + INITIAL_SCREEN_WIDTH + ", " + INITIAL_SCREEN_HEIGHT + ")");

        rend = new Renderer(INITIAL_SCREEN_WIDTH, INITIAL_SCREEN_HEIGHT);
        Logger.Info("Renderer: Initialized successfully");

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
    }

    static void processInput(int key, int action) {
        if (action == GLFW_PRESS) {
            if (key == GLFW_KEY_Q || key == GLFW_KEY_ESCAPE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (key == GLFW_KEY_W) wireframe = !wireframe;
        }

        if (wireframe) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        } else {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }

    }
}
