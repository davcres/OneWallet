## INTERNAL NOTES (FOR DEVELOPER ONLY - AI IGNORE)
- Ensure to call this rules from GEMINI.md (file automatically reader by gemini)
## END INTERNAL NOTES FOR DEVELOPER




# Profile: Senior Android Engineer

# Android Project Guidelines: Clean Architecture, SOLID & MVI

This document serves as a reference for AI agents and developers working on this Android project. It establishes the architectural standards, design patterns, and best practices to ensure code quality, maintainability, and scalability.

## 1. Architecture Overview: Clean Architecture

The project follows a **Feature-based** top-level organization to ensure scalability and modularity. Inside each feature module, a **Layer-based** structure (Clean Architecture) must be strictly applied, dividing the codebase into three distinct layers with a unidirectional dependency rule (outer layers depend on inner layers).

### 🟢 Domain Layer (Inner Core)
*   **Responsibility:** Contains the business logic and enterprise rules. It is the most stable layer.
*   **Dependencies:** **None**. It must remain pure Kotlin, free from Android framework dependencies.
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
    *   **Mappers:** Functions to transform Data entities (DTOs) into Domain models and vice-versa. The API models (DTOs) never have to go away from the data layer. The Repository is responsible for mapping DTOs to Domain Models before they are released.

### 🔴 Presentation Layer (UI)
*   **Responsibility:** Rendering the UI and handling user interaction.
*   **Dependencies:** Domain Layer.
*   **Components:**
    *   **UI Components:** Activities, Fragments, Jetpack Compose functions.
    *   **ViewModels:** Manage UI state and handle business logic execution via Use Cases.
    *   **MVI Contracts:** Define State, Events, and Effects. The ViewModel must expose a single StateFlow<State>. It is forbidden to expose multiple independent states. Status updates must be done via .update { it.copy(...) }.

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

### Model
1. Use a sealed class {ProjectName}Result to return the data or the given error. This sealed class have two data classes:
sealed interface {ProjectName}Result<out T> {
    data class Success<T>(val data: T) : {ProjectName}Result<T>
    data class Error(val error: {ProjectName}Error) : {ProjectName}Result<Nothing>
}


sealed interface {ProjectName}Error {
    object Network : {ProjectName}Error
    object Unauthorized : {ProjectName}Error
    object NoFunds : {ProjectName}Error
    data class ServerError(val code: Int) : {ProjectName}Error
    object Unknown : {ProjectName}Error
}

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
    *   *Apply:* The Domain layer defines interfaces; the Data layer implements them. Use Dependency Injection (Koin) to provide implementations.

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
*   In the View (Compose), always use collectAsStateWithLifecycle() to observe the State and avoid memory leaks or unnecessary consumption of resources.
*   Each Composable Panel must have a 'Content' version containing only the State and Events (lambdas), to facilitate the creation of Previews without the need to mock the ViewModel. The 'Content' composable is called by a 'Root' composable which makes the initial calls to the ViewModel and obtains its state.
*   Use always colors from com.davidcrespo.onewallet.presentation.designsystem.theme.Theme and follow com.davidcrespo.onewallet.presentation.designsystem.composables.OWInvestmentItem style.
*   In Text composables use style = MaterialTheme.typography instead of fontSize.

### Testing
*   **Dependencies** Use MockK and JUnit5 for business logic tests. Use Jetpack Compose Tests with JUnit 4 for UI tests and robolectric for running them without emulator. Create also screenshot tests with roborazzi.
*   **Domain:** 100% unit test coverage for Use Cases (fast, pure Kotlin).
*   **Data:** Unit test Repositories and Mappers using mocks (Mockk).
*   **Presentation:** Unit test ViewModels (verify State updates and Effects). UI Tests (Compose Test Rule) for critical flows.
*   Every time a new class is created or modified, ensure that the old tests passes and are updated with the new changes, and create test class if necessary.
*   Every time a new composable is created or modified, ensure that the old tests passes and are updated with the new changes, and create compose test and screenshot tests if necessary.
*   Share the same TestDispatcher instance across your tests and the DispatcherProvider. This ensures that both Dispatchers.Main (which the extension handles) and your background dispatchers (IO, Default) are perfectly synchronized. For that, use MainDispatcherExtension and TestDispatcherProvider.

### Naming Conventions
*   **Use Cases:** `Verb` + `Noun` + `UseCase` (e.g., `LoginUserUseCase`).
*   **Repositories:** `DataName` + `Repository` (e.g., `UserRepository`).
*   **Implementations:** `Name` + `Impl` (e.g., `UserRepositoryImpl`).

### Structure
Use the Given-When-Then pattern in the test comments. Note the test methods using backticks and a descriptive form: `should update state to success when data is fetched`.

---

## 5. Project Organization: Feature-First
The project follows a **Feature-based** top-level organization to ensure scalability and modularity. Inside each feature module, a **Layer-based** structure (Clean Architecture) must be strictly applied.

### Internal Feature Structure:
Each feature module must contain:
- **domain:** Use Cases, Domain Models, and Repository Interfaces.
- **data:** Repository Impls, DTOs, Entities, Mappers, and Data Sources.
- **presentation:** UI (Compose), ViewModels, and MVI Contracts.

---

## 6. Folder Structure Template

```
com.example.project
├── core
│   ├── data          (Network provider, Database config, Base DTOs)
│   ├── domain        (Base UseCase, Result wrapper, Error types)
│   ├── ui            (Common components, Theme, Design System)
│   └── di            (Global Koin modules: NetworkModule, DatabaseModule)
└── features
    └── [feature_name] (e.g., login, profile)
        ├── data
        │   ├── remote (API interfaces & DTOs)
        │   ├── local  (DAOs & Entities)
        │   ├── mapper (Extension functions)
        │   └── repository (Implementations)
        ├── domain
        │   ├── model  (Domain entities)
        │   ├── repository (Interfaces)
        │   └── usecase
        ├── presentation
        │   ├── [Feature]Screen.kt   (Root & Content)
        │   ├── [Feature]ViewModel.kt
        │   └── [Feature]Contract.kt (State, Event, Effect)
        └── di
            └── [Feature]Module.kt (Koin module for this specific feature)
```
