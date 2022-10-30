package com.mjp.cyberjump.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Utils {
    public static final Vector2 screenBounds = new Vector2(1080, 2215);;
    public static String texture = "texture/", music = "music/", sound = "sound/", font = "font/", data = "data/";

    public static Animation<TextureRegion> getAnimation(Texture spriteSheet, int row, int col, float frameDuration) {
        TextureRegion[][] tmp = new TextureRegion(spriteSheet).split(spriteSheet.getWidth() / col, spriteSheet.getHeight() / row);
        TextureRegion[] frames = new TextureRegion[col * row];
        int index = 0;
        for(int i = 0; i < row; ++i)
            for(int j = 0; j < col; ++j)
                frames[index++] = tmp[i][j];
        return new Animation<TextureRegion>(frameDuration, frames);
    }
}
