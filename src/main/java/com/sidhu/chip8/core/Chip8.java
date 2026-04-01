package com.sidhu.chip8.core;

import com.sidhu.chip8.ui.Display;
import com.sidhu.chip8.ui.Keypad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * The main emulator core that orchestrates the CPU, Memory, Display, and Keypad.
 * Responsible for fetching, decoding, and executing the CHIP-8 instruction set.
 */
public class Chip8 {
    private static final Logger log = LoggerFactory.getLogger(Chip8.class);

    private final Memory memory;
    private final Cpu cpu;
    private final Display display;
    private Keypad keypad;

    public Chip8(Display display, Keypad keypad) {
        this.memory = new Memory();
        this.cpu = new Cpu();
        this.display = display;
        this.keypad = keypad;
        log.info("CHIP-8 emulator initialized.");
    }

    public void setKeypad(Keypad keypad) {
        this.keypad = keypad;
    }

    public Cpu getCpu() {
        return cpu;
    }

    public Memory getMemory() {
        return memory;
    }

    public Display getDisplay() {
        return display;
    }

    /**
     * Loads a ROM file directly into memory.
     *
     * @param file the CHIP-8 ROM file
     */
    public void loadRom(File file) {
        try {
            byte[] program = Files.readAllBytes(file.toPath());
            memory.loadProgram(program);
            log.info("Loaded ROM: {} ({} bytes)", file.getName(), program.length);
        } catch (IOException e) {
            log.error("Failed to load ROM: {}", file.getAbsolutePath(), e);
        }
    }

    /**
     * Executes a single CPU cycle: fetch opcode, decode, and execute.
     */
    public void cycle() {
        // Fetch Opcode
        int opcode = memory.readOpcode(cpu.getPc());

        // Decode parts of the opcode
        int instruction = (opcode & 0xF000) >> 12;
        int x = (opcode & 0x0F00) >> 8;
        int y = (opcode & 0x00F0) >> 4;
        int lastNibble = opcode & 0x000F;
        int nn = opcode & 0x00FF;
        int nnn = opcode & 0x0FFF;

        if (log.isTraceEnabled()) {
            log.trace("PC: 0x{} | Opcode: 0x{}", String.format("%03X", cpu.getPc()), String.format("%04X", opcode));
        }

        // Execute Instruction
        switch (instruction) {
            case 0x0 -> {
                switch (nn) {
                    case 0xE0 -> { // 00E0: Clear screen
                        display.clear();
                        cpu.incrementPc(2);
                    }
                    case 0xEE -> { // 00EE: Return from subroutine
                        cpu.setPc(cpu.popStack());
                        cpu.incrementPc(2);
                    }
                    default -> logUnknownOpcode(opcode);
                }
            }
            case 0x1 -> cpu.setPc(nnn); // 1NNN: Jump to address NNN
            case 0x2 -> { // 2NNN: Call subroutine at NNN
                cpu.pushStack(cpu.getPc());
                cpu.setPc(nnn);
            }
            case 0x3 -> { // 3XNN: Skip next instruction if Vx == NN
                if (cpu.getRegister(x) == nn) cpu.incrementPc(4);
                else cpu.incrementPc(2);
            }
            case 0x4 -> { // 4XNN: Skip next instruction if Vx != NN
                if (cpu.getRegister(x) != nn) cpu.incrementPc(4);
                else cpu.incrementPc(2);
            }
            case 0x5 -> { // 5XY0: Skip next instruction if Vx == Vy
                if (lastNibble == 0 && cpu.getRegister(x) == cpu.getRegister(y)) cpu.incrementPc(4);
                else cpu.incrementPc(2);
            }
            case 0x6 -> { // 6XNN: Set Vx = NN
                cpu.setRegister(x, nn);
                cpu.incrementPc(2);
            }
            case 0x7 -> { // 7XNN: Add NN to Vx
                cpu.setRegister(x, cpu.getRegister(x) + nn);
                cpu.incrementPc(2);
            }
            case 0x8 -> { // 8 Series Logical operations
                switch (lastNibble) {
                    case 0x0 -> cpu.setRegister(x, cpu.getRegister(y)); // Vx = Vy
                    case 0x1 -> cpu.setRegister(x, cpu.getRegister(x) | cpu.getRegister(y)); // Vx |= Vy
                    case 0x2 -> cpu.setRegister(x, cpu.getRegister(x) & cpu.getRegister(y)); // Vx &= Vy
                    case 0x3 -> cpu.setRegister(x, cpu.getRegister(x) ^ cpu.getRegister(y)); // Vx ^= Vy
                    case 0x4 -> { // Vx += Vy, set VF if carry
                        int sum = cpu.getRegister(x) + cpu.getRegister(y);
                        cpu.setRegister(x, sum);
                        cpu.setFlag(sum > 255 ? 1 : 0);
                    }
                    case 0x5 -> { // Vx -= Vy, set VF if no borrow
                        int sub = cpu.getRegister(x) - cpu.getRegister(y);
                        cpu.setRegister(x, sub);
                        cpu.setFlag(sub >= 0 ? 1 : 0);
                    }
                    case 0x6 -> { // Vx >>= 1, set VF to LSB
                        int vx = cpu.getRegister(x);
                        cpu.setRegister(x, vx >> 1);
                        cpu.setFlag(vx & 0x1);
                    }
                    case 0x7 -> { // Vx = Vy - Vx, set VF if no borrow
                        int sub = cpu.getRegister(y) - cpu.getRegister(x);
                        cpu.setRegister(x, sub);
                        cpu.setFlag(sub >= 0 ? 1 : 0);
                    }
                    case 0xE -> { // Vx <<= 1, set VF to MSB
                        int vx = cpu.getRegister(x);
                        cpu.setRegister(x, vx << 1);
                        cpu.setFlag((vx & 0x80) >> 7);
                    }
                    default -> logUnknownOpcode(opcode);
                }
                cpu.incrementPc(2);
            }
            case 0x9 -> { // 9XY0: Skip next instruction if Vx != Vy
                if (lastNibble == 0) {
                    if (cpu.getRegister(x) != cpu.getRegister(y)) cpu.incrementPc(4);
                    else cpu.incrementPc(2);
                } else {
                    cpu.incrementPc(2);
                }
            }
            case 0xA -> { // ANNN: Set I = NNN
                cpu.setI(nnn);
                cpu.incrementPc(2);
            }
            case 0xB -> cpu.setPc(nnn + cpu.getRegister(0)); // BNNN: Jump to address NNN + V0
            case 0xC -> { // CXNN: Set Vx = Random byte & NN
                int rand = (int) (Math.random() * 256);
                cpu.setRegister(x, rand & nn);
                cpu.incrementPc(2);
            }
            case 0xD -> { // DXYN: Draw sprite at Vx, Vy with height N
                int vx = cpu.getRegister(x);
                int vy = cpu.getRegister(y);
                int[] sprite = new int[lastNibble];
                for (int i = 0; i < lastNibble; i++) {
                    sprite[i] = memory.read(cpu.getI() + i);
                }
                boolean collision = display.drawSprite(vx, vy, sprite);
                cpu.setFlag(collision ? 1 : 0);
                cpu.incrementPc(2);
            }
            case 0xE -> { 
                switch (nn) {
                    case 0x9E -> { // EX9E: Skip next instruction if key in Vx is pressed
                        if (keypad.isPressed(cpu.getRegister(x))) cpu.incrementPc(4);
                        else cpu.incrementPc(2);
                    }
                    case 0xA1 -> { // EXA1: Skip next instruction if key in Vx is NOT pressed
                        if (!keypad.isPressed(cpu.getRegister(x))) cpu.incrementPc(4);
                        else cpu.incrementPc(2);
                    }
                    default -> logUnknownOpcode(opcode);
                }
            }
            case 0xF -> {
                switch (nn) {
                    case 0x07 -> { // FX07: Set Vx = delay timer
                        cpu.setRegister(x, cpu.getDelayTimer());
                        cpu.incrementPc(2);
                    }
                    case 0x15 -> { // FX15: Set delay timer = Vx
                        cpu.setDelayTimer(cpu.getRegister(x));
                        cpu.incrementPc(2);
                    }
                    case 0x18 -> { // FX18: Set sound timer = Vx
                        cpu.setSoundTimer(cpu.getRegister(x));
                        cpu.incrementPc(2);
                    }
                    case 0x1E -> { // FX1E: Add Vx to I
                        cpu.incrementI(cpu.getRegister(x));
                        cpu.incrementPc(2);
                    }
                    case 0x0A -> { // FX0A: Wait for a key press, store in Vx
                        boolean keyPressed = false;
                        for (int i = 0; i < 16; i++) {
                            if (keypad.isPressed(i)) {
                                cpu.setRegister(x, i);
                                keyPressed = true;
                                break;
                            }
                        }
                        if (!keyPressed) {
                            return; // Don't advance PC, block execution here
                        }
                        cpu.incrementPc(2);
                    }
                    case 0x29 -> { // FX29: Set I to location of sprite for character in Vx
                        cpu.setI(cpu.getRegister(x) * 5); // Each char is 5 bytes
                        cpu.incrementPc(2);
                    }
                    case 0x33 -> { // FX33: Store BCD representation of Vx in memory locations I, I+1, I+2
                        int value = cpu.getRegister(x);
                        memory.write(cpu.getI(), value / 100);
                        memory.write(cpu.getI() + 1, (value / 10) % 10);
                        memory.write(cpu.getI() + 2, value % 10);
                        cpu.incrementPc(2);
                    }
                    case 0x55 -> { // FX55: Store registers V0 through Vx in memory starting at I
                        for (int i = 0; i <= x; i++) {
                            memory.write(cpu.getI() + i, cpu.getRegister(i));
                        }
                        cpu.incrementPc(2);
                    }
                    case 0x65 -> { // FX65: Read registers V0 through Vx from memory starting at I
                        for (int i = 0; i <= x; i++) {
                            cpu.setRegister(i, memory.read(cpu.getI() + i));
                        }
                        cpu.incrementPc(2);
                    }
                    default -> logUnknownOpcode(opcode);
                }
            }
            default -> logUnknownOpcode(opcode);
        }
    }

    private void logUnknownOpcode(int opcode) {
        log.warn("Unknown instruction: 0x{} at PC: 0x{}", 
                String.format("%04X", opcode), 
                String.format("%03X", cpu.getPc()));
        cpu.incrementPc(2); // Skip it to avoid infinite loop
    }
}
