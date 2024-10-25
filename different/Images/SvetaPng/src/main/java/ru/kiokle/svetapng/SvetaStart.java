/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.kiokle.svetapng;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Me
 */
public class SvetaStart {

    private static String TEMPLATE_IMAGE;
    private static String OUTPUT_IMAGE;
    private static String CONFIGS;
    private static int PAGE_COLUMNS = 8;
    private static int PAGE_ROWS = 15;
//    private static final String FONT_NAME = "Times New Roman";
    private static final String FONT_NAME = "Liberation Serif";

    // Usage example: java -Xmx8G -jar D:\GIT\UsefulUtils\different\Images\SvetaPng\target\SvetaPng.jar -t "C:/Users/Me/Downloads/Sveta Images 2/Sveta - template.png" -o D:/tmpZZZ/image.png -conf D:/tmpZZZ/config.txt -columns 8 -rows 15
    public static void main(String[] args) throws Exception {
        Date dateStart = new Date();
        System.out.println("Hello World!");
        List<String> argsList = Arrays.asList(args);
        TEMPLATE_IMAGE = argsList.get(argsList.indexOf("-t") + 1);
        OUTPUT_IMAGE = argsList.get(argsList.indexOf("-o") + 1);
        PAGE_COLUMNS = Integer.valueOf(argsList.get(argsList.indexOf("-columns") + 1));
        PAGE_ROWS = Integer.valueOf(argsList.get(argsList.indexOf("-rows") + 1));
        if (argsList.contains("-confLine")) {
            CONFIGS = argsList.get(argsList.indexOf("-confLine") + 1);
            List<ImageLabel> imageLabelCollection = parseConfigs();
            for (int i = 0; i < imageLabelCollection.size(); i++) {
                Collection<BufferedImage> images = generateImages(Arrays.asList(imageLabelCollection.get(i)), TEMPLATE_IMAGE, FONT_NAME);
                createBigImage(images, process2("", 250, 0, TEMPLATE_IMAGE, FONT_NAME), i, new File(TEMPLATE_IMAGE), OUTPUT_IMAGE, PAGE_COLUMNS, PAGE_ROWS);
            }
        } else {
            CONFIGS = argsList.get(argsList.indexOf("-conf") + 1);
            Collection<BufferedImage> images = generateImages(parseConfigs(), TEMPLATE_IMAGE, FONT_NAME);
            createBigImage(images, process2("", 250, 0, TEMPLATE_IMAGE, FONT_NAME), null, new File(TEMPLATE_IMAGE), OUTPUT_IMAGE, PAGE_COLUMNS, PAGE_ROWS);
        }
        Date dateEnd = new Date();
        Long seconds = (dateEnd.getTime() - dateStart.getTime()) / 1000L;
        System.out.println("Memory Usage: " + maxMemory + " program was processing for " + seconds + "!");
    }

    public static void createImage(File imageTemplate, boolean confLine, List<ImageLabel> imageLabelCollection, String fontName, File outputImage, int pageColumns, int pageRows) throws Exception {
        Date dateStart = new Date();
        System.out.println("Hello World!");
        if (confLine) {
            for (int i = 0; i < imageLabelCollection.size(); i++) {
                Collection<BufferedImage> images = generateImages(Arrays.asList(imageLabelCollection.get(i)), imageTemplate.getAbsolutePath(), fontName);
                createBigImage(images, process2("", 250, 0, imageTemplate.getAbsolutePath(), fontName), i, imageTemplate, outputImage.getAbsolutePath(), pageColumns, pageRows);
            }
        } else {
            Collection<BufferedImage> images = generateImages(imageLabelCollection, imageTemplate.getAbsolutePath(), fontName);
            createBigImage(images, process2("", 250, 0, imageTemplate.getAbsolutePath(), fontName), null, imageTemplate, outputImage.getAbsolutePath(), pageColumns, pageRows);
        }
        Date dateEnd = new Date();
        Long seconds = (dateEnd.getTime() - dateStart.getTime()) / 1000L;
        System.out.println("Memory Usage: " + maxMemory + " program was processing for " + seconds + "!");
    }

    private static final String ss = "\\s\\s\\s\\s";
    private static final String p = "(.+?)";
    private static final Pattern pattern = Pattern.compile("^" + p + ss + p + ss + p + ss + p + "$", Pattern.DOTALL);

    private static long maxMemory = 0L;
    private static final long MEGABYTES_DELIMETER = 1024L * 1024L;

    private static void getMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long memoryOfCurrentProcess = memoryBean.getHeapMemoryUsage().getUsed() / MEGABYTES_DELIMETER;
        System.out.println("Memory Usage: " + memoryOfCurrentProcess + "!");
        maxMemory = maxMemory > memoryOfCurrentProcess ? maxMemory : memoryOfCurrentProcess;
    }

    private static List<ImageLabel> parseConfigs() throws IOException {
        byte[] readAllBytes = Files.readAllBytes(new File(CONFIGS).toPath());
        String string = new String(readAllBytes);
        return Arrays.asList(string.split("\n")).stream().map(line -> {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.matches()) {
                String text = matcher.group(1).replace("\\n", "\n");
                Integer textSize = Integer.valueOf(matcher.group(2));
                Integer valign = Integer.valueOf(matcher.group(3));
                Integer count = Integer.valueOf(matcher.group(4).trim());
                return new ImageLabel(text, textSize, valign, count);
            } else {
                return null;
            }
        }).filter(obj -> obj != null).collect(Collectors.toList());
    }

    private static Collection<BufferedImage> generateImages(Collection<ImageLabel> imageLabels, String imageTemplate, String fontName) {
        return imageLabels.stream().flatMap(imageLabel -> IntStream.range(0, imageLabel.getCount()).boxed().map(i -> process2(imageLabel.getText(), imageLabel.getTextSize(), imageLabel.getValign(), imageTemplate, fontName))).collect(Collectors.toList());
    }

    private static void createBigImage(Collection<BufferedImage> images, BufferedImage emptyImage, Integer fileCounter, File imageTemplate, String outputImageStringPath, int pageColumns, int pageRows) throws IOException {
        BufferedImage templateImage = ImageIO.read(imageTemplate);
        int templateWidth = templateImage.getWidth();
        int templateHeight = templateImage.getHeight();
        File outputImageFile;
        if (fileCounter != null) {
            outputImageFile = new File(outputImageStringPath.substring(0, outputImageStringPath.lastIndexOf(".")) + fileCounter.toString() + outputImageStringPath.substring(outputImageStringPath.lastIndexOf(".")));
        } else {
            outputImageFile = new File(outputImageStringPath);
        }
        if (outputImageFile.exists()) {
            outputImageFile.delete();
        }
        BufferedImage result = new BufferedImage(templateImage.getWidth() * pageColumns, templateImage.getHeight() * pageRows, BufferedImage.TYPE_INT_RGB);
        Graphics resultGraphics = result.getGraphics();
        Iterator<BufferedImage> imagesIterator = images.iterator();
        int counter = 0;
        int width = 0;
        int height = 0;
        for (int i = 0; i < pageRows; i++) {
            for (int j = 0; j < pageColumns; j++) {
                BufferedImage image = imagesIterator.hasNext() ? imagesIterator.next() : emptyImage;
                resultGraphics.drawImage(image, width, height, null);
                width += templateWidth;
                counter++;
                System.out.println(counter + " image was saved!");
                getMemoryUsage();
            }
            width = 0;
            height += templateHeight;
        }
        getMemoryUsage();
        ImageIO.write(result, "png", outputImageFile);
        System.out.println("outputImageFile" + outputImageFile + " created!");
        getMemoryUsage();
    }

    private static BufferedImage process2(String text, int textSize, int valign, String imageTemplate, String fontName) {
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(imageTemplate));
            Graphics graphics = bufferedImage.getGraphics();
            graphics.setColor(Color.BLACK);
            graphics.setFont(new Font(fontName, Font.BOLD, textSize));
            if (text != null && text.length() > 0) {
                int height = getStringHeight(graphics, text);
                drawString(graphics, text, (bufferedImage.getWidth() / 2), (bufferedImage.getHeight() / 2) - (height / 2) + valign);
            }
            System.out.println((text != null && text.length() > 0 ? text : "Empty") + " image created!");
            getMemoryUsage();
            return bufferedImage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int getStringWidth(Graphics graphics, String text) {
        return Arrays.stream(text.split("\n")).map(te -> graphics.getFontMetrics().stringWidth(te)).max(Integer::compare).get();
    }

    private static int getStringHeight(Graphics graphics, String text) {
        return Arrays.stream(text.split("\n")).map(te -> graphics.getFontMetrics().getHeight()).reduce(0, (val1, val2) -> val1 + val2);
    }

    private static void drawString(Graphics graphics, String text, int x, int y) {
        for (String line : text.replace("\\n", "\n").split("\n")) {
            int width = getStringWidth(graphics, line);
            graphics.drawString(line, x - (width / 2), y += graphics.getFontMetrics().getHeight());
        }
    }

    static class ImageLabel {

        private final String text;
        private final int textSize;
        private final int valign;
        private final int count;

        public ImageLabel(String text, int textSize, int count) {
            this.text = text;
            this.textSize = textSize;
            this.valign = 0;
            this.count = count;
        }

        public ImageLabel(String text, int textSize, int valign, int count) {
            this.text = text;
            this.textSize = textSize;
            this.valign = valign;
            this.count = count;
        }

        public String getText() {
            return text;
        }

        public int getTextSize() {
            return textSize;
        }

        public int getValign() {
            return valign;
        }

        public int getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "ImageLabel{" + "text=" + text.replace("\n", " ") + ", textSize=" + textSize + ", valign=" + valign + ", count=" + count + '}';
        }
    }
}
