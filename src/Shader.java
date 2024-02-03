import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33C.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

// TODO: look into using java buffers instead of arrays for primitive types that need
// to be passed by reference

class Shader {
    // Stores the filepath of the vertex shader
    private final String vertexFilePath;
    // Stores the filepath of the fragment shader
    private final String fragmentFilePath;

    // Vertex shader ID in OpenGL
    private int vertexShaderID;
    // Fragment shader ID in OpenGL
    private int fragmentShaderID;
    // Shader program ID with which the vertex and fragment shader IDs are associated with
    private int programID;

    private ShaderType type;

    // 'Custom' shader constructor, which expects specific shader files
    public Shader(String vertPath, String fragmentPath) {
        vertexFilePath = vertPath;
        fragmentFilePath = fragmentPath;
        type = null;
    }

    // Predefined shader constructor
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
                Logger.fatal("Unknown type of shader");
                break;
        }
    }

    /* ========== UNIFORM SETTING METHODS ========== */
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

    // Attachs and links the compiled shader sources to the shader program ID
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
        if (success[0] != GL_TRUE) {
            Logger.warn("Unable to link shader to program.");
            System.err.println(glGetProgramInfoLog(progID));
            return false;
        }
        Logger.info("Successfully linked shaders to program.");
        // Use shader after compiling and linking shader program
        programID = progID;
        // NOTE: To apply the shader, the shader must be explicitly called, i.e. bound

        // After creating the shaders and linking them, the original shaders have to deleted
        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
        return true;
    }

    // Applies or uses this shader when called
    public void bind() {
        glUseProgram(programID);
    }

    // Deselects this shader when called
    public void unbind() {
        glUseProgram(0);
    }

    // Reads the source code of the vertex or fragment shader and compiles the code
    // the code appropriately (depending on which shader it is)
    private boolean compile(String filepath, boolean isVertexShader) {
        int shaderID = glCreateShader(isVertexShader ? GL_VERTEX_SHADER : GL_FRAGMENT_SHADER);
        String source = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(filepath)));
        } catch (IOException ex) {
            Logger.panic("Failed to read the file into memory.");
            return false;
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
            return false;
        }
        Logger.info(String.format("Compiled %s shader: '%s'.", 
                    isVertexShader ? "vertex" : "fragment", filepath));
        if (isVertexShader) {
            vertexShaderID = shaderID;
        } else {
            fragmentShaderID = shaderID;
        }
        return true;
    }
}
