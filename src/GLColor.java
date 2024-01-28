class GLColor {
    public static final GLColor BLACK = new GLColor(0, 0, 0);
    public static final GLColor WHITE = new GLColor(255, 255, 255);
    public static final GLColor RED = new GLColor(255, 0, 0);
    public static final GLColor GREEN = new GLColor(0, 255, 0);
    public static final GLColor BLUE = new GLColor(0, 0, 255);
    public static final GLColor YELLOW = new GLColor(255, 255, 0);
    public static final GLColor MAGENTA = new GLColor(255, 0, 255);
    public static final GLColor SKYBLUE = new GLColor(102, 191, 255);
    public static final GLColor MAROON = new GLColor(190, 33, 55);

    private float r;
    private float g;
    private float b;

    public GLColor() {
        r = 0;
        g = 0;
        b = 0;
    }

    public GLColor(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public GLColor(int r, int g, int b) {
        this.r = (float) r / 255.0f;
        this.g = (float) g / 255.0f;
        this.b = (float) b / 255.0f;
    }

    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        final GLColor cl = (GLColor) other;
        return r == cl.r() && g == cl.g() && b == cl.b(); 
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d)",
            (int)(r * 255.f), (int)(g * 255.f), (int)(b * 255.f)
        );
    }
}
