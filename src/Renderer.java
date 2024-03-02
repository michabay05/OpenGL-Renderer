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

    public void End() {
        glRend.BatchEnd();
        glRend.BatchFlush();
    }

    public void ClearBackground(Color color) {
        glRend.Clear(color.r(), color.g(), color.b(), 1.0f);
    }
}
