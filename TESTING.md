# Testing

Unit tests live in the `desktopTest` source set
(`composeApp/src/desktopTest/kotlin/`) and run on the JVM with
[kotlin.test](https://kotlinlang.org/api/latest/kotlin.test/) plus
`kotlinx-coroutines-test`.

## Running tests

```bash
./gradlew :composeApp:desktopTest
```

Reports land in `composeApp/build/reports/tests/desktopTest/index.html`
(JUnit XML in `composeApp/build/test-results/desktopTest/`).

## What's covered today

| Area | File | What it guards |
|------|------|----------------|
| Room type converter | `data/utils/ConvertersTest.kt` | `List<String>` ↔ JSON round-trip, empty lists, order/duplicates, values needing JSON escaping, and graceful fallback on a malformed DB value |
| Skill-build logic | `ui/QuestViewModelSkillBuildTest.kt` | `setWitcherLevel` clamping (1–100), `adjustSkillPoints` max-level/floor/point-pool rules, `applyRecommendedBuild` allocation + auto-level-bump |

### A cautionary tale

The very first run of `ConvertersTest` caught a real production bug:
`toStringList` used `adapter.fromJson(value) ?: emptyList()`, which reads as
"fall back to empty on bad data" — but Moshi's `fromJson` **throws**
`JsonDataException` on malformed input rather than returning null, so a corrupt
DB cell would have crashed the app. The converter now wraps the parse in a
try/catch so bad values degrade to an empty list. Moral: write the test for the
behavior you *think* the code has.

## Writing new tests

- **Pure logic** → plain `kotlin.test` classes; no scaffolding needed.
- **ViewModels** → `viewModelScope` requires a `Main` dispatcher. Install a
  `StandardTestDispatcher` via `Dispatchers.setMain(...)` in `@BeforeTest` and
  `Dispatchers.resetMain()` in `@AfterTest` (see
  `QuestViewModelSkillBuildTest`). Synchronous `MutableStateFlow` updates can
  be asserted directly; coroutine-launched work needs the dispatcher advanced.
- **Repositories/DAOs** → the DAO interfaces (`QuestDao`, `MonsterDao`,
  `SaddlebagItemDao`) are small; hand-written fakes returning `flowOf(...)`
  are usually enough (see the fakes in `QuestViewModelSkillBuildTest`). Reserve
  real Room (in-memory database) for testing the SQL itself.
- **AI paths** → `GeminiClient.consultAdvisor` is a network call on a static
  object; avoid invoking it from tests. Prefer extracting/asserting the prompt
  construction and state transitions around it.

## Suggested next targets

1. `QuestViewModel.filteredQuests` — the search/type/region/status/level-range
   filter + sort pipeline. Feed a fake `QuestDao` with a fixed quest list and
   collect the flow with the test dispatcher advanced.
2. `QuestRepository.initializeDefaultQuests` — the incremental-upgrade path
   (inserts only quests whose titles are missing) is easy to regress.
3. `QuestViewModel.craftRecipe` — ingredient sufficiency checks, quantity
   subtraction, and add-vs-update of the crafted product.
4. DAO/SQL layer — in-memory Room database tests for the `@Query` methods,
   especially `searchMonsters`' LIKE pattern.
