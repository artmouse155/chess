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
void main(String[] args) {
    
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

#### Benefits of Encapsulation

- Allows you to change the implementation in the backend easily
- Allows you to filter input to the class

### The Equal Operator

- The equal operator does NOT call equals.
- The equal operator checks memory.

### Records

- Records help us create **immutable classes**.
  - Usually, to make classes immutable, we use `private final` to describe immutable fields
  - But the shortcut for this is `Records`!!
  - Use Records for `UserData` and other classes we write later

### Phase 2 Unit Tests

**Important:** Every test case must have an assert statement.

#### KEY
- ❌ = should throw an error
- ✅ = should not throw an error

#### DELETE /db

- [x] ✅ Delete with no data in DB
- [x] ✅ Delete users and games

#### POST /user

- [x] ❌ Invalid `username` *2
- [x] ❌ Invalid `password` *2
- [x] ❌ Invalid `email` *2
- [x] ✅ Correct `username` and `password`

#### POST /session

- [x] ❌ Invalid `username` *2
- [x] ❌ Invalid `password` *2
- [x] ❌ Nonexistent `username`
- [x] ❌ Correct `username` and incorrect `password`
- [x] ✅ Correct `username` and `password`

#### AUTHENTICATE

- [x] ❌ Invalid `authToken` *3
- [x] ✅ Valid `authToken`

#### DELETE /session

- [x] ❌ Double logout
- [x] ❌ Logout with wrong authToken
- [x] ❌ Login session 1, logout session 1, attempt to authenticate with session 1
- [x] ✅ Login then logout

> Note: None of the /game endpoints test authentication because authentication is a separate method from the /game functions.

#### GET /game

- [x] ❌ A failed game creation does not update the list of games
- [x] ✅ Creating a game and getting it

#### CREATE /game

- [x] ❌ Invalid `gameName` *2
- [x] ✅ Create 2 games with same gameName Note: This is fine because game name does not distinguish the games; it is the gameID that does.
- [x] ✅ Creating 3 games

#### JOIN /game

- [ ] ❌ Invalid `playerColor` *2
- [ ] ❌ Invalid `gameID`
- [ ] ❌ `gameID` is the wrong type (Object, List, etc.)
- [ ] ❌ `gameID` is negative
- [ ] ❌ `gameID` is a VERY large number (greater than size of an int)
- [ ] ❌ `gameID` is a double
- [ ] ❌ Join a game in an occupied spot
- [ ] ❌ Same player tries to be both black and white
- [ ] ✅ A long combo involving creating, updating adding / removing games, with `GET /game` at the end

Developed by Chase Odom
