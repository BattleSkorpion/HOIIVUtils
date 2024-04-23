package com.HOIIVUtils.ui.map;

import com.HOIIVUtils.hoi4utils.clausewitz_map.gen.Heightmap;
import com.HOIIVUtils.hoi4utils.clausewitz_map.province.ProvinceGeneration;
import com.HOIIVUtils.hoi4utils.clausewitz_map.province.ProvinceMap;
import com.HOIIVUtils.hoi4utils.clausewitz_map.values;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import com.HOIIVUtils.ui.HOIUtilsWindow;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MapGenerationWindow extends HOIUtilsWindow {

	//Heightmap heightmap = null;
	@FXML Canvas heightmapCanvas;
	@FXML Canvas provinceCanvas;
	@FXML TextField heightmapTextField;
	@FXML Button browseHeightmapButton;
	@FXML Button provinceGenerationButton;
	Heightmap heightmap;
	ProvinceGeneration provinceGeneration;

		/* static initialization */
	{   // todo temp
		try {
			heightmap = new Heightmap(new File("src\\main\\resources\\map\\heightmap.bmp"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public MapGenerationWindow() {
		/* window */
		setFxmlResource("MapGenerationWindow.fxml");
		setTitle("Map Generation");
	}

	/**
	 * {@inheritDoc}
	 */
	@FXML void initialize() {
		heightmapCanvas.setWidth(heightmap.width() / 4.0);
		heightmapCanvas.setHeight(heightmap.height() / 4.0);
		provinceCanvas.setWidth(heightmap.width() / 4.0);
		provinceCanvas.setHeight(heightmap.height() / 4.0);

		drawHeightmap();
	}

	private void drawHeightmap() {
		GraphicsContext gc = heightmapCanvas.getGraphicsContext2D();

		WritableImage wImage = new WritableImage(heightmap.width() / 4, heightmap.height() / 4);
		PixelWriter pixelWriter = wImage.getPixelWriter();
		for (int y = 0; y < wImage.getHeight(); y++) {
			for (int x = 0; x < wImage.getWidth(); x++) {
				int px = heightmap.height_xy(x * 4, y * 4);
				Color c = new Color(px, px, px);
				pixelWriter.setArgb(x, y, c.getRGB());
			}
		}
		gc.drawImage(wImage, 0, 0);
	}

	private void drawProvinceMap() {
		GraphicsContext gc = provinceCanvas.getGraphicsContext2D();
		ProvinceMap map = provinceGeneration.getProvinceMap();

		WritableImage wImage = new WritableImage(map.width() / 4, map.height() / 4);
		PixelWriter pixelWriter = wImage.getPixelWriter();
		for (int y = 0; y < wImage.getHeight(); y++) {
			for (int x = 0; x < wImage.getWidth(); x++) {
				int px = map.getRGB(x * 4, y * 4);
				Color c = new Color(px);
				pixelWriter.setArgb(x, y, c.getRGB());
			}
		}
		gc.drawImage(wImage, 0, 0);
	}

	@FXML void OnBrowseHeightmap() {
		File f;
		try {
			f = openChooser(browseHeightmapButton, false);
			// f = openChooser(browseHeightmapButton, false, _____);    // todo init directory
			heightmap = new Heightmap(f);
		} catch (IOException exc) {
			heightmap = null;       // deselect any heightmap
			return;
		} catch (IllegalArgumentException exc) {
			// thrown when file selected is null
			return;
		}
		// draw heightmap etc.
		heightmapTextField.setText(f.getPath());    // todo- relative path maybe?
		drawHeightmap();
	}

	@FXML void onEnterHeightmap() {
		var p = Path.of(heightmapTextField.getText());
		if (!Files.exists(p)) {
			return;
		}

		try {
			heightmap = new Heightmap(p);
		} catch (IOException e) {
			System.out.println("Error: heightmap could not be loaded. Filepath selected: " + p);
			return;
		} catch (IllegalArgumentException exc) {
			// thrown when file selected is null
			return;
		}
		drawHeightmap();
	}

	@FXML void onGenerateProvinces() {
		provinceGeneration = new ProvinceGeneration();
		provinceGeneration.generate(heightmap);

		provinceGeneration.writeProvinceMap();
		drawProvinceMap();
	}
}
