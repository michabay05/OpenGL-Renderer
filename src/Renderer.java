import org.joml.Vector2f;

public class Renderer {
    private GLRenderer glRend;

    public Renderer(int width, int height) {
        glRend = new GLRenderer(width, height);
    }

    public int GetWidth() { return glRend.GetWidth(); }
    public int GetHeight() { return glRend.GetHeight(); }

    public void SetWidth(int newWidth) { glRend.SetWidth(newWidth); }
    public void SetHeight(int newHeight) { glRend.SetHeight(newHeight); }

    public void Init() {
        glRend.Init();
    }

    public void Begin() {
        glRend.BatchBegin();
    }

    public void DrawRect(int x, int y, int w, int h, Color color) {
        DrawRect(new Vector2f(x, y), new Vector2f(w, h), color);
    }

    public void DrawRect(Vector2f pos, Vector2f size, Color color) {
        glRend.AddQuad(pos, size, color);
    }

    public void DrawTexture(int x, int y, int w, int h, GLTexture2D tex) {
        DrawTexture(new Vector2f(x, y), new Vector2f(w, h), tex);
    }

    public void DrawTexture(Vector2f pos, Vector2f size, GLTexture2D tex) {
        glRend.AddTexturedQuad(pos, size, tex);
    }

    public void End() {
        glRend.BatchEnd();
        // TODO: Doing an additional flush is unnecessary
        // The reason it's here is because, currently, there is no mechanism to handle the rendering of leftover vertices
        // Therefore, the additional flush ensures that there aren't any vertices left that haven't been rendered
        glRend.BatchFlush();
    }

    public void ClearBackground(Color color) {
        glRend.Clear(color.r(), color.g(), color.b(), 1.0f);
    }
}
