package com.diego.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Spritesheet {
	private BufferedImage spritesheet;
	
	public Spritesheet(String path) {
		try {
			spritesheet = ImageIO.read(getClass().getResource(path));
		} catch(IOException err) {
			err.getStackTrace();
		}
	}
	
	public BufferedImage getSprite(int x, int y, int w, int h) {
		return spritesheet.getSubimage(x, y, w, h);
	}
	
	
}
