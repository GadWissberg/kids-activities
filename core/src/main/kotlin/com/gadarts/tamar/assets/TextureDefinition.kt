package com.gadarts.tamar.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.graphics.Texture

enum class TextureDefinition(fileNames: Int = 1) : AssetDefinition<Texture> {

    BACKGROUND_WATER,
    CHARACTER_RED,
    CHARACTER_GREEN,
    CHARACTER_PINK,
    CHARACTER_ORANGE,
    CARRIER_RED,
    CARRIER_GREEN,
    CARRIER_PINK,
    CARRIER_ORANGE;

    private val paths = ArrayList<String>()
    private val pathFormat = "textures/%s.png"

    init {
        initializePaths(pathFormat, fileNames)
    }

    override fun getPaths(): ArrayList<String> {
        return paths
    }

    override fun getParameters(): AssetLoaderParameters<Texture>? {
        return null
    }

    override fun getClazz(): Class<Texture> {
        return Texture::class.java
    }

    override fun getDefinitionName(): String {
        return name
    }

}
