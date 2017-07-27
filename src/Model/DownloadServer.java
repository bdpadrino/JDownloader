/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 * Clase DownloadServer.
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class DownloadServer {

         private String servidorDescargaIp;
         private int puertoEscucha;
         private int clientesEnviadosAlServidor;
         private int clientesEnServidor;
         private boolean activo;

         /**
          * Constructor de clase.
          */
         public DownloadServer() {
         }

         /**
          * Constructor de clase. Utilizado para registrar los datos de los
          * servidores de descarga.
          *
          * @param Ip Ip del servidor de descarga.
          * @param puerto Puerto del servidor de descarga.
          */
         public DownloadServer(String Ip, int puerto) {
                  this.servidorDescargaIp = Ip;
                  this.puertoEscucha = puerto;
                  this.clientesEnviadosAlServidor = 0;
                  this.clientesEnServidor = 0;
                  activo = true;
         }

         /**
          * Constructor de clase. Utilizado para registrar los datos de los
          * servidores de descarga.
          *
          * @param Ip Ip del servidor de descarga.
          * @param puerto Puerto del servidor de descarga.
          * @param historial Historial de clientes que han sido enviados al
          * servidor.
          */
         public DownloadServer(String Ip, int puerto, int historial) {
                  this.servidorDescargaIp = Ip;
                  this.puertoEscucha = puerto;
                  this.clientesEnviadosAlServidor = historial;
                  this.clientesEnServidor = 0;
                  activo = true;
         }

         /**
          * Método utilizado para llevar el conteo de clientes conectados.
          */
         public void sumarCliente() {
                  this.clientesEnviadosAlServidor++;
                  this.clientesEnServidor++;
         }

         /**
          * En caso de que se desconecte un cliente se debe actualizar el conteo
          * de clientes conectados.
          */
         public void restarCliente() {
                  this.clientesEnServidor--;
         }

         public void restarCliente(int i) {
                  this.clientesEnServidor = clientesEnServidor - i;
         }

         /**
          * Método utilizado para actualizar el ip de los servidores de descarga
          * y el puerto de escucha
          *
          * @param ip Nueva IP del servidor de descarga.
          * @param p Nuevo puerto del servidor de descarga.
          */
         public void actualizarIpYPuerto(String ip, int p) {
                  this.servidorDescargaIp = ip;
                  this.puertoEscucha = p;
         }

         /**
          * Método utilizado para obtener el número de puerto de los sevidores
          * de descarga.
          *
          * @return Puerto de escucha del servidor de descarga.
          */
         public int getPuerto() {
                  return this.puertoEscucha;
         }

         /**
          * Método utilizado para obtener la IP de los servidores de descarga.
          *
          * @return Ip del servidor de descarga.
          */
         public String getIp() {
                  return this.servidorDescargaIp;
         }

         /**
          * Método utilizado para obtener los clientes conectados en un momento
          * dado en los servidores de descarga.
          *
          * @return Cantidad de clientes conectados en un momento dado.
          */
         public int getClientesActuales() {
                  return this.clientesEnServidor;
         }

         /**
          * Método utilizado para obtener la cantidad de clientes enviados a los
          * servidores de descarga.
          *
          * @return Cantidad de clientes enviados a un servidor de descarga.
          */
         public int getClientesEnviados() {
                  return this.clientesEnviadosAlServidor;
         }

         /**
          * Método utilizado para setear el estado de los servidores de descarga
          * a activo.
          *
          */
         public void activo() {
                  activo = true;
                  clientesEnServidor = 0;
         }

         /**
          * Método utilizado para setear el estado de los servidores de descarga
          * a inactivo.
          *
          */
         public void inactivo() {
                  activo = false;
                  clientesEnServidor = 0;
         }

         /**
          * Método utiliazdo para obtener el estado de los servidores de
          * descarga.
          *
          * @return Estado del servidor de descarga.
          */
         public boolean getEstado() {
                  return activo;
         }

         @Override
         public boolean equals(Object o) {
                  if (o instanceof DownloadServer) {
                           String TempIp = ((DownloadServer) o).servidorDescargaIp;
                           int TempPuerto = ((DownloadServer) o).puertoEscucha;
                           return ((this.servidorDescargaIp).equals(TempIp) && (this.puertoEscucha == TempPuerto));
                  }
                  return false;
         }

         @Override
         public String toString() {
                  String S = "";
                  String sep = ".";
                  S = servidorDescargaIp + sep + puertoEscucha + sep + clientesEnviadosAlServidor + sep + clientesEnServidor + sep + activo + sep;
                  S = S + "fin";
                  return S;
         }
}
