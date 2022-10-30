package com.mjp.cyberjump;

import com.badlogic.gdx.*;
import com.mjp.cyberjump.screens.SplashScreen;

public class GameHandler extends Game
{
    @Override
    public void create() {      
        setScreen(new SplashScreen());
    }
}
