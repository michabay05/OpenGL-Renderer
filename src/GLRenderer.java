import java.util.ArrayList;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL33.*;

interface GLDraw {
    public void draw();
}

class GLRenderer {
    private int width;
    private int height;

    private ArrayList<GLDraw> objs;

    public GLRenderer(int w, int h) {
        width = w;
        height = h;
        objs = new ArrayList<>();
    }

    public void clear(int r, int g, int b) {
        float rf = (float) r / 255.0f;
        float gf = (float) g / 255.0f;
        float bf = (float) b / 255.0f;
        glClearColor(rf, gf, bf, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        objs.clear();
    }

    public void drawRect(int x, int y, int w, int h, GLColor color) {
        Vector2f[] corners = {
            makeNDC(new Vector2f(x, y)),
            makeNDC(new Vector2f(x, y + h)),
            makeNDC(new Vector2f(x + w, y + h)),
            makeNDC(new Vector2f(x + w, y)),
        };
        objs.add(new GLTriangle(corners[0], corners[1], corners[2], color));
        objs.add(new GLTriangle(corners[0], corners[2], corners[3], color));
    }

    public void drawCircle(int x, int y, int radius, GLColor color) {
        float anglePerVert = 360.0f / (float)GLCircle.DEFAULT_SEGMENTS_COUNT;
        Vector2f[] vertices = new Vector2f[GLCircle.DEFAULT_SEGMENTS_COUNT];
        for (int i = 0; i < vertices.length; i++) {
            float angle = anglePerVert * i;
            vertices[i] = makeNDC(new Vector2f(
                (float)x + (float)radius * (float) Math.cos(Math.toRadians(angle)),
                (float)y + (float)radius * (float) Math.sin(Math.toRadians(angle))
            ));
        }
        objs.add(new GLCircle(vertices, color));
    }

    public void present() {
        for (GLDraw t : objs) {
            t.draw();
        }
    }

    public void setWidth(int newWidth) {
        width = newWidth;
    }

    public void setHeight(int newHeight) {
        height = newHeight;
    }

    private Vector2f makeNDC(Vector2f v) {
        return new Vector2f(
            v.x / width * 2 - 1,
            -(v.y / height * 2 - 1)
        );
    }

    private String str(Vector2f v) {
        return String.format("(%.2f, %.2f)", v.x, v.y);
    }
}
