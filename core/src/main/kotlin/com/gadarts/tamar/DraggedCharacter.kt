package com.gadarts.tamar

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture

class DraggedCharacter(
    texture: Texture,
    val carrier: GameCharacter,
    val characterColor: Color,
    val startX: Float,
    val startY: Float
) :
    GameCharacter(texture) {
    var applied: Boolean = false

    init {
        setPosition(startX, startY)
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (applied && !hasActions()) {
            setPosition(
                carrier.x + carrier.width / 2F - width / 2F,
                carrier.y + carrier.height / 2F - height / 2F + CARRIED_Y_BIAS
            )
        }
    }

    companion object {
        const val CARRIED_Y_BIAS = 25F
    }
}
