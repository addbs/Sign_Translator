import ai.djl.translate.TranslateException;
import ai.djl.ModelException;

import java.io.IOException;
import java.io.File;

import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.event.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;




public class Main extends Application {
	// Moteur principal
	public static void main(String[] args) {
		launch(args);
	}
	// Méthode de lancement, qui sera exécuté périodiquement
	public void start(Stage stage) {
		
		double WIDTH = 1200;
	    double HEIGHT = 700;
	    double height_button = 75;
	    double width_button = 300;
	    // Création d'un group pour l'affichage des graphiques
	    Group root = new Group();
	    
	    // Mise en place de la fenêtre
	    Home accueil = new Home("Traducteur de Langage des Signes");
	    
	    // Paramètres fenêtre
	    stage.setTitle(accueil.name);
    	stage.setResizable(false);
    	
    	// Création de la scène pour les graphismes en se basant sur le group
    	Scene scene = new Scene(root);
    	Canvas canvas = new Canvas(WIDTH, HEIGHT);
    	
    	root.getChildren().add(canvas);
	    
    	// Application du canvas 
    	GraphicsContext gc = canvas.getGraphicsContext2D();
    	
    	// Création du texte 
    	Text title = accueil.Title();
    	Text subtitle = accueil.subTitle();
    	
    	//Création des boutons
	    Button end = accueil.showButton("Quitter", WIDTH - 125, 25, 100, 50);
	    Button webcam = accueil.showButton("Webcam", (WIDTH/2 + (width_button/3)) , (5 * HEIGHT)/7, width_button, height_button);
	    Button upload = accueil.showButton("Upload", (WIDTH/2 - ((4 * width_button)/3)), (5 * HEIGHT)/7 , width_button, height_button);

	    // Set up du fond d'écran
	    gc.setFill(Color.rgb(129,131,193));
		gc.fillRect(0, 0, canvas.getWidth() , canvas.getHeight());
		
		// Ajout du Logo TSP
		ImageView iv = accueil.Logo();
		
	    // Ajout des éléments au canvas
	    root.getChildren().add(end);
	    root.getChildren().add(webcam);
	    root.getChildren().add(upload);
	    root.getChildren().add(title);
	    root.getChildren().add(subtitle);
	    root.getChildren().add(iv);

	    // Affichage
	    stage.setScene(scene);
		stage.show();
	    
		// Gestion des événements pour le bouton upload
	    upload.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent e) {
		    	// Mise en place de la fenêtre de selection de fichier
		    	double NEW_WIDTH = 600;
		    	double NEW_HEIGHT = 600;
		    	// Tableau pour contrer l'enclosing type dans FileSelect
		    	final String[] path = {""};
		    	
		    	final FileChooser fileChooser = new FileChooser();
		    	FileSelect fileselect = new FileSelect();
		    	
		    	// Appel au gestionnaire de fichier
		    	fileselect.configuringFileChooser(fileChooser);
		    	
		    	// Gestion des graphismes fenêtre de sélection de fichiers
			    Group file_group = fileselect.getGroup();
			   	
			   	Scene file_scene = new Scene(file_group, NEW_WIDTH, NEW_HEIGHT);
			   	Stage newWindow = new Stage();
			   	
			   	// Création des boutons
			   	Button choix = fileselect.showButton("Choisir Image", 50, 450, 200, 50);
		    	Button retour = fileselect.showButton("Retour", 350, 450, 200, 50);
		    	
		    	// Génération du nouveau Canva 
		    	Canvas cv = new Canvas(WIDTH, HEIGHT);
		    	GraphicsContext gc_fs = cv.getGraphicsContext2D();
		    	file_group.getChildren().add(cv);
		    	
		    	// Paramètres de la fenêtre 
		    	newWindow.setTitle("Sélection de fichier");
			   	newWindow.setResizable(false);
			   	
			   	// Background
			   	gc_fs.setFill(Color.rgb(129,131,193));
				gc_fs.fillRect(0, 0, cv.getWidth() , cv.getHeight());
			   	
			   	// Ajout des boutons 
			   	file_group.getChildren().add(retour);
			   	file_group.getChildren().add(choix);
			     
			   	// Affichage
			   	newWindow.setScene(file_scene);
			   	newWindow.show();;
			   	
			   	// Gestion des événements pour le bouton de choix d'image
			   	choix.setOnAction(new EventHandler<ActionEvent>() {	
		            public void handle(ActionEvent event) {
		                File SelectedFile = fileChooser.showOpenDialog(newWindow);
		                path[0] = SelectedFile.getPath();
		                System.out.println(path[0]);
		                newWindow.close();
		             // lancement d'une nouvelle fenêtre pour la traduction  
				    	Traductor hd;
						try {
							hd = new Traductor(path[0]);
							Group g = hd.getGroup();
							Scene hand_scene = new Scene(g);
							
							// Création des boutons
							
							Button end = hd.showButton("Quitter", 650 , 625, 100, 50);
							Button retry = hd.showButton("Traduire à nouveau", 350, 625, 200, 50);
							
							// Ajout des boutons au Groupe
							g.getChildren().add(end);
							g.getChildren().add(retry);
							
							// Affichage de l'écran 
					    	stage.setScene(hand_scene);
					    	stage.show();
					    	
					    	// Gestion de l'événement pournle bouton pour quitter
					    	end.setOnAction(new EventHandler<ActionEvent>() {
							    @Override
							    public void handle(ActionEvent e) {
							    	stage.close();
							    	}
							 });
					    	// Gestion de l'évènement pour le bouton de relance
					    	retry.setOnAction(new EventHandler<ActionEvent>() {
							    @Override
							    public void handle(ActionEvent e) {
							    	stage.setScene(scene);
									stage.show();
							    	}
							 });
					    // Ensemble des exceptions
						} catch (TranslateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ModelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		            }
		            
		        });
			   	// Gestion des événements pour le bouton de retour
			   	retour.setOnAction(new EventHandler<ActionEvent>() {
				    @Override
				    public void handle(ActionEvent e) {
				    	// Fermeture de la fenêtre 
				    	newWindow.close();
				    	}
				    });
		    }
		});
	    // Gestion de l'évenement pour le bouton pour Quitter depuis la page d'accueil
		end.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent e) {
		    	stage.close();
		    	}
		 });
		
		// Timer pour la réitération de la méthode Main
		new AnimationTimer() {
			public void handle(long arg0) {
			}
		}.start(); 	
	}
}
