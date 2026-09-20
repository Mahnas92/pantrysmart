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

## Phase 9: Chip-based Ingredient Search

### Goal
Transform the search experience from a single text query to a multi-ingredient chip-based search, optimizing API usage and improving discovery.

#### [MODIFY] [SpoonacularService.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/remote/SpoonacularService.kt)
- Update `complexSearch` to accept a comma-separated string for `includeIngredients`.

#### [MODIFY] [SearchHistoryDao.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/local/dao/SearchHistoryDao.kt)
- Update logic to handle individual ingredients instead of full query strings.
- Increase storage capacity for recent ingredients.

#### [MODIFY] [SearchViewModel.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/search/SearchViewModel.kt)
- Change `searchQuery` state from `String` to `List<String>`.
- Add methods: `addIngredient(String)`, `removeIngredient(String)`.
- Update search execution logic to join ingredients with commas.

#### [MODIFY] [SearchScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/search/SearchScreen.kt)
- Implement a custom "Chip Field" in place of the standard `TextField`.
- Render a 3-row grid for "Recent Ingredients" chips.

## Verification Plan

### Automated Tests
- Update `SearchViewModelTest` for list-based queries.
- Test comma-separated string generation in `SpoonacularService` tests.

### Manual Verification
- Verify chip creation when typing comma or pressing enter.
- Test chip removal by clicking the 'x' icon.
- Confirm "Recent Ingredients" grid displays and correctly adds chips to the search list when clicked.
