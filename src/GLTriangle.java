import org.joml.Vector2f;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL33C.*;

class GLTriangle implements GLDraw {
    private static Shader shader;

    private int vaoID;
    private int vboID;
    private int[] corners;
    private GLColor color;

    static {
        shader = new Shader(ShaderType.Triangle);
        shader.load();
    }

    public GLTriangle(int indA, int indB, int indC, GLColor cl) {
        vaoID = 0;
        vboID = 0;
        corners = new int[] { indA, indB, indC };
        color = cl;
    }

    public void draw() {}
}
