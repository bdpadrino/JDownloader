
package SCentral;
import Model.DownloadServer;
import Model.VideoForDownloadServer;
import Model.Video;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * CLASE QUE MANEJA EL PROTOCOLO DE COMUNICACION CON EL SERVIDOR DE DESCARGA
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */        
public class DownloadServerConnection extends Thread {
    
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private int idSession;
    private int indiceServidorDActual;
    private ArrayList<DownloadServer> DatosServer;
    private ArrayList<VideoForDownloadServer> ListaVideos;

    
     /**
     * CONTRUCTOR DE LA CLASE
     * @param socket
     * @param id
     * @param ListaServers
     * @param ListaVideosYSusServidores 
     */
    public DownloadServerConnection(Socket socket, int id,ArrayList<DownloadServer> ListaServers,ArrayList<VideoForDownloadServer> ListaVideosYSusServidores ) {   
        this.socket = socket;
        this.idSession = id;
        this.DatosServer=ListaServers;
        this.ListaVideos=ListaVideosYSusServidores;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * METODO USADO PARA CERRAR CONEXION
     */
    public void Stop() {
        try {
            if (socket!=null){socket.close();}
            DatosServer.get(indiceServidorDActual).inactivo();
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    
     /**
     * METODO QUE REGISTRA EN SC LA LISTA DE VIDEOS QUE TIENE CADA SERVIDOR DE DESCARGA
     * @param listaVideosDelServerD
     * @param ListaVideos
     * @param ServidorDeDescarga 
     */   
    public void LecturaDeSD (String listaVideosDelServerD,ArrayList <VideoForDownloadServer> ListaVideos, int ServidorDeDescarga) {        
       String [] fileWithExtensionList = listaVideosDelServerD.split("\n");
       String[] pieceOfFile;
       int i =0;  //Empieza en 1 dado que en 0 lo que hay es /path/videos:
       Video Video;

       while(i< fileWithExtensionList.length) {
            pieceOfFile=((String)fileWithExtensionList[i]).split("\\.");
            if (pieceOfFile.length == 2) {
                Video = new Video(pieceOfFile[0]);
                int indiceVideo = (ListaVideos).indexOf(new VideoForDownloadServer(Video));
                if (indiceVideo == -1) {
                   ArrayList <Integer> lista= new ArrayList <Integer>();
                   lista.add(ServidorDeDescarga);
                   VideoForDownloadServer videoYServidor= new  VideoForDownloadServer(Video,lista); 
                   //si el video no esta en la lista lo agrega
                   ListaVideos.add( videoYServidor ); 
                   System.out.println("              Video nuevo registrado: "+ pieceOfFile[0]);
                } else { 
                   ListaVideos.get(indiceVideo).addServidorD(ServidorDeDescarga );
                   System.out.println("              Video ya registrado: "+ pieceOfFile[0]);
                }
            }
            i++;
        }
    }
    
    /**
     * METODO USADO PARA SINCRONIZAR VIDEOS DE LOS SERVIDORES DE DESCARGA
     * @return 
     */
    public ArrayList<VideoForDownloadServer> obtenerDiferencia() {
        
        /* SYNC DOWNLOAD SERVERS */
        ArrayList<VideoForDownloadServer> syncDownloadList = new ArrayList<VideoForDownloadServer>();
        for (final VideoForDownloadServer videoYServidores : ListaVideos) {
            if (!videoYServidores.getListaServidoresD().contains(idSession)) {
                syncDownloadList.add(new VideoForDownloadServer(videoYServidores.getVideo(),videoYServidores.getListaServidoresD()));
            }
        }

        if (syncDownloadList != null && !syncDownloadList.isEmpty()) {
            System.out.println("VAMOS A SINCRONIZAR");
            for (final VideoForDownloadServer videoYServidores : syncDownloadList) {
                // Here your room is available
                System.out.println("Nombre: " + videoYServidores.getNombre());
                System.out.print("Lista de servidores: ");

                for (final int idServer : videoYServidores.getListaServidoresD()) {
                    System.out.print(idServer);
                }
                System.out.print("\n");
            }
        }
        
        return syncDownloadList;
        /* END SYNC DOWNLOAD SERVERS */
    }
    @Override
    public void run() {
        String recibido = "";
        String [] instruccion;
  
        
        try {
           
            indiceServidorDActual=dis.readInt(); // lee el indice y identificador del servidor de descarga en la lista DatosServer
            
            int puertoSDEscucha=dis.readInt();
            System.out.println(socket.getInetAddress().getHostAddress());
            DownloadServer ServidorDeDescarga;
           
            if (indiceServidorDActual==-1){
                ServidorDeDescarga= new DownloadServer (socket.getInetAddress().getHostAddress(),puertoSDEscucha);
                ServidorDeDescarga.activo();
                DatosServer.add(ServidorDeDescarga);
                indiceServidorDActual= DatosServer.size() -1; //asigna el indice del servidor de descarga 
                
            }else {
                DatosServer.get(indiceServidorDActual).actualizarIpYPuerto(socket.getInetAddress().getHostAddress(),puertoSDEscucha);
                ServidorDeDescarga=DatosServer.get(indiceServidorDActual);
                ServidorDeDescarga.activo();
            }

            dos.writeInt(indiceServidorDActual);
            String listaVideosDelServerD=dis.readUTF();

           
            LecturaDeSD (listaVideosDelServerD,ListaVideos,indiceServidorDActual);
            System.out.println("Leyo los videos");
            //Ordenar videos por nombre del video
            Collections.sort(ListaVideos, (VideoForDownloadServer b1, VideoForDownloadServer b2) -> b1.getNombre().compareTo(b2.getNombre()));        
           
            dos.writeUTF("Bienvenido al sistema ");
            
            do {
                try{recibido = dis.readUTF();} catch(EOFException e){};
                instruccion= recibido.split(" ", 2); 
                System.out.println(instruccion[0]+" "+instruccion.length);
                
                if(instruccion[0].equals("hola")){
                    System.out.println("El cliente con idSesion "+this.idSession+" saluda");
                   try{ dos.writeUTF("Hola");} catch(EOFException e){};
                
                }else if (instruccion[0].equals("descarga") ||instruccion[0].equals("d") ) { 
                    System.out.println(instruccion.length);
                    if (instruccion.length!=2) {dos.writeUTF("Instruccion incompleta");
                    } else {
                        
                        dos.writeUTF("Descargar");
                         System.out.println("tamano lista: "+ListaVideos.size());
                       // System.out.println(ListaVideos.get(3).getNombre());
                        
                    }
                    
                    
                
                
                }else{
                    System.out.println("El cliente con idSesion "+this.idSession+" escribio un comando no valido");
                   try{  dos.writeUTF("comando invalido");}catch(EOFException e){System.out.println("EOFException OUT ");};
                }
            
            
            } while (!"fin".equals(instruccion[0]));
         System.out.println(" P ");
        } catch (IOException ex) {
            System.out.println("Se cerro la conexion con el servidor de descarga: "+ this.indiceServidorDActual+". ");
            Stop();
        }finally {
            Stop();
        }
        System.out.println("Se desconecto el ServidorD "+ this.indiceServidorDActual);
    }  
}
