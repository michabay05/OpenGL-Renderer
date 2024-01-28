import org.joml.Vector2f;

class Util {
    // A utility function that converts a vec2[] array to a float[] array
    public static float[] v2tofs(Vector2f[] vs) {
        float[] output = new float[vs.length * 2];
        for (int i = 0; i < vs.length; i++) {
            Vector2f it = vs[i];
            output[i*2] = it.x;
            output[i*2 + 1] = it.y;
        }
        return output;
    }
}
