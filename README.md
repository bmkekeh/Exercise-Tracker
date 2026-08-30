# 🏃 Exercise Tracker

A Java-based exercise tracking application for creating user profiles, recording activities, following other users, managing exercise routes, and finding paths through a grid-based map.

The project demonstrates **object-oriented programming, layered architecture, custom data structures, pathfinding, exception handling, and software testing**.

---

## ✨ Features

- Create and select user profiles
- Update user profile information
- Record exercise activities
- Support multiple exercise types: running, walking, biking, and swimming
- Follow and unfollow other users
- View activities from followed users in an activity feed
- Create routes using grid coordinates
- Duplicate routes from previous activities
- Add and remove obstacles from the map
- Find routes between two points using pathfinding
- Validate user input and application state
- Handle application errors with custom exceptions

---

## 🛠️ Technologies & Concepts

- **Java**
- **Maven**
- **Google Guava**
- Object-Oriented Programming
- Layered Architecture
- Design by Contract
- Data Structures
- Pathfinding
- Exception Handling
- Software Testing
- Mermaid

---

## 🏗️ Project Architecture

The application separates user interaction, business logic, domain objects, and output formatting into different components.

```text
Exercise Tracker
│
├── Application
│   └── Main
│
├── UI
│   └── ConsoleUI
│
├── Services
│   ├── UserService
│   ├── ActivityService
│   └── MapService
│
├── Domain
│   ├── ExerciseTypeTracker
│   ├── User
│   ├── Activity
│   ├── Path
│   ├── Point
│   ├── Grid
│   └── ExerciseType
│
├── Pathfinding
│   └── PathFinder
│
├── Data Structures
│   ├── Stack
│   ├── LinkedStack
│   └── StackNode
│
├── Output
│   ├── GridPrinter
│   ├── ActivityPrinter
│   └── ObstaclePrinter
│
└── Exceptions
    ├── InvalidInputException
    ├── UserNotFoundException
    ├── ActivityNotFoundException
    └── PathNotFoundException
```

### Layered Design

The application is organized so that each part has a clear responsibility:

- **UI Layer** — handles user input and the console command loop.
- **Service Layer** — handles application and business logic.
- **Domain Layer** — represents users, activities, paths, grids, and other core objects.
- **Output Layer** — formats and displays application data.
- **Exception Layer** — provides application-specific error handling.

This structure keeps business logic separate from user interaction and output formatting.

---

## 🏃 Exercise Activities

Users can create activities containing:

- An exercise type
- Duration
- A route represented by a `Path`
- Grid information

Supported exercise types include:

```text
RUN
WALK
BIKE
SWIM
```

Users can also duplicate routes from previous activities.

---

## 👥 Social Features

Users can follow other users in the application.

Each `User` maintains a list of followed users, allowing the application to build an activity feed containing activities from those users.

The application supports:

- Following users
- Unfollowing users
- Viewing followed users
- Viewing an activity feed

---

## 🗺️ Route & Map System

Exercise routes are represented using a collection of `Point` objects.

```text
Path
 └── Point
     ├── X coordinate
     └── Y coordinate
```

The `Grid` represents the map and keeps track of obstacles.

Coordinates are validated to ensure they are inside the grid before they are used.

---

## 🧭 Pathfinding

The project includes a `PathFinder` responsible for finding a route between a start point and an end point while accounting for the grid and its obstacles.

```text
Start Point
     │
     ▼
Check Grid
     │
     ▼
Explore Available Points
     │
     ▼
Avoid Obstacles
     │
     ▼
Find Destination
     │
     ▼
Return Path
```

The pathfinding functionality uses the project's custom `Stack` implementation during route calculation.

---

## 📚 Custom Stack

A custom stack data structure was implemented rather than relying entirely on Java's built-in data structures.

The `Stack` interface supports:

```java
push(data)
pop()
peek()
size()
isEmpty()
```

`LinkedStack` implements the interface using linked `StackNode` objects.

```text
Stack
  ▲
  │ implements
  │
LinkedStack
  │
  ▼
StackNode → StackNode → StackNode
```

The stack follows **LIFO (Last In, First Out)** behavior.

---

## 📐 Design by Contract

The project uses class invariants, preconditions, and postconditions to help maintain valid application state.

Google Guava's `Preconditions` are used for validation.

Examples of application rules include:

- User names cannot be null or blank.
- Activities must have a duration greater than zero.
- Paths cannot contain null points.
- Grid dimensions must be greater than zero.
- Obstacles must be placed inside the grid.
- Stack size cannot be negative.
- Users cannot follow themselves.
- Users cannot follow the same user more than once.

---

## ⚠️ Custom Exceptions

Application-specific exceptions are used to communicate errors between different layers of the program.

```text
InvalidInputException
UserNotFoundException
ActivityNotFoundException
PathNotFoundException
```

This allows different types of application errors to be handled explicitly instead of relying only on generic exceptions.

---

##  📊 Diagram

Here is the diagram for my domain model


```mermaid
---
config:
layout: dagre
look: classic
theme: default
themeVariables:
fontSize: 18px
---

classDiagram

%%========================
%% Stack 
%%========================
    class Stack {
        <<interface>>
        +push(data: Object): void
        +pop(): Object
        +peek(): Object
        +size(): int
        +isEmpty(): boolean
    }

    class LinkedStack {
        -StackNode top
        -int size
    }

    class StackNode {
        -Object data
        -StackNode next
    }

    Stack <|.. LinkedStack
    LinkedStack --> StackNode
    StackNode --> StackNode

%%========================
%% Core container
%%========================

    class ExerciseTypeTracker {
        - List~User~ users
        +registerUser(u: User): void
        +removeUser(u: User): void
    }

%%========================
%% Domain Entities
%%========================

    class User{
        -String name
        -List~Activity~ activities
        -List~User~ following
        +addActivity(a: Activity): void
        +removeActivity(a: Activity): void
        +follow(other: User): void
        +unfollow(other: User): void
        +setName(newName: String): void
        +getFollowing(): List~User~
    }

    class Activity {
        -int id
        -Path path
        -int duration
        -ExerciseType type
        -Grid grid
        +setDuration(minutes: int): void
    }

    class Path {
        -List~Point~ points
        +addPoint(p: Point): void
        +isEmpty(): bool
        +validateNotEmpty(): void
        +Path(other: Path)
    }

    class Point {
        -number pointX
        -number pointY
    }

    class Grid {
        -number width
        -number height
        -List~Point~ obstacles
        +addObstacle(p: Point): void
        +removeObstacle(id: int): void
        +isInside(p: Point): bool
        +isObstacle(p: Point): bool
    }

    class PathFinder {
        -stack Stack
        +findPath(grid: Grid, coveredPoints: Set, start: Point, end: Point): Path
    }

%%========================
%% Services
%%========================

    class UserService {
        -ExerciseTypeTracker tracker
        +createUser(name: String): User
        +selectUser(name: String): User
        +updateProfile(user: User, newName: String): void
        +followUser(currentUser: User, targetName: String): void
        +unfollowUser(currentUser: User, targetName: String): void
        +getAllUsers(): List~User~
    }

    class ActivityService {
        -Grid grid
        +addActivity(user: User, path: Path, duration: int, type: ExerciseType): Activity
        +duplicateRoute(user: User, activityId: int): Path
        +getFeed(user: User): List~Activity~
        +removeActivity(user: User, activityId: int): void
    }

    class MapService {
        -Grid grid
        -PathFinder pathFinder
        +addObstacle(point: Point): void
        +removeObstacle(id: int): void
        +findRoute(start: Point, end: Point, personalOnly: bool, user: User): Path
        +getGrid(): Grid
    }

%%========================
%% Exceptions
%%========================

    class InvalidInputException {
        <<exception>>
    }

    class UserNotFoundException {
        <<exception>>
    }

    class ActivityNotFoundException {
        <<exception>>
    }

    class PathNotFoundException {
        <<exception>>
    }

%%========================
%% Enumerations
%%========================

    class ExerciseType{
        <<enumeration>>
        RUN
        WALK
        BIKE
        SWIM
    }

%%========================
%% Relationships
%%========================

    ExerciseTypeTracker o-- User : Aggregate
    User *-- Activity : composition
    User o-- User : following
    Activity --> ExerciseType : uses
    Activity *-- Grid : composition
    Activity *-- Path : composition
    Path *-- Point : composition
    Grid o-- Point : aggregates
    PathFinder --> Stack
    PathFinder --> Grid
    PathFinder --> Path
    MapService --> PathFinder
    MapService --> Grid
    ActivityService --> Grid
    UserService --> ExerciseTypeTracker
    MapService ..> PathNotFoundException : throws
    MapService ..> InvalidInputException : throws
    UserService ..> UserNotFoundException : throws
    UserService ..> InvalidInputException : throws
    ActivityService ..> ActivityNotFoundException : throws
    ActivityService ..> InvalidInputException : throws

%%========================
%% Design-by-Contract (invariants)
%%========================

    note for Stack "Interface contract:
- push(data): pre: data != null; post: size increases by 1
- pop(): pre: isEmpty() == false; post: size decreases by 1
- peek(): pre: isEmpty() == false; post: returns top data, size unchanged
- size(): post: returns number of elements >= 0
- isEmpty(): post: returns true if size == 0"

    note for LinkedStack "Invariants:
- top == null if size == 0
- size >= 0
- No cycles in the node chain
- push(data): pre: data != null, post: size increases by 1, top is new node
- pop(): pre: size > 0, post: size decreases by 1, returns former top data
- peek(): pre: size > 0, post: size unchanged, returns top data"

    note for StackNode "Invariants:
- data != null
- next points to another StackNode or is null"

    note for ExerciseTypeTracker "
Invariants:
- users != null"

    note for User "
Invariants:
- name != null
- activities != null
- following != null

- addActivity(a): pre: a != null; post: activities includes a
- removeActivity(a): pre: activities contains a; post: activities does not contain a
- follow(other): pre: other != null, other != this, other not in following; post: following includes other
- unfollow(other): pre: other != null, other in following; post: following does not contain other
- setName(n): pre: n != null and not blank; post: name == n"

    note for Activity "
Invariants:
- id >= 0
- duration > 0
- path != null
- type != null
- grid != null

- setDuration(minutes): pre: minutes > 0; post: duration == minutes"

    note for Path "
Invariants:
- points != null

- addPoint(p): pre: p != null; post: points includes p
- validateNotEmpty(): pre: points.size() >= 1
- Path(other): pre: other != null; post: points == copy of other.points"

    note for Point "
Invariants:
- pointX >= 0
- pointY >= 0"

    note for Grid "
Invariants:
- width > 0
- height > 0
- obstacles != null

- addObstacle(p): pre: p != null AND isInside(p); post: obstacles includes p
- removeObstacle(id): pre: id >= 0 AND id < obstacles.size(); post: obstacles does not contain removed point"

    note for PathFinder "
Invariants:
- stack != null

- findPath(): pre: grid != null, start != null, end != null, coveredPoints != null,
  grid.isInside(start), grid.isInside(end)
- findPath(): post: returns Path if one exists, null if no path found"

    note for UserService "
- createUser(): pre: name not blank, name not taken; post: user registered in tracker
- selectUser(): pre: name matches existing user; post: returns that user
- followUser(): pre: target exists, not self, not already following; post: currentUser follows target
- unfollowUser(): pre: target exists, currently following; post: currentUser no longer follows target"

    note for ActivityService "
- addActivity(): pre: path not empty, duration > 0; post: activity added to user
- duplicateRoute(): pre: activityId matches user activity; post: returns copy of that path
- removeActivity(): pre: activityId matches user activity; post: activity removed from user"

    note for MapService "
- addObstacle(): pre: point inside grid, not already obstacle; post: obstacle added
- removeObstacle(): pre: id valid; post: obstacle removed
- findRoute(): pre: start and end inside grid; post: returns Path or throws PathNotFoundException"

```

---

## ▶️ Running the Application

### Requirements

Make sure you have **Java** installed.

### Running in IntelliJ IDEA

1. Clone or download the repository.
2. Open the project in IntelliJ IDEA.
3. Navigate to:

```text
src/main/java/ca/umanitoba/cs/ekehcb/app/Main.java
```

4. Run the `main` method.
5. Use the console interface to interact with the application.

---

## 🧪 Testing

The project includes tests for application functionality and the custom stack implementation.

All tests can be run using the `main` method in:

```text
src/test/java/ca/umanitoba/cs/ekehcb/TestHarness.java
```

The stack tests cover:

- Creating an empty stack
- Pushing elements
- Popping elements
- Peeking at the top element
- Checking stack size
- Checking whether the stack is empty
- LIFO ordering
- Error handling for invalid stack operations

---

## 💡 What I Learned

Building this project gave me practical experience with:

- Designing Java applications using object-oriented principles
- Separating application logic into multiple layers
- Creating and implementing interfaces
- Implementing a linked data structure from scratch
- Working with composition and aggregation
- Implementing pathfinding logic
- Applying class invariants and preconditions
- Creating custom exceptions
- Testing data structures and application behavior
- Refactoring code to improve separation of concerns

---

## 🚀 Future Improvements

Possible future improvements include:

- Graphical user interface
- Persistent storage for users and activities
- User authentication
- Exercise statistics and progress tracking
- Route visualization
- More advanced pathfinding algorithms
- Activity search and filtering
- REST API support

---

## 👨‍💻 Author

**Chukwuemeka Ekeh**

Computer Science  
University of Manitoba