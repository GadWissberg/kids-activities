package com.gadarts.tamar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ScreenUtils
import com.gadarts.tamar.assets.TexturesDefinitions

class GameScreen(private val assetsManager: GameAssetManager) : Screen {
    private lateinit var stage: Stage

    override fun show() {
        stage = Stage()
        val screenTable = Table()
        screenTable.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        screenTable.background =
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TexturesDefinitions.BACKGROUND_WATER))
        stage.addActor(screenTable)
        val gameCharacter =
            GameCharacter(assetsManager.getAssetByDefinition(TexturesDefinitions.CHARACTER_RED))
        gameCharacter.setPosition(50F, 50F)
        stage.addActor(gameCharacter)
    }

    override fun render(delta: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun hide() {
    }

    override fun dispose() {
        stage.dispose()
    }
}
