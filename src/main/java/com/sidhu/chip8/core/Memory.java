package com.sidhu.chip8.core;

import com.sidhu.chip8.exception.EmulatorException;
import java.util.Arrays;

/**
 * Represents the 4KB RAM (4096 bytes) of the CHIP-8 system.
 */
public class Memory {

    public static final int MEMORY_SIZE = 4096;
    public static final int PROGRAM_START_ADDRESS = 0x200;

    // Internal representation of 4KB memory using int arrays to avoid Java signed byte issues.
    private final int[] memory;

    /**
     * Built-in font sprites representing hexadecimal characters 0-F.
     * Each character is 5 bytes tall.
     */
    private static final int[] FONT_SET = {
            0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
            0x20, 0x60, 0x20, 0x20, 0x70, // 1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
            0x90, 0x90, 0xF0, 0x10, 0x10, // 4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
            0xF0, 0x10, 0x20, 0x40, 0x40, // 7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
            0xF0, 0x90, 0xF0, 0x90, 0x90, // A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
            0xF0, 0x80, 0x80, 0x80, 0xF0, // C
            0xE0, 0x90, 0x90, 0x90, 0xE0, // D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
            0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };

    public Memory() {
        this.memory = new int[MEMORY_SIZE];
        reset();
    }

    public void reset() {
        Arrays.fill(this.memory, 0);
        // Load fonts into memory starting at address 0x000 (0)
        for (int i = 0; i < FONT_SET.length; i++) {
            this.memory[i] = FONT_SET[i];
        }
    }

    /**
     * Read an unsigned byte (0-255) from the specified address.
     */
    public int read(int address) {
        validateAddress(address);
        return memory[address];
    }

    /**
     * Write an unsigned byte (0-255) to the specified address.
     */
    public void write(int address, int value) {
        validateAddress(address);
        memory[address] = value & 0xFF; // Ensure it stays within an 8-bit boundary
    }

    /**
     * Read a 16-bit opcode starting from the provided address.
     */
    public int readOpcode(int address) {
        int highByte = read(address);
        int lowByte = read(address + 1);
        return (highByte << 8) | lowByte;
    }

    /**
     * Loads a program (ROM) into memory starting at 0x200.
     *
     * @param program byte array representing the ROM.
     * @throws EmulatorException if program exceeds the available memory buffer.
     */
    public void loadProgram(byte[] program) {
        if (PROGRAM_START_ADDRESS + program.length > MEMORY_SIZE) {
            throw new EmulatorException("ROM is too large to fit in memory (size: " + program.length + " bytes)");
        }
        for (int i = 0; i < program.length; i++) {
            write(PROGRAM_START_ADDRESS + i, program[i] & 0xFF);
        }
    }

    private void validateAddress(int address) {
        if (address < 0 || address >= MEMORY_SIZE) {
            throw new EmulatorException(String.format("Memory address out of bounds: 0x%04X", address));
        }
    }
}
