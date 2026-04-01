package com.sidhu.chip8.exception;

/**
 * Custom runtime exception to handle operational failures within the emulator
 * such as memory out-of-bounds, invalid opcode fetching, or file loading errors.
 */
public class EmulatorException extends RuntimeException {

    public EmulatorException(String message) {
        super(message);
    }

    public EmulatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
