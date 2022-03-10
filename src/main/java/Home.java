import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


// Classe de la gestion de la page d'accueil 
public class Home {
	String name;
	// Constructeur
	public Home(String name) {
		this.name = name;
	}
	// Méthode de création de boutton pour la page d'accueil 
	public Button showButton(String button_name, double x, double y, double sizeX, double sizeY) {
		Button button = new Button(button_name);
	    button.setPrefSize(sizeX, sizeY);
	    button.setFont(new Font("Courier New",15));
	    button.setLayoutX(x);
	    button.setLayoutY(y);
		return button;
	}
	// Méthode d'affichage du titre
	public Text Title() {
		Text text = new Text();
		text.setFont(Font.font ("Copperplate", 50));
		text.setX(10);
		text.setY(150);
		text.setWrappingWidth(1200);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setUnderline(true);
		text.setText("Traducteur de langage des signes");
		return text;
	}
	// Méthode d'affichage du sous-titre
	public Text subTitle() {
		Text text = new Text();
		text.setFont(Font.font ("Copperplate", 20));
		text.setX(0);
		text.setY(220);
		text.setWrappingWidth(1200);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setText("Réalisé par Erwan LE BLEVEC, Adrien DUBOIS, Pierre CHECCHIN, Vincent TCHOUMBA "
				+ "\n \n dans le cadre du projet PRO3600 à Télécom SudParis");
		return text;
	}
	// Méthode d'affichage du logo TSP
	public ImageView Logo() {
		 Image image = new Image("/Logo_TSP.png");
		 ImageView imageView = new ImageView();
		 imageView.setImage(image);
		 imageView.setX(450);
		 imageView.setY(300);
		 imageView.setFitWidth(300);
		 imageView.setPreserveRatio(true);
		 return imageView;
	};
}