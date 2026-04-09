# ⚙️ Regex to DFA Converter & String Validator

A Java-based implementation of core Automata Theory concepts. This application converts **Regular Expressions (Infix)** into **Non-deterministic Finite Automata (NFA)** using **Thompson’s Construction**, then transforms them into **Deterministic Finite Automata (DFA)** using **Subset Construction** for high-speed string validation.

---

## 📖 1. Introduction
Automata Theory is fundamental to compiler design and pattern matching. While regular expressions describe patterns, computers utilize finite automata for execution. This project bridges that gap, providing a menu-driven Java tool to visualize state transitions and validate input strings against custom regex rules.

## 🎯 2. Objectives
* **Infix to Postfix:** Convert standard regex into a format suitable for stack-based processing.
* **NFA Construction:** Implement **Thompson’s Construction** to handle ε-transitions and basic operators.
* **DFA Transformation:** Apply **Subset Construction** and **ε-closures** to create a deterministic machine.
* **Validation:** Efficiently test whether specific strings are accepted or rejected by the generated DFA.

## 🛠 3. Tools and Technologies
* **Language:** Java (JDK 8 or higher)
* **Environment:** Console-based / Terminal
* **IDE:** IntelliJ IDEA, Eclipse, or VS Code
* **Theory:** Formal Languages, Thompson’s Construction, Subset Construction

---

## 🏗 4. System Architecture
The application follows a modular pipeline to ensure data integrity at each transformation stage:

1.  **Regex Input:** User provides an infix expression (e.g., `a(a|b)*`).
2.  **Infix to Postfix:** Automatically inserts concatenation operators (`.`) and reorders using a stack.
3.  **NFA Module:** Builds a fragment-based NFA with unique state IDs.
4.  **ε-Closure Generator:** Calculates reachable states via empty transitions.
5.  **DFA Module:** Generates a transition table and identifies final (accept) states.
6.  **Validation:** Simulates the DFA path for any user-provided string.

---

## 🚀 5. Features & Supported Syntax
* **Concatenation (`.`):** Implicitly handled (e.g., `ab` becomes `a.b`).
* **Union (`|`):** Standard OR operation.
* **Kleene Star (`*`):** Zero or more occurrences.
* **Parentheses `()`:** Overrides default operator precedence.
* **Alphabet:** Supports lowercase letters `a-z`.

---

## ⚙️ 6. Setup and Installation

### **Prerequisites**
* **Java Development Kit (JDK):** Ensure you have JDK 8 or higher installed.
* Check your version by running: `java -version`.

### **Installation & Execution**
1.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/your-username/your-repo-name.git](https://github.com/your-username/your-repo-name.git)
    ```
2.  **Navigate to Source:**
    ```bash
    cd your-repo-name/src
    ```
3.  **Compile the Program:**
    ```bash
    javac Main.java
    ```
4.  **Run the Application:**
    ```bash
    java Main
    ```

---

## 📊 7. Example Usage
1.  **Input Regex:** `a|b*`
2.  **Internal Postfix:** `ab*|`
3.  **DFA Transitions:** The console will print the state-to-state mapping (e.g., `DFA 0 --a--> DFA 1`).
4.  **Test Strings:**
    * `a` -> **ACCEPTED**
    * `bb` -> **ACCEPTED**
    * `c` -> **REJECTED** (Not in alphabet)

---

## ⚠️ 8. Limitations
* Visualization is currently text-based (printed transition table).
* Limited to the lowercase English alphabet (`a-z`).
* Does not support advanced regex features like `+` (plus) or `?` (optional) yet.

## 📜 9. References
* *Hopcroft, Motwani, & Ullman*: Introduction to Automata Theory.
* *Aho, Lam, Sethi, & Ullman*: Compilers: Principles, Techniques, and Tools (The Dragon Book).

---
Created by : Muhammad Uzair Ali : P
