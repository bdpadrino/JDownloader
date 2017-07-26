/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class DownloadServer {
    private String servidorDescargaIp ;
    private int puertoEscucha;
    private int clientesEnviadosAlServidor;
    private int clientesEnServidor;
    private boolean activo;

    
    
    public DownloadServer() {}
    /**
 * crea un DatosServerDescarga recien inicializado (para cuando se crea un registro nuevo)
 * @param Ip es el ip del servidor de descarga 
 * @param puerto es el numero de puerto del servidor de descarga
 */    
    public DownloadServer(String Ip, int puerto) {
        this.servidorDescargaIp=Ip;
        this.puertoEscucha=puerto;
        this.clientesEnviadosAlServidor=0;
        this.clientesEnServidor=0;
        activo=true;

    }
 /**
 * crea un DatosServerDescarga con todos los datos dados (esto es para crear un DatosServerDescarga con toda la informacion previamente almacenada)
 * @param Ip es el ip del servidor de descarga 
 * @param puerto es el numero de puerto del servidor de descarga
 * @param historial cantidad de clientes que han sido enviados al servidor de descarga a lo largo de su historia
 */    
    public DownloadServer(String Ip, int puerto, int historial) {
        this.servidorDescargaIp=Ip;
        this.puertoEscucha=puerto;
        this.clientesEnviadosAlServidor=historial;
        this.clientesEnServidor=0;
        activo=true;
    }
    /*
    public int getId(){
        return this.idServidorD;
    }
    */
    /**
 * Se agrega un cliente a el historial y a clientes conectados
 */    
    public void sumarCliente(){
        this.clientesEnviadosAlServidor++;
        this.clientesEnServidor++;    
    }
    /**
 * Se desconecto un cliente y por ello se debe actualizar el numero de clientes conectados
 */    
    public void restarCliente(){
        this.clientesEnServidor--;    
    }
    public void restarCliente(int i ){
        this.clientesEnServidor=clientesEnServidor-i;
    }
 /**
  * Esta funcion permite actualizar el ip del servidor y el puerto de escucha 
  * @param ip nuevo ip del servidor de descarga
  * @param p nuevo puerto de escucha del servidor de descarga
  */
    public void actualizarIpYPuerto (String ip, int p){
    this.servidorDescargaIp=ip;
    this.puertoEscucha=p;
    }
    /**
     * da el numero de puerto de escucha del servidor
     * @return El puerto de escucha del servidor
     */
    public int getPuerto (){return this.puertoEscucha;}
    /**
     * 
     * @return El ip del servidor 
     */
    public String getIp(){return this.servidorDescargaIp;}
    /**
     * da la cantidad de clientes que estan conectados al servidorD
     * @return cantidad de clientes actualmente conectados
     */
    public int getClientesActuales(){return this.clientesEnServidor;}
    /**
     * Da el total de clientes que han sido enviados al server
     * @return cantidad de clientes que han sido enviados al server
     */
    public int getClientesEnviados(){return this.clientesEnviadosAlServidor;}
    

    public void activo(){activo=true;clientesEnServidor=0;};
    public void inactivo(){activo=false;clientesEnServidor=0;}
    
    public boolean getEstado(){return activo;}
    
    @Override
    public boolean equals(Object o){
        if (o instanceof DownloadServer){
            String TempIp = ((DownloadServer) o).servidorDescargaIp;
            int TempPuerto = ((DownloadServer) o).puertoEscucha;
            return ((this.servidorDescargaIp).equals(TempIp)&&(this.puertoEscucha==TempPuerto) );
        }
        return false;
    }
    /*
     private String servidorDescargaIp ;
    private int puertoEscucha;
    private int clientesEnviadosAlServidor;
    private int clientesEnServidor;
    private boolean activo;

    */
    @Override
    public String toString(){
        String S="";
        String sep=".";
        S=servidorDescargaIp+sep+puertoEscucha+sep+clientesEnviadosAlServidor+sep+clientesEnServidor+sep+activo+sep;
        S=S+"fin";
        return S;
    }
    
}
