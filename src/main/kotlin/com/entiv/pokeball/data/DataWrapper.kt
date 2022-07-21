package com.entiv.pokeball.data

import de.tr7zw.nbtapi.NBTCompound
import de.tr7zw.nbtapi.NBTItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

sealed class DataWrapper<T : Any>(private val clazz: KClass<T>) {

    protected abstract fun entityWriteToNbt(entity: T, compound: NBTCompound)

    protected abstract fun nbtWriteToEntity(compound: NBTCompound, entity: T)

    protected abstract fun entityWriteToComponent(entity: T, components: MutableList<Component>)

    fun processEntity(entity: Entity, compound: NBTCompound) {

        castEntity(entity)?.let {
            nbtWriteToEntity(compound, it)
        }
    }

    fun processItemStack(itemStack: ItemStack, entity: Entity) {

        val castEntity = castEntity(entity) ?: return
        val nbtItem = NBTItem(itemStack)

        nbtItem.addCompound("PokeBall").apply {
            entityWriteToNbt(castEntity, this)
        }

        nbtItem.applyNBT(itemStack)

        val lore = itemStack.lore() ?: mutableListOf()
        entityWriteToComponent(castEntity, lore)
        itemStack.lore(lore)
    }

    private fun canCastEntity(entity: Entity) = entity::class.isSubclassOf(clazz)

    protected fun addComponent(components: MutableList<Component>, type: String, variable: Any) {
        val text = Component.text()
            .decoration(TextDecoration.ITALIC, false)
            .append(Component.text("$type: ", NamedTextColor.AQUA))

        if (variable is Component) {
            text.append(variable.color(NamedTextColor.YELLOW))
        } else {
            text.append(Component.text(variable.toString(), NamedTextColor.YELLOW))
        }

        components.add(text.build())
    }


    private fun castEntity(entity: Entity): T? {
        @Suppress("UNCHECKED_CAST")
        return if (canCastEntity(entity)) {
            entity as T
        } else {
            null
        }
    }

    protected fun noNeedComponent() {}
}