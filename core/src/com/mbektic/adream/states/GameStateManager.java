package com.mbektic.adream.states;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Stack;

public class GameStateManager {
    private Stack<State> states;
    static boolean resume;

    public GameStateManager(){
        states = new Stack<State>();
        resume = true;
    }

    public String getState(){return states.peek().stateName;}

    public void push(State state){
        states.push(state);
    }

    public void pop(){
        states.pop().dispose();
    }

    public void set(State state){
        states.pop().dispose();
        states.push(state);
    }

    public void update(float dt){
        states.peek().update(dt);
    }

    public void render(SpriteBatch sb){
        states.peek().render(sb);
    }

    public void setResume(boolean b){
        resume = b;
    }
}
