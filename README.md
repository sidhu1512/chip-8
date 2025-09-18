# Java Chip-8 Emulator

A fully functional emulator for the Chip-8, a simple interpreted programming language from the 1970s. This project faithfully re-creates the Chip-8 virtual machine in Java, allowing it to run classic 8-bit games and programs.

This emulator was built as a deep dive into computer architecture, low-level memory management, and CPU instruction sets.

![Chip-8 Emulator Screenshot](https://i.imgur.com/example.png)  ---

### So, What is Chip-8?

Chip-8 is a simple, interpreted, programming language which was first used on the COSMAC VIP and Telmac 1800 8-bit microcomputers in the mid-1970s. It's similar to a virtual machine or a simple "fantasy console." Because of its simplicity, writing an emulator for it is a popular and classic computer science challenge—a "Hello, World!" for emulator developers.

---

### ✨ Features Implemented

This emulator implements all the core components of the Chip-8 virtual machine:

- **CPU Emulation:** A main loop that accurately fetches, decodes, and executes opcodes.
- **Full Opcode Set:** Complete implementation of all 35 Chip-8 opcodes, handling everything from screen drawing to memory operations.
- **Memory:** A 4KB memory module to store the loaded game (ROM), screen data, and system state.
- **Graphics:** A 64x32 pixel monochrome display, rendered to the screen using Java Swing.
- **Input:** A 16-key hexadecimal keypad, mapped to the user's keyboard for game input.
- **Timers:** Both delay and sound timers, implemented to run at the specified 60Hz.

---

### 🛠️ Technology Stack

- **Core Language:** **Java**
- **User Interface:** **Java Swing** for creating the display window and handling keyboard input.

---

### 🚀 How to Run

**1. Prerequisites:**
- You must have Java Development Kit (JDK) 8 or higher installed.

**2. Clone the Repository:**
```bash
git clone [https://github.com/sidhu1215/chip-8.git](https://github.com/sidhu1215/chip-8.git)
cd chip-8
```

**3. Compile the Code:**
```bash
# Navigate to the source directory
cd src
# Compile all Java files
javac com/sidhu/chip8/*
```

**4. Run the Emulator:**
You need a Chip-8 ROM file to run. You can find many classic ROMs for free online (search for "Chip-8 ROMs").

Run the emulator from the `src` directory, passing the path to your ROM file as an argument:

```bash
java com.sidhu.chip8.Main path/to/your/rom/file.ch8
```
**Example:**
```bash
java com.sidhu.chip8.Main ../roms/PONG
```

**5. Keypad Mapping:**
The original Chip-8 had a 16-key keypad. This emulator maps it to your keyboard as follows:
```
1 2 3 C  ->  1 2 3 4
4 5 6 D  ->  Q W E R
7 8 9 E  ->  A S D F
A 0 B F  ->  Z X C V
```

---

### Project Learnings

This project was a fantastic exercise in understanding the fundamentals of how a computer works at a low level, including:
- The CPU fetch-decode-execute cycle.
- Direct memory manipulation and memory-mapped I/O.
- The relationship between timers, graphics, and input in a simple system.
