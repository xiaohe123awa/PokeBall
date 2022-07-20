package com.entiv.pokeball.data

import de.tr7zw.nbtapi.NBTCompound
import net.kyori.adventure.text.Component
import org.bukkit.entity.Phantom

class PhantomData(private val size:Int): EntityData<Phantom>() {
    override fun applyCompound(nbtCompound: NBTCompound) {
        nbtCompound.setInteger("Size", size)
    }

    override fun applyComponent(components: MutableList<Component>) {
        loreComponent("大小", size).also { components.add(it) }
    }

    override fun applyEntity(entity: Phantom) {
        entity.size = size
    }

    companion object : DataCreator<Phantom>() {
        override val dataEntityClass = Phantom::class.java

        override fun getEntityData(nbtCompound: NBTCompound): EntityData<*> {
            return PhantomData(nbtCompound.getInteger("Size"))
        }

        override fun getEntityData(entity: Phantom): EntityData<*> {
            return PhantomData(entity.size)
        }
    }
}