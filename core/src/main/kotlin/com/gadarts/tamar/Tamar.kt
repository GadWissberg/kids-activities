package com.gadarts.tamar

import com.badlogic.gdx.Game

class Tamar : Game() {
    private lateinit var assetsManager: GameAssetManager

    override fun create() {
        assetsManager = GameAssetManager()
        assetsManager.loadAssets()
        setScreen(GameScreen(assetsManager))
    }

    override fun dispose() {
        super.dispose()
        assetsManager.dispose()
    }

}
