package com.HOIIVUtils.ui;

import com.HOIIVUtils.clauzewitz.HOIIVUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class HOIIVUtilsWindow implements FXWindow {
	private String fxmlResource;
	private String title;
	protected Stage stage;
	protected FXMLLoader loader;

	/**
	 * Opens the stage. If the stage is not null, it shows the stage. If the fxmlResource is null, it
	 * prints an error message and calls the openError method. Otherwise, it creates a new FXMLLoader,
	 * loads the fxml file, creates a new Scene, sets the stylesheets, creates a new Stage, sets the
	 * scene and title, calls the decideScreen method, shows the stage, sets the loader and stage
	 * variables, and prints a success message.
	 */
	@Override
	public void open() {
		if (stage != null) {
			stage.show();
			System.out.println("HOIIVUtilsStageLoader showed stage with open cuz stage was NOT null. fxml: " + fxmlResource + " title: " + title);
		} else if (fxmlResource == null) {
			System.out.println(
					"HOIIVUtilsStageLoader couldn't create a new scene cause the fxml was null. fxmlResource: " + fxmlResource + " title: " + title);
			openError("FXML Resource does not exist, Window Title: " + title);
		} else {
			try {
				FXMLLoader launchLoader = new FXMLLoader(getClass().getResource(fxmlResource));
				System.out.println("HOIIVUtilsStageLoader creating stage with fxml" + fxmlResource);
				Parent root = launchLoader.load();
				Scene scene = new Scene(root);

				//addSceneStylesheets(scene);

				this.loader = launchLoader;
				this.stage = createLaunchStage(scene);
				System.out.println("HOIIVUtilsStageLoader created and showed stage with open cuz stage was null and fxml resource is: " + fxmlResource
						+ " title: " + title);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens the stage with optional initialization arguments.
	 *
	 * @param initargs the initialization arguments for the controller
	 */
	public void open(Object... initargs) {
		Class<?>[] initargs_classes = new Class[initargs.length];
		for (int i = 0; i < initargs.length; i++) {
			initargs_classes[i] = initargs[i].getClass();
		}

		if (stage != null) {
			stage.show();
			System.out.println("HOIIVUtilsStageLoader showed stage with open cuz stage was NOT null. fxml: " + fxmlResource + " title: " + title);
		} else if (fxmlResource == null) {
			System.out.println(
					"HOIIVUtilsStageLoader couldn't create a new scene cause the fxml was null. fxmlResource: " + fxmlResource + " title: " + title);
			openError("FXML Resource does not exist, Window Title: " + title);
		} else {
			try {
//				FXMLLoader launchLoader = new FXMLLoader(getClass().getResource(fxmlResource));
//				launchLoader.setControllerFactory(c -> {
//					try {
//						return getClass().getConstructor(initargs_classes).newInstance(initargs);
//					} catch (InstantiationException | IllegalAccessException | InvocationTargetException
//					         | NoSuchMethodException e) {
//						throw new RuntimeException(e);
//					}
//				});

//				FXMLLoader launchLoader = new FXMLLoader(getClass().getResource(fxmlResource));
//				launchLoader.setControllerFactory(c -> {
//					for (Class<?> argClass : initargs_classes) {
//						Class<?> currentClass = argClass;
//						while (currentClass != Object.class) {
//							try {
//								return getClass().getConstructor(currentClass).newInstance(initargs);
//							} catch (InstantiationException | IllegalAccessException
//							         | InvocationTargetException | NoSuchMethodException e) {
//								currentClass = currentClass.getSuperclass();
//							}
//						}
//					}
//					throw new RuntimeException("No suitable constructor found");
//				});
				FXMLLoader launchLoader = new FXMLLoader(getClass().getResource(fxmlResource));
				launchLoader.setControllerFactory(c -> {
					List<List<Class<?>>> classHierarchies = new ArrayList<>(initargs.length);
					for (int i = 0; i < initargs.length; i++) {
						classHierarchies.add(getClassHierarchy(initargs[i].getClass()));
					}
					for (List<Class<?>> combination : generateCombinations(classHierarchies, 0)) {
						try {
							return getClass().getConstructor(combination.toArray(new Class[0])).newInstance(initargs);
						} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
						}
					}
					throw new RuntimeException("No suitable constructor found");
				});
				System.out.println("HOIIVUtilsStageLoader creating stage with fxml" + fxmlResource);
				Parent root = launchLoader.load();
				Scene scene = new Scene(root);

				//addSceneStylesheets(scene);


				Stage launchStage = createLaunchStage(scene);
				this.loader = launchLoader;
				this.stage = launchStage;
				System.out.println("HOIIVUtilsStageLoader created and showed stage with open cuz stage was null and fxml resource is: " + fxmlResource
						+ " title: " + title);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens stage and updates fxmlResource and title
	 * 
	 * @param fxmlResource stage .fxml resource
	 * @param title stage title
	 */
	@Override
	public void open(String fxmlResource, String title) {
		this.fxmlResource = fxmlResource;
		this.title = title;
		System.out.println("open(String fxmlResource, String title)" + "fxmlResource: " + fxmlResource + " title: " + title);
		open();
	}

	@NotNull
	private Stage createLaunchStage(Scene scene) {
		Stage launchStage = new Stage();
		launchStage.setScene(scene);

		launchStage.setTitle(title);
		decideScreen(launchStage);
		launchStage.show();
		return launchStage;
	}

	private void addSceneStylesheets(Scene scene) {
		scene.getStylesheets().add(HOIIVUtils.DARK_MODE_STYLESHEETURL);
		scene.getStylesheets().add(getClass().getResource("../highlight-background.css").toExternalForm());
	}

	@Override
	public String getFxmlResource() {
		return fxmlResource;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setFxmlResource(String fxmlResource) {
		this.fxmlResource = fxmlResource;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	private List<Class<?>> getClassHierarchy(Class<?> clazz) {
		List<Class<?>> hierarchy = new ArrayList<>();
		while (clazz != null) {
			hierarchy.add(clazz);
			clazz = clazz.getSuperclass();
		}
		return hierarchy;
	}

	private List<List<Class<?>>> generateCombinations(List<List<Class<?>>> classHierarchies, int index) {
		List<List<Class<?>>> combinations = new ArrayList<>();
		if (index == classHierarchies.size()) {
			combinations.add(new ArrayList<>());
		} else {
			for (Class<?> clazz : classHierarchies.get(index)) {
				for (List<Class<?>> combination : generateCombinations(classHierarchies, index + 1)) {
					combination.add(0, clazz);
					combinations.add(new ArrayList<>(combination));
				}
			}
		}
		return combinations;
	}
}
