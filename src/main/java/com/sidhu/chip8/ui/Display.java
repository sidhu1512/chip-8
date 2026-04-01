package com.sidhu.chip8.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Handles the 64x32 monochrome display mapping and renders the graphics using Swing.
 */
public class Display extends JPanel {
    public static final int CHIP8_WIDTH = 64;
    public static final int CHIP8_HEIGHT = 32;
    
    private final int scale;
    private final boolean[][] pixels = new boolean[CHIP8_WIDTH][CHIP8_HEIGHT];

    public Display(int scale) {
        this.scale = scale;
        setPreferredSize(new Dimension(CHIP8_WIDTH * scale, CHIP8_HEIGHT * scale));
        setBackground(Color.BLACK);
    }

    public void clear() {
        for (int x = 0; x < CHIP8_WIDTH; x++) {
            for (int y = 0; y < CHIP8_HEIGHT; y++) {
                pixels[x][y] = false;
            }
        }
        repaint();
    }

    /**
     * Transforms and draws a given sprite into the display buffer.
     * Calculates wrapping according to CHIP-8 standards.
     * 
     * @param x X coordinate
     * @param y Y coordinate
     * @param sprite Array of integers where each int represents an 8-bit row.
     * @return true if a pixel collision occurred (used for the CPU's VF register)
     */
    public boolean drawSprite(int x, int y, int[] sprite) {
        boolean collision = false;

        for (int row = 0; row < sprite.length; row++) {
            int currentByte = sprite[row];
            for (int col = 0; col < 8; col++) {
                int pixelX = (x + col) % CHIP8_WIDTH;
                int pixelY = (y + row) % CHIP8_HEIGHT;

                // Extract the specific bit for this pixel
                boolean spriteBit = (currentByte & (0x80 >> col)) != 0;

                if (spriteBit) {
                    if (pixels[pixelX][pixelY]) {
                        collision = true;
                    }
                    pixels[pixelX][pixelY] ^= true;
                }
            }
        }

        repaint();
        return collision;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);

        for (int x = 0; x < CHIP8_WIDTH; x++) {
            for (int y = 0; y < CHIP8_HEIGHT; y++) {
                if (pixels[x][y]) {
                    g.fillRect(x * scale, y * scale, scale, scale);
                }
            }
        }
    }
}
