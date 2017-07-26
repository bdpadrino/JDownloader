
package SCentral;
import Model.DownloadServer;
import Model.VideoForDownloadServer;
import Model.Video;
import Model.Client;
import java.io.*;
import java.util.*;


/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class CentralServer {

    
    public static void main(String args[]) throws IOException, InterruptedException {
        final int puertoEscuchaClientes= 10580;
        final int puertoEscuchaServidoresD= 10581;
        
        //Cargar lista de clientes nombres
        //Cargar lista de cantidad de visitas de clientes 
        ArrayList<Client> ListaClientes= new ArrayList<Client>();
        
        //Cargar lista de servidores de descarga
        ArrayList<DownloadServer> ListaServersD=new ArrayList<DownloadServer>();

        //Cargar lista de libros
        ArrayList<VideoForDownloadServer> ListaLibrosYSusServidores=new ArrayList<VideoForDownloadServer>() ;
        
        CSDemonForDownloadServer centralServerDemonForDServer= new CSDemonForDownloadServer(puertoEscuchaServidoresD,ListaServersD,ListaLibrosYSusServidores);
        centralServerDemonForDServer.start();
        
        
        
        
        CSDemonForClient centralServerDemonForClient=new CSDemonForClient(puertoEscuchaClientes,ListaClientes,ListaServersD,ListaLibrosYSusServidores);
        centralServerDemonForClient.start();
        Scanner teclado = new Scanner(System.in);
        String comando;
        Video LibroTemp;
        String autor;
        String nombreLibro;
        String sTemp;
        int x;
        int y;
        Thread.sleep(1500);
        do{
            System.out.println("Opciones:");
            System.out.println("1: DESCARGASxSERVIDOR");
            System.out.println("2: CLIENTESxSERVIDOR");
            System.out.println("3: CLIENTES_FIELES");
            System.out.println("4: Salir");
            comando=(teclado.nextLine());
            if (comando.equals("DESCARGASxSERVIDOR") ||comando.equals("1") ){
                
                for (int i=0; i <ListaLibrosYSusServidores.size();i++ ){
                    nombreLibro=ListaLibrosYSusServidores.get(i).getNombre();
                    autor=ListaLibrosYSusServidores.get(i).getAutor();
                    System.out.println();
                    System.out.printf( "%-20s %-10s %-10s %-10s %-5s  %n",nombreLibro," Autor: ", autor, "descargas Total:",ListaLibrosYSusServidores.get(i).getDescargas());
                    
                    System.out.printf( "%-10s %-10s %n", "Servidor", "Numero de peticiones");
                    System.out.println("------------------------------------------------------");
                    for (int j=0; j<ListaLibrosYSusServidores.get(i).getServidoresSize();j++ ){
                        x= ListaLibrosYSusServidores.get(i).getServidor(j);
                        y=ListaLibrosYSusServidores.get(i).getDescargasServidor(j);
                      //  if (y!=0){
                            System.out.printf( "%-10s %-10s %n", x, y);
                      //  }
                    }
                    

                    
                }
            }else if (comando.equals("CLIENTESxSERVIDOR") ||comando.equals("2")) {
                System.out.printf( "%-15s %-15s %-15s %n", "Servidor", "#Clientes", "#Actual");
                for (int i=0; i<ListaServersD.size();i++ ){
                    
                    System.out.printf( "%-15s %-15s %-15s %n", i, ListaServersD.get(i).getClientesEnviados(), ListaServersD.get(i).getClientesActuales());
                  //  numClientesEnServidorActual=ListaServersD.get(indiceServidorD).getClientesActuales();    
                    
                }
                
                
            }else if (comando.equals("CLIENTES_FIELES")||comando.equals("3")){
                System.out.printf( "%-15s %-15s %n", "Nombre", "#Visitas");
                for (int i=0; i<ListaClientes.size();i++){
                    x=ListaClientes.get(i).getVisitas();
                    if (x>1){
                        System.out.printf( "%-15s %-15s %n", ListaClientes.get(i).getNombre(), ListaClientes.get(i).getVisitas());
                    }
                }
            }
            
            /*
            Thread.sleep(10000);
            System.out.println("Clientes    : #Seciones: "+centralServerDemonForClient.idSession+" #Clientes conectados "+centralServerDemonForClient.cantidadClientes());
            System.out.println("ServidoresD : #Seciones: "+centralServerDemonForDServer.idSession+" #Clientes conectados "+centralServerDemonForDServer.cantidadClientes());
            */

        }while(!comando.equals("Salir") && !comando.equals("4") );

        centralServerDemonForClient.Stop();
        centralServerDemonForDServer.Stop();
     //   Thread.sleep(1000);
    //    String path="/home/eliud/Documents/Servidor";
       // EscrituraYLectura Funciones= new EscrituraYLectura();
        // Aqui guardar toda la informacion
        //Guardar lista de servidores de descarga
        //ListaClientes ArrayList<DatosCliente>();
      //  Funciones.SaveDatosClientes(ListaClientes,path+"/"+"DatosClientes.txt");
        //Guardar lista de servidores de descarga
        //ListaServersD  ArrayList<DatosServerDescarga>();

        //Guardar lista de libros
        //ListaLibrosYSusServidores ArrayList<LibroYServidores>() ;
        
    }
    
}
