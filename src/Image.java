import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import static org.lwjgl.stb.STBImage.*;
import org.lwjgl.BufferUtils;

public class Image {
    private final int width;
    private final int height;
    private final int components;
    private final Color[] pixels;

    public Image(Color color, int width, int height) {
        this.width = width;
        this.height = height;
        components = 4;
        pixels = new Color[width*height];
        for (int i = 0; i < pixels.length; i++) pixels[i] = color;
        Logger.Info(String.format("Image: Created with dimensions (%d, %d) and a solid color %s", width, height, color.toString()));
    }

    public Image(String filepath) {
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);
        IntBuffer comp = BufferUtils.createIntBuffer(1);
        if (!stbi_info(filepath, w, h, comp)) {
            Logger.Fatal("Image: " + stbi_failure_reason());
        }
        width = w.get(0);
        height = h.get(0);
        components = comp.get(0);
        Logger.Info("Width = " + width);
        Logger.Info("Height = " + height);
        Logger.Info("Comp = " + components);

        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(new File(filepath));
        } catch (Exception ex) {}
        DataBufferByte dbb = (DataBufferByte) bufferedImage.getRaster().getDataBuffer();
        byte[] bytes = dbb.getData();
        Logger.Info("Bytes.length = " + bytes.length);

        pixels = new Color[bytes.length/components];
        Logger.Fatal("Pixels.length = " + pixels.length);
        for (int i = 0; i < pixels.length; i++) {
            int r = (int) bytes[i*components+0];
            int g = (int) bytes[i*components+1];
            int b = (int) bytes[i*components+2];
            int a = components == 4 ? (int) bytes[i*components + 3] : 255;
            pixels[i] = new Color(r, g, b, a);
            Logger.Info(String.format("color[i] = (%d, %d, %d, %d)", r, g, b, a));
        }
        Logger.Info(String.format("Image: Created with dimensions (%d, %d) from '%s'", width, height, filepath));
    }

    public void ToPPMFile(String filepath) {
        try {
            PrintWriter out = new PrintWriter(filepath);
            out.print("P6\n");
            out.printf("%d %d\n", width, height);
            out.print("255\n");

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color c = pixels[y * height + x];
                    int r = (int)(c.r() * 255.0f);
                    int g = (int)(c.g() * 255.0f);
                    int b = (int)(c.b() * 255.0f);
                    out.printf("%d %d %d ", r, g, b);
                }
                out.println();
            }
            out.flush();
            out.close();
            Logger.Info(String.format("Image: Saved image to '%s'", filepath));
        } catch (Exception ex) {}
    }

    public int GetWidth() { return width; }
    public int GetHeight() { return height; }
    public int GetComponent() { return components; }

    public float[] ToColorArray() {
        float[] output = new float[pixels.length * 4];
        for (int i = 0; i < pixels.length; i++) {
            float[] colorfs = pixels[i].ToFloatArray();
            for (int j = 0; j < 4; j++) {
                output[i*4+j] = colorfs[j];
            }
        }
        return output;
    }
}
