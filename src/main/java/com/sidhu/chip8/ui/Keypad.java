package com.sidhu.chip8.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Represents the 16-key CHIP-8 hexadecimal keypad and binds it to standard keyboard keys.
 * Also acts as a KeyAdapter to listen to Swing Window events directly.
 */
public class Keypad extends KeyAdapter {
    private final boolean[] keys = new boolean[16];

    public void press(int key) {
        if (key >= 0 && key < 16) keys[key] = true;
    }

    public void release(int key) {
        if (key >= 0 && key < 16) keys[key] = false;
    }

    public boolean isPressed(int key) {
        return key >= 0 && key < 16 && keys[key];
    }

    public void reset() {
        for (int i = 0; i < 16; i++) {
            keys[i] = false;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int chip8Key = mapKey(e.getKeyChar());
        if (chip8Key != -1) {
            press(chip8Key);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int chip8Key = mapKey(e.getKeyChar());
        if (chip8Key != -1) {
            release(chip8Key);
        }
    }

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
