package ketola.wunderpahkina.vol5;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class WunderPahkina {

	private static final String IMAGE_LOCATION = "src/main/resources/kuva.png";
	private static final String RESULT_LOCATION = "src/main/resources/%d.png";
	
	private static final Integer COLOR_RED = new Color(120, 0, 0).getRGB();
	private static final Integer COLOR_WHITE = new Color(255, 255, 255).getRGB();
	private static final Integer COLOR_START_UP = new Color(7, 84, 19).getRGB(); 
	private static final Integer COLOR_START_LEFT = new Color(139, 57, 137).getRGB(); 
	private static final Integer COLOR_STOP = new Color(51, 69, 169).getRGB(); 
	private static final Integer COLOR_TURN_RIGHT = new Color(182, 149, 72).getRGB(); 
	private static final Integer COLOR_TURN_LEFT = new Color(123, 131, 154).getRGB(); 

	public static void main(String[] args) throws Exception {
		long time = currentTimeMillis();
		String result = format(RESULT_LOCATION, time);
		
		ImageIO.write(
				processImage(
						ImageIO.read(new File(IMAGE_LOCATION))), 
						"png", 
						new File(result));
		
		System.out.println(format("The result is saved to %s. The operation took %d ms.", result, currentTimeMillis() - time));
	}
	
	public static BufferedImage processImage(BufferedImage image) {
		List<Pixel> allPixels = range(0, image.getHeight() * image.getWidth())
			.mapToObj(idx -> 
				{	int x = idx % image.getWidth();  
					int y = idx / image.getWidth();
					return new Pixel(x, y, image.getRGB(x, y));
				}).collect(toList());
		
		Map<Integer, List<Pixel>> pixelsByColor = allPixels.stream().collect(groupingBy(Pixel::color));
		pixelsByColor.get(COLOR_START_UP).forEach(pixel -> draw(pixel, allPixels, image, Direction.UP));
		pixelsByColor.get(COLOR_START_LEFT).forEach(pixel -> draw(pixel, allPixels, image, Direction.LEFT));
			
		allPixels.stream().filter(pixel -> pixel.color != COLOR_RED)
			.forEach(pixel -> image.setRGB(pixel.x, pixel.y, COLOR_WHITE));
		
		return image;
	}
	
	private static void draw(Pixel p, List<Pixel> allPixels, BufferedImage bufferedImage, Direction direction){
		bufferedImage.setRGB(p.x, p.y, COLOR_RED);
		p.color = COLOR_RED;
		
		if(direction == Direction.STOP)
			return;
		
		final int x = direction.newX(p.x);
		final int y = direction.newY(p.y);
	
		allPixels.stream().filter(pixel -> (pixel.x == x && pixel.y == y))
			.limit(1)
			.forEach(pixel -> {
				draw(pixel, allPixels, bufferedImage, direction(pixel, direction));
			});
	}
	
	private static Direction direction(Pixel pixel, Direction currentDirection){
		if(pixel.color == COLOR_STOP || pixel.color == COLOR_START_LEFT || pixel.color == COLOR_START_UP){
			return Direction.STOP;
		} else if(pixel.color == COLOR_TURN_RIGHT){
			return Direction.turnRightFrom(currentDirection);
		} else if(pixel.color == COLOR_TURN_LEFT){
			return Direction.turnLeftFrom(currentDirection);
		}
		return currentDirection;
	}
	
	public enum Direction {
		UP(0, -1),RIGHT(1, 0),DOWN(0,1),LEFT(-1,0),STOP(0,0);
		
		private final int changeX, changeY;
		
		private Direction(int changeX, int changeY) {
			this.changeX = changeX;
			this.changeY = changeY;
		}
		
		private static List<Direction> directions = asList(UP,RIGHT,DOWN,LEFT);
		
		private static Direction turnRightFrom(Direction direction){
			int index = directions.indexOf(direction);
			return (index  < 3 ? directions.get(index + 1) : directions.get(0));
		}
		
		private static Direction turnLeftFrom(Direction direction){
			int index = directions.indexOf(direction);
			return (index == 0 ? directions.get(3) : directions.get(index - 1));
		}
		
		private int newX(int currentX){
			return currentX + changeX;
		}
		
		private int newY(int currentY){
			return currentY + changeY;
		}
	};
	
	public static class Pixel {
		public int x, y, color;

		public Pixel(int x, int y, int color) {
			super();
			this.x = x;
			this.y = y;
			this.color = color;
		}
		
		public Integer color() {
			return color;
		}
	}
}
