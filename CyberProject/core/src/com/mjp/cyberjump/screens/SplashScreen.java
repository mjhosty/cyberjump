package com.mjp.cyberjump.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mjp.cyberjump.utils.Utils;

public class SplashScreen implements Screen
{
	int row, col;
	Texture texture;
	Sprite sprite;
	SpriteBatch batch;
	Animation<TextureRegion> anim;
	float time, ct, dif;
	boolean isDesktop;
	
	OrthographicCamera cam;
	@Override
	public void show()
	{

		time = 0;
		row = 10;
		col = 5;
		texture = new Texture(Utils.texture + "splashintro.png");
		TextureRegion[][] tmp = new TextureRegion().split(texture, texture.getWidth() / col, texture.getHeight() / row);
		TextureRegion[] frames = new TextureRegion[col * row];
		int index = 0;
		batch = new SpriteBatch();
		for(int i = 0; i < row; ++i)
			for(int j = 0; j < col; ++j)
				frames[index++] = tmp[i][j];
		anim = new Animation<TextureRegion>(0.1f, frames);
		sprite = new Sprite(anim.getKeyFrame(0));
		dif = Utils.screenBounds.x - sprite.getWidth();
		sprite.setPosition(Utils.screenBounds.x / 2, Utils.screenBounds.y / 2);
		cam = new OrthographicCamera();
		cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		port = new FitViewport(Utils.screenBounds.x, Utils.screenBounds.y, cam);
		cam.update();
	}
	Viewport port;
	@Override
	public void render(float delta)
	{ 
		Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		batch.begin();
		time += Gdx.graphics.getDeltaTime();
		
		sprite = new Sprite(anim.getKeyFrame(time, false));
		sprite.setSize(sprite.getWidth() + dif, sprite.getHeight() + dif);
		sprite.setPosition((Utils.screenBounds.x / 2) - (sprite.getWidth() / 2), (Utils.screenBounds.y / 2) - (sprite.getHeight() / 2));
		
		sprite.draw(batch);
		
		if(anim.isAnimationFinished(time))
		{
			ct += Gdx.graphics.getDeltaTime();
		}
		
		if(ct > 3)
		{
			((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen());
		}
		
		batch.end();
	}

	@Override
	public void resize(int p1, int p2) {
		port.update(p1, p2);
		cam.position.set(Utils.screenBounds.x / 2, Utils.screenBounds.y / 2, cam.position.z);
	}

	@Override
	public void resume() { }

	@Override
	public void hide() { }

	@Override
	public void dispose() { }

	@Override
	public void pause() { }
}
