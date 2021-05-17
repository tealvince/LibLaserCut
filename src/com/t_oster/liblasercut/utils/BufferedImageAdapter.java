/**
 * This file is part of LibLaserCut.
 * Copyright (C) 2011 - 2014 Thomas Oster <mail@thomas-oster.de>
 *
 * LibLaserCut is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibLaserCut is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with LibLaserCut. If not, see <http://www.gnu.org/licenses/>.
 *
 **/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.t_oster.liblasercut.utils;

import com.t_oster.liblasercut.GreyscaleRaster;
import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 *
 * @author Thomas Oster <thomas.oster@rwth-aachen.de>
 */
public class BufferedImageAdapter implements GreyscaleRaster
{

  private BufferedImage img;
  private int colorShift = 0;
  private int contrastBoost = 0;
  private boolean invertColors = false;
  private int minColor = 255;
  private int maxColor = 0;

  public BufferedImageAdapter(BufferedImage img)
  {
    this(img, false);
  }
  
  public BufferedImageAdapter(BufferedImage img, boolean invertColors)
  {
    this.img = img;

	 // Get used range (before colorshift or invertColors are set)
	 this.invertColors = false;
	 int x = 0;
	 int y = 0;
	 int width = img.getWidth();
	 int height = img.getHeight();

	 for (y = 0; y < height; y++)
	 {
      for (x = 0; x < width; x++)
      {
			int value = this.getGreyScale(x,y);
         if (this.minColor > value) {
				this.minColor = value;
			}
			if (this.maxColor < value) {
				this.maxColor = value;
			}
      }
    }

    this.invertColors = invertColors;
  }

  public void setColorShift(int cs){
      this.colorShift = cs;
  }

  public int getColorShift(){
      return this.colorShift;
  }

  public void setContrastBoost(int boost){
      this.contrastBoost = boost;
  }

  public int getContrastBoost(){
      return this.contrastBoost;
  }

  public int getGreyScale(int x, int line)
  {
    Color c = new Color(img.getRGB(x, line));

	 // Get perceived brightness
	 double brightness = (0.3 * c.getRed() + 0.59 * c.getGreen() + 0.11 * c.getBlue());

	 // Boost contrast levels
	 if (this.contrastBoost > 0) {
		int average = (this.minColor + this.maxColor)/2;
		int minimum;
		int maximum;

		// 50% contrastBoost maps grayscale to full dynamic range
		if (this.contrastBoost <= 127) {
			minimum = (this.contrastBoost * this.minColor) / 127;
			maximum = 255 - (this.contrastBoost * (255 - this.maxColor)) / 127;
		}
		// Above 50% clips in further toward average brightness, clamping top and bottom ranges
		else {
			minimum = this.minColor + (average - this.minColor) * (this.contrastBoost - 128) / 150;
			maximum = this.maxColor - (this.maxColor - average) * (this.contrastBoost - 128) / 150;
		}

		if (maximum > minimum) {
			brightness = minimum + (brightness - minimum) * 255 / (maximum - minimum);
		}
	 }

	 // Add color shift
    int value = colorShift+(int)brightness;

	 // Apply optional inversion and clamp
    return invertColors ? 255-Math.max(Math.min(value, 255), 0) : Math.max(Math.min(value, 255), 0);
  }

  public void setGreyScale(int x, int y, int grey)
  {
    Color c = new Color(grey, grey, grey);
    img.setRGB(x, y, c.getRGB());
  }

  public int getWidth()
  {
    return img.getWidth();
  }

  public int getHeight()
  {
    return img.getHeight();
  }
}
