import static org.lwjgl.opengl.GL33C.*;

public class GLTexture2D {
    private int width;
    private int height;
    private int id;

    public GLTexture2D(Image image) {
        this.width = image.GetWidth();
        this.height = image.GetHeight();
        this.id = createTextureFromImage(image);
    }

    public GLTexture2D(int id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public int GetID() { return id; }
    public int GetWidth() { return width; }
    public int GetHeight() { return height; }

    private static int createTextureFromImage(Image image) {
        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        // TODO: add a way to externally set the texture parameters
        // set the texture wrapping parameters
        // set texture wrapping to GL_REPEAT (default wrapping method)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        // set texture filtering parameters
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        float[] pixels = image.ToColorArray();
        int format = image.GetComponent() == 3 ? GL_RGB : GL_RGBA8;
        glTexImage2D(GL_TEXTURE_2D, 0, format, image.GetWidth(), image.GetHeight(), 0, GL_RGBA, GL_FLOAT, pixels);
        glGenerateMipmap(GL_TEXTURE_2D);

        return textureID;
    }
}
