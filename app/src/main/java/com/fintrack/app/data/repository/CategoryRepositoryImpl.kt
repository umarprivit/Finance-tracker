package com.fintrack.app.data.repository

import com.fintrack.app.data.local.db.daos.CategoryDao
import com.fintrack.app.data.local.db.entities.CategoryEntity
import com.fintrack.app.domain.model.Category
import com.fintrack.app.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getCategoryById(id: Long): Flow<Category?> {
        return categoryDao.getCategoryById(id).map { it?.toDomain() }
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category.toEntity())
    }

    private fun CategoryEntity.toDomain() = Category(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex
    )

    private fun Category.toEntity() = CategoryEntity(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex
    )
}
