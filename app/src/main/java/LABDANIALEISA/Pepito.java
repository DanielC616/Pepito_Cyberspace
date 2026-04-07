/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LABDANIALEISA;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Revan
 */
public class Pepito implements KeyListener,MouseMotionListener {
    
    private Game game; //referencia a la clase principal para obtener información de ahí como la del mapa
    private float x,y;
    private double angle;
    public boolean upPressed,downPressed,leftPressed,rightPressed,runPressed;//boolean, es un switch de si o no
    private Robot robot;//este es un robot, usa una clase robot y se usará tomar control y mover el mouse
    //en este caso, es porque queremos hallar la diferencia en x del mouse y luego devolverlo al centro
    private float mouseSensitivity;
    double twoPI = Math.PI*2;
    
    public Pepito(Game game){
        this.game = game;
        this.x=Settings.PLAYER_POS.x;
        this.y=Settings.PLAYER_POS.y;
        this.angle=Settings.PLAYER_ANGLE;
        
        //el try catch por cierto intenta ejecutar el codigo en try, y si falla ejecuta catch pero no crashea el programa en general
        //es recomendado el try catch en toda parte del codigo con Robot para evitar errores
        //por cierto, los "e" son variables que guarda información, puede ser cambiado sin problema
        try{//try catch obligatorio al crear un robot, pues se considera peligroso (porque controla tu pc)
            this.robot=new Robot();
        }catch(AWTException e){//AWTException es un error de la libreria graphica awt del cual se basa el juego(swing y JFrame)
            System.err.println("El robot fallo en inicializar, estás mal jajaja");//System.err arroja el mensaje de error que queramos
        }
        
        
    
}
    private void movement(){
        double sen_a = Math.sin(this.angle);
        double cos_a = Math.cos(this.angle);
        float dx=0,dy=0; 
        double dt= game.deltaTime;
        double speed=Settings.PLAYER_SPEED*dt;
        
        if(runPressed==true){
            speed=speed*1.5;
        }
        double speed_sen_a = speed*sen_a;
        double speed_cos_a = speed*cos_a;
        if(upPressed==true){
            dx+=speed_cos_a;//+= suma a si mismo mas speed_cos_a y si es positivo o no depende del cuadrante y como definimosla dirección
            dy+=speed_sen_a;//w se considera adelante, en modo cartesiano, es el primer cuadrante, donde seno y coseno son positivos
            
        }
        if(leftPressed==true){
            dx+=speed_sen_a;//opuesto del moverse a la derecha, invertir signos
            dy+= -speed_cos_a;
        }
        if(downPressed==true){
            dx+= -speed_cos_a;//seno y coseno negativo, tercer cuadrante
            dy+= -speed_sen_a;
        }
        if(rightPressed==true){
            dx+= -speed_sen_a;//te desplazas a cos(a+90) = -sen(a)
            dy+= speed_cos_a;//te desplazas a sen(a+90) = cos(a)
        }
        
        
        this.x+=dx;//actualiza las posiciones
        this.y+=dy;
}
    public void update(){
        this.movement();
    }
    public void draw(Graphics2D g){
        g.setColor(Color.yellow);
        g.draw(new Line2D.Float(this.x*Settings.SCALE,this.y*Settings.SCALE,this.x*Settings.SCALE+
                Settings.WIDTH*(float)Math.cos(this.angle),this.y*Settings.SCALE+Settings.HEIGHT*(float)Math.sin(this.angle)));
        g.setColor(Color.blue);
        g.draw(new Ellipse2D.Float(this.x*Settings.SCALE-7.5f,this.y*Settings.SCALE-7.5f,15,15));
    }
    public Point2D.Float getPos() { //para la posición exacta
    return new Point2D.Float(this.x,this.y);
}
    public Point getMapPos(){//posición en el mapa
        return new Point((int)this.x,(int)this.y);
    }
    
    @Override
    public void keyTyped(KeyEvent e){//necesita ser implementado por default, este es innecesario para el juego
        
    }
    @Override
    public void keyPressed(KeyEvent e){
        int code = e.getKeyCode();//escucha y espera una tecla, si una tecla es tocada, lo pasa a su codigo que lo identifica que es un int
        
        if(code == KeyEvent.VK_W){//si el codigo es igual al valor de codigo para w
            upPressed=true;
        }
        if(code == KeyEvent.VK_A){
            leftPressed=true;
        }
        if(code == KeyEvent.VK_S){
            downPressed=true;
        }
        if(code == KeyEvent.VK_D){
            rightPressed=true;
        }
        if(code == KeyEvent.VK_SHIFT){
            runPressed=true;
        }
        
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        int code = e.getKeyCode();//escucha y espera una tecla, si una tecla es tocada, lo pasa a su codigo que lo identifica que es un int
        
        if(code == KeyEvent.VK_W){//si el codigo es igual al valor de codigo para w
            upPressed=false;
        }
        if(code == KeyEvent.VK_A){
            leftPressed=false;
        }
        if(code == KeyEvent.VK_S){
            downPressed=false;
        }
        if(code == KeyEvent.VK_D){
            rightPressed=false;
        }
        if(code == KeyEvent.VK_SHIFT){
            runPressed=false;
        }
        
    }
    @Override
    public void mouseMoved(MouseEvent e){
        int centerX = game.window.getWidth()/2;//hallar el centro de la pantalla
        int centerY = game.window.getHeight()/2;// esto funciona porque si hallas el punto de inicio y sumas la mitad (sea height o width, hallas el centro)
        int dxMouse = e.getX()-centerX;
        if (dxMouse!=0){//para evitar traer devuelta al mouse si está en el centro
            
        this.angle+=dxMouse*Settings.PLAYER_MOUSE_SENSITIVITY;
        if (this.angle<0){
            this.angle+=twoPI;//si es negativo suma 2pi o 360 grados para convertirlo positivo (para evitar errores en el raycasting
        }
        if (this.angle>twoPI){
            this.angle-=twoPI;//si es mayor a 360 grados, traerlo devuelta a ese rango
        }
        try{
            //queremos guardar en un Point la posición de origen de la ventana
            Point windowPos;
            windowPos = new Point(game.window.getLocationOnScreen().x,game.window.getLocationOnScreen().y);
            //se usa game.window.getLocationonScreen para hallar el punto de origen sin importar si se mueve la pantalla del juego de lugar
            //si es origen es windowPos, el origen+la mitad de la altura y el ancho nos lleva al centro
            robot.mouseMove(windowPos.x+centerX, windowPos.y+centerY);
        } catch(Exception ex){//aggarar cualquier error, se usa Exception como un aggarador de errores generales
            //se usó AWTException antes porque se sabe que ese es el error común que genera, aca pueden ser varios errores distintos
            //también, se usó ex porque ya se está usando e para MouseEvent
        }
        
    }
    }
    @Override
    public void mouseDragged(MouseEvent e){
        mouseMoved(e);//si esta siendo clickeado y movido, todavia aplicar lo de mouseMoved
    }
    
    
    
}
