import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL33.*;

import java.nio.ByteBuffer;

public class GLRenderer {
    private int width;
    private int height;

    private int quadVao;
    private int quadVbo;
    private int quadIbo;
    private float[] vertices;
    private int vertCount;
    private int indexCount;
    private int[] textureSlots;
    private int textureCount;

    // Statistics
    private int quadCounter;
    private int drawCounter;

    private boolean isShaderBound;

    private static final int MAX_QUAD_COUNT = 1000;
    private static final int MAX_VERTEX_COUNT = MAX_QUAD_COUNT * 4;
    private static final int MAX_INDEX_COUNT = MAX_QUAD_COUNT * 6;
    // Some devices have 32 texture slots, some might have more, some might have less. '32' is just a guess
    // TODO: query the maximum texture slots of a given machine, instead of hardcoding it
    private static final int MAX_TEXTURE_COUNT = 32;
    private static final int WHITE_TEXTURE_INDEX = 0;

    public GLRenderer(int width, int height) {
        this.width = width;
        this.height = height;
        quadVao = 0;
        quadVbo = 0;
        quadIbo = 0;
        vertices = null;
        vertCount = 0;
        indexCount = 0;
        textureSlots = null;
        textureCount = 0;
        quadCounter = 0;
        drawCounter = 0;
        isShaderBound = false;
    }

    public int GetWidth() { return width; }
    public int GetHeight() { return height; }

    public void SetWidth(int newWidth) { width = newWidth; }
    public void SetHeight(int newHeight) { height = newHeight; }

    // NOTE: this should called after the OpenGL context has been created.
    // Otherwise, it could crash the whole system
    public void Init() {
        quadVao = glGenVertexArrays();
        glBindVertexArray(quadVao);

        quadVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, quadVbo);
        vertices = new float[VertexInfo.FLOATS * MAX_VERTEX_COUNT];
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);


        // Fill indices with this pattern
        // Pattern: [[0, 1, 2] [0, 2, 3]]
        int[] indices = new int[MAX_INDEX_COUNT];
        int num = 0;
        for (int i = 0; i < indices.length; i += 6) {
            indices[i+0] = 0+num;
            indices[i+1] = 1+num;
            indices[i+2] = 2+num;

            indices[i+3] = 0+num;
            indices[i+4] = 2+num;
            indices[i+5] = 3+num;
            num += 4;
        }
        quadIbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, quadIbo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, VertexInfo.TOTAL_BYTES, VertexInfo.POS_OFFSET);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, VertexInfo.TOTAL_BYTES, VertexInfo.COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, VertexInfo.TOTAL_BYTES, VertexInfo.TEX_COORD_OFFSET);
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(3, 1, GL_FLOAT, false, VertexInfo.TOTAL_BYTES, VertexInfo.TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        // Textures
        int whiteTex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, whiteTex);
        // set the texture wrapping parameters
        // set texture wrapping to GL_REPEAT (default wrapping method)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        // set texture filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        float[] color = Color.WHITE.ToFloatArray();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_FLOAT, color);
        glGenerateMipmap(GL_TEXTURE_2D);

        // int maxTextureSlots;
        // glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, (GLint*)&maxTextureSlots);
        textureSlots = new int[MAX_TEXTURE_COUNT];
        textureSlots[WHITE_TEXTURE_INDEX] = whiteTex;
        textureCount = 1;

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        GLShader.loadAll();
    }

    public void Clear(float r, float g, float b, float a) {
        glClearColor(r, g, b, a);
        glClear(GL_COLOR_BUFFER_BIT);
    }

    public void AddQuad(Vector2f pos, Vector2f size, Color color) {
        if (indexCount >= MAX_INDEX_COUNT) {
            BatchEnd();
            BatchFlush();
            BatchBegin();
        }

        float texID = 0.0f;
        float[] colorfs = color.ToFloatArray();
        Vector3f[] corners = {
            // Top Left
            new Vector3f(pos.x, pos.y, 0.0f),
            // Bottom Left
            new Vector3f(pos.x, pos.y + size.y, 0.0f),
            // Bottom Right
            new Vector3f(pos.x + size.x, pos.y + size.y, 0.0f),
            // Top Right
            new Vector3f(pos.x + size.x, pos.y, 0.0f),
        };
        float[][] texCoords = {
            // Top Left
            { 0.0f, 1.0f },
            // Bottom Left
            { 0.0f, 0.0f },
            // Bottom Right
            { 1.0f, 0.0f },
            // Top Right
            { 1.0f, 1.0f },
        };

        for (int i = 0; i < 4; i++) {
            Vector3f current = toNDC(corners[i]);
            float[] currentfs = { current.x, current.y, current.z };
            VertexInfo vi = new VertexInfo(currentfs, colorfs, texCoords[i], texID);
            float[] vifs = vi.ToFloatArray();
            for (int j = 0; j < vifs.length; j++) {
                vertices[vertCount*VertexInfo.FLOATS+j] = vifs[j];
            }
            vertCount++;
        }

        indexCount += 6;
        quadCounter++;
    }

    public void BatchBegin() {
        vertCount = 0;
        if (!isShaderBound) {
            GLShader.bind(ShaderType.Texture);
            isShaderBound = true;
            Logger.Info("Shader: Bound Texture shader");
        }
    }

    public void BatchFlush() {
        for (int i = 0; i < textureCount; i++) {
            /* Opengl 3.3 (or even earlier, not sure tho) approach */
            glActiveTexture(GL_TEXTURE0 + i);
            glBindTexture(GL_TEXTURE_2D, textureSlots[i]);
            /* Opengl 4.0+ approach */
            // glBindTextureUnit(i, s);
        }
        glBindVertexArray(quadVao);
        glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        drawCounter++;
        indexCount = 0;
        textureCount = 1;
    }

    public void BatchEnd() {
        glBindBuffer(GL_ARRAY_BUFFER, quadVbo);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
    }

    public boolean NeedsFlush() {
        return indexCount > 0;
    }

    private Vector3f toNDC(Vector3f v) {
        // The return vectors z-value is 0.0, for no reason
        return new Vector3f(
            (v.x / (float)width) * 2 - 1,
            -((v.y / (float)height) * 2 - 1),
            0.0f
        );
    }
}

class VertexInfo {
    public static final int FLOATS = 3 + 4 + 2 + 1;
    public static final int TOTAL_BYTES = FLOATS * Float.BYTES;

    // Attribute offset of vertex info
    // These values are expressed in total bytes -> (n*Float.BYTES)
    public static final int POS_OFFSET = 0;
    public static final int COLOR_OFFSET = 3*Float.BYTES;
    public static final int TEX_COORD_OFFSET = (3+4)*Float.BYTES;
    public static final int TEX_ID_OFFSET = (3+4+2)*Float.BYTES;

    public final float[] pos;
    public final float[] color;
    public final float[] texCoords;
    public final float texID;

    public VertexInfo(Vector3f pos, Color color, Vector2f texCoords, float texID) {
        this.pos = new float[] { pos.x, pos.y, pos.z };
        this.color = new float[] { color.r(), color.g(), color.b(), color.a() };
        this.texCoords = new float[] { texCoords.x, texCoords.y };
        this.texID = texID;
    }

    public VertexInfo(float[] pos, float[] color, float[] texCoords, float texID) {
        this.pos = pos;
        this.color = color;
        this.texCoords = texCoords;
        this.texID = texID;
    }

    public float[] ToFloatArray() {
        float[] output = new float[VertexInfo.FLOATS];
        output[0] = pos[0];
        output[1] = pos[1];
        output[2] = pos[2];

        output[3] = color[0];
        output[4] = color[1];
        output[5] = color[2];
        output[6] = color[3];

        output[7] = texCoords[0];
        output[8] = texCoords[1];

        output[9] = texID;
        return output;
    }
}
