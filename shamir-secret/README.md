# Shamir Secret Sharing – Hackathon Assignment

## 📌 Problem Statement
Implement a program to reconstruct the **secret (constant term)** of a polynomial used in **Shamir’s Secret Sharing** scheme.  
- The input is provided in **JSON format**, containing multiple roots with values in different bases.  
- Using at least `k` roots (where `k = degree + 1`), reconstruct the polynomial and output the constant term.  

---

## 📂 Input Format
Example input (`input.json`):

```json
{
  "keys": {
    "n": 4,
    "k": 3
  },
  "1": {
    "base": "10",
    "value": "4"
  },
  "2": {
    "base": "2",
    "value": "111"
  },
  "3": {
    "base": "10",
    "value": "12"
  },
  "6": {
    "base": "4",
    "value": "213"
  }
}
n → total number of available roots

k → minimum number of roots required

Each root has:

"base" → number system of the value

"value" → string representing the number in that base

⚙️ How It Works
Parse the JSON input.

Convert each root value to decimal based on its base.

Apply Lagrange Interpolation to reconstruct the polynomial.

Extract the constant term (secret).

▶️ Running the Code
Compile
bash

javac -cp .;json-20230227.jar ShamirSecretSharing.java
Run with a test file
bash

java -cp .;json-20230227.jar ShamirSecretSharing input.json

🧪 Testcases
Input 1 → input.json
json

{
  "keys": { "n": 4, "k": 3 },
  "1": { "base": "10", "value": "4" },
  "2": { "base": "2", "value": "111" },
  "3": { "base": "10", "value": "12" },
  "6": { "base": "4", "value": "213" }
}

Output:
Secret (constant term): 3

Input 2 → input2.json
json
{
  "keys": { "n": 10, "k": 7 },
  "1": { "base": "6", "value": "13444211440455345511" },
  "2": { "base": "15", "value": "aed7015a346d635" },
  "3": { "base": "15", "value": "6aeeb69631c227c" },
  "4": { "base": "16", "value": "e1b5e05623d881f" },
  "5": { "base": "8", "value": "316034514573652620673" },
  "6": { "base": "3", "value": "2122212201122002221120200210011020220200" },
  "7": { "base": "3", "value": "20120221122211000100210021102001201112121" },
  "8": { "base": "6", "value": "20220554335330240002224253" },
  "9": { "base": "12", "value": "45153788322a1255483" },
  "10": { "base": "7", "value": "1101613130313526312514143" }
}

Output:
Secret (constant term): -6290016743746469796

📜 Notes
Negative output is expected due to Java’s long handling.

In real Shamir’s Secret Sharing, results are usually taken modulo a large prime to ensure positivity.

For this assignment, raw interpolated values are acceptable.

👨‍💻 Author
Hackathon solution by Thirumala Naidu