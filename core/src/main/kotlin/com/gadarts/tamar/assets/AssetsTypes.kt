package com.gadarts.tamar.assets

enum class AssetsTypes(
    val assets: Array<out AssetDefinition<*>> = arrayOf()
) {
    TEXTURES(TexturesDefinitions.entries.toTypedArray()),

}
