package eu.europeana.inspire.common;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@europeana.eu)
 * @since 2016-06-21
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class MosaicGenerator {
    private static final Logger logger = LogManager.getLogger();
    private String tiles_dir = "/tmp/test/tiles-heroes-resize";
    private String input_img = "/tmp/test/europeana.png";
    private String output_img = "/tmp/test/output.png";
    private static int tileWidth = 100;
    private static int tileHeight = 100;
    private static int tileScale = 14;
    private static final boolean IS_BW = false;
    private static final int THREADS = 2;

    public MosaicGenerator() {
    }

    public MosaicGenerator(String tiles_dir, String input_img, String output_img, int tileWidth, int tileHeight, int tileScale) {
        this.tiles_dir = tiles_dir;
        this.input_img = input_img;
        this.output_img = output_img;
        MosaicGenerator.tileWidth = tileWidth;
        MosaicGenerator.tileHeight = tileHeight;
        MosaicGenerator.tileScale = tileScale;
    }

    public void generateMosaic() throws IOException, InterruptedException {
        logger.info("Reading tiles...");
        final Collection<Tile> tileImages = getImagesFromTiles(new File(tiles_dir));

        logger.info("Processing input image...");
        File inputImageFile = new File(input_img);
        Collection<BufferedImagePart> inputImageParts = getImagesFromInput(inputImageFile);
        final Collection<BufferedImagePart> outputImageParts = Collections.synchronizedSet(new HashSet<BufferedImagePart>());

        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(THREADS);

        final AtomicInteger i = new AtomicInteger();
        final int partCount = inputImageParts.size();
        for (final BufferedImagePart inputImagePart : inputImageParts) {
            newFixedThreadPool.execute(new Runnable(){
                public void run() {
                    Tile bestFitTile = getBestFitTile(inputImagePart.image, tileImages);
                    logger.info(String.format("Matching part %s of %s", i.incrementAndGet(), partCount));
                    outputImageParts.add(new BufferedImagePart(bestFitTile.image, inputImagePart.x, inputImagePart.y));
                }
            });
        }

        newFixedThreadPool.shutdown();
        newFixedThreadPool.awaitTermination(10000000, TimeUnit.SECONDS);

        logger.info("Writing output image...");
        BufferedImage inputImage = ImageIO.read(inputImageFile);
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        BufferedImage output = makeOutputImage(width, height, outputImageParts);
        ImageIO.write(output, "png", new File(output_img));
        logger.info("FINISHED");
    }

    private static BufferedImage makeOutputImage(int width, int height, Collection<BufferedImagePart> parts){
        BufferedImage image = new BufferedImage(width * tileScale, height * tileScale, BufferedImage.TYPE_3BYTE_BGR);

        for(BufferedImagePart part : parts){
            BufferedImage imagePart = image.getSubimage(part.x * tileScale, part.y * tileScale, tileWidth, tileHeight);
            imagePart.setData(part.image.getData());
        }

        return image;
    }

    private static Tile getBestFitTile(BufferedImage target, Collection<Tile> tiles) {
        Tile bestFit = null;
        int bestFitScore = -1;

        for(Tile tile : tiles){
            int score = getScore(target, tile);
            if (score > bestFitScore){
                bestFitScore = score;
                bestFit = tile;
            }
        }

        return bestFit;
    }

    private static int getScore(BufferedImage target, Tile tile){
        assert target.getHeight() == Tile.SCALED_HEIGHT;
        assert target.getWidth() == Tile.SCALED_WIDTH;

        int total = 0;
        for(int x = 0; x< Tile.SCALED_WIDTH; x++){
            for(int y = 0; y< Tile.SCALED_HEIGHT; y++){
                int targetPixel = target.getRGB(x, y);
                Pixel candidatePixel = tile.pixels[x][y];
                int diff = getDiff(targetPixel, candidatePixel);
                int score;
                if (IS_BW){
                    score = 255 - diff;
                } else {
                    score = 255 * 3 - diff;
                }

                total += score;
            }
        }

        return total;
    }

    private static int getDiff(int target, Pixel candidate){
        if (IS_BW){
            return Math.abs(getRed(target) - candidate.r);
        } else {
            return Math.abs(getRed(target) - candidate.r) +
                    Math.abs(getGreen(target) - candidate.g) +
                    Math.abs(getBlue(target) - candidate.b);
        }
    }

    private static int getRed(int pixel){
        return  (pixel >>> 16) & 0xff;
    }

    private static int getGreen(int pixel){
        return  (pixel >>> 8) & 0xff;
    }

    private static int getBlue(int pixel){
        return  pixel & 0xff;
    }

    private static Collection<Tile> getImagesFromTiles(File tilesDir) throws IOException {
        Collection<Tile> tileImages = Collections.synchronizedSet(new HashSet<Tile>());

        ArrayList<File> files = new ArrayList<File>();
        getAllFilesFromSubdirectories(tilesDir, files);
        for(File file : files){
            BufferedImage img = ImageIO.read(file);
            if (img != null){
                tileImages.add(new Tile(img));
            } else {
                System.err.println("null image for file " + file.getName());
            }
        }
        return tileImages;
    }

    private static void getAllFilesFromSubdirectories(File directory, ArrayList<File> files) {
        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList) {
            if (file.isFile()) {
                files.add(file);
            } else if (file.isDirectory()) {
                getAllFilesFromSubdirectories(file, files);
            }
        }
    }

    private static Collection<BufferedImagePart> getImagesFromInput(File inputImgFile) throws IOException {
        Collection<BufferedImagePart> parts = new HashSet<BufferedImagePart>();

        BufferedImage inputImage = ImageIO.read(inputImgFile);
        int totalHeight = inputImage.getHeight();
        int totalWidth = inputImage.getWidth();

        int x=0, y=0, w= Tile.SCALED_WIDTH, h= Tile.SCALED_HEIGHT;
        while(x+w <= totalWidth){
            while(y+h <= totalHeight){
                BufferedImage inputImagePart = inputImage.getSubimage(x, y, w, h);
                parts.add(new BufferedImagePart(inputImagePart, x, y));
                y+=h;
            }
            y=0;
            x+= w;
        }

        return parts;
    }

    public static class Tile {
        public static int SCALED_WIDTH = tileWidth / tileScale;
        public static int SCALED_HEIGHT = tileHeight / tileScale;
        public Pixel[][] pixels = new Pixel[SCALED_WIDTH][SCALED_HEIGHT];
        public BufferedImage image;

        public Tile(BufferedImage image) {
            this.image = image;
            calcPixels();
        }

        private void calcPixels(){
            for(int x=0; x<SCALED_WIDTH; x++){
                for(int y=0; y<SCALED_HEIGHT; y++){
                    pixels[x][y] = calcPixel(x * tileScale, y * tileScale, tileScale, tileScale);
                }
            }
        }

        private Pixel calcPixel(int x, int y, int w, int h){
            int redTotal = 0, greenTotal = 0, blueTotal = 0;

            for(int i=0; i<w; i++){
                for(int j=0; j<h; j++){
                    int rgb = image.getRGB(x+i, y+j);
                    redTotal   += getRed(rgb);
                    greenTotal += getGreen(rgb);
                    blueTotal  += getBlue(rgb);
                }
            }
            int count = w*h;
            return new Pixel(redTotal/count, greenTotal/count, blueTotal/count);
        }
    }

    public static class BufferedImagePart{
        public BufferedImagePart(BufferedImage image, int x, int y) {
            this.image = image;
            this.x = x;
            this.y = y;
        }

        public BufferedImage image;
        public int x;
        public int y;
    }

    public static class Pixel{
        public int r,g,b;

        public Pixel(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
        @Override
        public String toString() {
            return r + "." + g + "." + b;
        }
    }
}