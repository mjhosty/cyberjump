package com.mjp.cyberjump.objects;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.*;
import com.mjp.cyberjump.utils.Utils;

import java.util.*;

public class Obstacle
{
	public Vector2 position;
	private Sprite top, tube, tube2, bottom;
	private ArrayList<Sprite> sprites;
	private float y2;
	private ShapeRenderer sr;
	private boolean added;
	private Player player;
	private float size = (Utils.screenBounds.x + Utils.screenBounds.y) / 10;
	public Obstacle(Sprite top, Sprite tube, Sprite bottom, float y, Player player)
	{
		this.player = player;
		this.top = top;
		this.tube = tube;
		this.tube2 = new Sprite(tube);
		this.bottom = bottom;
		sr = new ShapeRenderer();

		this.bottom.setSize(size, size);
		this.tube2.setSize(size, size * 5);
		this.top.setSize(size, size);
		this.tube.setSize(size, size * 5);

		position = new Vector2(Utils.screenBounds.x + top.getWidth(), y);

		float minY = 275 * (player.score / 100);
		minY = MathUtils.clamp(minY, 275, Utils.screenBounds.y - 275);

		y2 = MathUtils.random(y + minY, (y + minY + 275));
		y2 = MathUtils.clamp(y2, y + minY, Utils.screenBounds.y - 275);


		top.setPosition(position.x - (top.getWidth() / 2), position.y - top.getHeight());
		tube.setPosition(top.getX(), top.getY() - tube.getHeight());
		bottom.setPosition(top.getX(), y2);
		tube2.setPosition(bottom.getX(), bottom.getY() + bottom.getHeight());

		sprites = new ArrayList<Sprite>();
		sprites.add(top);
		sprites.add(bottom);
		sprites.add(tube);
		sprites.add(tube2);
	}

	public float getX()
	{
		return position.x;
	}

	public ArrayList<Sprite> getAll()
	{
		return sprites;
	}

	public float getWidth()
	{
		return top.getWidth();
	}

	public void transform(Player player, Sound sound, Music music)
	{
		this.player = player;
		
		if (!player.collide)
			position.add(new Vector2(-100 * 2, 0).scl(Gdx.graphics.getDeltaTime()));

		top.setPosition(position.x - (top.getWidth() / 2), position.y - top.getHeight());
		tube.setPosition(top.getX(), top.getY() - tube.getHeight());
		bottom.setPosition(top.getX(), y2);
		tube2.setPosition(bottom.getX(), bottom.getY() + bottom.getHeight());

		if (player.getX() > getX() && !added)
		{
			if (!music.isPlaying()) sound.play();
			added = true;
			player.score++;
		}
		
	}

	public void draw(SpriteBatch batch)
	{
		top.draw(batch);
		tube.draw(batch);
		bottom.draw(batch);
		tube2.draw(batch);
	}

}
