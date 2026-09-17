# Tasks

## Phase 4: Branding, Localization & Clean-up
- [ ] Rename App to "PantrySmart" in `strings.xml` and Manifest
- [ ] Rename core classes and components (Application, App, Scaffold, TopBar, Theme, Database)
- [ ] Move hardcoded strings to `res/values/strings.xml` (Localization)
- [ ] Rename `OfflineFirstRecipeRepository` to `OfflineRecipeRepository` and update all usages
- [ ] Fix naming inconsistencies in main and test sources
- [ ] Resolve lint errors, warnings, and deprecations

## Phase 5: Advanced Caching & Navigation Logic
- [ ] Add `lastUpdated` timestamp to `RecipeEntity`
- [ ] Implement TTL-based caching in `OfflineRecipeRepository`
- [x] Fix `DetailScreen` back button to always return to `SearchScreen`
- [ ] Update `SearchScreen` to show cached recipes when query is empty
- [ ] Implement "Recent Search" clickable labels in `SearchScreen`

## Phase 6: Interactive Feedback & List Management
- [ ] Update `DetailScreen` ingredient list with visual confirmation (checkmark/delete icon)
- [ ] Implement visual success feedback for "Add All" ingredients action
- [ ] Synchronize "added" state on `DetailScreen` with Shopping List database
