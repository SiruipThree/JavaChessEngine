# JavaChessEngine

A 2-player terminal-based chess game implemented in Java with complete rule support and clean object-oriented design. The project models all legal moves and edge cases including castling, en passant, pawn promotion, resignation, and draw conditions. Designed to showcase proficiency in modular logic, rule-based systems, and data encapsulation in Java.

---

## ♟️ Features

- **Full Chess Rule Support**:
  - Legal movement for all standard pieces
  - **Castling** (king-side and queen-side)
  - **En passant** support
  - **Pawn promotion** (defaults to queen)
  - **Draws** by repetition or stalemate
  - **Resignation** handling
  - **Check/checkmate** detection

- **Object-Oriented Architecture**:
  - Separate Java classes for `Piece`, `Board`, `Move`, `Player`, etc.
  - Game state encapsulation and clean method abstraction
  - Designed for maintainability and potential future GUI/network extensions

- **Command-Line Interface**:
  - Input format: `e2 e4`, `g8 f6`, `resign`, etc.
  - Outputs board after every move and provides rule-based messages

---

## 🧪 Testing and Automation

The project includes several files for automated testing and validation:

- `input.txt` – Main test input with standard move sequences
- `output.txt` – Sample expected output
- `output_ours.txt` / `output_theirs.txt` – Comparison between your implementation and a reference
- `test.sh` – Shell script to automate test runs and diff outputs
- `test_input.txt`, `test.txt` – Auxiliary testing utilities
- `gen.py` – Python-based input generator

### 🔁 Run automated tests

```bash
sh test.sh
````

---

## 📁 File Structure

```
chess/              # Java source code (core logic)
gen.py              # Test input generator (optional)
input.txt           # Sample input test case
output.txt          # Expected output sample
output_ours.txt     # Your output after running tests
output_theirs.txt   # Reference implementation output
test.sh             # Shell script to compare outputs
test.txt            # Manual test case file
test_input.txt      # Auxiliary input for testing
```

---

## 🚀 How to Compile & Run

### Step 1: Compile

```bash
javac chess/*.java
```

### Step 2: Run

```bash
java chess/Chess
```

### Step 3: Play by entering moves like:

```
e2 e4
g8 f6
resign
```

---

## 🧠 Technical Highlights

* Built in **pure Java** without third-party dependencies
* Uses **polymorphism and inheritance** for piece behavior
* Encapsulates board and piece state to ensure modularity
* Supports robust **input validation and game-end conditions**

---

## ✅ Summary

This project demonstrates:

* Mastery of object-oriented programming in Java
* Handling of complex rule-based logic
* Command-line game engine development
* Automated testing and debugging strategy

It serves as a solid foundation for expanding into GUI interfaces, multiplayer networking, or chess AI.

