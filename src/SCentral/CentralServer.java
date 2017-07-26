
package SCentral;
import Model.DownloadServer;
import Model.VideoForDownloadServer;
import Model.Client;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
class CSDemonForDownloadServer extends Thread {
    private ServerSocket ss;
    public int idSession;
    public ArrayList<DownloadServerConnection> servidoresD ;
    private ArrayList<String> ListaLibros ;
    private ArrayList<VideoForDownloadServer>ListaLibrosYSusServidores; 
    private ArrayList<DownloadServer> ListaServersD;
    
    /**
    * Constructor de la clase
    */      
    public CSDemonForDownloadServer() {
    }
    
    /**
    * Crea un CentralServerDemonForDServer con las variables inicializadas
    * @param puerto es el # de puerto usado para recibir las peticiones de los servidores de descarga 
    * @param ListaServersD 
    * @param ListaLibrosYSusServidores 
    * @throws java.io.IOException 
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
            System.err.println("Error IOException DemonDownloadServer");
        } finally {
            try {
                ss.close();
            } catch(IOException ex) {
                System.err.print("No logro cerrar el socket del Demonio");
            } 
        }
    }
}

/**
*
* @author Atahualpa Silva F. <https://github.com/atahualpasf>
*/
class CSDemonForClient extends Thread {
    private ServerSocket ss;
    public int idSession;
    public ArrayList<ClientConnection> clientes ;
    private ArrayList<Client> Clientes ;
    private ArrayList<DownloadServer> DatosServersD;
     
    private ArrayList<DownloadServer> ListaServersD;
    private ArrayList<VideoForDownloadServer>ListaLibrosYSusServidores;
    
   /**
    * Crea un ServerDemon con las variables inicializadas
     * @param puerto
     * @param ListaClientes
     * @param ListaServersD
     * @param ListaLibrosYSusServidores
     * @throws java.io.IOException
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
    * borra del ArrayList < Thread > clientes los hilos que se hallan desconectado
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
    
    /**
     * METODO QUE CORRE EL HILO PARA PERMITIR CONCURRENCIA DE CLIENTES
     */
    @Override
    public void run () {
        try {
            Socket socket;
            System.out.println("Iniciando demonio de Clientes... ");

            this.idSession=0;
            ClientConnection clienteActual;
            
            while (true) {
               
                socket = ss.accept();
                
                clienteActual=new ClientConnection(socket, idSession,Clientes,DatosServersD,ListaLibrosYSusServidores);
                this.clientes.add(clienteActual);
                clienteActual.start();
                idSession++;
                
                System.out.println("Nueva conexión: "+socket);
            }
        } catch (IOException ex) {
            System.out.print("No logro cerrar el socket del Demonio IOException Catch");
        } finally {
            try {
                ss.close();
            } catch(IOException ex) {
                System.out.print("No logro cerrar el socket del Demonio");
            } 
        }
    }
}

/**
 * CLASE PPAL DEL SERVIDOR CENTRAL QUE CREA LOS HILOS DEL CLIENTE Y SERVIDORES DE DESCARGA
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class CentralServer {

    
    public static void main(String args[]) throws IOException, InterruptedException {
        try{   
            final int puertoEscuchaClientes= 10580;
            final int puertoEscuchaServidoresD= 10581;

           //Cargar lista de clientes con sus nombres y cantidad de visitas 
            ArrayList<Client> ListaClientes= new ArrayList<Client>();        
            //Cargar lista de servidores de descarga
            ArrayList<DownloadServer> ListaServersD=new ArrayList<DownloadServer>();
            //Cargar lista de videos
            ArrayList<VideoForDownloadServer> ListaVideosYSusServidores = new ArrayList<VideoForDownloadServer>() ;

            //CORRE EL HILO PARA CONEXION CON EL SERVIDOR DE DESCARGA
            CSDemonForDownloadServer centralServerDemonForDServer= new CSDemonForDownloadServer(puertoEscuchaServidoresD,ListaServersD,ListaVideosYSusServidores);
            centralServerDemonForDServer.start();

             //CORRE EL HILO PARA CONEXION CON EL CLIENTE
            CSDemonForClient centralServerDemonForClient = new CSDemonForClient(puertoEscuchaClientes,ListaClientes,ListaServersD,ListaVideosYSusServidores);
            centralServerDemonForClient.start();


            Scanner teclado = new Scanner(System.in);
            String comando;
            
            String nombreVideo;
           
            int x;
            int y;
            Thread.sleep(1500);
            do{
                System.out.println("Opciones:");
                System.out.println("1: NUMERO_DESCARGAS_VIDEO");
                System.out.println("2: VIDEOS_CLIENTE");
                //System.out.println("3: CLIENTES_FIELES");
                System.out.println("4: Salir");
                System.out.print("Opcion a Elegir:");
                comando=(teclado.nextLine());

                //COMANDO 1
                if (comando.equals("NUMERO_DESCARGAS_VIDEO") ||comando.equals("1") ){

                    for (int i=0; i <ListaVideosYSusServidores.size();i++ ){
                        nombreVideo=ListaVideosYSusServidores.get(i).getNombre();
                        //autor=ListaVideosYSusServidores.get(i).getAutor();
                        System.out.println();
                        System.out.printf( "%-20s %-10s %-5s  %n",nombreVideo, "descargas Total:",ListaVideosYSusServidores.get(i).getDescargas());

                        System.out.printf( "%-10s %-10s %n", "Servidor", "Numero de peticiones");
                        System.out.println("------------------------------------------------------");
                        for (int j=0; j<ListaVideosYSusServidores.get(i).getServidoresSize();j++ ){
                            x= ListaVideosYSusServidores.get(i).getServidor(j);
                            y=ListaVideosYSusServidores.get(i).getDescargasServidor(j);

                            System.out.printf( "%-10s %-10s %n", x, y);

                        }
                    }

                //COMANDO 2   
                }else if (comando.equals("VIDEOS_CLIENTE") ||comando.equals("2")) {
                    System.out.printf( "%-15s %-15s %-15s %n", "Servidor", "#Clientes", "#Actual");
                    for (int i=0; i<ListaServersD.size();i++ ){

                        System.out.printf( "%-15s %-15s %-15s %n", i, ListaServersD.get(i).getClientesEnviados(), ListaServersD.get(i).getClientesActuales());

                    }

                //COMANDO 3    
                }/*else if (comando.equals("CLIENTES_FIELES")||comando.equals("3")){
                    System.out.printf( "%-15s %-15s %n", "Nombre", "#Visitas");
                    for (int i=0; i<ListaClientes.size();i++){
                        x=ListaClientes.get(i).getVisitas();
                        if (x>1){
                            System.out.printf( "%-15s %-15s %n", ListaClientes.get(i).getNombre(), ListaClientes.get(i).getVisitas());
                        }
                    }
                }*/


            }while(!comando.equals("Salir") && !comando.equals("4") );

            centralServerDemonForClient.Stop();
            centralServerDemonForDServer.Stop();

       }
        catch(IOException | InterruptedException e ){
            System.err.println("Servidor ya Corriendo");
        } 
    }
    
}
