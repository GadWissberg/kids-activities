package com.gadarts.tamar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ScreenUtils
import com.gadarts.tamar.assets.TextureDefinition


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
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TextureDefinition.BACKGROUND_WATER))
        stage.addActor(screenTable)
        val colors = mutableListOf(Color.RED, Color.ORANGE, Color.PINK, Color.GREEN)
        colors.removeAt(MathUtils.random(colors.size - 1))
        val colorsPairs = mapOf(
            Color.RED to
                Pair(
                    TextureDefinition.CHARACTER_RED,
                    TextureDefinition.CARRIER_RED
                ),
            Color.ORANGE to Pair(
                TextureDefinition.CHARACTER_ORANGE,
                TextureDefinition.CARRIER_ORANGE
            ),
            Color.PINK to
                Pair(
                    TextureDefinition.CHARACTER_PINK,
                    TextureDefinition.CARRIER_PINK
                ),
            Color.GREEN to Pair(
                TextureDefinition.CHARACTER_GREEN,
                TextureDefinition.CARRIER_GREEN
            )
        )
        colors.shuffle()
        for (i in 0 until colors.size) {
            addGameCharacter(
                Gdx.graphics.height * (2F + i * 2.5F) / 9F,
                colorsPairs,
                colors[i]
            )
        }
        colors.shuffle()
        for (i in 0 until colors.size) {
            addCarrier(
                colorsPairs[colors[i]]!!.second,
                Gdx.graphics.height * (2F + i * 2.5F) / 9F
            )
        }
        Gdx.input.inputProcessor = InputMultiplexer(this, stage)
    }

    private fun addCarrier(
        textureDefinition: TextureDefinition,
        y: Float,
    ) {
        val texture = assetsManager.getAssetByDefinition(textureDefinition)
        carrier =
            GameCharacter(texture)
        carrier.setPosition(Gdx.graphics.width - 500F, y - texture.height / 2F)
        stage.addActor(carrier)
    }

    private fun addGameCharacter(
        y: Float,
        colors: Map<Color, Pair<TextureDefinition, TextureDefinition>>,
        color: Color
    ): GameCharacter {
        val texture = assetsManager.getAssetByDefinition(colors[color]!!.first)
        val gameCharacter =
            GameCharacter(texture)
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
        gameCharacter.setPosition(
            75F, y - texture.height / 2F
        )
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
