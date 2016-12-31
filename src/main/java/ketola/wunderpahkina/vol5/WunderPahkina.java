package ketola.wunderpahkina.vol5;

import static java.lang.Math.random;
import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

public class WunderPahkina {
	
	private static final Integer COLOR_SNOW = new Color(255, 255, 255).getRGB();
	private static final Integer COLOR_SKY = new Color(204, 229, 255).getRGB();
	private static final Integer COLOR_RED = new Color(120, 0, 0).getRGB();
	private static final Integer COLOR_START_UP = new Color(7, 84, 19).getRGB(); 
	private static final Integer COLOR_START_LEFT = new Color(139, 57, 137).getRGB(); 
	private static final Integer COLOR_STOP = new Color(51, 69, 169).getRGB(); 
	private static final Integer COLOR_TURN_RIGHT = new Color(182, 149, 72).getRGB(); 
	private static final Integer COLOR_TURN_LEFT = new Color(123, 131, 154).getRGB(); 
	private static final Integer SCALE = 3;

	public static void main(String[] args) throws Exception {
		long time = currentTimeMillis();
		
		BufferedImage solved = processImage(ImageIO.read(new File("src/main/resources/kuva.png")));
		BufferedImage scaled = scaleImage(solved);
		
		ImageIO.write(scaled, "png", new File("src/main/resources/solution.png"));
		
		System.out.println(format("The result is saved to src/main/resources/solution.png. The operation took %d ms.", currentTimeMillis() - time));
	}

	public static BufferedImage processImage(BufferedImage image) {
		int w = image.getWidth();
		
		range(0, image.getHeight() * w)
			.filter(idx -> image.getRGB(idx % w, idx / w) == COLOR_START_UP ? draw(idx % w, idx / w, image, Direction.UP) : true)
			.filter(idx -> image.getRGB(idx % w, idx / w) == COLOR_START_LEFT ? draw(idx % w, idx / w, image, Direction.LEFT) : true)
			.boxed()
			.collect(toList())
			.stream()
			.filter(idx -> image.getRGB(idx % w, idx / w) != COLOR_RED )
			.forEach(idx -> image.setRGB(idx % w, idx / w, (random() > 0.02 ? COLOR_SKY : COLOR_SNOW)));
			
		return image;
	}

	private static boolean draw(int x, int y, BufferedImage bufferedImage, Direction direction){
		bufferedImage.setRGB(x, y, COLOR_RED);
		
		if(direction == Direction.STOP)
			return false;
		
		int newX = direction.newX(x);
		int newY = direction.newY(y);
		
		return draw(newX, newY, bufferedImage, direction(bufferedImage.getRGB(newX, newY), direction));
	}
	
	private static Direction direction(int color, Direction currentDirection){
		if(color == COLOR_STOP || color == COLOR_START_LEFT || color == COLOR_START_UP){
			return Direction.STOP;
		} else if(color == COLOR_TURN_RIGHT){
			return Direction.turnRightFrom(currentDirection);
		} else if(color == COLOR_TURN_LEFT){
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
	
	private static BufferedImage scaleImage(BufferedImage solved) {
		BufferedImage scaled = new BufferedImage(SCALE * solved.getWidth(null),
				SCALE * solved.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
		Graphics2D grph = (Graphics2D) scaled.getGraphics();
        grph.scale(SCALE, SCALE);
        grph.drawImage(solved, 0, 0, null);
        grph.dispose();
		return scaled;
	}
}
