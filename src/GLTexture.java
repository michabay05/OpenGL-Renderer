import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL33.*;

class GLTexture {
    public final int id;
    public final int width;
    public final int height;

    private GLTexture(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public static GLTexture fromImage(GLImage image) {
        int id = glGenTextures();
        glBindBuffer(GL_TEXTURE_2D, id);

        // TODO: figure out the texture parameter for the image
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);
        // Set a border color
        float borderColor[] = { 1.0f, 1.0f, 0.0f, 1.0f };
        glTexParameterfv(GL_TEXTURE_2D, GL_TEXTURE_BORDER_COLOR, borderColor);
        // Set the filter: Nearest(point) and linear(bilinear)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        float[] arr = clrtofs(image.pixels);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.width, image.height, 0, GL_RGB, GL_FLOAT, arr);
        glGenerateMipmap(GL_TEXTURE_2D);
        return new GLTexture(id, image.width, image.height);
    }

    private static float[] clrtofs(Color[] color) {
        // Multiply by 3 because each color has 3 components (r, g, b)
        float[] arr = new float[color.length * 3];
        for (int i = 0; i < color.length; i++) {
            Color it = color[i];
            arr[i*3+0] = it.r();
            arr[i*3+1] = it.g();
            arr[i*3+2] = it.b();
        }
        return arr;
    }
}
