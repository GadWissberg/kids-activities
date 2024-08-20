package com.gadarts.tamar

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import com.gadarts.tamar.DraggedCharacter.Companion.CARRIED_Y_BIAS
import com.gadarts.tamar.assets.TextureDefinition


class GameScreen(private val assetsManager: GameAssetManager) : Screen, InputProcessor {
    private val colorsPairs: Map<Color, Pair<TextureDefinition, TextureDefinition>> by lazy {
        mapOf(
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
    }
    private val colors: MutableList<Color> by lazy {
        mutableListOf(
            Color.RED,
            Color.ORANGE,
            Color.PINK,
            Color.GREEN
        )
    }
    private val carriers = HashMap<Color, GameCharacter>()
    private var draggedPointer: Int = -1
    private var draggedCharacter: DraggedCharacter? = null
    private val stage: Stage by lazy { Stage() }

    override fun show() {
        val screenTable = Table()
        screenTable.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        screenTable.background =
            TextureRegionDrawable(assetsManager.getAssetByDefinition(TextureDefinition.BACKGROUND_WATER))
        stage.addActor(screenTable)
        colors.removeAt(MathUtils.random(colors.size - 1))
        startGame()
        Gdx.input.inputProcessor = InputMultiplexer(this, stage)
    }

    private fun startGame() {
        colors.shuffle()
        for (i in 0 until colors.size) {
            addCarrier(
                colors[i],
                colorsPairs[colors[i]]!!.second,
                Gdx.graphics.height * (2F + i * 2.5F) / 9F
            )
        }
        colors.shuffle()
        for (i in 0 until colors.size) {
            addGameCharacter(
                Gdx.graphics.height * (2F + i * 2.5F) / 9F,
                colorsPairs,
                colors[i]
            )
        }
    }

    private fun addCarrier(
        color: Color,
        textureDefinition: TextureDefinition,
        y: Float,
    ) {
        val texture = assetsManager.getAssetByDefinition(textureDefinition)
        val carrier =
            GameCharacter(texture)
        carrier.setPosition(Gdx.graphics.width - 500F, y - texture.height / 2F)
        carriers[color] = carrier
        carrier.zIndex = 1
        applyIdleAnimationOnCharacter(carrier)
        stage.addActor(carrier)
    }

    private fun addGameCharacter(
        y: Float,
        colors: Map<Color, Pair<TextureDefinition, TextureDefinition>>,
        color: Color
    ): GameCharacter {
        val texture = assetsManager.getAssetByDefinition(colors[color]!!.first)
        val gameCharacter = DraggedCharacter(
            texture,
            carriers[color]!!,
            color,
            75F,
            y - texture.height / 2F
        )
        gameCharacter.addListener(object : InputListener() {
            override fun touchDown(
                event: InputEvent?,
                x: Float,
                y: Float,
                pointer: Int,
                button: Int
            ): Boolean {
                if (gameCharacter.applied) return false
                draggedCharacter = gameCharacter
                draggedCharacter?.clearActions()
                draggedPointer = pointer
                return true
            }
        })
        stage.addActor(gameCharacter)
        gameCharacter.zIndex = 2
        gameCharacter.setOrigin(Align.center)
        applyIdleAnimationOnCharacter(gameCharacter)
        return gameCharacter
    }

    private fun applyIdleAnimationOnCharacter(gameCharacter: GameCharacter) {
        val randomDirection = MathUtils.randomSign()
        val randomIdleStepMoveBySize = MathUtils.random(IDLE_STEP_MOVE_BY - 5F, IDLE_STEP_MOVE_BY)
        val randomIdleStepMoveByDuration = MathUtils.random(1F, 2F)
        gameCharacter.addAction(
            Actions.parallel(
                Actions.forever(
                    Actions.sequence(
                        Actions.scaleBy(
                            0F,
                            0.1F,
                            IDLE_STEP_SHRINK_DURATION,
                            IDLE_STEP_SHRINK_INTERPOLATION
                        ),
                        Actions.scaleBy(
                            0F,
                            -0.1F,
                            IDLE_STEP_SHRINK_DURATION,
                            IDLE_STEP_SHRINK_INTERPOLATION
                        ),
                        Actions.scaleBy(
                            0F,
                            -0.1F,
                            IDLE_STEP_SHRINK_DURATION,
                            IDLE_STEP_SHRINK_INTERPOLATION
                        ),
                        Actions.scaleBy(
                            0F,
                            0.1F,
                            IDLE_STEP_SHRINK_DURATION,
                            IDLE_STEP_SHRINK_INTERPOLATION
                        ),
                    )
                ),
                Actions.forever(
                    Actions.sequence(
                        Actions.moveBy(
                            0F,
                            randomDirection * -randomIdleStepMoveBySize,
                            randomIdleStepMoveByDuration,
                            IDLE_STEP_MOVE_BY_INTERPOLATION
                        ),
                        Actions.moveBy(
                            0F,
                            randomDirection * randomIdleStepMoveBySize * 2F,
                            randomIdleStepMoveByDuration,
                            IDLE_STEP_MOVE_BY_INTERPOLATION
                        ),
                        Actions.moveBy(
                            0F,
                            randomDirection * -randomIdleStepMoveBySize,
                            randomIdleStepMoveByDuration,
                            IDLE_STEP_MOVE_BY_INTERPOLATION
                        )
                    )
                )
            )
        )
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
        if (this.draggedCharacter != null) {
            val draggedCharacter = draggedCharacter!!
            draggedCharacter.clearActions()
            draggedCharacter.addAction(
                Actions.sequence(
                    Actions.moveTo(
                        draggedCharacter.startX,
                        draggedCharacter.startY,
                        3F,
                        Interpolation.exp5
                    ),
                    Actions.run { applyIdleAnimationOnCharacter(draggedCharacter) }
                ),
            )
        }
        this.draggedCharacter = null
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
            if (getBoundingRectangle(
                    draggedCharacter!!,
                    auxRect1,
                ).overlaps(
                    getBoundingRectangle(
                        draggedCharacter!!.carrier,
                        auxRect2
                    )
                )
            ) {
                characterAppliedOnHisColor()
            }
        }
        return handled
    }

    private fun characterAppliedOnHisColor() {
        draggedCharacter!!.applied = true
        val carrier = carriers[draggedCharacter!!.characterColor]
        draggedCharacter!!.addAction(characterExitSequence(carrier!!, draggedCharacter!!))
        draggedCharacter = null
    }

    private fun characterExitSequence(
        carrier: GameCharacter,
        ball: DraggedCharacter
    ): SequenceAction =
        Actions.sequence(
            Actions.moveTo(
                carrier.x + carrier.width / 2F - ball.width / 2F,
                carrier.y + carrier.height / 2F - ball.height / 2F + CARRIED_Y_BIAS,
                2F,
                Interpolation.swing
            ),
            Actions.run {
                carrier.addAction(
                    Actions.sequence(
                        Actions.moveBy(
                            Gdx.graphics.width + carrier.width * 2F,
                            0F,
                            2F,
                            Interpolation.smoother
                        ),
                        Actions.run {
                            removeDraggedCharacterAndCarrier(ball)
                            if (carriers.isEmpty()) {
                                startGame()
                            }
                        }
                    )
                )
            }
        )

    private fun removeDraggedCharacterAndCarrier(character: DraggedCharacter) {
        character.remove()
        character.carrier.remove()
        carriers.remove(character.characterColor)
    }

    private fun getBoundingRectangle(actor: Actor, output: Rectangle): Rectangle {
        return output.set(
            actor.x + BOUNDING_BOX_PADDING,
            actor.y + BOUNDING_BOX_PADDING,
            actor.width - BOUNDING_BOX_PADDING,
            actor.height - BOUNDING_BOX_PADDING
        )
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    companion object {
        private const val BOUNDING_BOX_PADDING: Int = 100
        private val auxRect1 = Rectangle()
        private val auxRect2 = Rectangle()
        private const val IDLE_STEP_SHRINK_DURATION = 3F
        private val IDLE_STEP_SHRINK_INTERPOLATION = Interpolation.smoother
        private const val IDLE_STEP_MOVE_BY = 10F
        private val IDLE_STEP_MOVE_BY_INTERPOLATION = Interpolation.smoother
    }
}
