class GLImage {
    public final Color[] pixels;
    public final int width;
    public final int height;

    private GLImage(Color[] pixels, int width, int height) {
        this.pixels = pixels;
        this.width = width;
        this.height = height;
    }

    public static GLImage genColor(int width, int height, Color color) {
        Color[] pixels = new Color[width*height];
        for (int i = 0; i < pixels.length; i++) pixels[i] = color;
        return new GLImage(pixels, width, height);
    }
}
