
package Client;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.ArrayList;

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

                instruccion=teclado.nextLine();

                Instrucciones= instruccion.split("-");
               
                if ( "L".equals(Instrucciones[0])){
                    dataToServer.writeUTF("L");
                    //LEE EL TAMANO DE LA LISTA DE VIDEOS A RECIBIR
                    x = dataFromServer.readInt(); 
                    System.out.println(x);
                    System.out.printf( "%-40s %n", "Lista de Peliculas Disponibles");
                    System.out.println("***********************************");
                    for(int i=0; i<x; i++){
                        lectura =dataFromServer.readUTF();
                        Lectura= lectura.split("\\.",3);
                        System.out.printf( "%-40s %n", Lectura[0]); 
                    }
                    System.out.println("************************************");
                }
                
                else if ( ("D".equals(instruccion))){
                    System.out.print("Introduzca Nombre del Video:");
                    String Comando= teclado.nextLine();
                    dataToServer.writeUTF("a-"+Comando);
                    //LEE EL TAMANO DEL ARCHIVO
                    x = dataFromServer.readInt(); 
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
                            
                            //LLAMADO AL HILO PARA RECIBIR LA DESCARGA
                            ClientDownloadConnection clientDownloadConnection=new ClientDownloadConnection(0,indiceServ,Ip,puerto,Lectura[1],Lectura[2],clientId);
                            downloadList.add(clientDownloadConnection);
                            clientDownloadConnection.start();
                        }
                        else { 
                            System.out.println("El video no se encuentra en el sistema"+Lectura[1]);
                        }
                    }
                }
                    
                int i=0;
                while( i<downloadList.size()){
                    if(!downloadList.get(i).isAlive()){
                        if (downloadList.get(i).descargaTerminada()){
                            System.out.println("descarga finalizada");
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
                            System.out.println("Retomando Descarga");
                            dataToServer.writeUTF("dr-"+downloadList.get(i).getNombreVideo()+"-"+downloadList.get(i).getAutorVideo());
                            dataToServer.writeInt(downloadList.get(i).getIndiceServidor());
                            String respues=dataFromServer.readUTF();
                            if (respues.equals("ok")){
                                int indiceServ=dataFromServer.readInt();
                                int puerto=dataFromServer.readInt();
                                String Ip=dataFromServer.readUTF();
                                //RETOMANDO DESCARGA CON EL HILO QUE RECIBE EL ARCHIVO    
                                ClientDownloadConnection clientDownloadConnection=new ClientDownloadConnection(downloadList.get(i).descargadoDelArchivo(),indiceServ,Ip,puerto,downloadList.get(i).getNombreVideo(),downloadList.get(i).getAutorVideo(),clientId);
                                downloadList.remove(i);
                                downloadList.add(clientDownloadConnection);
                                clientDownloadConnection.start();
                            }
                            else { 
                                System.out.println("El video no esta disponible");
                            }
                        }
                    }
                    i++;
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
