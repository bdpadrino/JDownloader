package Client;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
class ClientDemonForManageDownloads extends Thread {
    String STemp = "";
    private boolean mostrarImpresiones = true;
    private boolean primeraVez = true;
    private String clientId = null;
    private DataOutputStream dataToServer                   = null;
    private DataInputStream dataFromServer                  = null;
    private ArrayList<ClientDownloadConnection>downloadList = new ArrayList<ClientDownloadConnection>();
    
    /**
    * Constructor de la clase
    */      
    public ClientDemonForManageDownloads() {
    }
    
    /**
    * Crea un CentralServerDemonForDServer con las variables inicializadas
    * @param puerto es el # de puerto usado para recibir las peticiones de los servidores de descarga 
    * @param ListaServersD 
    * @param ListaLibrosYSusServidores 
    * @throws java.io.IOException 
    */
    public ClientDemonForManageDownloads (ArrayList<ClientDownloadConnection> downloadList, DataOutputStream dataToServer, DataInputStream dataFromServer, String clientId) {
        this.downloadList = downloadList;
        this.dataToServer = dataToServer;
        this.dataFromServer = dataFromServer;
        this.clientId = clientId;
    }
    
    /**
    * borra del ArrayList DownloadServerConnection los los hilos que se hallan desconectado
    *
    */
    private void dropDisconectedClientDownloadConnection() {
        this.downloadList.removeIf(p -> !p.isAlive());
    }
    
    /**
     * Detiene al demon de los Servidores de descarga y envia el Stop() a sus hijos
     * @throws IOException si no logra cerrar el serverSocket ss 
     */
    public void Stop() throws IOException {
        
        for (int i=0; i< downloadList.size() ; i++){
            ((ClientDownloadConnection)downloadList.get(i)).Stop();
        }
        
        this.stop();       
    }
    
    /**
    * borra del ArrayList<Thread> servidoresD los hilos que se hallan desconectado
    *
    * @return cantidad de clientes en la lista 
    */  
    public int cantidadDeDescargas() {
        this.dropDisconectedClientDownloadConnection();
        return this.downloadList.size(); 
    }
    
    
    @Override
    public void run () {   
        try {
            while (true) {
                int i=0;
                int numIteraciones = downloadList.size();
                while( i<downloadList.size() ) {
                    if(!downloadList.get(i).isAlive()) {
                        if (downloadList.get(i).descargaTerminada()){
                            System.out.println("descarga finalizada");
                            dataToServer.writeUTF("actualizar");
                            STemp=downloadList.get(i).getIp();
                            dataToServer.writeUTF(STemp);
                            dataToServer.writeInt(downloadList.get(i).getPuerto());
                            dataToServer.writeUTF(downloadList.get(i).getNombreVideo());
                            downloadList.remove(i);
                            i--;
                        } else {
                            mostrarImpresiones = (mostrarImpresiones) ? false : mostrarImpresiones;
                            dataToServer.writeUTF("DR-"+downloadList.get(i).getNombreVideo());
                            dataToServer.writeInt(downloadList.get(i).getIndiceServidor());
                            String respues=dataFromServer.readUTF();
                            if (respues.equals("ok")) {
                                int indiceServ=dataFromServer.readInt();
                                int puerto=dataFromServer.readInt();
                                String Ip=dataFromServer.readUTF();
                                //RETOMANDO DESCARGA CON EL HILO QUE RECIBE EL ARCHIVO
                                ClientDownloadConnection clientDownloadConnection=new ClientDownloadConnection(downloadList.get(i).descargadoDelArchivo(),indiceServ,Ip,puerto,downloadList.get(i).getNombreVideo(),clientId);
                                System.out.println("Retomando la descarga de: " + downloadList.get(i).getNombreVideo());
                                downloadList.remove(i);
                                downloadList.add(clientDownloadConnection);
                                clientDownloadConnection.start();
                            }
                            Thread.sleep(5000);
                        }
                    }
                    i++;
                    Thread.sleep(1000);
                }
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println("Error general de todo el demonio");
        }
    }
}


/**
 * CLASE PPAL DEL CLIENTE CON MENU DE OPCIONES PARA LISTAR Y DESCARGAR VIDEOS
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 * @version 0.9 Sin Problemas
 */
public class Client {
    
    private static String clientId                                 = "";
    //public static String ipServer                                  = "192.168.1.100";
    private static final String ipServer                           = "localhost";
    private static final int portServer                            = 10580; 
    private static Socket clientSocket                             = null;
    private static DataOutputStream dataToServer                   = null;
    private static DataInputStream dataFromServer                  = null;
    private static final ArrayList<ClientDownloadConnection>downloadList = new ArrayList<ClientDownloadConnection>();
    
    public static void Stop() throws IOException {
        
        if (clientSocket != null ) { clientSocket.close(); }
        if (dataFromServer != null ) { dataFromServer.close(); }
        if (dataToServer != null ) { dataToServer.close(); }
        for (int i=0; i<downloadList.size(); ) {
            downloadList.get(i).Stop();
        }
    }
    
    /**
     * METODO PRINCIPAL CON EL MENU DEL CLIENTE
     * @param args
     * @throws InterruptedException 
     */
    public static void main(String[] args)  throws InterruptedException {
        Scanner teclado    = new Scanner(System.in);
        System.out.print("Introduzca su ID: ");
        clientId = (teclado.nextLine());
        String instruccion = "";
        String [] Instrucciones;
              
        try 
        {
            clientSocket = new Socket(ipServer, portServer);
            dataToServer = new DataOutputStream(clientSocket.getOutputStream());
            dataFromServer = new DataInputStream(clientSocket.getInputStream());
            
            dataToServer.writeUTF(clientId); 
            System.out.println(dataFromServer.readUTF()); 
            
            int x; 

            String lectura; 
            String[] Lectura;
            String STemp="";
            do {
                
                System.out.println("D : Solicita descargar Videos");
                System.out.println("L : Mostrar los videos Disponibles");
                System.out.print("Cliente#"+clientId+" Introduzca opcion a Elegir:");
                ClientDemonForManageDownloads clientDemonForManageDownloads = new ClientDemonForManageDownloads(downloadList, dataToServer, dataFromServer, clientId);
                clientDemonForManageDownloads.start();
                instruccion=teclado.nextLine();

                Instrucciones= instruccion.split("-");
               
                if ( "L".equals(Instrucciones[0])){
                    dataToServer.writeUTF("L");
                    x = dataFromServer.readInt(); 
                    System.out.println(x);
                    System.out.printf( "%-40s %n", "Lista de Peliculas Disponibles");
                    System.out.println("***********************************");
                    for(int i=0; i<x; i++){
                        lectura =dataFromServer.readUTF();
                        Lectura= lectura.split(Pattern.quote("."));
                        System.out.printf( "%-40s %n", Lectura[0]); 
                    }
                    System.out.println("************************************");
                }
                
                else if ( ("D".equals(instruccion))){
                    // INICIO - PROCESO DE DESCARGA
                    System.out.print("Introduzca Nombre del Video:");
                    String videoName = teclado.nextLine();
                    
                    // INICIO - BUSCAR VIDEO EN EL SERVIDOR
                    System.out.println("Buscando video...");
                    dataToServer.writeUTF("B-" + videoName);
                    x = dataFromServer.readInt();
                    ArrayList<String> videosADescargar=new ArrayList<String>();
                   
                    for(int i=0; i<x; i++){                        
                        lectura = dataFromServer.readUTF();
                        if ("fin".equals(lectura)) {
                            break; 
                        } else {
                            videosADescargar.add("D-"+lectura);
                            System.out.println("Se encontró video");
                        }
                    }
                    // FIN - BUSCAR VIDEO EN EL SERVIDOR
                    
                    Lectura = null;
                    
                    // INICIO - DESCARGAR VIDEOS DEL SERVIDOR (Por ahora solo uno, pero se puede escalar)
                    for (String videoADescargar : videosADescargar) {
                        dataToServer.writeUTF(videoADescargar);
                        String respuesta=dataFromServer.readUTF();
                        if (respuesta.equals("ok")) {
                            int indiceServ=dataFromServer.readInt();
                            int puerto=dataFromServer.readInt();
                            String Ip=dataFromServer.readUTF();
                            Lectura = videoADescargar.split("-");
                            System.out.println(Lectura[1]);
                            //LLAMADO AL HILO PARA RECIBIR LA DESCARGA
                            ClientDownloadConnection clientDownloadConnection=new ClientDownloadConnection(0,indiceServ,Ip,puerto,Lectura[1],clientId);
                            downloadList.add(clientDownloadConnection);
                            clientDownloadConnection.start();
                        } else { 
                            System.out.println("El video no se encuentra en el sistema"+Lectura[1]);
                        }
                    }
                    // FIN - DESCARGAR VIDEOS DEL SERVIDOR
                    // FIN - PROCESO DE DESCARGA
                }
                
                System.out.println("Descargando : "+downloadList.size()+" videos");
                
            } while(!("adios".equals(Instrucciones[0]) || "salir".equals(Instrucciones[0]) || "exit".equals(Instrucciones[0]) ));
        } 
        catch (IOException ex) {
            System.out.println("El servidor central cerro");
        } finally { 
            try{
                System.out.println("Cerro el cliente");
                Stop();

            } catch (IOException e){ 
                
            } 
        }
    }
}
