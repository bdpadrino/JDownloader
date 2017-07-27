/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;

/**
 * Clase Video.
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class Video extends Object {

         private String Nombre;
         private String nombreArchivo;

         // Variables utilizadas por los servidores de descarga.
         private int descargas;
         private int cantidadDescargando;

         /**
          * Contructor de clase. Utilizado para registrar los datos de los
          * videos.
          *
          * @param Nombre Nombre del video.
          */
         public Video(String Nombre) {
                  this.Nombre = Nombre;
                  descargas = 0;
                  cantidadDescargando = 0;
         }

         /**
          * Contructor de Clase.  Utilizado para registrar los datos de los
          * videos.
          *
          * @param Nombre Nombre del video
          * @param path contiene el nombre completo del archivo del video (si a
          * este se le agrega el path de carpeta se tiene el path del video)
          */
         public Video(String Nombre, String path) {
                  this.Nombre = Nombre;
                  this.nombreArchivo = path;
                  descargas = 0;
                  cantidadDescargando = 0;
         }

         /**
          * Contructor de Clase. Este lo usa el servidor de descarga por que el
          * es el que conoce el path y al unico que le importa.
          *
          * @param Nombre Nombre del video.
          * @param path Ruta donde esta contenido el video.
          * @param descargas Número de descargas que se han realizado de un
          * video.
          * @param cantidadDescargando Cantidad de descargas activas de un
          * video.
          */
         public Video(String Nombre, String path, int descargas, int cantidadDescargando) {
                  this.Nombre = Nombre;
                  this.nombreArchivo = path;
                  this.descargas = descargas;
                  this.cantidadDescargando = descargas;
         }

         /**
          * Método utilizado para obtener el nombre de los videos.
          *
          * @return Nombre del video.
          */
         public String getNombre() {
                  return this.Nombre;
         }

         /**
          * Método utilizado para obtener la ruta donde esta ubicado el video.
          *
          * @return Ruta donde esta contenido el video.
          */
         public String getNombreArchivo() {
                  return this.nombreArchivo;
         }

         /**
          * Método utilizado para llevar el conteo de las descargas realizadas
          * de los video.
          *
          */
         public void sumarDescarga() {
                  this.descargas++;
                  this.cantidadDescargando--;
         }

         /**
          * Método utilizado para llevar e conteo de las descargas activas de
          * los videos.
          *
          */
         public void sumarDescargando() {
                  this.cantidadDescargando++;
         }

         /**
          * Método utilizado para obtener el número de descargas que se han
          * realizado de los videos.
          *
          * @return Número de descargas.
          */
         public int getDescargas() {
                  return this.descargas;
         }

         /**
          * Método utilizado para obtener la cantidad descargas activas de los
          * videos.
          *
          * @return Número de descargas activas.
          */
         public int getDescargando() {
                  return this.cantidadDescargando;
         }

         @Override
         public boolean equals(Object o) {
                  if (o instanceof Video) {
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
