# Project Plan

A recipe and shopping list manager Android app called "Skafferiet" (The Pantry).
Purpose: Demonstrate clean, production-ready development using modern Android best practices (Offline-first, MVVM/UDF, Room, Retrofit, Jetpack Compose).
Concept: Use Spoonacular REST API, cache data locally in Room.
Architecture: MVVM with UDF (hybrid state approach) and Manual DI.
Testing: Structural Unit, UI, and Integration tests.
Workflow: Incremental step-by-step builds with professional Git commits.

## Project Brief

# Project Brief: Skafferiet (The Pantry)

A modern, offline-first Android application designed to manage recipes and shopping lists using the Spoonacular REST API.

## Features

1.  **Recipe Search & Discovery**: Search for recipes via the Spoonacular API with support for dietary filters and cuisines.
2.  **Detailed Recipe View**: View comprehensive recipe information including ingredients, step-by-step instructions, and nutritional facts.
3.  **Offline Recipe Library**: Save favorite recipes to a local database using Room for persistent, offline access.
4.  **Integrated Shopping List**: Automatically generate and manage a shopping list based on ingredients from saved recipes.
5.  **Adaptive UI Layouts**: A responsive interface that optimizes the viewing experience across phones, foldables, and tablets.

## High-Level Technical Stack

-   **Language**: Kotlin
-   **UI Framework**: Jetpack Compose
-   **Navigation**: **Jetpack Navigation 3** (State-driven approach)
-   **Adaptive Design**: **Compose Material Adaptive** library
-   **Networking**: Retrofit & OkHttp (for Spoonacular REST API)
-   **Persistence**: Room Database (Offline-first architecture)
-   **Concurrency**: Kotlin Coroutines & Flow
-   **Architecture**: MVVM with Unidirectional Data Flow (UDF) and Manual Dependency Injection
-   **Testing Strategy**:
    -   **Unit Tests**: Logic verification for ViewModels, Repositories, and Data Sources.
    -   **UI Tests**: Declarative UI testing using the Compose Test library.
    -   **Integration Tests**: Validating the end-to-end flow between the network, database, and UI layers.

## Implementation Steps
**Total Duration:** 12m 14s

### Task_1_SetupFoundation: Configure the project foundation including Version Catalog, build files, folder structure, and Manual Dependency Injection container.
- **Status:** COMPLETED
- **Updates:** Configured libs.versions.toml with Compose, Room, Retrofit, and testing dependencies. Updated build.gradle.kts files. Created the 'feature-by-package' folder structure under com.example.skafferiet. Implemented a Manual DI 'AppContainer' at the root level to manage dependencies. Project builds and is ready for data layer implementation.
- **Acceptance Criteria:**
  - libs.versions.toml is correctly configured with all required dependencies
  - Build files (build.gradle.kts) are updated for Compose, Room, and Retrofit
  - Folder structure follows modern Android patterns (data, ui, di, domain)
  - Manual DI container is initialized and accessible
  - Project builds successfully
- **Duration:** 12m 14s

### Task_2_DataLayerIntegration: Implement the data layer including Spoonacular API integration with Retrofit, Room Database for local persistence, and an offline-first Repository.
- **Status:** COMPLETED
- **Updates:** Verified implementation of Room DB, Retrofit service, and Offline-first repository. The project has been refactored to the name PantrySmart with English resources.
- **Acceptance Criteria:**
  - Spoonacular API service is defined using Retrofit
  - API_KEY integration is functional and securely handled
  - Room entities, DAOs, and Database are implemented
  - Offline-first Repository logic correctly handles data fetching and caching
  - Unit tests for the repository and data sources are present

### Task_5_InteractiveFeedbackAndAdvancedCaching: Implement Phase 6 (Interactive Feedback & List Management) and Phase 5 (Advanced Caching & Navigation Logic).
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Ingredient button toggles state (checkmark/delete) based on shopping list presence
  - Batch 'Add All' button functional with visual feedback
  - Navigation back from details screen returns to Search screen
  - Search screen auto-populates with cached recipes when query is blank (Omni Feed)
  - Clickable Recent Search labels implemented under search bar
  - TTL caching layer implemented to optimize API calls
  - Granular git commits and full test coverage for all new logic
  - The implemented UI must match the design provided in [Insert UI design image Path].
- **StartTime:** 2026-09-15 10:21:25 CEST

### Task_6_RefactorAndFinalVerification: Implement Phase 4 (Clean-up Refactoring) and perform final Run and Verify step.
- **Status:** PENDING
- **Acceptance Criteria:**
  - OfflineFirstRecipeRepository renamed to OfflineRecipeRepository
  - All lint errors, deprecations, and warnings resolved across source and tests
  - Build passes and app does not crash during standard user flows
  - Existing tests pass
  - Critic_agent verifies stability and requirement alignment
  - The implemented UI must match the design provided in [Insert UI design image Path].

