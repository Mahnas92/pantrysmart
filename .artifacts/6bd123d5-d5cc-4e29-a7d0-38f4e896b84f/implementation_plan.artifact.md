# Implementation Plan - PantrySmart Enhancements

This plan outlines the steps for branding, localization, caching, navigation logic, and interactive feedback for the PantrySmart app (formerly Skafferiet).

## Phase 4: Branding, Localization & Clean-up

### Goal
Standardize the app's identity, language, and code quality.

#### [MODIFY] [strings.xml](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/res/values/strings.xml)
- Change `app_name` to "PantrySmart".
- Add all user-facing strings (Search, Favorites, Shopping List, Ingredients, Instructions, etc.).

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/AndroidManifest.xml)
- Update theme names and application label if necessary.

#### [MODIFY] [OfflineFirstRecipeRepository.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/repository/OfflineFirstRecipeRepository.kt)
- Rename to `OfflineRecipeRepository`.

#### [MODIFY] UI Components & Classes
- Rename `SkafferietApplication` to `PantrySmartApplication`.
- Rename `SkafferiApp` to `PantrySmartApp`.
- Rename `SkafferiScaffold` to `PantrySmartScaffold`.
- Rename `SkafferiTopBar` to `PantrySmartTopBar`.
- Rename `SkafferietTheme` to `PantrySmartTheme`.
- Rename `SkafferiDatabase` to `PantrySmartDatabase`.
- Update all usages and imports.

---

## Phase 5: Advanced Caching & Navigation Logic

### Goal
Optimize API usage and improve navigation flow.

#### [MODIFY] [RecipeEntity.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/local/entity/RecipeEntity.kt)
- Add `lastUpdated` timestamp field.

#### [MODIFY] [OfflineRecipeRepository.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/repository/OfflineRecipeRepository.kt)
- Implement TTL check. Only fetch from network if data is missing or older than TTL (e.g., 24 hours).

#### [MODIFY] [SkafferiApp.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/SkafferiApp.kt)
- Fix backstack logic: `onBackClick` from `DetailScreen` should navigate to `Destination.Search`.

#### [MODIFY] [SearchViewModel.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/search/SearchViewModel.kt)
- Load cached recipes when query is empty.
- Maintain a list of "Recent Searches".

#### [MODIFY] [SearchScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/search/SearchScreen.kt)
- Display recent search labels.
- Show cached feed when query is empty.

---

## Phase 6: Interactive Feedback & List Management

### Goal
Provide real-time feedback for user actions and ensure state consistency.

#### [MODIFY] [DetailViewModel.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/detail/DetailViewModel.kt)
- Expose `addedIngredientIds` as a StateFlow, synced with `ShoppingListDao`.

#### [MODIFY] [DetailScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/detail/DetailScreen.kt)
- Update `IngredientsList` to show checkmark/delete icon if ingredient is already in the shopping list.
- Show Snackbar/Visual confirmation when "Add All" is clicked.

---

## Verification Plan

### Automated Tests
- Rename repository in tests.
- Add tests for TTL logic in `OfflineRecipeRepositoryTest`.
- Verify navigation logic in `NavigationFlowTest`.

### Manual Verification
- Verify app name change in launcher and top bar.
- Test offline feed when opening the app without internet.
- Test "Add to Shopping List" feedback in `DetailScreen`.
- Verify back button behavior from `DetailScreen`.
