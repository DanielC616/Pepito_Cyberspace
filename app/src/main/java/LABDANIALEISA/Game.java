/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LABDANIALEISA;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.Image;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Revan
 */

public class Game implements Runnable { //se usa para threading, basicamente permite multitasking con la memoria
    Thread gamethread;
    private boolean isRunning;
    JFrame window = new JFrame();
    public double deltaTime = 0;
    private GameMap map;
    private Pepito pepito;
    public Game() {
        
        
        // Cargar el icono, esto es una mini prueba
        URL iconURL = Game.class.getResource("icon.png");//buca en los recursos de el folder de recursos
        ImageIcon icon = new ImageIcon(iconURL);
        window.setIconImage(icon.getImage()); // Usa "icon", todo en minuscula
       
        // Configurar la ventana
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(Settings.WIDTH, Settings.HEIGHT);
        window.setResizable(false);
        
        window.setVisible(true); // Siempre al final
        this.newgame(); //this son variables local, seria lo mismo que ponerle un nuevo nombre al mismo objeto, pero esto es más facil
        //evita choque de nombres de variables
        window.createBufferStrategy(3);//siempre al final setVisible, pero en este caso necesitamos crear un buffer strategy
        //triple buffering es para asegurar que no haya flickering en la pantalla
        //triple buffering es una estrategia como lo dice
        //un solo buffer es como para una película, llevar frente a la audiencia, dibujar la imagen, borrarla y luego empezar a dibujar la siguiente
        //esto es muy lento para un juego
        // 2 buffers es que un primer frame A está siendo mostrado mientras B está siendo renderizado en el background
        //3 buffers. hay un frame siendo mostrado, un segundo esperando en cola y el tercero está siendo dibujado, a diferencia de 2 buffers, debido a 
        //la potencia de un PC, el 2 sigue siendo muy lento para un juego
        window.addKeyListener(this.pepito);//estas partes todavia deben ser activadas por la parte principal
        window.addMouseMotionListener(this.pepito);
        window.setFocusable(true);
        
    }
    public void startThread(){
        gamethread = new Thread(this);
        isRunning=true;
        gamethread.start();//llama a la clase run
    }
    // Aquí pondremos una gran parte bucle del juego más adelante
    public void run() {
        //System.out.println("El juego esta corriendo!");
        
        //lo que sigue contiene 2 sistemas de tiempo
        //el primero es el deltatime, calcula el tiempo entre frames y cuando llega al tiempo que deberia refrescar las cosas, lo hace
        //el segundo es usando remainingTime, lo cual le dice al cpu de descansar por el tiempo entre los frames
        double drawInterval = 1000000000/Settings.FPS; //0.01666 segundos a 60fps
        double proximoDrawt = System.nanoTime()+drawInterval; //calcula la proxima vez que se debe ejecutar
        
        //el proceso para hallar los frames por segundo y el tiempo real entre frames
        //double deltaTime = 0;
        
        long lastTime = System.nanoTime();
        long currentTime;
        long timer=0;
        int drawCount=0;
        //delta time es para hallar el tiempo entre frames
        //debido a que un juego corre a ciertos frames por segundo según la potencia
        //el tiempo que se demora en refrescar varia y al calcular la velocidad directa esto cambiaría bastante
        //por ejemplo, si tienes un juego en un pc vieja que corre a 30fps, cambiarlo a una pc que va a 120fps haría que la velocidad sea el cuadruple
        
        //en otras palabras, si lo pienas en la formula velocidad = distancia*tiempo
        // (velocidad = distancia*tiempo_entre_frames)*frames
        //esto asegura tener una velocidad constante porque a mayor frames, menor tiempo entre frames y vice versa
        
        //lasttime = currentTime;
        //fps = 1.0 / deltaTime;
        while (isRunning){ //llama al boolean isRunning, es similar a istrue pero permite threading
            currentTime = System.nanoTime();
            //deltatime será usado más adelante para calcular velocidad
            deltaTime += (currentTime-lastTime)/drawInterval;//esencialmente, deltaTime calcula la cantidad de veces que se deberia ejecutar por segundo
            timer+=(currentTime-lastTime);
            lastTime = currentTime;
            if(deltaTime>=1){
            update();
            draw();
            
            deltaTime--;
            drawCount++;
            }
            if (timer>=1000000000){
                window.setTitle(String.format("FPS: %d", drawCount));
                drawCount=0;
                timer=0;
            }
            //lo de abajo es el thread.sleep metodo
            try {//necesitamos Thread.sleep para decirle a la pc de descansar y no pasarse del fps, y para eso java nos lo pega a un try catch automatico
                double remainingTime = proximoDrawt-System.nanoTime();//del tiempo entre intervalo, cuanto falta para el siguiente
            remainingTime = remainingTime/1000000; //pasar de nanosegundos a millisegundos (thread.sleep es en millisegundos)
            if (remainingTime <0){ //si se demora más del fps en cargarlo, cargar la siguiente enseguida (porque da negativo el remainingtime)
                remainingTime=0;
            }
                Thread.sleep((long) remainingTime);
                proximoDrawt += drawInterval;
        
            } catch (InterruptedException ex) {
                Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
            }
            //arriba este codigo implementa un doble sistema de tiempo, el deltatime es esencial para calcular velocidades y Thread.sleep para descanso del pc
            //si causa mucho problema probablemente simplificaré o encontrare una manera alterna de calcular
        }
        
    }
    public void newgame(){
        this.map = new GameMap(this);
        this.pepito= new Pepito(this);
    }
    public void draw(){
        //window.getContentPane().setBackground(new Color(0,0,0));
        //this.map.draw(g); lo use anteriormente, escuché que setBackground es muy lento para esto y esta variable g es un error, pues seria null
        java.awt.image.BufferStrategy bs = window.getBufferStrategy(); //escuché que es recomendado usar double o triple buffering como la estrategia de renderizar para performance
        if (bs == null) return; //para evitar errores (si no esta siendo renderizado, detengase y así no se devuelve al estado original(pantalla en blacno
        java.awt.Graphics2D g2d = (java.awt.Graphics2D)bs.getDrawGraphics(); //le da capacidad a la variable para dibujar (como un pincel)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, Settings.WIDTH, Settings.HEIGHT);
        map.draw(g2d);//ahora si, con estos cambios se da mayor acceso a las componentes graficas más alla de jframe y swing
        pepito.draw(g2d);
        g2d.dispose();//cierra el pincel de la interfaz grafica para liberar memoria
        bs.show();//muestra lo dibujado (como si se dibujara en un lienzo y después se mostrara)
        
    }
    public void update(){
        pepito.update();
    }
    
}
