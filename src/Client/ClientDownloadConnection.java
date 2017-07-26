
package Client;


import java.io.*;
import java.net.Socket;
import java.util.logging.*;

/**
 * CLASE QUE RECIBE EL ARCHIVO A DESCARGAR
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class ClientDownloadConnection extends Thread {
    private String Ip;
    private int puerto;
    private Socket socket;
    private long tamañoArchivo;
    private long descargadoDelArchivo;
    private String idCliente;
    private String nombreVideo;
    private String autorVideo;
    private int indiceServidor;
    private long byteInicio;
    private FileOutputStream out = null;
    private DataInputStream dis=null;
          
    private DataOutputStream dos = null;
    public ClientDownloadConnection(long byteInicio, int indiceServ,String Ip, int puerto,String nombreVideo,String autorVideo,String id) {
    this.Ip= Ip;
    this.puerto=puerto;
    this.idCliente= id;
    this.nombreVideo=nombreVideo;
    this.autorVideo=autorVideo;
    this.indiceServidor=indiceServ;
    this.byteInicio=byteInicio;
    try{
        this.socket=new Socket (Ip,puerto);
    }catch (IOException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    this.tamañoArchivo=-1;
    this.descargadoDelArchivo=byteInicio;
    }
    public String getNombreVideo(){return nombreVideo;}
    public String getAutorVideo(){return autorVideo;}
    public String getIp() {return Ip;}
    public int getPuerto() {return puerto;}    
    public int getIndiceServidor(){return indiceServidor;}
    public long getByteInicio(){return byteInicio;}
    
    /**
     * 
     * @return true si la descarga finalizo
     */
    public boolean descargaTerminada(){return tamañoArchivo==descargadoDelArchivo;}
    /**
     * 
     * @return cantidad de bytes descargados hasta ahora
     */
    public long descargadoDelArchivo(){return descargadoDelArchivo; }
    
    /**
     * METODO PARA CERRAR LA CONEXION
     * @throws IOException 
     */
    public void Stop()throws IOException{
        if (socket != null) {socket.close();}
        if (out != null) {out.close();}
        if (dis!=null)  {dis.close();}
        if (dos!=null) {dos.close();}
        this.stop();        
        
    }
    
    /**
     * HILO PARA REALIZAR MULTIPLES DESCARGAS
     */
    @Override
    public void run() {

        String nombreArchivo="";

        try {
        dos = new DataOutputStream(socket.getOutputStream());
        dis= new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }
        byte[] bytes = new byte[512]; // 1/2 kb

        int count;

        try {
            dos.writeLong(byteInicio);
            dos.writeUTF (idCliente);
            dos.writeUTF(this.nombreVideo);
            dos.writeUTF(this.autorVideo);
            nombreArchivo =dis.readUTF();
            try {  
                if (byteInicio==0){
                out = new FileOutputStream("C:\\Videos\\Cliente\\"+nombreArchivo);
                }else {out = new FileOutputStream("C:\\Videos\\Cliente\\"+nombreArchivo, true); }

            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }
            tamañoArchivo=dis.readLong();
            descargadoDelArchivo= this.byteInicio;

            int  display=10;
            while ((count = dis.read(bytes)) > 0) {
                if ( (descargadoDelArchivo*100/tamañoArchivo)>display){
                    System.out.println(this.nombreVideo+" "+(descargadoDelArchivo*100/tamañoArchivo)+"%");
                    display=display+10;
                }

                descargadoDelArchivo= descargadoDelArchivo +count;
                out.write(bytes, 0, count);
            }
            System.out.println(this.nombreVideo+" "+(descargadoDelArchivo*100/tamañoArchivo)+"%");
        } catch (IOException e) {
            System.out.println(" ");
            System.err.println("Upps se callo la descarga. ");
        }finally
        {
            try
            {
                if (socket != null) {socket.close();}
                if (out != null) {out.close();}
                if (dis!=null)  {dis.close();}
                if (dos!=null) {dos.close();}

                this.stop();
            }catch (IOException e)
            {       }
        }

        System.out.println(" Descarga finalizada: "+this.nombreVideo);        
    }
}
