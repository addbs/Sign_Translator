import ai.djl.ModelException;
import ai.djl.modality.Classifications;
import ai.djl.translate.TranslateException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.String;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.shape.Rectangle;

// Classe pour la page de résultat
public class Traductor {
	private double WIDTH = 1200;
    private double HEIGHT = 700;
	Group group;
	String path;
	
	// Constructeur avec les initilisations graphiques
	public Traductor(String path) throws TranslateException, IOException, ModelException {
		this.path = path;
		this.group = new Group();
		
		// Initialisation du canvas
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		group.getChildren().add(canvas);
		
		//Initailisation des graphismes
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		// Mise en place du fond d'écran
		gc.setFill(Color.rgb(129,131,193));
		gc.fillRect(0, 0, canvas.getWidth() , canvas.getHeight());
		
		// Affichage de l'image sélectionnée précédemment
		InputStream stream = new FileInputStream(path);
	    Image image = new Image(stream);
	    ImageView imageView = new ImageView();
	    imageView.setImage(image);
	    imageView.setX(150);
	    imageView.setY(200);
	    imageView.setFitWidth(300);
	    imageView.setPreserveRatio(true);
		
	    // Affichage des textes du haut de page
		Text text = choiceText();
		Text title = Title();
		
		// Affichage d'un élément graphique 
		Rectangle r = Rectangle();
		
		// Ajout au canvas de l'ensemle des éléments créés précédemment
		group.getChildren().add(text);
		group.getChildren().add(imageView);
		group.getChildren().add(r);
		group.getChildren().add(title);
		
		// Résultat de la traduction avec l'image sélectionnée
		Classifications c = Translate(false, 1);
		
		// Récupération du résultat au format String puis extraction de la classe la plus probable
		String s = (c.best()).toString();
		String classe = s.substring(8, 9);
		
		// Affichage du résultat 
		Text resultat = new Text();
		resultat.setFont(new Font(20));
		resultat.setX(600);
		resultat.setY(250);
		resultat.setWrappingWidth(400);
		resultat.setUnderline(true);
		resultat.setText("D'après notre algorithme, le signe représenté est : "+classe);
		
		// Affichage de l'image associé au résultat
		ImageView result = resultImage(classe);
		
		// Ajout des éléments manquants au Groupe
		group.getChildren().add(resultat);
		group.getChildren().add(result);
	}
	// Méthode de traduction (possibilité d'entrainer selon 2 modèles)
	private Classifications Translate(boolean train, int model) throws TranslateException, IOException, ModelException {
		// Entrainement 
		if (train) {
			switch (model) {
			case 1: Training t1 = new Training();
					t1.train();
					break;
			case 2: Training2 t2 = new Training2();
					t2.train();
					break;
			}
		}
		// Traduction 
		Inference inference = new Inference(path);
		Classifications classification = inference.predict();
		return classification;
	}
	// Méthode qui renvoie le groupe pour l'affichage des boutons dans la classe Main
	public Group getGroup() {
		return group;
	}
	// Méthode d'affichage du rectangle 
	private Rectangle Rectangle() {
		Rectangle r = new Rectangle();
		r.setFill(Color.rgb(102, 102, 153));
		r.setX(500);
		r.setY(200);
		r.setWidth(600);
		r.setHeight(300);
		r.setArcWidth(20);
		r.setArcHeight(20);
		return r;
	}
	// Méthode d'affichage des boutons pour la page de résultats
	public Button showButton(String button_name, double x, double y, double sizeX, double sizeY) {
		Button button = new Button(button_name);
	    button.setPrefSize(sizeX, sizeY);
	    button.setFont(new Font("Courier New",15));
	    button.setLayoutX(x);
	    button.setLayoutY(y);
		return button;
	}
	// Méthode d'affichage du titre
	private Text Title() {
		Text text = new Text();
		text.setFont(Font.font ("Copperplate", 50));
		text.setX(0);
		text.setY(50);
		text.setWrappingWidth(1200);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setUnderline(true);
		text.setText("Traducteur de langage des signes");
		return text;
	}
	//Méthode d'affichage du texte de choix du fichier
	private Text choiceText() {
		Text text = new Text();
		text.setFont(Font.font ("Verdana", 15));
		text.setX(0);
		text.setY(110);
		text.setWrappingWidth(1200);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setUnderline(true);
		text.setText("La photo que vous avez choisi est "+path);
		return text;
	}
	// Méthode d'affichage de l'image associé au résultat
	private ImageView resultImage(String classe) {
		Image image = new Image("/examples/"+(classe.toLowerCase())+".png");
	    ImageView imageView = new ImageView();
	    imageView.setImage(image);
	    imageView.setX(700);
	    imageView.setY(300);
	    imageView.setFitWidth(150);
	    imageView.setPreserveRatio(true);
	    return imageView;
	}
}
