
package SDownload;

import Model.Video;
import Model.Client;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.ArrayList;

public class ClientConnection extends Thread {
    
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private int idSession;
    private String idUsuario;
    private ArrayList<Video> ListaVideos ;
    private String path;
    private int indiceVideo;
    private ArrayList<Client> ListaClientes;
    private long byteInicio;
    
    public ClientConnection(Socket socket, int id,ArrayList<Video> ListaVideos,ArrayList<Client> ListaClientes, String path) {
        this.socket = socket;
        this.idSession = id;
        this.ListaVideos=ListaVideos;
        this.ListaClientes=ListaClientes;
        this.path=path;
        try {
            this.dos = new DataOutputStream(socket.getOutputStream());
            this.dis = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * METODO PARA CERRAR CONEXIONES
     */
    public void Close() {
        try {            
            if (socket != null) {socket.close();}
            if (dis!=null)  {dis.close();}
            if (dos!=null) {dos.close();}             
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {

        
        try {
           // accion = dis.readUTF();
            byteInicio=dis.readLong();
            this.idUsuario=dis.readUTF();
            Client ClienteActual= new Client(this.idUsuario,0);
            int indiceCliente= ListaClientes.indexOf(ClienteActual);
            if (indiceCliente==-1){
                ListaClientes.add(ClienteActual );
            }else {
                ClienteActual=ListaClientes.get(indiceCliente);
            }
            String nombreVideo= dis.readUTF();
            String autor= dis.readUTF();
            System.out.println("El usuario "+this.idUsuario+" solicita "+nombreVideo+" de "+autor);
            this.indiceVideo=ListaVideos.indexOf(new Video(nombreVideo,autor));
            
            ListaVideos.get(this.indiceVideo).sumarDescargando();
            
            String pathVideo=this.path+"/"+(ListaVideos.get(this.indiceVideo).getNombreArchivo());
            dos.writeUTF(ListaVideos.get(this.indiceVideo).getNombreArchivo());
            //System.out.println("Indice del libro "+indiceVideo);
            
            File file = new File(pathVideo);
            // Get the size of the file
            long length = file.length();
            long enviadoDelArchivo=byteInicio;
            byte[] bytes = new byte[ 512]; // 1/2 kb
            
            
            FileInputStream in = new FileInputStream(file);
            in.skip(byteInicio);
            dos.writeLong(length);
            //DataOutputStream out = new DataOutputStream(socket.getOutputStream());
           // DataOutputStream out = dos;
            int count;
            while ((count = in.read(bytes)) > 0) {
             //   System.out.println(""+count);
                dos.write(bytes, 0, count);
                enviadoDelArchivo= enviadoDelArchivo +count;
                Thread.sleep(50);
            }
            System.out.println("Enviado: "+enviadoDelArchivo +" Tamano: "+length);
            if (enviadoDelArchivo==length){
                ListaVideos.get(this.indiceVideo).sumarDescarga();
                
                ClienteActual.sumarCliente();
            }
           // System.out.println("  ");
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Se callo la conexion: "+ this.idUsuario+" session "+idSession+". ");
            Close();
        }finally {Close();}
        System.out.println("Se desconecto "+ this.idUsuario+" session "+idSession+". ");
        
    }  
}
