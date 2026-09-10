package com.example.skafferiet.data.mapper

import com.example.skafferiet.data.api.model.IngredientDto
import com.example.skafferiet.data.api.model.RecipeDto
import com.example.skafferiet.data.local.entity.IngredientEntity
import com.example.skafferiet.data.local.entity.RecipeEntity
import com.example.skafferiet.data.local.entity.ShoppingListItemEntity
import com.example.skafferiet.domain.model.Ingredient
import com.example.skafferiet.domain.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test
    fun `IngredientDto toDomain maps correctly`() {
        val dto = IngredientDto(id = 1, name = "Tomato", original = "1 Tomato", amount = 1.0, unit = "piece", image = "tomato.jpg")
        val domain = dto.toDomain()
        
        assertEquals(1, domain.id)
        assertEquals("Tomato", domain.name)
        assertEquals("1 Tomato", domain.original)
        assertEquals(1.0, domain.amount, 0.0)
        assertEquals("piece", domain.unit)
        assertEquals("tomato.jpg", domain.image)
    }

    @Test
    fun `RecipeDto toDomain maps correctly and prefixes image URL if needed`() {
        val dto = RecipeDto(
            id = 123,
            title = "Test Recipe",
            image = "recipe.jpg",
            readyInMinutes = 45,
            servings = 4,
            sourceUrl = "http://example.com",
            extendedIngredients = listOf(
                IngredientDto(id = 1, name = "Ingredient", original = "1 Ingredient", amount = 1.0, unit = "unit", image = "ing.jpg")
            ),
            summary = "Summary",
            instructions = "Instructions"
        )
        val domain = dto.toDomain()

        assertEquals(123L, domain.id)
        assertEquals("Test Recipe", domain.title)
        assertEquals("https://spoonacular.com/recipeImages/recipe.jpg", domain.image)
        assertEquals(45, domain.readyInMinutes)
        assertEquals(4, domain.servings)
        assertEquals("http://example.com", domain.sourceUrl)
        assertEquals(1, domain.ingredients.size)
        assertEquals("Summary", domain.summary)
        assertEquals("Instructions", domain.instructions)
    }

    @Test
    fun `RecipeEntity toDomain maps correctly`() {
        val entity = RecipeEntity(
            id = 1L,
            title = "Title",
            image = "image",
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = "url",
            summary = "summary",
            instructions = "instructions",
            ingredients = listOf(
                IngredientEntity(id = 1, name = "name", original = "original", amount = 1.0, unit = "unit", image = "image")
            ),
            isFavorite = true
        )
        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("Title", domain.title)
        assertEquals(1, domain.ingredients.size)
        assertEquals(true, domain.isFavorite)
    }

    @Test
    fun `Recipe toEntity maps correctly`() {
        val domain = Recipe(
            id = 1L,
            title = "Title",
            image = "image",
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = "url",
            summary = "summary",
            instructions = "instructions",
            ingredients = listOf(
                Ingredient(id = 1, name = "name", original = "original", amount = 1.0, unit = "unit", image = "image")
            ),
            isFavorite = true
        )
        val entity = domain.toEntity()

        assertEquals(1L, entity.id)
        assertEquals("Title", entity.title)
        assertEquals(1, entity.ingredients.size)
        assertEquals(true, entity.isFavorite)
    }

    @Test
    fun `ShoppingListItemEntity toDomain maps correctly`() {
        val entity = ShoppingListItemEntity(name = "Sugar", amount = 2.0, unit = "cups")
        val domain = entity.toDomain()

        assertEquals("Sugar", domain.name)
        assertEquals(2.0, domain.amount, 0.0)
        assertEquals("cups", domain.unit)
        assertEquals("2.0 cups Sugar", domain.original)
    }

    @Test
    fun `Ingredient toShoppingListItemEntity maps correctly`() {
        val domain = Ingredient(id = 1, name = "Sugar", original = "2 cups Sugar", amount = 2.0, unit = "cups", image = null)
        val entity = domain.toShoppingListItemEntity()

        assertEquals("Sugar", entity.name)
        assertEquals(2.0, entity.amount, 0.0)
        assertEquals("cups", entity.unit)
    }
}
