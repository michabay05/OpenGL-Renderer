import java.util.ArrayList;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL33.*;

class GLRenderer {
    // Width of the viewport
    private int width;
    // Height of the viewport
    private int height;

    // An array of all 'unique' vertex information for all objects
    private ArrayList<VertexInfo> info;
    // A list of indices that point to the vertex information array
    private ArrayList<Integer> inds;
    // Handles both the vertex and fragment shader
    private Shader shader;
    // Used to check if shader is already loaded so as to not load
    // the shader multiple times
    private boolean isShaderLoaded;

    public GLRenderer(int w, int h) {
        width = w;
        height = h;
        info = new ArrayList<>();
        inds = new ArrayList<>();

        shader = new Shader(ShaderType.Triangle);
        isShaderLoaded = false;
    }

    // Clear the screen with the specified color and clear out
    // all the vertex information array and indices list
    public void clear(int r, int g, int b) {
        float rf = (float) r / 255.0f;
        float gf = (float) g / 255.0f;
        float bf = (float) b / 255.0f;
        glClearColor(rf, gf, bf, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        // Clear primitive list for new frame
        info.clear();
        inds.clear();
    }

    public void drawRect(int x, int y, int w, int h, Color color) {
        drawRect(new Vector2f(x, y), new Vector2f(w, h), color);
    }

    // Renders a rectangle of a specified color
    public void drawRect(Vector2f pos, Vector2f size, Color color) {
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
            handleVertInfo(it, color);
        }
    }

    public void drawCircle(Vector2f center, float radius, Color color) {
        // Implementation from:
        // Source: https://faun.pub/draw-circle-in-opengl-c-2da8d9c2c103
        float anglePerVert = 360.0f / (float)GLCircle.DEFAULT_SEGMENTS_COUNT;
        Vector2f[] vertices = new Vector2f[GLCircle.DEFAULT_SEGMENTS_COUNT];
        for (int i = 0; i < vertices.length; i++) {
            float angle = anglePerVert * i;
            vertices[i] = makeNDC(new Vector2f(
                center.x + radius * (float) Math.cos(Math.toRadians(angle)),
                center.y + radius * (float) Math.sin(Math.toRadians(angle))
            ));
        }

        ArrayList<Vector2f> vl = new ArrayList<>();
        for (int i = 0; i < vertices.length - 2; i++) {
            handleVertInfo(vertices[0], color);
            handleVertInfo(vertices[i+1], color);
            handleVertInfo(vertices[i+2], color);
        }
    }

    public void drawCircle(int x, int y, int radius, Color color) {
        drawCircle(new Vector2f(x, y), (float)radius, color);
    }

    public void begin() {
        if (!isShaderLoaded) {
            shader.load();
            isShaderLoaded = true;
        }
    }

    public void end() {
        // Generate ID for a new vertex array objects
        int vaoID = glGenVertexArrays();

        // Bind vertex array to attach the following vertex and index buffers
        // to this vertex array object (VAO)
        glBindVertexArray(vaoID);

        // Compling the list of vertex information into one float[] array
        // to send this data to GPU once.
        float[] vs = makeVBOData();

        // Generate ID for a vertex buffer objects
        int vboID = glGenBuffers();
        // Bind `vboID` to an OpenGL array buffer
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        // Bind the compiled float[] to the current bound buffer data
        glBufferData(GL_ARRAY_BUFFER, vs, GL_STATIC_DRAW);

        // Specify the information of the 1st (index 0) attribute sent to the vertex shader
        // DETAIL: a vec2(2 floats) that specifies 'position' of the current vertex
        glVertexAttribPointer(0, 2, GL_FLOAT, false, VertexInfo.BYTES, VertexInfo.POS_OFFSET);
        // Enable the attribute at (index 0)
        glEnableVertexAttribArray(0);

        // Specify the information of the 2nd (index 1) attribute sent to the vertex shader
        // DETAIL: a vec3(3 floats) that specifies 'color' of the current vertex
        glVertexAttribPointer(1, 3, GL_FLOAT, false, VertexInfo.BYTES, VertexInfo.CLR_OFFSET);
        // Enable the attribute at (index 1)
        glEnableVertexAttribArray(1);

        // Compiling all the index buffer list into an int[] array
        int[] indArr = new int[inds.size()];
        for (int i = 0; i < inds.size(); i++) {
            indArr[i] = inds.get(i);
        }

        // Generate an ID for an index buffer objects 
        int eboID = glGenBuffers();
        // Bind the `eboID` to an Opengl element array buffer
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        // Bind the compiled int[] to the current bound index buffer object
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indArr, GL_STATIC_DRAW);

        shader.bind();
        // The main(actual) rendering function
        glDrawElements(GL_TRIANGLES, inds.size(), GL_UNSIGNED_INT, 0);

        // Disabling vertex attrib pointers and unbinding the shader
        // NOTE: Don't really know if this has to be included or not
        shader.unbind();
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
    }

    public void setWidth(int newWidth) { width = newWidth; }

    public void setHeight(int newHeight) { height = newHeight; }

    // Adds the vertex information if doesn't exist and updates
    // the index buffer list
    private void handleVertInfo(Vector2f v, Color c) {
        int index = addIfNotExist(v, c);
        inds.add(index);
    }

    // Add a new vertex information if it doesn't exist in the list
    private int addIfNotExist(Vector2f v, Color c) {
        VertexInfo vi = new VertexInfo(v, c);
        for (int i = 0; i < info.size(); i++) {
            if (info.get(i).equals(vi)) {
                return i;
            }
        }
        info.add(vi);
        return info.size() - 1;
    }

    // Make a new float[] from an arraylist of vertex informations
    private float[] makeVBOData() {
        int n = info.size();
        int count = VertexInfo.BYTES / Float.BYTES;
        float[] data = new float[count*n];
        for (int i = 0; i < n; i++) {
            VertexInfo vi = info.get(i);
            data[i*count+0] = vi.pos.x;
            data[i*count+1] = vi.pos.y;
            data[i*count+2] = vi.color.r();
            data[i*count+3] = vi.color.g();
            data[i*count+4] = vi.color.b();
        }

        return data;
    }

    // Take a specified vector and transform it to a normalized
    // device coordinate (NDC), which ranges from [-1, 1] on both the x and y
    //     NOTE: Since OpenGL sets (0, 0) at the bottom left of the screen and 
    //           it makes sense to place (0, 0) on the top left, the y axis
    //           gets flipped (negated) 
    private Vector2f makeNDC(Vector2f v) {
        return new Vector2f(
            v.x / width * 2 - 1,
            -(v.y / height * 2 - 1)
        );
    }

    // A utility method that prints a vec2 in a more intuitive format
    private String str(Vector2f v) {
        return String.format("(%.2f, %.2f)", v.x, v.y);
    }
}

// Each unit of 'data' is made up of a vertex and color
// size of vertex info -> 2
//  size of color info -> 3
//  size of one 'data' -> 5
//  [[ verts ][ color ]]
//  [       data       ]

// Stores all the information about a vertex's position and color
class VertexInfo {
    // Total number of bytes need to represent all the info related
    // to one vertex
    public static final int BYTES = 5 * Float.BYTES;

    // Offset required to get to the 'position' component
    public static final int POS_OFFSET = 0;
    // Offset required to get to the 'color' component
    public static final int CLR_OFFSET = 2 * Float.BYTES;

    // Stores the location of a vertex as an normalized device coordinate (NDC)
    public Vector2f pos;
    // Stores the color associate with this vertex in terms of floats from [0, 1]
    // instead of ints from [0, 255]
    public Color color;

    public VertexInfo(Vector2f pos, Color color) {
        this.pos = pos;
        this.color = color;
    }

    // Redefines equality from the default implementation of Objects
    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        final VertexInfo vi = (VertexInfo) other;
        return pos.equals(vi.pos) && color.equals(vi.color);
    }
}
