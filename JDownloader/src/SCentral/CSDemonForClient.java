/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SCentral;

import Model.Client;
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
public class CSDemonForClient extends Thread {
    private ServerSocket ss;
    public int idSession;
    public ArrayList<ClientConnection> clientes ;
    private ArrayList<Client> Clientes ;
    private ArrayList<DownloadServer> DatosServersD;
     
    private ArrayList<DownloadServer> ListaServersD;
    private ArrayList<VideoForDownloadServer>ListaLibrosYSusServidores;
    
    /**
     * Crea un ServerDemon Vacio
     */    
    public CSDemonForClient() {}
    
    /**
    * Crea un ServerDemon con las variables inicializadas
    *
    * @param socket es el ServerSocket usado para recibir las peticiones (ya tiene el puerto asignado)
    */
    public CSDemonForClient (int puerto,ArrayList<Client> ListaClientes,ArrayList<DownloadServer> ListaServersD,ArrayList<VideoForDownloadServer> ListaLibrosYSusServidores ) throws IOException {
        this.ss = new ServerSocket(puerto);
        this.clientes =new ArrayList<ClientConnection>();
        this.Clientes=ListaClientes;
        this.DatosServersD= ListaServersD;
        this.ListaLibrosYSusServidores =ListaLibrosYSusServidores;
        //this.ListaLibrosYSusServidoresOrdenadoAutor=(ArrayList<LibroYServidores>)ListaLibrosYSusServidores.clone();
        //Ordena la lista por Autor
        //Collections.sort(this.ListaLibrosYSusServidoresOrdenadoAutor, (VideoForDownloadServer b1, VideoForDownloadServer b2) -> b1.getAutor().compareTo(b2.getAutor()));
    }
    
    /**
    * borra del ArrayList<Thread> clientes los hilos que se hallan desconectado
    *
    * @param
    */
    private void dropDisconectedSD() {
        clientes.removeIf(p -> !p.isAlive());
    }
    
    /**
    * borra del ArrayList<Thread> clientes los hilos que se hallan desconectado
    *
    * @return cantidad de clientes en la lista 
    */  
    public int cantidadClientes() {
        this.dropDisconectedSD();
        return clientes.size(); 
    }
    
    /**
     * Detiene al demon de escucha de clientes y envia el Stop a sus hijos
     * @throws IOException  
     */
    public void Stop() throws IOException {
        
        for (int i=0; i< clientes.size() ; i++){
            ((ClientConnection)clientes.get(i)).Stop();
        }
        if (ss!=null){ss.close();}
        this.stop();
        
    }
    
    @Override
    public void run () {
        try {
            Socket socket;
            System.out.println("Iniciando demonio de Clientes... ");
            
            
            System.out.println("\t Demonio de clientes [OK]");
           // int idSession = 0;
            this.idSession=0;
            ClientConnection clienteActual;
            
            while (true) {
               // Socket socket;
                //socket = ss.accept();
                //System.out.println("Nueva conexión: "+socket);
                socket = ss.accept();
                //(puertoEscuchaClientes,ListaClientes,ListaServersD,ListaLibrosYSusServidores);
                clienteActual=new ClientConnection(socket, idSession,Clientes,DatosServersD,ListaLibrosYSusServidores);
                this.clientes.add(clienteActual);
                clienteActual.start();
                idSession++;
                System.out.println("Nueva conexión: "+socket);
            }
        } catch (IOException ex) {
            Logger.getLogger(CentralServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {ss.close();} catch(IOException ex) {System.out.print("No logro cerrar el socket del Demonio");} }
    }
}
