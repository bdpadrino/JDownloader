package Model;

import java.util.ArrayList;

/**
 * Clase VideoForDownloadServer.
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class VideoForDownloadServer {

         private Video Video;
         // Contiene los índices de los servidores que tienen el video
         private ArrayList<Integer> ListaServidoresD;
         // Contiene en la posicion [i] la cantidad de descargas que tiene de un video el servidor en la posicion [i] de ListaServidoresD.
         private ArrayList<Integer> CantidadDeDescargasPorServidor;
         private int numeroDescargas;

         /**
          * Constructor de clase.
          */
         public VideoForDownloadServer() {
         }

         /**
          * Constructor de clase. Utilizado para registrar los datos descarga de
          * los videos.
          *
          * @param Video Video que será descargado.
          */
         public VideoForDownloadServer(Video Video) {
                  this.Video = Video;
                  this.ListaServidoresD = new ArrayList<Integer>();
                  this.numeroDescargas = 0;
         }

         /**
          * Constructor de clase. Utilizado para registrar los datos descarga de
          * los videos.
          *
          * @param Video Video que será descargado.
          * @param ListaServidoresD Indices de los servidores que tienen el
          * video.
          */
         public VideoForDownloadServer(Video Video, ArrayList<Integer> ListaServidoresD) {
                  this.Video = Video;
                  this.ListaServidoresD = ListaServidoresD;
                  this.CantidadDeDescargasPorServidor = new ArrayList<Integer>();
                  for (int i = 0; i < ListaServidoresD.size(); i++) {
                           CantidadDeDescargasPorServidor.add((Integer) 0);
                  }
                  this.numeroDescargas = 0;
         }

         /**
          * * Constructor de clase. Utilizado para registrar los datos descarga
          * de los videos.
          *
          * @param Video Video que será descargado.
          * @param ListaServidoresD Indices de los servidores que tienen el
          * video.
          * @param CantidadDeDescargasPorServidor Número de descragas que tiene
          * activas el video en cada servidor.
          * @param numeroDescargas Cantidad de descargas realizadas de este
          * video.
          */
         public VideoForDownloadServer(Video Video, ArrayList<Integer> ListaServidoresD, ArrayList<Integer> CantidadDeDescargasPorServidor, int numeroDescargas) {
                  this.Video = Video;
                  this.ListaServidoresD = ListaServidoresD;
                  this.CantidadDeDescargasPorServidor = CantidadDeDescargasPorServidor;

                  this.numeroDescargas = numeroDescargas;
         }

         /**
          * Método utilizado para obtener el nombre del video a descargar.
          *
          * @return Nombre del video.
          */
         public String getNombre() {
                  return Video.getNombre();
         }

         /**
          * Método utilizado para obterner el Video a descargar.
          *
          * @return Video.
          */
         public Video getVideo() {
                  return Video;
         }

         /**
          * Método utilizado para agregar el servidor a la lista de servidores
          * que tienen el video solo si este no estaba en la lista antes.
          *
          * @param ServerD Servidor de descarga a agregar en la lista.
          */
         public void addServidorD(Integer ServerD) {
                  if (this.ListaServidoresD.indexOf(ServerD) == -1) {
                           this.ListaServidoresD.add(ServerD);
                           this.CantidadDeDescargasPorServidor.add(0);
                  }

         }

         /**
          * Método utilizado para obtener el servidor de descarga que será
          * utilizado.
          *
          * @param i Indice del servidor de descarga.
          * @return Servidor de descarga.
          */
         public int getServidor(int i) {
                  return this.ListaServidoresD.get(i);
         }

         /**
          * Método utilizado para obtener el número de descragas que tiene
          * activas el video en cada servidor.
          *
          * @param i Indice del servidor de descarga.
          * @return Número de descragas que tiene activas el video en cada
          * servidor.
          */
         public int getDescargasServidor(int i) {
                  return this.CantidadDeDescargasPorServidor.get(i);
         }

         /**
          * Método utilizado para obtener el tamaño de la lista de servdores de
          * descarga.
          *
          * @return Tamaño de la lista de servidores.
          */
         public int getServidoresSize() {
                  return this.ListaServidoresD.size();
         }

         /* BEGIN SYNC DOWNLOAD SERVERS */
         /**
          * Método utilizado para obtener la lista de servidores de descarga.
          *
          * @return Lista de servidores de descarga.
          */
         public ArrayList<Integer> getListaServidoresD() {
                  return ListaServidoresD;
         }
         /* END SYNC DOWNLOAD SERVERS */
         
         /**
          * Método utilizado para obtener la cantidad de descargas realizadas de
          * este video.
          *
          * @return Cantidad de descargas realizadas de este video.
          */
         public int getDescargas() {
                  return numeroDescargas;
         }

         /**
          * Método utilizado para cambiar el estado de una descarga activa a
          * completada.
          *
          * @param servidor Indice del servidor de descarga.
          */
         public void sumarDescarga(int servidor) {
                  this.numeroDescargas++;
                  int indiceServidor = ListaServidoresD.indexOf(servidor);
                  int temp = CantidadDeDescargasPorServidor.get(indiceServidor) + 1;
                  CantidadDeDescargasPorServidor.set(indiceServidor, temp);

         }

         @Override
         public boolean equals(Object o) {
                  if (o instanceof VideoForDownloadServer) {
                           String TempNombre = (((VideoForDownloadServer) o).Video).getNombre();
                           return ((this.Video.getNombre()).equals(TempNombre));
                  }
                  return false;
         }

         @Override
         public String toString() {
                  String object = "";
                  String separator = ".";
                  object = numeroDescargas + "";
                  for (int i = 0; i < ListaServidoresD.size(); i++) {
                           object = object + separator + ListaServidoresD.get(i);
                  }
                  object = object + "-";
                  for (int i = 0; i < CantidadDeDescargasPorServidor.size(); i++) {
                           object = object + CantidadDeDescargasPorServidor.get(i) + separator;
                  }
                  object = object + "fin";
                  return object;
         }

}
