import java.util.ArrayList;

import org.joml.Vector2f;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33C.*;

class GLCircle implements GLDraw {
    private static Shader shader;
    public static final int DEFAULT_SEGMENTS_COUNT = 64;

    private int vaoID;
    private int vboID;

    private Vector2f[] vertices;
    private int segments;
    private GLColor color;

    static {
        shader = new Shader(ShaderType.Triangle);
        shader.load();
    }

    public GLCircle(Vector2f[] vertices, GLColor color) {
        this.vertices = vertices;
        this.segments = DEFAULT_SEGMENTS_COUNT;
        this.color = color;
    }

    public GLCircle(Vector2f[] vertices, int segments, GLColor color) {
        this.vertices = vertices;
        this.segments = segments;
        this.color = color;
    }

    public void draw() {
        vaoID = glGenVertexArrays();
        // Binding before use
        glBindVertexArray(vaoID);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0); 
        glEnableVertexAttribArray(0);

        // Implementation from:
        // Source: https://faun.pub/draw-circle-in-opengl-c-2da8d9c2c103
        ArrayList<Vector2f> vl = new ArrayList<>();
        for (int i = 0; i < vertices.length - 2; i++) {
            vl.add(vertices[0]);
            vl.add(vertices[i+1]);
            vl.add(vertices[i+2]);
        }
        Vector2f[] v = new Vector2f[vl.size()];
        v = vl.toArray(v);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, Util.v2tofs(v), GL_STATIC_DRAW);

        shader.use();
        shader.setV3f("color", color.r(), color.g(), color.b());
        glDrawArrays(GL_TRIANGLES, 0, v.length);

        // Unbinding after use
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }
}
