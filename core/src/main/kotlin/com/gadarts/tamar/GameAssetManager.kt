package com.gadarts.tamar

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.gadarts.tamar.assets.AssetDefinition
import com.gadarts.tamar.assets.AssetsTypes

class GameAssetManager : AssetManager() {
    inline fun <reified T> getAssetByDefinition(definition: AssetDefinition<T>): T {
        return get(definition.getPaths().random(), T::class.java)
    }

    fun loadAssets() {
        AssetsTypes.entries.forEach { type ->
            if (type.assets.isNotEmpty()) {
                type.assets.forEach { asset ->
                    asset.getPaths().forEach { path ->
                        if (asset.getParameters() != null) {
                            load(
                                path,
                                BitmapFont::class.java,
                                (asset.getParameters() as FreetypeFontLoader.FreeTypeFontLoaderParameter)
                            )
                        } else {
                            load(path, asset.getClazz())
                        }
                    }
                }
            }
        }
        finishLoading()
    }

}
