package com.example.pantrysmart.data.mapper

import com.example.pantrysmart.data.api.model.RecipeDto
import com.example.pantrysmart.data.local.entity.RecipeEntity
import org.junit.Assert.assertEquals
import org.junit.Test

class RecipeMappersTest {

    @Test
    fun `RecipeDto toDomain maps correctly`() {
        val dto = RecipeDto(
            id = 123,
            title = "Pasta",
            image = "pasta.jpg",
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = "http://example.com"
        )
        val domain = dto.toDomain()
        
        assertEquals(123L, domain.id)
        assertEquals("Pasta", domain.title)
        assertEquals("https://img.spoonacular.com/recipes/pasta.jpg", domain.image)
        assertEquals(30, domain.readyInMinutes)
        assertEquals(4, domain.servings)
    }

    @Test
    fun `RecipeEntity toDomain and back maps correctly`() {
        val entity = RecipeEntity(
            id = 123L,
            title = "Pasta",
            image = "pasta.jpg",
            readyInMinutes = 30,
            servings = 4,
            sourceUrl = "http://example.com",
            ingredients = emptyList(),
            summary = "Summary",
            instructions = "Instructions",
            isFavorite = true,
            lastUpdated = 1000L
        )
        val domain = entity.toDomain()
        assertEquals(123L, domain.id)
        assertEquals(true, domain.isFavorite)
        
        val backToEntity = domain.toEntity().copy(lastUpdated = 1000L)
        assertEquals(entity, backToEntity)
    }
}
