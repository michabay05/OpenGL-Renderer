import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.IntBuffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

enum ShaderType {
    Triangle,
    Texture,
}

class GLShader {
    private static int triProgramID;
    private static int texProgramID;

    private static ShaderType current;

    public static void update(ShaderType st) {
        current = st;
    }

    // NOTE: changing program frequently might lead to worse performance
    //       this needs to be refactored at some point in the future
    public static void bind() {
        glUseProgram(getCurrentProgramID());
    }

    public static void unbind() {
        glUseProgram(0);
    }

    public static boolean loadAll() {
        int tempTri = load(ShaderType.Triangle);
        int tempTex = load(ShaderType.Texture);
        if (tempTri != 0) triProgramID = tempTri;
        if (tempTex != 0) texProgramID = tempTex;
        return tempTri != 0 && tempTex != 0;
    }

    // Attachs and links the compiled shader sources to the shader program ID
    // NOTE: Return 0 signals failure
    public static int load(ShaderType st) {
        String vertexFilePath = null, fragmentFilePath = null;
        getShaderPaths(st, vertexFilePath, fragmentFilePath);
        int vertexShaderID = compile(vertexFilePath, true);
        int fragmentShaderID = compile(fragmentFilePath, true);
        if (vertexShaderID == 0 || fragmentShaderID == 0) return 0;

        int progID = glCreateProgram();
        glAttachShader(progID, vertexShaderID);
        glAttachShader(progID, fragmentShaderID);
        glLinkProgram(progID);

        int[] success = new int[1];
        glGetProgramiv(progID, GL_LINK_STATUS, success);
        if (success[0] != GL_TRUE) {
            Logger.warn("Unable to link shader to program.");
            System.err.println(glGetProgramInfoLog(progID));
            return 0;
        }
        Logger.info("Successfully linked shaders to program.");

        // After creating the shaders and linking them, the original shaders have to deleted
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        return progID;
    }


    /* ========== UNIFORM SETTING METHODS ========== */
    public void setFloat(String name, float v0) {
        int loc = glGetUniformLocation(getCurrentProgramID(), name);
        glUniform1f(loc, v0);
    }

    public void setV2f(String name, float v0, float v1) {
        int loc = glGetUniformLocation(getCurrentProgramID(), name);
        glUniform2f(loc, v0, v1);
    }

    public void setV3f(String name, float v0, float v1, float v2) {
        int loc = glGetUniformLocation(getCurrentProgramID(), name);
        glUniform3f(loc, v0, v1, v2);
    }

    // Reads the source code of the vertex or fragment shader and compiles the code
    // the code appropriately (depending on which shader it is)
    // NOTE: Return 0 signals failure
    private static int compile(String filepath, boolean isVertexShader) {
        if (filepath == null) Logger.panic("Shader filepath is null");

        int shaderID = glCreateShader(isVertexShader ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
        String source = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException ex) {
            Logger.panic("Failed to read the file into memory.");
            return 0;
        }
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        int[] success = new int[1];
        glGetShaderiv(shaderID, GL_COMPILE_STATUS, success);
        if (success[0] != GL_TRUE) {
            Logger.warn(String.format("Unable to load %s shader file: '%s'.",
                        isVertexShader ? "vertex" : "fragment",
                        filepath));
            System.err.println(glGetShaderInfoLog(shaderID));
            return 0;
        }
        Logger.info(String.format("Compiled %s shader: '%s'.", 
                    isVertexShader ? "vertex" : "fragment", filepath));
        return shaderID;
    }

    private static void getShaderPaths(ShaderType st, String vertPath, String fragPath) {
        switch (st) {
            case Triangle:
                vertPath = "shader/simple.vert";
                fragPath = "shader/simple.frag";
                break;
            case Texture:
                vertPath = "shader/texture.vert";
                fragPath = "shader/texture.frag";
                break;
            default:
                Logger.fatal("Unknown shader type");
        }
    }

    private static int getCurrentProgramID() {
        switch (current) {
            case Triangle:
                return triProgramID;
            case Texture:
                return texProgramID;
            default:
                Logger.warn("Unknown shader type");
                return 0;
        }
    }
}
