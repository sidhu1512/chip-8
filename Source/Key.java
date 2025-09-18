import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Key implements KeyListener {
    private final Keypad keypad;

    public Key(Keypad keypad) {
        this.keypad = keypad;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int chip8Key = mapKey(e.getKeyChar());
        if (chip8Key != -1) {
            keypad.press(chip8Key);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int chip8Key = mapKey(e.getKeyChar());
        if (chip8Key != -1) {
            keypad.release(chip8Key);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {} // unused

    private int mapKey(char c) {
        return switch (Character.toLowerCase(c)) {
            case '1' -> 0x1;
            case '2' -> 0x2;
            case '3' -> 0x3;
            case '4' -> 0xC;
            case 'q' -> 0x4;
            case 'w' -> 0x5;
            case 'e' -> 0x6;
            case 'r' -> 0xD;
            case 'a' -> 0x7;
            case 's' -> 0x8;
            case 'd' -> 0x9;
            case 'f' -> 0xE;
            case 'z' -> 0xA;
            case 'x' -> 0x0;
            case 'c' -> 0xB;
            case 'v' -> 0xF;
            default -> -1;
        };
    }
}
