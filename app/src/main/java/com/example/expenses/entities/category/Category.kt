package com.example.expenses.entities.category

import com.example.expenses.extensions.randomColor
import kotlin.random.Random

data class Category(
    val name: String,
    val fullName: String,
    var parent: Category? = null,
    val subCategories: MutableList<Category> = mutableListOf(),
    val color: Int = Random.randomColor()
) {
    fun hasSubCategories() = subCategories.isNotEmpty()
    fun hasParent() = parent != null
    fun toCategoryDBEntity() = CategoryDBEntity(fullName, parent?.fullName, color)
    fun getFriendlyName() = CategoryDBEntity.getFriendlyName(name)

    companion object {
        fun getDefaultRootCategory() = Category("Root", "Root", subCategories = mutableListOf(
            Category("Entertainment", "Root#Entertainment"),
            Category("Food", "Root#Food"),
            Category("Taxes", "Root#Taxes"),
            Category("Clothes", "Root#Clothes"),
            Category("Health", "Root#Health"),
            Category(
                "Transport", "Root#Transport", subCategories = mutableListOf(
                    Category("Public transport", "Root#Transport#Public transport"),
                    Category("Taxi", "Root#Transport#Taxi"),
                    Category("Flights", "Root#Transport#Flights"),
                    Category(
                        "Car maintenance",
                        "Root#Transport#Car maintenance",
                        subCategories = mutableListOf(
                            Category("Fuel", "Root#Transport#Car maintenance#Fuel"),
                            Category(
                                "Technical maintenance",
                                "Root#Transport#Car maintenance#Technical maintenance"
                            ),
                            Category("Fines", "Root#Transport#Car maintenance#Fines"),
                        )
                    ),
                )
            ),
            Category(
                "Recreation", "Root#Recreation", subCategories = mutableListOf(
                    Category("Hotels", "Root#Recreation#Hotels"),
                    Category("Transport", "Root#Recreation#Transport"),
                    Category("Food", "Root#Recreation#Food"),
                    Category("Trips", "Root#Recreation#Trips"),
                )
            ),
            Category(
                "House", "Root#House", subCategories = mutableListOf(
                    Category(
                        "Housing service", "Root#House#Housing service", subCategories = mutableListOf(
                            Category("Electricity", "Root#House#Housing service#Electricity"),
                            Category("Water", "Root#House#Housing service#Water"),
                            Category("Heating", "Root#House#Housing service#Heating"),
                            Category("Gas", "Root#House#Housing service#Gas"),
                        )
                    ),
                    Category("Furniture", "Root#House#Furniture"),
                    Category("Repair", "Root#House#Repair"),
                    Category("Appliances", "Root#House#Appliances"),
                )
            ),
            Category("Other", "Root#Other"),
        )).also { setParentsForCategory(it) }

        private fun setParentsForCategories(mutableList: MutableList<Category>) {
            mutableList.forEach { category ->
                category.subCategories.forEach {
                    it.parent = category
                }
                setParentsForCategories(category.subCategories)
            }
        }
        private fun setParentsForCategory(category: Category) {
            category.subCategories.forEach {
                it.parent = category
                setParentsForCategory(it)
            }
        }

        fun allToString(categories: List<Category>): String{
            val stringBuilder = StringBuilder()
            categories.forEach {
                stringBuilder.append(it.name).append(" :   {").append(allToString(it.subCategories)).append("}\n")
            }
            return stringBuilder.toString()
        }
    }

    override fun toString(): String {
        return "Category{name=\"$name\", fullName=\"$fullName\", parent=$parent}"
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other is Category && this.fullName == other.fullName
    }

    override fun hashCode(): Int {
        return fullName.hashCode()
    }
}