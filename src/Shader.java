import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33C.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// TODO: look into using java buffers instead of arrays for primitive types that need
// to be passed by reference

enum ShaderType {
    Triangle,
}

class Shader {
    private final String vertexFilePath;
    private final String fragmentFilePath;

    private int vertexShaderID;
    private int fragmentShaderID;
    private int programID;

    private ShaderType type;

    public Shader(String vertPath, String fragmentPath) {
        vertexFilePath = vertPath;
        fragmentFilePath = fragmentPath;
        type = null;
    }

    public Shader(ShaderType type) {
        this.type = type;
        switch (type) {
            case ShaderType.Triangle:
                vertexFilePath = "shader/simple.vert";
                fragmentFilePath = "shader/simple.frag";
                break;
            default:
                vertexFilePath = null;
                fragmentFilePath = null;
                System.err.println("Unknown type of shader");
                // Currently, this simply exits but should handled more gracefully
                // in the future
                System.exit(0);
                break;
        }
    }

    public void setFloat(String name, float v0) {
        int loc = glGetUniformLocation(programID, name);
        glUniform1f(loc, v0);
    }

    public void setV2f(String name, float v0, float v1) {
        int loc = glGetUniformLocation(programID, name);
        glUniform2f(loc, v0, v1);
    }

    public void setV3f(String name, float v0, float v1, float v2) {
        int loc = glGetUniformLocation(programID, name);
        glUniform3f(loc, v0, v1, v2);
    }

    public boolean load() {
        if (!compile(vertexFilePath, true) || !compile(fragmentFilePath, false)) {
            return false;
        }
        int progID = glCreateProgram();
        glAttachShader(progID, vertexShaderID);
        glAttachShader(progID, fragmentShaderID);
        glLinkProgram(progID);

        int[] success = new int[1];
        glGetProgramiv(progID, GL_LINK_STATUS, success);
        if (success[0] == GL_TRUE) {
            System.err.println("[INFO] Successfully linked shaders to program.");
        } else {
            System.err.println("[ERROR] Unable to link shaders to program.");
            System.err.println(glGetProgramInfoLog(progID));
            return false;
        }
        // Use shader after compiling and linking shader program
        programID = progID;
        // To apply the shader, 'use()' must be explicitly called

        // After creating the shaders and linking them, the original shaders have to deleted
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        return true;
    }

    public void use() {
        glUseProgram(programID);
    }

    private boolean compile(String filepath, boolean isVertexShader) {
        int shaderID = glCreateShader(isVertexShader ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
        String source = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException ex) {
            System.err.println("[ERROR] Failed to read shader into memory");
            System.err.println(ex.getMessage());
            return false;
        }
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        int[] success = new int[1];
        glGetShaderiv(shaderID, GL_COMPILE_STATUS, success);
        if (success[0] == GL_TRUE) {
            System.out.printf("[INFO] Compiled %s shader: '%s'.\n", 
                    isVertexShader ? "vertex" : "fragment", filepath);
        } else {
            System.err.printf("[ERROR] Unable to load %s shader file: '%s'.\n",
                    isVertexShader ? "vertex" : "fragment",
                    filepath);
            System.err.println(glGetShaderInfoLog(shaderID));
            return false;
        }
        if (isVertexShader) {
            vertexShaderID = shaderID;
        } else {
            fragmentShaderID = shaderID;
        }
        return true;
    }
}
