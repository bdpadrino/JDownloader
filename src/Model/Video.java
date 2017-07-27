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
    * crea un DatosLibro con solo nombre y autor (se usa para crear un DatosLibro para hacer comparaciones)
    * @param IdNombre es el nombre del libro 
    */      
    public Video(String Nombre) {
        this.Nombre=Nombre;       
        descargas=0;
        cantidadDescargando=0;

    }

    /**
    * Este lo usa el servidor de descarga por que el es el que conoce el path y al unico que le importa
    * @param Nombre Nombre del libro
    * @param Autor Autor del libro
    * @param Genero ArrayList <String> que contiene los generos del libro
    * @param path contiene el nombre completo del archivo del libro (si a este se le agrega el path de carpeta se tiene el path del libro)
    */
    public Video(String Nombre, String path) {
        this.Nombre=Nombre;
        this.nombreArchivo= path;
        descargas=0;
        cantidadDescargando=0;
    }
    
    /**
    * Este lo usa el servidor de descarga para recojer los datos almacenados de sesiones anteriores
    * @param Nombre Nombre del libro
    * @param Autor Autor del libro
    * @param Genero ArrayList <String> que contiene los generos del libro
    * @param path contiene el nombre completo del archivo del libro (si a este se le agrega el path de carpeta se tiene el path del libro)
    * @param descargas # de descargas que ha tenido el libro
    * @param cantidadDescargando # de descargas activas de este libro
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