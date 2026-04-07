/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LABDANIALEISA;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashMap;
import javax.swing.JFrame;

/**
 *
 * @author Revan
 */
public class GameMap {
    int minimap [][] = {
        {1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
        {1,0,0,0,0,0,1,0,0,0,0,0,1,1,1,0},
        {0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0},
        {0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
        
    };
    
    private Game game; //una caja vacía basicamente
    private HashMap<Point,Integer> worldMap = new HashMap<>(); //creamos un hashmap, es similar a un arreglo, pero está organizado por llaves que tienen valores
    //las llaves con valores es un sistema que hará llamarlo más facil, por lo cual no tendremos que recorrer toda la matriz para hallar el valor en un punto (x,y)
    //también, point es una mini matriz de dos valores A(x,y) basicamente, sirve como sistema de coordenadas acá
    public GameMap(Game game){
        this.game = game; //este game es un parámetro que podremos modificar más adelante si lo necesitamos
        //basicamente toma una instancia de game y guardala para más tarde
        this.getMap();
    }
    private void getMap(){
        for (int j=0;j<minimap.length;j++) { //recorrer el mapa  horizontal luego vertical(y), j es vertical (las coordenadas y arreglos inician en 0
            for (int i=0;i<minimap[j].length;i++){//usamos minimap.length para calcular el largo del vector Y y para x usamos minimap[j].length
                //minimap.length = largo de la columna que contiene las filas
                //minimap[j].length = largo de una fila
                int value = minimap[j][i];
                if (value>0){
                    if (value==1){
                    worldMap.put(new Point(i,j), value); 
                    }//le asigna una llave (coordenada) y su valor a(j,i) es decir, si hay un muro o otra cosa, vamos a usar 1 para muro por ahora, pero lo mantenemos para otras cosas
                    //eventualmente se usara para crear puntos de inicio para enemigos, jugador, objetos,texturas, etc
                    if(value==8){
                        Settings.PLAYER_POS= new Point(i,j);
                    }
                }
                
            }
        }
    }
    public void draw (Graphics2D g){ //graphics 2d será la libreria que usaremos para crear figuras basicas como circulos o rectangulos
        g.setColor(Color.DARK_GRAY); //creamos la instancia de graphics2d g
        for (Point pos: worldMap.keySet()){ //este es un for each loop, hace algo por cada elemento en un grupo
            //es decir, el for each es un ciclo para hacer algo con todos sus valores o indices, key es su indice, en un arreglo, el indice sería el lugar en el cual se encuentra
            //point pos es una variable temporal donde se guardará, en este caso, un Point que es un mini arreglo de dos valores (x,y) es decir coordenadas
            //point pos guarda temporalmente la llave (en este caso point) para dibujar un rectangulo en base a esas coordenadas
            //
            
            g.drawRect(pos.x*Settings.SCALE, pos.y*Settings.SCALE, Settings.SCALE, Settings.SCALE); //crea un cuadrado de 100x100 en la coordenadas
            //como la resolución es 1600x900 y el mapa es un arreglo 2D de 16x9, esto divide la pantalla en áreas de 100x100 pixeles
            //esto está sujeto a cambios en siguientes versiones, pues no se ha diseñado el mapa oficial
            //además, no hay problema si el mapa se sale del cuadro, porque no veremos este mapa sino una proyección 3d del mapa que se mueve, entonces se podrá salir
        }
    }
}

