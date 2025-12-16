# Android Project Guidelines: Clean Architecture, SOLID & MVI

This document serves as a reference for AI agents and developers working on this Android project. It establishes the architectural standards, design patterns, and best practices to ensure code quality, maintainability, and scalability.

## 1. Architecture Overview: Clean Architecture

The project strictly follows **Clean Architecture**, dividing the codebase into three distinct layers with a unidirectional dependency rule (outer layers depend on inner layers).

### 🟢 Domain Layer (Inner Core)
*   **Responsibility:** Contains the business logic and enterprise rules. It is the most stable layer.
*   **Dependencies:** **None**. It must remain pure Kotlin/Java, free from Android framework dependencies.
*   **Components:**
    *   **Entities/Models:** Pure data classes representing business objects.
    *   **Use Cases (Interactors):** Encapsulate specific business logic (e.g., `GetUserInfoUseCase`). Each Use Case should ideally have a single public `invoke` function.
    *   **Repository Interfaces:** Abstractions defining data operations.

### 🟡 Data Layer (Outer Core)
*   **Responsibility:** Handling data retrieval and storage. It implements the interfaces defined in the Domain layer.
*   **Dependencies:** Domain Layer.
*   **Components:**
    *   **Repository Implementations:** Implement domain repository interfaces. They orchestrate data flow between local and remote sources.
    *   **Data Sources:**
        *   *Remote:* API clients (Retrofit), Network services.
        *   *Local:* Databases (Room), DataStore, Files.
    *   **Mappers:** Functions to transform Data entities (DTOs) into Domain models and vice-versa.

### 🔴 Presentation Layer (UI)
*   **Responsibility:** Rendering the UI and handling user interaction.
*   **Dependencies:** Domain Layer.
*   **Components:**
    *   **UI Components:** Activities, Fragments, Jetpack Compose functions.
    *   **ViewModels:** Manage UI state and handle business logic execution via Use Cases.
    *   **MVI Contracts:** Define State, Events, and Effects.

---

## 2. Design Pattern: MVI (Model-View-Intent)

We use **MVI** for state management to ensure a unidirectional data flow and predictable UI states.

### Key Components
1.  **State:** An **immutable** data class representing the single source of truth for a specific screen/view.
    *   *Example:* `data class HomeState(val isLoading: Boolean, val items: List<Item>, val error: String?)`
2.  **Event (Intent):** Represents user actions or system events that trigger logic.
    *   *Example:* `sealed interface HomeEvent { data object LoadData : HomeEvent; data class ItemClicked(val id: Int) : HomeEvent }`
3.  **Effect (Side Effect):** One-time events that shouldn't persist in the state (e.g., Navigation, Snackbars, Toasts).
    *   *Example:* `sealed interface HomeEffect { data class NavigateToDetails(val id: Int) : HomeEffect }`

### Flow
1.  **View** emits an **Event**.
2.  **ViewModel** processes the Event, executes Use Cases (Domain).
3.  **ViewModel** updates the **State** (via `StateFlow`) or emits an **Effect** (via `SharedFlow`/`Channel`).
4.  **View** observes **State** to render UI and collects **Effects** for navigation/alerts.

---

## 3. SOLID Principles in Context

*   **S - Single Responsibility Principle:**
    *   *Apply:* Each Use Case does exactly one thing. ViewModels handle state logic, not UI rendering or data fetching.
*   **O - Open/Closed Principle:**
    *   *Apply:* Use interfaces for Repositories and Data Sources to allow swapping implementations (e.g., Mock vs Real) without changing client code.
*   **L - Liskov Substitution Principle:**
    *   *Apply:* Implementations of interfaces should behave predictably so they can be interchanged without breaking the app.
*   **I - Interface Segregation Principle:**
    *   *Apply:* Create specific interfaces (e.g., `ReadOnlyRepository`, `WriteRepository`) if a client doesn't need all methods.
*   **D - Dependency Inversion Principle:**
    *   *Apply:* The Domain layer defines interfaces; the Data layer implements them. Use Dependency Injection (Hilt/Koin) to provide implementations.

---

## 4. Best Practices

### Kotlin & Coroutines
*   Use `suspend` functions for asynchronous operations.
*   Use `Flow` for reactive data streams (especially from Data to Domain to UI).
*   Always use appropriate `Dispatcher` (IO for DB/Network, Main for UI).

### UI (Jetpack Compose)
*   Composables should be stateless whenever possible (hoist state to parent or ViewModel).
*   Use `preview` functions to test UI components in isolation.
*   Follow Material Design guidelines.

### Testing
*   **Domain:** 100% unit test coverage for Use Cases (fast, pure Kotlin).
*   **Data:** Unit test Repositories and Mappers using mocks (Mockk/Mockito).
*   **Presentation:** Unit test ViewModels (verify State updates and Effects). UI Tests (Compose Test Rule) for critical flows.

### Naming Conventions
*   **Use Cases:** `Verb` + `Noun` + `UseCase` (e.g., `LoginUserUseCase`).
*   **Repositories:** `DataName` + `Repository` (e.g., `UserRepository`).
*   **Implementations:** `Name` + `Impl` (e.g., `UserRepositoryImpl`).

---

## 5. Folder Structure Template

```
com.example.project
├── data
│   ├── api (remote sources)
│   ├── database (local sources)
│   ├── mapper
│   └── repository (implementations)
├── di (Dependency Injection modules)
├── domain
│   ├── model
│   ├── repository (interfaces)
│   └── usecase
└── presentation
    ├── common (shared UI components)
    ├── theme
    └── [feature_name]
        ├── [Feature]Screen.kt
        ├── [Feature]ViewModel.kt
        └── [Feature]Contract.kt (State, Event, Effect)
```
