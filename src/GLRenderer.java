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

    private ArrayList<Vector2f> verts;
    private ArrayList<GLColor> colors;
    private ArrayList<Integer> inds;
    private Shader shader;

    // private ArrayList<GLDraw> objs;

    public GLRenderer(int w, int h) {
        width = w;
        height = h;
        verts = new ArrayList<>();
        colors = new ArrayList<>();
        inds = new ArrayList<>();

        shader = new Shader(ShaderType.Triangle);
    }

    public void clear(int r, int g, int b) {
        float rf = (float) r / 255.0f;
        float gf = (float) g / 255.0f;
        float bf = (float) b / 255.0f;
        glClearColor(rf, gf, bf, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        // Clear primitive list for new frame
        verts.clear();
        colors.clear();
        inds.clear();
    }

    public void drawRect(int x, int y, int w, int h, GLColor color) {
        drawRectV(new Vector2f(x, y), new Vector2f(w, h), color);
    }

    public void OLD_drawRectV(Vector2f pos, Vector2f size, GLColor color) {
        Vector2f[] corners = {
            makeNDC(new Vector2f(pos.x, pos.y)),
            makeNDC(new Vector2f(pos.x, pos.y + size.y)),
            makeNDC(new Vector2f(pos.x + size.x, pos.y + size.y)),
            makeNDC(new Vector2f(pos.x + size.x, pos.y)),
        };
        // objs.add(new GLTriangle(corners[0], corners[1], corners[2], color));
        // objs.add(new GLTriangle(corners[0], corners[2], corners[3], color));

        // new GLTriangle(corners[0], corners[1], corners[2], color).draw();
        // new GLTriangle(corners[0], corners[2], corners[3], color).draw();
    }

    public void drawRectV(Vector2f pos, Vector2f size, GLColor color) {
        Vector2f[] corners = {
            makeNDC(new Vector2f(pos.x, pos.y)),
            makeNDC(new Vector2f(pos.x, pos.y + size.y)),
            makeNDC(new Vector2f(pos.x + size.x, pos.y + size.y)),
            makeNDC(new Vector2f(pos.x + size.x, pos.y)),
        };
        int[] localInds = {
            0, 1, 2, // First triangle
            0, 2, 3  // Second triangle
        };
        // Modify index list from a local index array to global 'verts' index array
        for (int i = 0; i < localInds.length; i++) {
            Vector2f it = corners[localInds[i]];
            addIfNotExist(it, color);
        }
        // objInds.add(new GLTriangle(inds[0], inds[1], inds[2]));
        // objInds.add(new GLTriangle(inds[3], inds[4], inds[5]));
    }

    public void drawCircleV(Vector2f center, float radius, GLColor color) {
        float anglePerVert = 360.0f / (float)GLCircle.DEFAULT_SEGMENTS_COUNT;
        Vector2f[] vertices = new Vector2f[GLCircle.DEFAULT_SEGMENTS_COUNT];
        for (int i = 0; i < vertices.length; i++) {
            float angle = anglePerVert * i;
            vertices[i] = makeNDC(new Vector2f(
                center.x + radius * (float) Math.cos(Math.toRadians(angle)),
                center.y + radius * (float) Math.sin(Math.toRadians(angle))
            ));
        }
        // objs.add(new GLCircle(vertices, color));
        new GLCircle(vertices, color).draw();
    }

    public void drawCircle(int x, int y, int radius, GLColor color) {
        drawCircleV(new Vector2f(x, y), (float)radius, color);
    }

    public void present() {
        int vaoID = glGenVertexArrays();

        glBindVertexArray(vaoID);
        // Vertex information
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 5*Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        // Color information
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 5*Float.BYTES, 2*Float.BYTES);
        glEnableVertexAttribArray(1);

        float[] vs = makeVBOData();
        int vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vs, GL_STATIC_DRAW);

        int[] indArr = new int[inds.size()];
        for (int i = 0; i < inds.size(); i++) {
            indArr[i] = inds.get(i);
        }

        int eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indArr, GL_STATIC_DRAW);

        shader.bind();
        glDrawElements(GL_TRIANGLES, inds.size(), GL_UNSIGNED_INT, 0);
        shader.unbind();

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }

    public void setWidth(int newWidth) { width = newWidth; }

    public void setHeight(int newHeight) { height = newHeight; }

    private void addIfNotExist(Vector2f v, GLColor c) {
        for (int i = 0; i < verts.size(); i++) {
            Vector2f itV = verts.get(i);
            GLColor itC = colors.get(i);
            if (v.equals(itV) && c.equals(itC)) {
                inds.add(i);
                return;
            }
        }
        verts.add(v);
        colors.add(c);
        inds.add(verts.size() - 1);
    }

    private float[] makeVBOData() {
        if (verts.size() != colors.size()) {
            System.err.printf("[ERROR] vertex list size(%d) != color list size(%d)\n",
                    verts.size(), colors.size());
            System.err.println(verts);
            System.err.println(colors);
            System.err.println(inds);
            System.exit(1);
        }
        int n = verts.size();
        // Each unit of 'data' is made up of a vertex and color
        // size of vertex info -> 2
        //  size of color info -> 3
        //  size of one 'data' -> 5
        //  [[ verts ][ color ]]
        //  [       data       ]
        int each = 5;
        float[] data = new float[each*n];
        for (int i = 0; i < n; i++) {
            Vector2f v = verts.get(i);
            GLColor c = colors.get(i);
            data[i*each+0] = v.x;
            data[i*each+1] = v.y;
            data[i*each+2] = c.r();
            data[i*each+3] = c.g();
            data[i*each+4] = c.b();
            System.out.println(str(new Vector2f(data[i*each], data[i*each+1])));
            System.out.printf("(%.2f, %.2f, %.2f)\n", 
                    data[i*each + 2],
                    data[i*each + 3],
                    data[i*each + 4]
                    );
            System.out.println("================================");
        }
        System.out.println(inds);
        System.exit(0);

        return data;
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
