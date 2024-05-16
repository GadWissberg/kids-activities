package com.gadarts.tamar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ScreenUtils
import com.gadarts.tamar.assets.TexturesDefinitions


class GameScreen(private val assetsManager: GameAssetManager) : Screen, InputProcessor {
    private lateinit var carrier: GameCharacter
    private var draggedPointer: Int = -1
    private var draggedCharacter: GameCharacter? = null
    private lateinit var stage: Stage

    override fun show() {
        stage = Stage()
        val screenTable = Table()
        screenTable.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        screenTable.background =
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TexturesDefinitions.BACKGROUND_WATER))
        stage.addActor(screenTable)
        val gameCharacter = addGameCharacter()
        gameCharacter.setOrigin(gameCharacter.width / 2F, gameCharacter.height / 2F)
        carrier =
            GameCharacter(assetsManager.getAssetByDefinition(TexturesDefinitions.CARRIER_RED))
        gameCharacter.setPosition(50F, Gdx.graphics.height / 5F)
        carrier.setPosition(Gdx.graphics.width - 500F, Gdx.graphics.height / 5F)
        stage.addActor(carrier)
        Gdx.input.inputProcessor = InputMultiplexer(this, stage)
    }

    private fun addGameCharacter(): GameCharacter {
        val gameCharacter =
            GameCharacter(assetsManager.getAssetByDefinition(TexturesDefinitions.CHARACTER_RED))
        gameCharacter.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                draggedCharacter = gameCharacter
                draggedPointer = pointer
                return true
            }
        })
        stage.addActor(gameCharacter)
        return gameCharacter
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

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        draggedCharacter = null
        return true
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        var handled = false
        if (draggedCharacter != null && draggedPointer == pointer) {
            draggedCharacter!!.setPosition(
                screenX.toFloat() - draggedCharacter!!.width / 2F,
                stage.height - screenY.toFloat() - draggedCharacter!!.height / 2F
            )
            handled = true
            if (getBoundingRectangle(draggedCharacter!!, auxRect1).overlaps(
                    getBoundingRectangle(
                        carrier,
                        auxRect2
                    )
                )
            ) {
                draggedCharacter!!.remove()
                carrier.remove()
                draggedCharacter = null
            }
        }
        return handled
    }

    private fun getBoundingRectangle(actor: Actor, output: Rectangle): Rectangle {
        return output.set(actor.x, actor.y, actor.width, actor.height)
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    companion object {
        private val auxRect1 = Rectangle()
        private val auxRect2 = Rectangle()
    }
}
