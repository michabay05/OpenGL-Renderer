import org.joml.Vector2f;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33C.*;

class GLTriangle implements GLDraw {
    private static Shader shader;

    private int vaoID;
    private int vboID;
    private Vector2f[] corners;
    private GLColor color;

    static {
        shader = new Shader(ShaderType.Triangle);
        shader.load();
    }

    public GLTriangle(Vector2f a, Vector2f b, Vector2f c, GLColor cl) {
        vaoID = 0;
        vboID = 0;
        color = GLColor.BLACK;
        corners = new Vector2f[] { a, b, c };
        color = cl;
    }

    public void draw() {
        vaoID = glGenVertexArrays();
        // Binding before use
        glBindVertexArray(vaoID);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0); 
        glEnableVertexAttribArray(0);

        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, Util.v2tofs(corners), GL_STATIC_DRAW);

        shader.use();
        shader.setV3f("color", color.r(), color.g(), color.b());
        glDrawArrays(GL_TRIANGLES, 0, corners.length);

        // Unbinding after use
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    private float[] vboArray() {
        float[] arr = new float[corners.length * 2];
        for (int i = 0; i < corners.length; i++) {
            Vector2f it = corners[i];
            arr[i*2] = it.x;
            arr[i*2+1] = it.y;
        }
        return arr;
    }
}
