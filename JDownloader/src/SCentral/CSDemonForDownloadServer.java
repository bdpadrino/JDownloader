/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SCentral;

import Model.DownloadServer;
import Model.VideoForDownloadServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class CSDemonForDownloadServer extends Thread {
    private ServerSocket ss;
    public int idSession;
    public ArrayList<DownloadServerConnection> servidoresD ;
    private ArrayList<String> ListaLibros ;
    private ArrayList<VideoForDownloadServer>ListaLibrosYSusServidores; 
    private ArrayList<DownloadServer> ListaServersD;
    
    /**
    * Crea un CentralServerDemonForDServer Vacio
    */    
    public CSDemonForDownloadServer() {}
    /**
    * Crea un CentralServerDemonForDServer con las variables inicializadas
    *
    * @param puerto es el # de puerto usado para recibir las peticiones de los servidores de descarga 
    */
    public CSDemonForDownloadServer (int puerto,ArrayList<DownloadServer> ListaServersD, ArrayList<VideoForDownloadServer> ListaLibrosYSusServidores) throws IOException {
        this.ss = new ServerSocket(puerto);
        this.servidoresD =new ArrayList<DownloadServerConnection>();
        this.ListaLibrosYSusServidores= ListaLibrosYSusServidores;
        this.ListaServersD= ListaServersD;
    }
    
    /**
    * borra del ArrayList DownloadServerConnection los los hilos que se hallan desconectado
    *
    */
    private void dropDisconectedDSD() {
        this.servidoresD.removeIf(p -> !p.isAlive());
    }
    
    /**
     * Detiene al demon de los Servidores de descarga y envia el Stop() a sus hijos
     * @throws IOException si no logra cerrar el serverSocket ss 
     */
    public void Stop() throws IOException {
        
        for (int i=0; i< servidoresD.size() ; i++){
            ((DownloadServerConnection)servidoresD.get(i)).Stop();
        }
        
        this.stop();
        ss.close();        
    }
    
    /**
    * borra del ArrayList<Thread> servidoresD los hilos que se hallan desconectado
    *
    * @return cantidad de clientes en la lista 
    */  
    public int cantidadClientes(){
        this.dropDisconectedDSD();
        return this.servidoresD.size(); }
    
    
    @Override
    public void run () {
       
        
        try {
            Socket socket;
            System.out.println("Iniciando demonio de servidores de descarga... ");
            System.out.println("\tDemonio de servidores de descarga [OK]");
           // int idSession = 0;
            this.idSession=0;
            DownloadServerConnection ServidorDActual;
            
            
            while (true) {
                socket = ss.accept();
                ServidorDActual=new DownloadServerConnection(socket,idSession , this.ListaServersD, this.ListaLibrosYSusServidores);
                this.servidoresD.add(ServidorDActual);
                ServidorDActual.start();
                idSession++;
                System.out.println("Nueva conexión: "+socket);
            }
        } catch (IOException ex) {
            Logger.getLogger(CentralServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {ss.close();} catch(IOException ex) {System.out.print("No logro cerrar el socket del Demonio");} }
    }
}
