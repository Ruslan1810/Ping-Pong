package ping.pong;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.AnimationTimer;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class FXMLDocumentController implements Initializable {

    @FXML
    private AnchorPane myAnchorPane;

    Sprite racket1;

    int count1 = 0;
    int count2 = 0;
    @FXML
    private TextField countField1;
    @FXML
    private TextField countField2;

    MediaPlayer losesound;
    MediaPlayer winsound;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Canvas canvas = new Canvas(512, 512);
        myAnchorPane.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();

        Image bollImg = new Image(getClass().getResource("boll.png").toExternalForm(), 20, 20, true, false);
        Image racketImg = new Image(getClass().getResource("racketImg.png").toExternalForm());
        Image playingField = new Image(getClass().getResource("green.jpg").toExternalForm());
        Image gameoverImg = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Ping-Pong\\src\\ping\\pong\\lose.jpg").toURI().toString(), 300, 300, true, false);
        Image winImg = new Image(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Ping-Pong\\src\\ping\\pong\\win.png").toURI().toString(), 300, 300, true, false);

        Media sound1 = new Media(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Ping-Pong\\src\\ping\\pong\\lose.mp3").toURI().toString());
        losesound = new MediaPlayer(sound1);
        Media sound2 = new Media(new File("C:\\Users\\Руслан\\Documents\\NetBeansProjects\\Ping-Pong\\src\\ping\\pong\\winsound.mp3").toURI().toString());
        winsound = new MediaPlayer(sound2);

        final long startNanoTime = System.nanoTime();
        racket1 = new Sprite(210, 487, 100, 25, 0, 0, racketImg);
        Sprite racket2 = new Sprite(210, 0, 100, 25, 0, 0, racketImg);
        Sprite boll = new Sprite(400, 100, 50, 50, -1, -2, bollImg);
        Sprite gameover = new Sprite(100, 100, 50, 50, 0, 0, gameoverImg);
        Sprite win = new Sprite(100, 100, 50, 50, 0, -0, winImg);

        new AnimationTimer() {
            @Override
            public void handle(long currentNanoTime) {
                //частота кадра
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;

                //фон
                gc.drawImage(playingField, 0, 0);

                //отрисовк обЪектов
                racket1.render(gc);
                boll.render(gc);
                racket2.render(gc);

                //движение обЪектов
                racket1.move(t);
                boll.move(t);
                racket2.move(t);

                //условия достижения мяча границ поля
                if (boll.positionX > 492) {
                    boll.velocityX = -boll.velocityX;
                }
                if (boll.positionX < 0) {
                    boll.velocityX = -boll.velocityX;
                }
                if (boll.positionY > 492) {
                    boll.velocityY = -boll.velocityY;

                    //подсчет очков и запуск картинки с музыкой
                    count2 = count2 + 1;
                    String str2 = Integer.toString(count2);
                    countField2.setText(str2);
                    if (count2 == 3) {

                        losesound.play();
                        gameover.render(gc);
                        stop();
                    }
                }
                if (boll.positionY < 0) {
                    boll.velocityY = -boll.velocityY;
                    count1 = count1 + 1;
                    String str1 = Integer.toString(count1);
                    countField1.setText(str1);
//подсчет очков и запуск картинки с музыкой
                    if (count1 == 3) {
                        winsound.play();
                        win.render(gc);
                        stop();

                    }
                }
                //движение ракетки 2 за мячом
                if (boll.positionX > 256 & racket2.positionX < 256) {
                    racket2.velocityX = 5;

                }
                if (boll.positionX < 256 & racket2.positionX > 256) {
                    racket2.velocityX = -5;

                }

                //достижение ракеткой2 границ поля
                if (racket2.positionX > 492) {
                    racket2.velocityX = -racket2.velocityX;

                }
                if (racket2.positionX < 0) {
                    racket2.velocityX = -racket2.velocityX;

                }

                //условие столкновения мяча и ракетки1
                if (racket1.intersects(boll)) {
                    boll.velocityY *= -1;

                }

                //условие столкновения мяча и ракетки 2
                if (racket2.intersects(boll)) {
                    boll.velocityY *= -1;

                }
            }
        }.start();

    }

    class Sprite {

        double positionX;
        double positionY;
        double width;
        double height;
        double velocityX;
        double velocityY;
        Image image;

        public Sprite(double positionX, double positionY, double width, double height, double velocityX, double velocityY, Image image) {
            this.positionX = positionX;
            this.positionY = positionY;
            this.width = width;
            this.height = height;
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.image = image;
        }

        void move(double time) {
            positionX += velocityX;// * time;
            positionY += velocityY;// * time;

        }

        void render(GraphicsContext gc) {
            gc.drawImage(image, positionX, positionY);
        }

        Rectangle2D getBoundary() {
            return new Rectangle2D(positionX, positionY, width, height);
        }

        boolean intersects(Sprite s) {
            return s.getBoundary().intersects(this.getBoundary());
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        System.out.println("handleOnKeyPressed");
        if (event.getCode() == KeyCode.A) {
            racket1.positionX -= 10;
        }
        if (event.getCode() == KeyCode.D) {
            racket1.positionX += 10;
        }
    }

    @FXML
    private void onKeyRelased(KeyEvent event) {
    }

    @FXML
    private void onKeyTyped(KeyEvent event) {
    }

}
