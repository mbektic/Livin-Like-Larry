package com.mbektic.adream.states;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mbektic.adream.sprites.Bird;
import com.mbektic.adream.sprites.Tube;

public class LossState extends State {
    private Array<Tube> tubes;
    private Bird bird;
    private Texture bg, ground, loss;
    private BitmapFont font, font2;
    private Vector2 groundPos1, groundPos2;
    private int score, highScore;
    private OrthographicCamera cam;

    protected LossState(GameStateManager gsm, Bird bird, Array<Tube> tubes, BitmapFont font, Texture ground, Vector2 ground1, Vector2 ground2, int score, int highScore, OrthographicCamera cam){
        super(gsm);
        this.bird = bird;
        this.bg = new Texture("menu.png");
        this.loss = new Texture("lossmenu..png");
        this.ground = ground;
        this.tubes = tubes;
        this.font = font;
        groundPos1 = ground1;
        groundPos2 = ground2;
        this.score = score;
        this.highScore = highScore;
        this.cam = cam;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 30;
        font2 = generator.generateFont(parameter);
        generator.dispose();
    }

    @Override
    protected void handleInput() {
        if(Gdx.input.justTouched()){
            gsm.set(new PlayState(gsm));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(bg,cam.position.x - (cam.viewportWidth/2), 0 ,cam.viewportWidth,cam.viewportHeight);
        sb.draw(bird.getTexture(), bird.getPos().x,bird.getPos().y);
        for(Tube tube: tubes){
            sb.draw(tube.getTopTube(), tube.getPosTopTube().x, tube.getPosTopTube().y);
            sb.draw(tube.getBotTube(), tube.getPosBotTube().x, tube.getPosBotTube().y);
        }
        sb.draw(ground,groundPos1.x,groundPos1.y);
        sb.draw(ground,groundPos2.x,groundPos2.y);

        font.setUseIntegerPositions(false);
        font.getData().setScale(.75f);
        String s;
        if((""+score).length() == 1)
            s = "0"+score;
        else
            s = ""+score;
        font.draw(sb, s, cam.position.x - 20,cam.viewportHeight);
        sb.draw(loss,cam.position.x - 100,cam.position.y - 75,200,150);
        font2.setColor(Color.BLACK);
        if((""+highScore).length() == 1)
            s = "0"+highScore;
        else
            s = ""+highScore;
        font2.draw(sb,""+s,cam.position.x + 45, cam.position.y +5);
        sb.end();
    }

    @Override
    public void dispose() {
        bg.dispose();
        bird.dispose();
        ground.dispose();
        font.dispose();
        font2.dispose();
        loss.dispose();
        for(Tube tube: tubes)
            tube.dispose();
    }
}
