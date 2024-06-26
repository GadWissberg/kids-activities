package com.gadarts.tamar.assets

import com.badlogic.gdx.assets.AssetLoaderParameters
import java.util.ArrayList
import java.util.Locale

interface AssetDefinition<T> {
    fun getPaths(): ArrayList<String>
    fun getDefinitionName(): String
    fun getClazz(): Class<T>
    fun getParameters(): AssetLoaderParameters<T>?
    fun initializePaths(pathFormat: String, fileNames: Int = 1) {
        val paths = getPaths()
        val definitionName = getDefinitionName()
        if (fileNames == 1) {
            paths.add(pathFormat.format(definitionName.lowercase(Locale.ROOT)))
        } else {
            for (i in 0 until fileNames) {
                paths.add(pathFormat.format(definitionName.lowercase(Locale.ROOT) + "_" + i))
            }
        }
    }

}
