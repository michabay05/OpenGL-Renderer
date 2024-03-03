public class Image {
    private final int width;
    private final int height;
    private final Color[] pixels;

    public Image(Color color, int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Color[width*height];
        for (int i = 0; i < pixels.length; i++) pixels[i] = color;
    }

    public int GetWidth() { return width; }
    public int GetHeight() { return height; }

    public float[] ToColorArray() {
        float[] output = new float[width * height * 4];
        for (int i = 0; i < width * height; i++) {
            float[] colorfs = pixels[i].ToFloatArray();
            for (int j = 0; j < 4; j++) {
                output[i*4+j] = colorfs[j];
            }
        }
        return output;
    }
}