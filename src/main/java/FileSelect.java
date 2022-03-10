import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

// Classe pour la fenêtre de sélection de fichier
public class FileSelect {
    Group group;
    // COnstructeur
	public FileSelect() {
		this.group = new Group();
	}
	// Méthode de création de bouton
	public Button showButton(String button_name, double x, double y, double sizeX, double sizeY) {
		Button button = new Button(button_name);
	    button.setPrefSize(sizeX, sizeY);
	    button.setFont(new Font("Courier New",15));
	    button.setLayoutX(x);
	    button.setLayoutY(y);
		return button;
	}
	// Méthode qui retourne le groupe associé à la classe pour la classe Main
	public Group getGroup() {
		return group;
	}
	// Méthode la configuration pour la sélection de fichier
	public void configuringFileChooser(FileChooser fileChooser) {
        // Définition du titre pour le FileChooser
        fileChooser.setTitle("Select Some Files");
 
        // Définition du path de la racine à partir de laquelle le fileChooser débutera
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        
        // Application d'un filtre sur le type d'image autorisée à la recherche 
        fileChooser.getExtensionFilters().addAll(
        		new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));  
    }
}