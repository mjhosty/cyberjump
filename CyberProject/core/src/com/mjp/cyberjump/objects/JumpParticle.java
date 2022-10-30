package com.mjp.cyberjump.objects;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import java.util.*;

public class JumpParticle
{
	private Animation<TextureRegion> anim;
	private Vector2 pos;
	private int scaleX, scaleY;
	public Sprite cFrame;

	public JumpParticle(Texture texture, int row, int col, int scaleX, int scaleY, float speed)
	{
		TextureRegion[][] tmp = new TextureRegion(texture).split(texture.getWidth() / col, texture.getHeight() / row);
		TextureRegion[] frames = new TextureRegion[row * col];
		int index = 0;
		
		for(int i = 0; i < row; ++i)
			for(int j = 0; j < col; ++j)
				frames[index++] = tmp[i][j];
				
		anim = new Animation<TextureRegion>(speed, frames);
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		
		cFrame = new Sprite(anim.getKeyFrame(0));
		cFrame.setSize(scaleX, scaleY);

	}
	
	public void setPosition(float x, float y)
	{
		pos = new Vector2(x, y);
	}
	
	public Vector2 getPosition()
	{
		return pos;
	}
	
	public float getHeight()
	{
		return scaleY;
	}
	
	public float getWidth()
	{
		return scaleX;
	}
	
	public float time;
	
	public Animation getAnimation()
	{
		return anim;
	}
	
	public void update(ArrayList<JumpParticle> list)
	{
		time += Gdx.graphics.getDeltaTime();

		if(anim.isAnimationFinished(time))
		{
			for(int i = 0; i < list.size(); ++i)
			{
				if(list.get(i) == this)
				{
					list.remove(i);
				}
			}
		}
	}
	
	public void draw(SpriteBatch batch)
	{
		cFrame = new Sprite(anim.getKeyFrame(time, true));
		cFrame.setSize(scaleX, scaleY);
		cFrame.setPosition(pos.x, pos.y);
		cFrame.draw(batch);
	}
}
