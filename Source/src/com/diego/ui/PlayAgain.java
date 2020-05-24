package com.diego.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.diego.main.Game;

public class PlayAgain{
	private int xMouse, yMouse;
	private boolean clicked;
	private final int SCALE = Game.SCALE;
	private int x, y, width, height;
	
	public PlayAgain() {
		clicked = false;
		xMouse = yMouse = 0;
		x = 0;
		y = 20;
		width = 15;
		height = 80;
	}
	
	public void setClicked(boolean input) {
		clicked = input;
	}
	
	public void setXMouse(int input) {
		xMouse = input/SCALE;
	}
	
	public void setYMouse(int input) {
		yMouse = input/SCALE;
	}
	
	public void tick() {
		if(clicked) {
			clicked = false;
			if(xMouse >= x && xMouse <= width) {
				if(yMouse >= y && yMouse <= height) {
					Game.restart = true;
				}
			}
		}		
	}
	
	public void render(Graphics g) {
		// Rectangle
		g.setColor(Color.orange);
		g.fillRect(x, y, width, height);
		
		// Text
		g.setColor(Color.black);
		g.setFont(new Font("arial", Font.BOLD, 8));
		String word = "PLAY AGAIN";
		
		for(int i = 0; i < word.length(); i++) {
			String singleChar = word.charAt(i) + "";
			g.drawString(singleChar, 5, 30 + 7*i);
		}
	}

}
