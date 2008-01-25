/*
 * Thresholding (IBV assignment)
 * By Laurens Holst, 0122173
 */

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageCanvas extends Canvas {
	
	final static long serialVersionUID = 7834234273249327714L;
	
	BufferedImage imageOriginal, imageGrayscale, imageBlurred, imageStandard, imageImproved;
	
	File file;
	Thresholding main;
	
	int K = 4;
	int[] thresholdsStandard, thresholdsImproved;
	int[] histogramStandard, histogramImproved;
	
	// Adjust this matrix to achieve different kinds of blur/effects
	double[][] blurMatrix = {
			{ 1, 1, 1 },
			{ 1, 1, 1 },
			{ 1, 1, 1 }
		};
	
	/**
	 * Create a canvas to show the images
	 * @param main
	 */
	public ImageCanvas(Thresholding main) {
		this.main = main;
	}
	
	/**
	 * Create a canvas to show the images, and load an image
	 * Note: it’s not possible to create a canvas without an image
	 * @param file Reference to the image file
	 * @param main Reference to the main frame
	 */
	public ImageCanvas(File file, Thresholding main) {
		this.main = main;
		load(file);
	}
	
	/**
	 * Load an image
	 * @param file Reference to the file
	 */
	public void load(File file) {
		main.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			System.out.println("Opening: " + file);
			imageOriginal = ImageIO.read(file);
		} catch (IOException e) {
			System.out.println("*** Can’t read selected file");
			main.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		this.file = file;
		int width = imageOriginal.getWidth();
		int height = imageOriginal.getHeight();
		this.setSize(width, height);
		// create grayscale image
		imageGrayscale = grayscaleImage(imageOriginal);
		main.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		initialize();
	}
	
	/**
	 * Save the images
	 */
	public void save() {
		main.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			System.out.println("Saving: " + file.getName() + ".grayscale.png");
			ImageIO.write(imageGrayscale, "png", new File(file.getParent(), file.getName() + ".grayscale.png"));
		} catch (Exception e) {
			System.err.println("*** Can’t write grayscale image");
		}
		try {
			System.out.println("Saving: " + file.getName() + ".blurred.png");
			ImageIO.write(imageBlurred, "png", new File(file.getParent(), file.getName() + ".blurred.png"));
		} catch (Exception e) {
			System.err.println("*** Can’t write blurred image");
		}
		try {
			System.out.println("Saving: " + file.getName() + ".standard.png");
			ImageIO.write(imageStandard, "png", new File(file.getParent(), file.getName() + ".standard.png"));
		} catch (Exception e) {
			System.err.println("*** Can’t write standard thresholded image");
		}
		try {
			System.out.println("Saving: " + file.getName() + ".improved.png");
			ImageIO.write(imageImproved, "png", new File(file.getParent(), file.getName() + ".improved.png"));
		} catch (Exception e) {
			System.err.println("*** Can’t write improved thresholded image");
		}
		main.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * Initialise after loading
	 */
	public void initialize() {
		main.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		// create standard thresholded image
		histogramStandard = computeHistogram(imageGrayscale);
		thresholdsStandard = computeThresholds(histogramStandard);
		imageStandard = thresholdImage(imageGrayscale, thresholdsStandard);
		
		// create blurred and improved thresholded image
		imageBlurred = blurImage(imageGrayscale, blurMatrix);
		histogramImproved = computeHistogram(imageBlurred);
		thresholdsImproved = computeThresholds(histogramImproved);
		imageImproved = thresholdImage(imageGrayscale, thresholdsImproved);
		
		main.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		repaint();
	}
	
	/**
	 * Create thresholded image
	 * @param source Source image
	 * @param thresholds The thresholds to use
	 * @return The thresholded image
	 */
	public BufferedImage thresholdImage(BufferedImage sourceImage, int[] thresholds) {
		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();
		double step = 255.0 / K;
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int lum = sourceImage.getRGB(x, y) & 0xff;
				int value = (int)(step * getSegmentNr(lum, thresholds) + 0.5);
				destImage.setRGB(x, y, new Color(value, value, value).getRGB());
			}
		}
		return destImage;
	}
	
	/**
	 * Convert an image to grayscale
	 * @param sourceImage Source image
	 * @return Grayscaled image
	 */
	public BufferedImage grayscaleImage(BufferedImage sourceImage) {
		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color color = new Color(sourceImage.getRGB(x, y));
				int lum = (int)(.299 * color.getRed() + .587 * color.getGreen() + .114 * color.getBlue() + 0.5);
		        destImage.setRGB(x, y, new Color(lum, lum, lum).getRGB());
			}
		}
		return destImage;
	}
	
	/**
	 * Apply a (blur) matrix to an image
	 * @param sourceImage Source image
	 * @param blurMatrix The matrix to use
	 * @return Blurred image
	 */
	public BufferedImage blurImage(BufferedImage sourceImage, double[][] blurMatrix) {
		int width = sourceImage.getWidth();
		int height = sourceImage.getHeight();
		BufferedImage destImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				
				int lum = 0;
				int lengthx = blurMatrix[0].length;
				int lengthy = blurMatrix.length;
				int offsetx = (lengthx - 1) / -2;
				int offsety = (lengthy - 1) / -2;
				for (int u = 0; u < lengthx; u++) {
					for (int v = 0; v < lengthy; v++) {
						int x2 = x + u + offsetx;
						int y2 = y + v + offsety;
						x2 = x2 < 0 ? 0 : x2 >= width ? width - 1 : x2;
						y2 = y2 < 0 ? 0 : y2 >= height ? height - 1 : y2;
						lum += (sourceImage.getRGB(x2, y2) & 0xff) * blurMatrix[u][v];
					}
				}
				double divider = 0;
				for (int u = 0; u < lengthx; u++) {
					for (int v = 0; v < lengthy; v++) {
						divider += blurMatrix[u][v];
					}
				}
				lum = (int)(lum / divider + 0.5);
				destImage.setRGB(x, y, new Color(lum, lum, lum).getRGB());
			}
		}
		return destImage;
	}
	
	/**
	 * Compute the thresholds (and thus the segments)
	 * Use K-means clustering algorithm
	 */
	public int[] computeThresholds(int[] histogram) {
		int[] thresholds = new int[K - 1];
		int[] clusterCenters = new int[K];
		
		// step 1
		int step = 256 / K;
		for (int i=0; i < thresholds.length; i++) {
			thresholds[i] = step + i * step;
		}
		for (int i=0; i < K; i++) {
			clusterCenters[i] = step / 2 + i * step;
		}
		
		int[] oldClusterCenters = new int[K];
		int count = 0;
		boolean changed = true;
		while (changed) {
			count++;
			
			// copy old clusterCenters
			for (int i=0; i < K; i++) {
				oldClusterCenters[i] = clusterCenters[i];
			}
			
			// step 2 + 3
			for (int i=0; i < K; i++) {
				clusterCenters[i] = (int) (meanPixelValue(i, thresholds, histogram));
			}
			
			// step 4
			for (int i=0; i < thresholds.length; i++) {
				thresholds[i] = (int)((clusterCenters[i] + clusterCenters[i + 1]) / 2.0 + 0.5);
			}
			
			// compare old clusterCenters with new clusterCenters
			changed = false;
			for (int i=0; i < K; i++) {
				changed = changed || (oldClusterCenters[i] != clusterCenters[i]);
			}
		}
		System.out.println("computeThresholds iterations: " + count);
		
		return thresholds;
	}
	
	/**
	 * Determine the mean pixel value for a segment
	 * @param segment
	 * @return
	 */
	public double meanPixelValue(int segment, int[] thresholds, int[] histogram) {
		int start = segment == 0 ? 0 : thresholds[segment - 1];
		int end = segment == thresholds.length ? 256 : thresholds[segment];
		int total = 0;
		for (int i=start; i < end; i++) {
			total += histogram[i];
		}
		double m = 0;
		for (int i=start; i < end; i++) {
			m += i * (histogram[i] / (double)total);
		}
		return m;
	}
	
	/**
	 * Compute the histogram for the current image
	 */
	public int[] computeHistogram(BufferedImage image) {
		int[] histogram = new int[256];
		int width = image.getWidth();
		int height = image.getHeight();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int value = image.getRGB(x, y) & 0xff; // take red; all colours are the same
				histogram[value]++;
			}
		}
		return histogram;
	}
	
	/**
	 * Get the segment number based on the luminance
	 * @param lum The luminance (gray value)
	 * @param thresholds The thresholds to base the segment number on
	 * @return Segment number
	 */
	public int getSegmentNr(int lum, int[] thresholds) {
		for (int i=0; i < thresholds.length; i++) {
			if (lum < thresholds[i])
				return i;
		}
		return thresholds.length;
	}
	
	@Override
	/**
	 * Draw the canvas
	 */
	public void paint(Graphics g) {
		if (imageOriginal == null) 
			return;
		
		BufferedImage image;
		int[] histogram;
		int[] thresholds;
		int width = imageGrayscale.getWidth();
		int height = imageGrayscale.getHeight();
		
		if (main.getView() == Thresholding.View.GRAYSCALE) {
			image = imageGrayscale;
			histogram = histogramStandard;
			thresholds = thresholdsStandard;
		} else if (main.getView() == Thresholding.View.BLURRED) {
			image = imageBlurred;
			histogram = histogramImproved;
			thresholds = thresholdsImproved;
		} else if (main.getView() == Thresholding.View.STANDARD) {
			image = imageStandard;
			histogram = histogramStandard;
			thresholds = thresholdsStandard;
		} else if (main.getView() == Thresholding.View.IMPROVED) {
			image = imageImproved;
			histogram = histogramImproved;
			thresholds = thresholdsImproved;
		} else { // if (main.getView() == Thresholding.View.ORIGINAL) {
			image = imageOriginal;
			histogram = histogramStandard;
			thresholds = thresholdsStandard;
		}
		
		g.drawImage(image, 0, 0, this);
		
		if (main.isViewHistogram()) {
			drawHistogram(histogram, g, 0, 0, width, height);
			drawThresholds(thresholds, g, 0, 0, width, height);
		}
	}
	
	@Override
	/**
	 * Override update; don’t clear canvas before repainting.
	 */
	public void update(Graphics g) {
		this.paint(g);
	}

	/**
	 * Draw the histogram on a Graphics context (red)
	 * @param g Reference to Graphics object
	 * @param x Origin X-coordinate
	 * @param y Origin Y-coordinate
	 * @param w Width
	 * @param h Height
	 */
	public void drawHistogram(int[] histogram, Graphics g, int x, int y, int w, int h) {
		w -= 1;
		h -= 1;
		g.setColor(Color.RED);
		double max = 0;
		for (int i=0; i < histogram.length - 1; i++) {
			max = Math.max(max, histogram[i]);
		}
		for (int i=0; i < histogram.length - 1; i++) {
			g.drawLine(
					x + (int)(i / 256.0 * w + 0.5),
					(int)(y + h - (histogram[i]/max) * h + 0.5),
					x + (int)((i + 1) / 256.0 * w + 0.5),
					(int)(y + h - (histogram[i + 1]/max) * h + 0.5)
				);
		}
	}
	
	/**
	 * Draw thresholds (yellow) and cluster centres (orange)
	 * @param g Reference to Graphics object
	 * @param x Origin X-coordinate
	 * @param y Origin Y-coordinate
	 * @param w Width
	 * @param h Height
	 */
	public void drawThresholds(int[] thresholds, Graphics g, int x, int y, int w, int h) {
		w -= 1;
		h -= 1;
		g.setColor(Color.YELLOW);
		for (int i=0; i < thresholds.length; i++) {
			g.drawLine(
					x + (int)(thresholds[i] / 256.0 * w + 0.5),
					y,
					x + (int)(thresholds[i] / 256.0 * w + 0.5),
					y + h
				);
		}
	}
	
}
