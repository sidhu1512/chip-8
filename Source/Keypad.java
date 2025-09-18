public class Keypad {
    private final boolean[] keys = new boolean[16];

    public void press(int key) {
        if (key >= 0 && key < 16)
            keys[key] = true;
    }

    public void release(int key) {
        if (key >= 0 && key < 16)
            keys[key] = false;
    }

    public boolean isPressed(int key) {
        return key >= 0 && key < 16 && keys[key];
    }

    public void reset() {
        for (int i = 0; i < 16; i++) {
            keys[i] = false;
        }
    }
}
