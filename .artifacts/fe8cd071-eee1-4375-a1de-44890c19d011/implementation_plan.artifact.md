# Phase 4: Branding, Localization & Clean-up - Branding & Localization

This task involves renaming the application to "PantrySmart", refactoring core components to reflect this branding, and localizing all user-facing strings to English while moving them to `res/values/strings.xml`.

## Proposed Changes

### Branding & Renaming

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/AndroidManifest.xml)
- Update `android:name` to `.PantrySmartApplication`.
- Update `android:label` to `PantrySmart` (via `strings.xml`).
- Update `android:theme` and `MainActivity` theme to `@style/Theme.PantrySmart` (if applicable, or keep as is if only Compose is used).

#### [NEW] [PantrySmartApplication.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/PantrySmartApplication.kt)
- Rename `SkafferietApplication` to `PantrySmartApplication`.
- [DELETE] [SkafferietApplication.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/SkafferietApplication.kt)

#### [NEW] [PantrySmartApp.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/PantrySmartApp.kt)
- Rename `SkafferiApp` to `PantrySmartApp`.
- Update references to `PantrySmartApplication`, `PantrySmartTheme`, etc.
- [DELETE] [SkafferiApp.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/SkafferiApp.kt)

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/MainActivity.kt)
- Update `SkafferiApp()` to `PantrySmartApp()`.

#### [NEW] [PantryDatabase.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/local/PantryDatabase.kt)
- Rename `SkafferiDatabase` to `PantryDatabase`.
- [DELETE] [SkafferiDatabase.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/local/SkafferiDatabase.kt)

#### [NEW] [PantryScaffold.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/components/PantryScaffold.kt)
- Rename `SkafferiScaffold` to `PantryScaffold`.
- [DELETE] [SkafferiScaffold.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/components/SkafferiScaffold.kt)

#### [NEW] [PantryTopBar.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/components/PantryTopBar.kt)
- Rename `SkafferiTopBar` to `PantryTopBar`.
- [DELETE] [SkafferiTopBar.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/components/SkafferiTopBar.kt)

#### [MODIFY] [Theme.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/theme/Theme.kt)
- Rename `SkafferietTheme` to `PantrySmartTheme`.

### Localization & Strings

#### [MODIFY] [strings.xml](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/res/values/strings.xml)
- Update `app_name` to `PantrySmart`.
- Add all user-facing strings in English.

#### [MODIFY] [SearchScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/search/SearchScreen.kt)
- Use `PantryScaffold` and `PantryTopBar`.
- Extract and localize strings.

#### [MODIFY] [DetailScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/detail/DetailScreen.kt)
- Use `PantryScaffold` and `PantryTopBar`.
- Extract and localize strings.

#### [MODIFY] [FavoritesScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/favorites/FavoritesScreen.kt)
- Use `PantryScaffold` and `PantryTopBar`.
- Extract and localize strings.

#### [MODIFY] [ShoppingListScreen.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/ui/screens/shopping/ShoppingListScreen.kt)
- Use `PantryScaffold` and `PantryTopBar`.
- Extract and localize strings.

### DI & Data Layer Updates

#### [MODIFY] [AppContainer.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/di/AppContainer.kt)
- Update `SkafferiDatabase` to `PantryDatabase`.

#### [MODIFY] [OfflineFirstRecipeRepository.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/main/java/com/example/skafferiet/data/repository/OfflineFirstRecipeRepository.kt)
- Update imports if necessary (though usually they are in the same package or already handled).

### Test Updates

#### [MODIFY] [NavigationFlowTest.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/androidTest/java/com/example/skafferiet/ui/NavigationFlowTest.kt)
- Update class/function names and string expectations.

#### [MODIFY] [SkafferiScaffoldTest.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/androidTest/java/com/example/skafferiet/ui/components/SkafferiScaffoldTest.kt)
- Rename to `PantryScaffoldTest.kt`.
- Update class/function names.

#### [MODIFY] [DetailScreenTest.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/androidTest/java/com/example/skafferiet/ui/screens/detail/DetailScreenTest.kt)
- Update string expectations.

#### [MODIFY] [SearchScreenTest.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/androidTest/java/com/example/skafferiet/ui/screens/search/SearchScreenTest.kt)
- Update string expectations.

#### [MODIFY] [ShoppingListScreenTest.kt](file:///C:/Users/Mahmoud/home/android-projects/SkafferiRecept/app/src/androidTest/java/com/example/skafferiet/ui/screens/shopping/ShoppingListScreenTest.kt)
- Update string expectations.

## Verification Plan

### Automated Tests
- Run all unit tests: `./gradlew test`
- Run instrumented tests: `./gradlew connectedAndroidTest`

### Manual Verification
- Build and run the app to ensure all UI elements show English strings and "PantrySmart" branding.
- Verify navigation and features still work correctly after refactoring.
