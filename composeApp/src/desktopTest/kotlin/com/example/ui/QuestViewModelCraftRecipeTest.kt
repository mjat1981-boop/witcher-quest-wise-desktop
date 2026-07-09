package com.example.ui

import com.example.data.AlchemyRecipe
import com.example.data.InMemoryMonsterDao
import com.example.data.InMemoryQuestDao
import com.example.data.InMemorySaddlebagDao
import com.example.data.MonsterRepository
import com.example.data.QuestRepository
import com.example.data.SaddlebagItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Covers craftRecipe: ingredient sufficiency checks, quantity subtraction,
 * and add-vs-update of the crafted product. Uses the static Necrophage Oil
 * recipe (3x Celandine Petals) so the tests track the real content data.
 * SFX are disabled up front so the success path doesn't attempt audio output.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuestViewModelCraftRecipeTest {

    private val dispatcher = StandardTestDispatcher()
    private val recipe: AlchemyRecipe =
        AlchemyRecipe.getStaticRecipes().first { it.id == "necrophage_oil" }

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        assertEquals(listOf("Celandine Petals" to 3), recipe.formula.map { it.name to it.quantity })
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun ingredient(name: String, qty: Int) = SaddlebagItem(
        name = name, category = "ALCHEMY_INGREDIENT", quantity = qty,
        description = "", rarity = "COMMON", iconLabel = "🌿",
    )

    private fun vmWith(dao: InMemorySaddlebagDao): QuestViewModel {
        val vm = QuestViewModel(
            QuestRepository(InMemoryQuestDao(acceptBulkInserts = false), dao),
            MonsterRepository(InMemoryMonsterDao()),
        )
        vm.setSfxEnabled(false)
        return vm
    }

    @Test
    fun craftingSubtractsIngredientsAndAddsTheProduct() = runTest(dispatcher) {
        val dao = InMemorySaddlebagDao(listOf(ingredient("Celandine Petals", 5)))
        val vm = vmWith(dao)
        advanceUntilIdle() // let init settle

        vm.craftRecipe(recipe)
        advanceUntilIdle()

        val petals = dao.snapshot().first { it.name == "Celandine Petals" }
        assertEquals(2, petals.quantity, "5 petals minus the 3 the formula requires")

        val product = dao.snapshot().find { it.name == recipe.name }
        assertNotNull(product, "crafted product should be added to the saddlebag")
        assertEquals(1, product.quantity)
    }

    @Test
    fun craftingIsRefusedWhenIngredientsAreInsufficient() = runTest(dispatcher) {
        val dao = InMemorySaddlebagDao(listOf(ingredient("Celandine Petals", 2))) // needs 3
        val vm = vmWith(dao)
        advanceUntilIdle()

        vm.craftRecipe(recipe)
        advanceUntilIdle()

        assertEquals(2, dao.snapshot().first { it.name == "Celandine Petals" }.quantity)
        assertNull(dao.snapshot().find { it.name == recipe.name }, "no product on failed craft")
    }

    @Test
    fun craftingIsRefusedWhenIngredientHasWrongCategory() = runTest(dispatcher) {
        // Same name but not an ALCHEMY_INGREDIENT: must not be consumed.
        val questItem = SaddlebagItem(
            name = "Celandine Petals", category = "QUEST_ITEM", quantity = 5,
            description = "", rarity = "COMMON", iconLabel = "🌿",
        )
        val dao = InMemorySaddlebagDao(listOf(questItem))
        val vm = vmWith(dao)
        advanceUntilIdle()

        vm.craftRecipe(recipe)
        advanceUntilIdle()

        assertEquals(5, dao.snapshot().first { it.name == "Celandine Petals" }.quantity)
        assertNull(dao.snapshot().find { it.name == recipe.name })
    }

    @Test
    fun craftingAgainIncrementsTheExistingProduct() = runTest(dispatcher) {
        val dao = InMemorySaddlebagDao(listOf(ingredient("Celandine Petals", 6)))
        val vm = vmWith(dao)
        advanceUntilIdle()

        vm.craftRecipe(recipe)
        advanceUntilIdle()
        vm.craftRecipe(recipe)
        advanceUntilIdle()

        // The repository deletes items whose quantity reaches 0, so fully
        // consumed ingredients vanish from the saddlebag entirely.
        assertNull(dao.snapshot().find { it.name == "Celandine Petals" }, "depleted ingredient is removed")
        val products = dao.snapshot().filter { it.name == recipe.name }
        assertEquals(1, products.size, "second craft should update, not insert a duplicate")
        assertEquals(2, products.single().quantity)
    }
}
