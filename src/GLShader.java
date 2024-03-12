import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.IntBuffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

enum ShaderType {
    Texture
}

class GLShader {
    private static int texProgramID;
    private static ShaderType current;
    private static boolean isLoaded = false;

    // NOTE: changing program frequently might lead to worse performance
    //       this needs to be refactored at some point in the future
    public static void bind(ShaderType st) {
        current = st;
        glUseProgram(getCurrentProgramID());
    }

    public static void unbind() {
        glUseProgram(0);
    }

    public static void LoadAll() {
        if (!isLoaded) {
            int tempTex = Load(ShaderType.Texture);
            if (tempTex == 0) {
                Logger.Warn("Couldn't load texture shader");
            } else {
                if (tempTex != 0) texProgramID = tempTex;
                isLoaded = true;
            }
        }
    }

    // Attachs and links the compiled shader sources to the shader program ID
    // NOTE: Return 0 signals failure
    public static int Load(ShaderType st) {
        String vertexFilePath = getVertexPath(st);
        String fragmentFilePath = getFragmentPath(st);

        int vertexShaderID = compile(vertexFilePath, true);
        int fragmentShaderID = compile(fragmentFilePath, false);
        if (vertexShaderID == 0 || fragmentShaderID == 0) return 0;

        int progID = glCreateProgram();
        glAttachShader(progID, vertexShaderID);
        glAttachShader(progID, fragmentShaderID);
        glLinkProgram(progID);

        int[] success = new int[1];
        glGetProgramiv(progID, GL_LINK_STATUS, success);
        if (success[0] != GL_TRUE) {
            Logger.Warn("Unable to link shader to program.");
            System.err.println(glGetProgramInfoLog(progID));
            return 0;
        }
        Logger.Info("Successfully linked shaders to program.");

        // After creating the shaders and linking them, the original shaders have to deleted
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        return progID;
    }

    /* ========== UNIFORM SETTING METHODS ========== */
    public static void SetFloat(String name, float v0) {
        int loc = glGetUniformLocation(getCurrentProgramID(), name);
        glUniform1f(loc, v0);
    }

    public static void SetV2f(String name, float v0, float v1) {
        int loc = glGetUniformLocation(getCurrentProgramID(), name);
        glUniform2f(loc, v0, v1);
    }

    public static void SetV3f(String name, float v0, float v1, float v2) {
        int loc = glGetUniformLocation(getCurrentProgramID(), name);
        glUniform3f(loc, v0, v1, v2);
    }

    public static void SetIntegerArr(String name, int[] arr) {
        int loc = glGetUniformLocation(getCurrentProgramID(), name);
        glUniform1iv(loc, arr);
    }

    // Reads the source code of the vertex or fragment shader and compiles the code
    // the code appropriately (depending on which shader it is)
    // NOTE: Return 0 signals failure
    private static int compile(String filepath, boolean isVertexShader) {
        if (filepath == null) Logger.Panic("Shader filepath is null");

        int shaderID = glCreateShader(isVertexShader ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
        String source = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException ex) {
            Logger.Panic("Failed to read the file into memory.");
            return 0;
        }
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        int[] success = new int[1];
        glGetShaderiv(shaderID, GL_COMPILE_STATUS, success);
        if (success[0] != GL_TRUE) {
            Logger.Warn(String.format("Unable to load %s shader file: '%s'.",
                        isVertexShader ? "vertex" : "fragment",
                        filepath));
            System.err.println(glGetShaderInfoLog(shaderID));
            return 0;
        }
        Logger.Info(String.format("Compiled %s shader: '%s'.", 
                    isVertexShader ? "vertex" : "fragment", filepath));
        return shaderID;
    }

    private static String getVertexPath(ShaderType st) {
        switch (st) {
            case Texture:
                return "src/texture.vert";
            default:
                Logger.Unreachable();
        }
        // Should be unreachable here! The compiler considers this an
        // error, for some reason
        return null;
    }

    private static String getFragmentPath(ShaderType st) {
        switch (st) {
            case Texture:
                return "src/texture.frag";
            default:
                Logger.Unreachable();
        }
        // Should be unreachable here! The compiler considers this an
        // error, for some reason
        return null;
    }

    private static int getCurrentProgramID() {
        switch (current) {
            case Texture:
                return texProgramID;
            default:
                Logger.Warn("Unknown shader type");
                return 0;
        }
    }
}
