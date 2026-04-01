package com.sidhu.chip8.ui;

import com.sidhu.chip8.core.Chip8;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.File;

/**
 * Main application window and game loop for the CHIP-8 Emulator.
 */
public class GameWindow {
    private static final Logger log = LoggerFactory.getLogger(GameWindow.class);

    // CHIP-8 timers strictly run at 60Hz
    private static final double TARGET_HZ = 60.0;
    private static final double TIME_PER_TICK_NS = 1_000_000_000.0 / TARGET_HZ;

    // CPU execution speed. Modern CHIP-8 specs suggest ~500-700 MHz for emulation.
    // We execute 10 instructions per 60Hz tick -> ~600 CPU instructions per second.
    private static final int INSTRUCTIONS_PER_TICK = 10;

    public static void main(String[] args) {
        log.info("Starting CHIP-8 Emulator UI...");

        // Setup native UI Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.warn("Could not set system look and feel.", e);
        }

        // Initialize core architecture
        Display display = new Display(12); // Scaling by 12 gives a nice 768x384 window
        Keypad keypad = new Keypad();
        Chip8 chip8 = new Chip8(display, keypad);

        // Build JFrame
        JFrame window = new JFrame("CHIP-8 Emulator");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(display);
        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.addKeyListener(keypad);
        window.setFocusable(true);
        window.requestFocus();

        // Load ROM via arg or File Chooser
        File romFile = null;
        if (args.length > 0) {
            romFile = new File(args[0]);
        } else {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            fileChooser.setDialogTitle("Select a CHIP-8 ROM");
            if (fileChooser.showOpenDialog(window) == JFileChooser.APPROVE_OPTION) {
                romFile = fileChooser.getSelectedFile();
            }
        }

        if (romFile == null || !romFile.exists()) {
            log.error("No valid ROM selected. Exiting.");
            JOptionPane.showMessageDialog(window, "No valid ROM selected. The emulator will close.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        chip8.loadRom(romFile);
        window.setTitle("CHIP-8 Emulator - " + romFile.getName());
        window.setVisible(true);

        // Start Fixed-Timestep Loop
        runGameLoop(chip8);
    }

    /**
     * Executes the main emulator loop using a fixed-timestep algorithm to ensure accurate emulation speed.
     *
     * @param chip8 The core engine instance to cycle.
     */
    private static void runGameLoop(Chip8 chip8) {
        long lastTime = System.nanoTime();
        double delta = 0;

        log.info("Game loop started running at {} Hz.", TARGET_HZ);

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / TIME_PER_TICK_NS;
            lastTime = now;

            // Process a 60Hz tick
            if (delta >= 1) {
                for (int i = 0; i < INSTRUCTIONS_PER_TICK; i++) {
                    chip8.cycle();
                }

                // Decrement timers strictly at 60hz
                chip8.getCpu().decrementTimers();
                
                delta--;
            } else {
                // Yield thread to prevent 100% CPU lock in a busy-wait scenario
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Game loop was interrupted", e);
                    break;
                }
            }
        }
    }
}
