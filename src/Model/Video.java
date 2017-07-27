/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class Video extends Object {
    private String Nombre;
    private String nombreArchivo;
    
    //Estas variables de abajo solo las usan los Servidores de descarga
    private int descargas;
    private int cantidadDescargando;


    
    /**
    * Crea un Video con solo nombre (se usa para crear un Video para hacer comparaciones)
    * @param Nombre es el nombre del video 
    */      
    public Video(String Nombre) {
        this.Nombre=Nombre;       
        descargas=0;
        cantidadDescargando=0;
    }

    /**
    * Este lo usa el servidor de descarga por que el es el que conoce el path y al unico que le importa
    * @param Nombre Nombre del video
    * @param path contiene el nombre completo del archivo del video (si a este se le agrega el path de carpeta se tiene el path del video)
    */
    public Video(String Nombre, String path) {
        this.Nombre=Nombre;
        this.nombreArchivo= path;
        descargas=0;
        cantidadDescargando=0;
    }
    
    /**
    * Este lo usa el servidor de descarga para recojer los datos almacenados de sesiones anteriores
    * @param Nombre Nombre del video
    * @param path contiene el nombre completo del archivo del video (si a este se le agrega el path de carpeta se tiene el path del video)
    * @param descargas # de descargas que ha tenido el video
    * @param cantidadDescargando # de descargas activas de este video
    */
    public Video(String Nombre, String path, int descargas, int cantidadDescargando ) {
        this.Nombre=Nombre;
        this.nombreArchivo= path;
        this.descargas=descargas;
        this.cantidadDescargando=descargas;
    }    
 
 
    /**
    * devuelve el nombre del cliente
    */   
    public String  getNombre() {
        return this.Nombre;
    }
    
    public String getNombreArchivo() { 
        return this.nombreArchivo;
    }

    public void sumarDescarga() {
        this.descargas++; 
        this.cantidadDescargando--;
    }
    
    public void sumarDescargando() {
        this.cantidadDescargando++;
    }
    
    public int getDescargas() {
        return this.descargas;
    }
    public int getDescargando() {
        return this.cantidadDescargando;
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Video){
            String tempName = ((Video) o).Nombre;
            return ((tempName).equals(this.Nombre));
        }
        return false;
    }
    

    @Override
    public String toString() {
        String object = "";
        String separtor = ".";
        object = Nombre + separtor + nombreArchivo + separtor + descargas + separtor + cantidadDescargando;
        object = object + "fin";
        return object;
    }
        
}