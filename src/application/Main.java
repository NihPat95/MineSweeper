package application;



import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class Main extends Application {

	private final int PreferredHeight = 600;
	private final int PreferredWidth = 800;
	private final int TileSize = 40;
	private final int XTiles = PreferredHeight/TileSize;
	private final int YTiles = PreferredWidth/TileSize;
	private Tile[][] grid = new Tile[XTiles][YTiles];
	private Scene scene;
	private class Tile extends StackPane{
		private int x,y;
		private boolean hasBomb;
		private boolean isOpen = false;
		private Rectangle border = new Rectangle(TileSize-2,TileSize-2);
		private Text text = new Text();

		public Tile(int x,int y,boolean hasBomb){
			this.x = x;
			this.y = y;
			this.hasBomb = hasBomb;
			border.setFill(Color.web("#bbbbbb"));
			border.setStroke(Color.LIGHTGRAY);
			text.setFont(Font.font(18));
			text.setText(hasBomb?"X":"");
			text.setVisible(false);
			getChildren().addAll(border,text);
			setTranslateX(x*TileSize);
			setTranslateY(y*TileSize);
			setOnMouseClicked(e->open());
		}
		private void open() {

			if(isOpen){
				//do nothing
				return;
			}
			if(hasBomb){
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("Game Over");
				alert.setHeaderText(null);
				alert.showAndWait();
				scene.setRoot(createContent());
			}
			isOpen = true;
			text.setVisible(true);
			border.setFill(null);
			if(text.getText().isEmpty()){
				getNeighbours(this).forEach(Tile::open);
			}
		}}

	private List<Tile> getNeighbours(Tile tile) {

		List<Tile> neighbour = new ArrayList<>();
		int[] points = new int[]{
				-1,-1,
				-1,0,
				-1,1,
				0,-1,
				0,1,
				1,-1,
				1,0,
				1,1
		};

		for(int i=0; i<points.length; i++){
			int dx = points[i];
			int dy = points[++i];
			int newX = tile.x + dx;
			int newY = tile.y + dy;

			if(newX>=0 && newX<XTiles
					&& newY >=0 && newY<YTiles){
				neighbour.add(grid[newX][newY]);
			}
		}

		return neighbour;
	}
	@Override
	public void start(Stage primaryStage) {
		try {
			scene = new Scene(createContent());
			primaryStage.setTitle("MineSweeper");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Parent createContent() {
		// TODO Auto-generated method stub
		Pane root = new Pane();
		root.setPrefSize(PreferredHeight,PreferredWidth);
		for(int i=0; i<YTiles; i++){
			for(int j=0; j<XTiles; j++){
				Tile tile = new Tile(j,i,Math.random()<0.2);
				grid[j][i] = tile;
				root.getChildren().add(tile);
			}
		}
		for(int i=0; i<YTiles; i++){
			for(int j=0; j<XTiles; j++){
				Tile tile = grid[j][i];
				if(tile.hasBomb){
					continue;
				}
				long bombs = getNeighbours(tile).stream().filter(t->t.hasBomb).count();
				if(bombs>0){
					tile.text.setText(String.valueOf(bombs));
				}
			}
		}
		return root;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
