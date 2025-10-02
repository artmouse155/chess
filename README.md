# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security. This project is being created by Chase Odom.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAsCGBnSByZd6WZgypAE4BuR0AIjiAOYB2AUPYgMbAD2hc4Iktw9AB0SFQzEEL7QCJIoOGjxiSQAklAEyiE5IkGInApRYrsjaF+iomCIAgs2Y5k9NVcQAjFDDVvGvYIQBPZCExWmpoAAYAOgBGempCNgBXAQBiABYAZgAOACYATmZoVO5qeGA3cCSYACVIahBkfysQNgZYbj9oAFoAPkMZQgAuaABtAAUAeTwAFQBdaAB6JNRCAB1aAG8AIhWiWkQAW0htoe3obYAaC6FcAHcONVPzq4vIQ8QQcGeLgF96aSkTh9aCqWgaIgjHZ7QgHY4-V7bW7IB6EJ5nC7XbbvT7fDHbf5giHA-qAkwjQj1RrAIgACjqDSaRDqAEdqk0AJQAowmHr9ciuOwOXAjaiQYAAVVWtJhcMgXIF1iFjj50G8QwAYiBwdApURFYhoG4AtBZUdTG5KYgANa61bQO5geCm1Zy6CIcBWtQmyAAD2pTgNytwPRBZIcIz1hAN3JIvO6YaMkOgNk9kEQ3pmNt4AFFfQ4BKA2rGgaH+h0eHwRukIpkNjtjrhEGLThcc4REsMXftze60xmTdZrbwCfReGp6EH7CqE6SeRHoLQkuBwCX4-zBdORcwrTSozLVgaFZvhZgQeqbGo1Hb9a41w5VVPT0Md+maTYksB4LTEJ-4EfJxPGd+gvK8Uz-GNwxgWdQXUTQRgZalmRwZd+CJTQywGIEoV2V1zXOEYXixX8vxmNhh1oAiLj+EsyBgis-BGXIIgietcJ7eFoEIzELhI+AyIoqiXn+cdGASZI0g4JQxWKDVYBzchYBsaAABk2AadpOkkRNBhGCZpnmJZUFwVpaHrM1jmuZFUTUETwTExIUlSBJIF4YpSnKFy3LU6hkn4BjtLnXSxnIHMVJzGYcwWRZjOQUyNj4gTeDHez4kctJKWvVIBG1W0VOpaAAHFzScAKDB07CxkKiLouoc0Er-JKGFEtKJNSNQAHZ8giSAImKHNMlgeSADY4F3GBiuOaB6DK1VAWTfTZlq+qDka8jeHrOrjgAOXNOyJ1apyAkgFc2DudyaHKY7TvOgApNhtSK3tUkqFhrRmrTyqCyqJglQzFi2yAGtI9azK2ARwEQY7CFgNhwA4a5AYASXIfaHLahIoYuspgExk0OnTThEAECHdBaNpptmiqFtC8LIui7wUonIA)

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
