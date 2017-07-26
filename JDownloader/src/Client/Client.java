
package Client;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.ArrayList;

/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class Client {
    
    
    private static String clientId                                 = "";
    //public static String ipServer                                = "192.168.0.105";
    private static final String ipServer                           = "localhost";
    private static final int portServer                            = 10580; 
    private static Socket clientSocket                             = null;
    private static DataOutputStream dataToServer                   = null;
    private static DataInputStream dataFromServer                  = null;
    private static ArrayList<ClientDownloadConnection>downloadList = new ArrayList<ClientDownloadConnection>();
    
    public static void Stop() throws IOException {
        if (clientSocket != null ) { clientSocket.close(); }
        if (dataFromServer != null ) { dataFromServer.close(); }
        if (dataToServer != null ) { dataToServer.close(); }
        for (int i=0; i<downloadList.size(); ) {
            downloadList.get(i).Stop();
        }
    }
    
    public static void main(String[] args)  throws InterruptedException {
        Scanner teclado    = new Scanner(System.in);
        System.out.println("Introdusca su ID: ");
        clientId           = (teclado.nextLine());
        String instruccion = "";
        String [] Instrucciones;
        String respuesta   ="";
       
        try {
            // Cargar la lista de descargas
            //downloadList=new ArrayList<clienteHiloDescarga>() ;
            
            
            clientSocket = new Socket(ipServer, portServer);
            dataToServer = new DataOutputStream(clientSocket.getOutputStream());
            dataFromServer = new DataInputStream(clientSocket.getInputStream());
            dataToServer.writeUTF(clientId); 
            System.out.println(dataFromServer.readUTF()); 
            
            int x; //variable temporal para toda clase de cosas
            int y;//variable temporal para toda clase de cosas
            String lectura; 
            String[] Lectura;
            String STemp="";
            do {
                 System.out.println("b-n/a/g-XX  : busqueda de XX este siendo nombre del video, autor o genero");
                 System.out.println("c-a/g-XX    : Solicita descargar todataToServer losvideos con XX de autor o genero");
                 System.out.println("l           : Muestra todataToServer los videos");
                 System.out.println(clientId + " introdusca instruccion: ");

                 instruccion=teclado.nextLine();

                 Instrucciones= instruccion.split("-");
                 if ( "b".equals(Instrucciones[0]) || "buscar".equals(Instrucciones[0]) ){


                     if ( ("n".equals(Instrucciones[1]) || "nombre".equals(Instrucciones[1]))&&Instrucciones.length>2 ){
                             dataToServer.writeUTF(instruccion);
                             x=dataFromServer.readInt(); //lee el tamano de la lista de videos que va a recibir
                             System.out.println(x);
                             System.out.printf( "%-40s %n", "Nombre");
                             System.out.println("-----------------------");
                             for(int i=0; i<x; i++){
                                 lectura =dataFromServer.readUTF();
                                 Lectura= lectura.split("\\.",3);
                                 if ("fin".equals(Lectura[0])){break; }
                                 System.out.printf( "%-40s %n", Lectura[0]); 
                             }
                             System.out.println();

                         }else if (("a".equals(Instrucciones[1]) || "autor".equals(Instrucciones[1]))&&Instrucciones.length>2){

                             dataToServer.writeUTF(instruccion);
                             x=dataFromServer.readInt(); //lee el tamano de la lista de videos que va a recibir
                             System.out.println(x);
                             System.out.printf( "%-40s %n", "Nombre");
                             System.out.println("-----------------------");
                             for(int i=0; i<x; i++){
                                 lectura =dataFromServer.readUTF();
                                 Lectura= lectura.split("\\.",3);
                                 if ("fin".equals(Lectura[0])){break; }
                                 System.out.printf( "%-40s %n", Lectura[0]); 
                             }
                             System.out.println();
                         }else if (("g".equals(Instrucciones[1]) || "genero".equals(Instrucciones[1]))&&Instrucciones.length>2){
                             dataToServer.writeUTF(instruccion);
                             x=dataFromServer.readInt(); //lee el tamano de la lista de videos que va a recibir
                             System.out.println(x);
                             System.out.printf( "%-40s %n", "Nombre");
                             System.out.println("-----------------------");
                             for(int i=0; i<x; i++){
                                 lectura =dataFromServer.readUTF();
                                 Lectura= lectura.split("\\.",3);
                                 if ("fin".equals(Lectura[0])){break; }
                                 System.out.printf( "%-40s %n", Lectura[0]); 
                             }
                             System.out.println();                           
                         }

                 }else if ( "l".equals(Instrucciones[0]) || "lista".equals(Instrucciones[0]) ){
                     dataToServer.writeUTF(instruccion);
                     x=dataFromServer.readInt(); //lee el tamano de la lista de videos que va a recibir
                     System.out.println(x);
                     System.out.printf( "%-40s %n", "Nombre");
                     System.out.println("-----------------------");
                     for(int i=0; i<x; i++){
                         lectura =dataFromServer.readUTF();
                         Lectura= lectura.split("\\.",3);
                         System.out.printf( "%-40s %n", Lectura[0]); 
                    }
                      System.out.println();
                 }else if ( ("d".equals(Instrucciones[0]) || "descarga".equals(Instrucciones[0]))&&Instrucciones.length>2 ){
                     dataToServer.writeUTF(instruccion);
                     String respues=dataFromServer.readUTF();
                     if (respues.equals("ok")){

                         int indiceServ=dataFromServer.readInt();
                         int puerto=dataFromServer.readInt();
                         String Ip=dataFromServer.readUTF();

                         ClientDownloadConnection clientDownloadConnection=new ClientDownloadConnection(0,indiceServ,Ip,puerto,Instrucciones[1],Instrucciones[2],clientId);
                         downloadList.add(clientDownloadConnection);
                         clientDownloadConnection.start();
                     }else { System.out.println("El video no se encuentra en el sistema");}
                 }else if ( "c".equals(Instrucciones[0]) || "coleccion".equals(Instrucciones[0]) ){
                     if ( ("a".equals(Instrucciones[1]) || "autor".equals(Instrucciones[1]))&& Instrucciones.length>=3 ){
                             dataToServer.writeUTF("b-a-"+Instrucciones[2]);
                             x=dataFromServer.readInt(); //lee el tamano maximo
                             ArrayList<String> videosADescargar=new ArrayList<String>();
                             //System.out.println(x);

                             //System.out.printf( "%-40s %-20s %-30s %n", "Nombre", "Autor","Generos");
                             //System.out.println("---------------------------------------------------------------------------------");
                             for(int i=0; i<x; i++){
                                 lectura =dataFromServer.readUTF();
                                 Lectura= lectura.split("\\.",3);
                                 if ("fin".equals(Lectura[0])){break; }
                                     videosADescargar.add("d-"+Lectura[0]+"-"+Lectura[1]);
                                     //System.out.printf( "%-40s %-20s %-30s %n", Lectura[0], Lectura[1],Lectura[2]);
                             }
                             Lectura=null;
                             for(int i=0; i<videosADescargar.size(); i++){

                                 dataToServer.writeUTF(videosADescargar.get(i));
                                 String respues=dataFromServer.readUTF();
                                 if (respues.equals("ok")){
                                     int indiceServ=dataFromServer.readInt();
                                     int puerto=dataFromServer.readInt();
                                     String Ip=dataFromServer.readUTF();
                                     Lectura=videosADescargar.get(i).split("-",3);
                                     System.out.println(Lectura[1]+" "+Lectura[2]);

                                     ClientDownloadConnection clientDownloadConnection=new ClientDownloadConnection(0,indiceServ,Ip,puerto,Lectura[1],Lectura[2],clientId);
                                     downloadList.add(clientDownloadConnection);
                                     clientDownloadConnection.start();
                                 }else { System.out.println("El video no se encuentra en el sistema"+Lectura[1]);}

                             }
                     }else if ( ( "g".equals(Instrucciones[1]) || "genero".equals(Instrucciones[1]))&& Instrucciones.length>=3 ){
                         dataToServer.writeUTF("b-g-"+Instrucciones[2]);
                         x=dataFromServer.readInt(); //lee el tamano maximo
                         ArrayList<String> videosADescargar=new ArrayList<String>();

                         for(int i=0; i<x; i++){
                             lectura =dataFromServer.readUTF();
                             Lectura= lectura.split("\\.",3);
                             if ("fin".equals(Lectura[0])){break; }
                                videosADescargar.add("d-"+Lectura[0]+"-"+Lectura[1]);
                         }
                         Lectura=null;
                         for(int i=0; i<videosADescargar.size(); i++){

                             dataToServer.writeUTF(videosADescargar.get(i));
                             String respues=dataFromServer.readUTF();
                             if (respues.equals("ok")){
                                 int indiceServ=dataFromServer.readInt();
                                 int puerto=dataFromServer.readInt();
                                 String Ip=dataFromServer.readUTF();
                                 Lectura=videosADescargar.get(i).split("-",3);
                                 System.out.println(Lectura[1]+" "+Lectura[2]);

                                 ClientDownloadConnection clientDownloadConnection=new ClientDownloadConnection(0,indiceServ,Ip,puerto,Lectura[1],Lectura[2],clientId);
                                 downloadList.add(clientDownloadConnection);
                                 clientDownloadConnection.start();
                             }else { System.out.println("El video no esta dataFromServerponible"+Lectura[1]);}

                         }

                     }
                 }



                 int i=0;
                 while( i<downloadList.size()){
                       if(!downloadList.get(i).isAlive()){
                         if (downloadList.get(i).descargaTerminada()){
                             System.out.println("descarga terminada");
                             dataToServer.writeUTF("actualizar");
                             STemp=downloadList.get(i).getIp();
                             dataToServer.writeUTF(STemp);
                             dataToServer.writeInt(downloadList.get(i).getPuerto());
                             dataToServer.writeUTF(downloadList.get(i).getNombreVideo());
                             dataToServer.writeUTF(downloadList.get(i).getAutorVideo());
                             downloadList.remove(i);
                             i--;
                         }else {
                             System.out.println("Ups se callo la descarga de: "+downloadList.get(i).getNombreVideo());
                             System.out.println("Procedemos a recuperarla");
                             dataToServer.writeUTF("dr-"+downloadList.get(i).getNombreVideo()+"-"+downloadList.get(i).getAutorVideo());
                             dataToServer.writeInt(downloadList.get(i).getIndiceServidor());
                             String respues=dataFromServer.readUTF();
                             if (respues.equals("ok")){
                                 int indiceServ=dataFromServer.readInt();
                                 int puerto=dataFromServer.readInt();
                                 String Ip=dataFromServer.readUTF();

                                 ClientDownloadConnection clientDownloadConnection=new ClientDownloadConnection(downloadList.get(i).descargadoDelArchivo(),indiceServ,Ip,puerto,downloadList.get(i).getNombreVideo(),downloadList.get(i).getAutorVideo(),clientId);
                                 downloadList.remove(i);
                                 downloadList.add(clientDownloadConnection);
                                 clientDownloadConnection.start();
                             }else { System.out.println("El video no esta dataFromServerponible");}
                         }

                     }
                     i++;
                 }
                 System.out.println("Descargando : "+downloadList.size()+" videos");


            } while(!("adios".equals(Instrucciones[0]) || "salir".equals(Instrucciones[0]) || "exit".equals(Instrucciones[0]) ));
        } catch (IOException ex) {
            System.out.println("El servidor central cerro");
        } finally { 
            try{
                System.out.println("Cerro el cliente");
                Stop();

            } catch (IOException e){ } 
        }
    }
}