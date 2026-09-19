package com.example.pantrysmart.data.mapper

import com.example.pantrysmart.data.api.model.IngredientDto
import com.example.pantrysmart.data.local.entity.IngredientEntity
import com.example.pantrysmart.data.local.entity.ShoppingListItemEntity
import com.example.pantrysmart.domain.model.Ingredient
import org.junit.Assert.assertEquals
import org.junit.Test

class MappersTest {

    @Test
    fun `extractAdditionalInfo works correctly`() {
        // Case 1: Simple qualifier
        assertEquals("red", extractAdditionalInfo("1 red bell pepper", 1.0, "piece", "bell pepper"))
        
        // Case 2: Multiple qualifiers with spaces
        assertEquals("organic, extra, virgin", extractAdditionalInfo("2 cups organic extra virgin olive oil", 2.0, "cups", "olive oil"))
        
        // Case 3: Mixed case and extra spaces
        assertEquals("Fresh, Chopped", extractAdditionalInfo("1 cup Fresh Chopped Cilantro", 1.0, "cup", "cilantro"))
        
        // Case 4: No qualifiers
        assertEquals(null, extractAdditionalInfo("1 cup Sugar", 1.0, "cup", "Sugar"))
    }

    @Test
    fun `IngredientDto toDomain maps correctly with additionalInfo`() {
        val dto = IngredientDto(
            id = 1,
            name = "Bell pepper",
            original = "1 red bell pepper",
            amount = 1.0,
            unit = "piece",
            image = "pepper.jpg",
        )
        val domain = dto.toDomain()
        
        assertEquals(1, domain.id)
        assertEquals("Bell pepper", domain.name)
        assertEquals("1 red bell pepper", domain.original)
        assertEquals(1.0, domain.amount, 0.0)
        assertEquals("piece", domain.unit)
        assertEquals("https://spoonacular.com/cdn/ingredients_100x100/pepper.jpg", domain.image)
        assertEquals("red", domain.additionalInfo)
    }

    @Test
    fun `IngredientEntity toDomain and toEntity preserve additionalInfo`() {
        val entity = IngredientEntity(
            id = 1,
            name = "Sugar",
            original = "1 cup sugar",
            amount = 1.0,
            unit = "cup",
            image = null,
            additionalInfo = "refined",
        )
        val domain = entity.toDomain()
        assertEquals("refined", domain.additionalInfo)
        
        val backToEntity = domain.toEntity()
        assertEquals("refined", backToEntity.additionalInfo)
    }

    @Test
    fun `ShoppingListItemEntity toDomain maps correctly`() {
        val entity = ShoppingListItemEntity(name = "Sugar", additionalInfo = "refined", amount = 2.0, unit = "cups")
        val domain = entity.toDomain()

        assertEquals("Sugar", domain.name)
        assertEquals(2.0, domain.amount, 0.0)
        assertEquals("cups", domain.unit)
        assertEquals("refined", domain.additionalInfo)
        assertEquals("2.0 cups Sugar", domain.original)
    }

    @Test
    fun `Ingredient toShoppingListItemEntity maps correctly`() {
        val domain = Ingredient(id = 1, name = "Sugar", original = "2 cups Sugar", amount = 2.0, unit = "cups", image = null, additionalInfo = "refined")
        val entity = domain.toShoppingListItemEntity()

        assertEquals("Sugar", entity.name)
        assertEquals("refined", entity.additionalInfo)
        assertEquals(2.0, entity.amount, 0.0)
        assertEquals("cups", entity.unit)
    }
}
