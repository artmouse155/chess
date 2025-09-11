# Notes

## Phase 0: Chess Moves

The tests use a textual representation of a gameboard.

- Uppercase letters are white pieces
- Lowercase letters are black pieces
- Chess positions are given by `(rowsFromBottom / row #,ColumnsFromLeft / col #)`
  - For example, `ChessPosition(5, 4)` refers to the position 5 rows up from the bottom of the board and 4 columns right from the left of the board

## Java Fundamentals

### Entry

Java has a special signature entry point. We can put it in any class that we like.
```java
public static void main(String[] args) {
    
}
```

### Class constructors

Constructors can be empty, parameterized, or a copy constructor. A **copy constructor** makes a copy of an object to disassociate it in memory

- named same as class name

### Enumerations

Creates static values
- `Enum.valueOf` will try to match a string to an enumeration
- A great way to avoid string typo errors

### Design Principle: Encapsulation

- Create a **boundary** between internal code and external code
- Prevents external code from being concerned with the internal workings of an object
- Put it in a **capsule**.

Developed by Chase Odom
