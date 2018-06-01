package graphics.Input;

import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.glfw.GLFW.*;

public class KeyboardHandler extends GLFWKeyCallback
{
    public static boolean[] keys = new boolean[65536];
    @Override
    public void invoke(long window, int key, int scancode, int action, int mods)
    {
        // TODO Auto-generated method stub
        keys[key] = action != GLFW_RELEASE;
    }
    // returns true if a given key is pressed
    public static boolean isKeyDown(int keycode)
    {
        return keys[keycode];
    }

    public static boolean[] getKeys()
    {
        return keys;
    }
}