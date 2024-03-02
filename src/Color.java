class Color {
    // Predefined set of colors
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color DARKGRAY = new Color(20, 20, 20);
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color YELLOW = new Color(255, 255, 0);
    public static final Color MAGENTA = new Color(255, 0, 255);
    public static final Color SKYBLUE = new Color(102, 191, 255);
    public static final Color MAROON = new Color(190, 33, 55);

    // Stores the red, green, and blue channels as floating point values
    // from [0, 1]
    private float r;
    private float g;
    private float b;
    private float a;

    public Color() {
        r = 0;
        g = 0;
        b = 0;
        a = 0;
    }

    public Color(float r, float g, float b) { this(r, g, b, 1.0f); }
    public Color(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int r, int g, int b) { this(r, g, b, 255); }
    public Color(int r, int g, int b, int a) {
        this.r = (float) r / 255.0f;
        this.g = (float) g / 255.0f;
        this.b = (float) b / 255.0f;
        this.a = (float) a / 255.0f;
    }

    /* ========== GET METHODS ========== */
    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }

    public float a() {
        return a;
    }

    public float[] ToFloatArray() {
        return new float[] { r, g, b, a };
    }

    // Redefines equality from the default implementation of Objects
    public boolean equals(Color other) {
        return r == other.r() && g == other.g() && b == other.b(); 
    }

    // Overrides the default format of printing objects
    @Override
    public String toString() {
        return String.format("(%d, %d, %d)",
            (int)(r * 255.f), (int)(g * 255.f), (int)(b * 255.f)
        );
    }
}
