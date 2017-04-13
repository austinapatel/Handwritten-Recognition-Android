
/**
 * Author: Austin Patel
 * Project: Handwritten Recognition
 * File Name: LetterData.java
 * Created: 01/01/17
 */

package austinpatel.handwrittenletterrecognition.data;

import austinpatel.handwrittenletterrecognition.ui.MyColor;

/** Contains data for a specific letter data. */
public class LetterData {

	private int[][] data;
	private char character;

	public LetterData(int[][] data) {
		this.data = data;
	}

	public LetterData(int[][] data, char character) {
		this(data);

		this.character = character;
	}

	public LetterData(String rawData) {
		this.data = rawDataToData(rawData);
	}
	
	public LetterData(int[] data) {
		String rawData = "";
		
		for (int i = 0; i < data.length; i++)
			rawData += data[i];
		
		this.data = rawDataToData(rawData);
	}
	
	private int[][] rawDataToData(String rawData) {
		int[][] data = new int[Constants.GRID_WIDTH][Constants.GRID_HEIGHT];

		for (int i = 0; i < rawData.length(); i++) {
			int posY = i / Constants.GRID_HEIGHT;
			int posX = i % Constants.GRID_WIDTH;

			data[posX][posY] = Character.getNumericValue(rawData.charAt(i));
		}
		
		return data;
	}

	public LetterData(String rawData, char character) {
		this(rawData);

		this.character = character;
	}

	public int[][] getData2D() {
		return data;
	}

	public int[] getData1D() {
		int[] data1D = new int[Constants.GRID_WIDTH * Constants.GRID_HEIGHT];

		int index = 0;
		for (int j = 0; j < Constants.GRID_HEIGHT; j++) 
			for (int i = 0; i < Constants.GRID_WIDTH; i++) {
				data1D[index] = data[i][j];
				index++;
			}

		return data1D;
	}
	
	public static int[][] colorDataToRaw(MyColor[][] colors, MyColor backgroundColor) {
		int[][] rawData = new int[Constants.GRID_WIDTH][Constants.GRID_HEIGHT];
		for (int x = 0; x < Constants.GRID_WIDTH; x++)
			for (int y = 0; y < Constants.GRID_HEIGHT; y++)
				if (colors[x][y] == null)
					rawData[x][y] = 0;
				else
					rawData[x][y] = (colors[x][y].getColor() == backgroundColor.getColor()) ? 0 : 1;
		
		return rawData;				
	}
	
	public static int[] data2DTo1D(int[][] data2D) {
		int[] data1D = new int[Constants.GRID_WIDTH * Constants.GRID_HEIGHT];
		
		int index = 0;
		for (int y = 0; y < Constants.GRID_HEIGHT; y++)
			for (int x = 0; x < Constants.GRID_WIDTH; x++) {
				data1D[index] = data2D[x][y];
				index++;
			}
		
		return data1D;
	}

	public char getCharacter() {
		return character;
	}
	
	public void setCharacter(char character) {
		this.character = character;
	}

	public static LetterData[] getLetterData(char c) {
		String[] fileContent = FileManager.readFileContent(c);
		LetterData[] letterData = new LetterData[fileContent.length];

		for (int i = 0; i < letterData.length; i++)
			if (c == ' ')
				letterData[i] = new LetterData(fileContent[i]);
			else
				letterData[i] = new LetterData(fileContent[i], c);

		return letterData;
	}
	
//	public static LetterData[] getStockData() {
//		LetterData[] stockData = new LetterData[Alphabet.getLength()];
//		for (char c : Alphabet.getCharacterArray()) {
//			String path = LetterData.getDestinationPath(Constants.STOCK_LETTERS_FOLDER, c);
//			
//			LetterData curLetterData = LetterData.getLetterData(path, c)[0];
//			
//			stockData[Alphabet.indexOf(c)] = curLetterData;
//		}
//		
//		return stockData;
//	}

//	public static LetterData[] getLetterData(String filePath) {
//		return getLetterData(' ');
//	}

	/** Converts color data from a "ColorGrid" into a text format. */
	public static String colorsToText(MyColor[][] colors, MyColor drawingColor) {
		String text = "";

		for (int y = 0; y < Constants.GRID_HEIGHT; y++)
			for (int x = 0; x < Constants.GRID_WIDTH; x++)
				text += (colors[x][y].getColor() == drawingColor.getColor()) ? '1' : '0';

		return text;
	}
	
	/**Converts an int[][] pixel data to a text format.*/
	public static String dataToText(int[][] data) {
		String text = "";
		
		for (int y = 0; y < Constants.GRID_HEIGHT; y++)
			for (int x = 0; x < Constants.GRID_WIDTH; x++)
				text += data[x][y];
		
		return text;
	}

	/** Converts text data of a letter to a color array. */
	public static MyColor[][] textToColorArray(String text, int gridWidth,
											   MyColor drawingColor) {
		MyColor[][] colorData = new MyColor[gridWidth][text.length() / gridWidth];
		int x = 0, y = 0;

		for (char c : text.toCharArray()) {
			colorData[x][y] = (c == '1') ? drawingColor : null;
			x++;
			if (x % gridWidth == 0) {
				y++;
				x = 0;
			}
		}

		return colorData;
	}
	
	private String getDestinationPath(String dataset) {
		return LetterData.getDestinationPath(dataset, character);
	}
	
	public static String getDestinationPath(String dataset, char character) {
		return Constants.RESOURCES_PATH + dataset + "\\" + character + ".txt";
	}
	
	/**Save the LetterData to its correct file*/
//	public void save(String dataset) {
//		String filePath = getDestinationPath(dataset);
//		String content = LetterData.dataToText(data);
//
//		FileManager.writeFileContent(filePath, content, true);
//	}

}
