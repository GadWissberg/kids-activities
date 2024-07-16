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
    init {
        setPosition(startX, startY)
    }
}
