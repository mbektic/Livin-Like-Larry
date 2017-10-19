package com.mbektic.adream;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mbektic.adream.states.GameStateManager;
import com.mbektic.adream.states.MenuState;

public class ADream extends ApplicationAdapter {
	public static final int width = 480;
	public static final int height = 800;

	public static final String title = "A Dream";
	private GameStateManager gsm;
	private SpriteBatch batch;
	private Music music;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm = new GameStateManager();
		music = Gdx.audio.newMusic(Gdx.files.internal("larry.mp3"));
		music.setLooping(true);
		music.setVolume(0.1f);
		music.play();
		Gdx.gl.glClearColor(1, 0, 0, 1);
		gsm.push(new MenuState(gsm));
	}

	@Override
	public void render () {
		if(!gsm.getState().equals("play"))
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}

	@Override
	public void dispose() {
		super.dispose();
		music.dispose();
	}

	@Override
    public void pause(){
        gsm.setResume(false);
    }

	@Override
	public void resume(){
		gsm.setResume(true);
        Gdx.graphics.getDeltaTime();
	}
}
