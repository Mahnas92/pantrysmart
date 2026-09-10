# feat(ui): implement Favorites and Smart Shopping List screens

This plan outlines the steps to complete the Favorites and Smart Shopping List screens, including the necessary changes to the domain and data layers to support fetching favorited recipes.

## User Review Required

> [!IMPORTANT]
> The `RecipeRepository` and `RecipeDao` will be updated to include a method for fetching all favorited recipes.

## Proposed Changes

### Data & Domain Layer

#### [MODIFY] [RecipeDao.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/local/dao/RecipeDao.kt)
- Add `@Query("SELECT * FROM recipes WHERE isFavorite = 1") fun getFavoriteRecipes(): Flow<List<RecipeEntity>>`.

#### [MODIFY] [RecipeRepository.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/domain/repository/RecipeRepository.kt)
- Add `fun getFavorites(): Flow<List<Recipe>>`.

#### [MODIFY] [OfflineFirstRecipeRepository.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/repository/OfflineFirstRecipeRepository.kt)
- Implement `getFavorites()` using `recipeDao.getFavoriteRecipes()`.

---

### UI Layer - Favorites

#### [NEW] [FavoritesViewModel.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/favorites/FavoritesViewModel.kt)
- Create `FavoritesViewModel` to collect favorited recipes from `RecipeRepository`.

#### [NEW] [FavoritesScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/favorites/FavoritesScreen.kt)
- Create `FavoritesScreen` to display the list of favorited recipes.

---

### UI Layer - Shopping List

#### [NEW] [ShoppingListViewModel.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/shopping/ShoppingListViewModel.kt)
- Create `ShoppingListViewModel` with smart aggregation logic:
    - Fetch favorited recipes.
    - Extract ingredients.
    - Group by name and aggregate amounts.

#### [NEW] [ShoppingListScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/shopping/ShoppingListScreen.kt)
- Create `ShoppingListScreen` to display the aggregated ingredients checklist.

---

### App Wiring

#### [MODIFY] [SkafferiApp.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/SkafferiApp.kt)
- Register `FavoritesScreen` and `ShoppingListScreen` in the `NavDisplay`.

## Verification Plan

### Automated Tests
- Run unit tests for repository changes if any.
- Build the project to ensure no compilation errors.

### Manual Verification
- Navigate to the Search screen, favorite some recipes.
- Navigate to the Favorites screen and verify the recipes are listed.
- Navigate to the Shopping List screen and verify the ingredients are aggregated correctly.
