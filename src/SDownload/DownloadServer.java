
package SDownload;
import Model.Video;
import Model.Client;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

class ServerDDemonCliente extends Thread {
    private ServerSocket ss;
    public int idSession;
    public ArrayList<ClientConnection> clientes ;
    private ArrayList<Video> ListaVideos ;
    private ArrayList<Client> ListaClientes;
    private String path;
    

    /**
     * Crea un ServerDemon Vacio
     */    
    public ServerDDemonCliente() {
    }
    
    /**
    * Crea un ServerDemon con las variables inicializadas
    * @param socket es el ServerSocket usado para recibir las peticiones (ya tiene el puerto asignado)
    */
    public ServerDDemonCliente (ArrayList<Video> ListaVideos,ArrayList<Client> ListaClientes,String path ) throws IOException {
        this.ss = new ServerSocket(0);
        this.clientes =new ArrayList<ClientConnection>();
        this.ListaVideos=ListaVideos;
        this.path=path;
        this.ListaClientes=ListaClientes;
    }
    /**
     * Da el numero de puerto del socket de escucha
     * @return retorna el # de puerto de escucha
     */
    public int getLocalPort(){return ss.getLocalPort();}
    
    /**
    * borra del ArrayList<Thread> clientes los hilos que se hallan desconectado
    *
    * @param
    */
    private void dropDisconectedSDD(){
        clientes.removeIf(p -> !p.isAlive());
}
    /**
    * borra del ArrayList<Thread> clientes los hilos que se hallan desconectado
    * @return cantidad de clientes en la lista 
    */  
    public int cantidadClientes(){
        this.dropDisconectedSDD();
        return clientes.size(); }
    
    /**
     * Detiene al ServerDDCliente y libera los sockets asociados
     * @throws IOException 
     */
    public void Stop() throws IOException{
        for (int i=0;i<clientes.size();i++){
            if (clientes.get(i)!=null){clientes.get(i).Close();};
        }
        
        this.stop();
        if (ss!= null){
            ss.close();
        }        
        
    }
    
    @Override
    public void run () {
                
        try {
            Socket socket;

            System.out.println("\t Demonio de clientes [OK]");
            this.idSession=0;
            ClientConnection clienteActual;
            
            while (true) {
               
                socket = ss.accept();
                clienteActual= new ClientConnection(socket, idSession,ListaVideos,ListaClientes,path);
                this.clientes.add(clienteActual);
                System.out.print("Demon esperar al cliente");
                clienteActual.start();
                this.idSession++;
                System.out.println("Nueva conexión cliente: "+socket);
            }
        } catch (IOException ex) {
            Logger.getLogger(DownloadServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {ss.close();} catch(IOException ex) {System.out.print("No logro cerrar el socket del Demonio");} }
    }
}

    




public class DownloadServer {
    
    private static ArrayList<SyncConnection>downloadList = new ArrayList<SyncConnection>();    
    
    public static void main(String args[]) throws IOException, InterruptedException {
        
        
    final String path="C:\\Videos";
    //final String ipServer= "192.168.250.1";
    final String ipServer= "localhost";
    final int puertoConeccionServer=10581;

    ServerDDemonCliente SDDaC=null;

    //Cargar lista de videos y sus historiales del txt 
    ArrayList<Video> ListaVideos= new ArrayList<Video>();
    
    //Cargar lista de Clientes y sus historiales del txt
    ArrayList <Client> ListaClientes=new ArrayList<Client>();        
   
    //Una vez cargado la lista de videos del txt de config verificamos la carpeta y agregamos cualquier otro
    String textoListaVideos = CargarVideosCarpeta (ListaVideos,path );

    //Leer meta data del SD
    int idServidorD=-1; // el -1 es cuando no han habido conexiones previas y se espera que el server te asigne un ID


    Scanner teclado = new Scanner(System.in);

    Socket sk;
  

    try {

        sk = new Socket(ipServer, puertoConeccionServer);

        DataOutputStream dos = new DataOutputStream(sk.getOutputStream());
        DataInputStream dis = new DataInputStream(sk.getInputStream());

        dos.writeInt(idServidorD);

        SDDaC= new ServerDDemonCliente(ListaVideos,ListaClientes,path);
        int puertoClientes=SDDaC.getLocalPort();
        dos.writeInt(puertoClientes);
        System.out.println("Puerto de escucha para Clientes: "+puertoClientes);

        dos.writeUTF(textoListaVideos); 

        idServidorD=dis.readInt();

        if (dis.readUTF().equals("Sync")) {
            System.out.println("Sincronizar......");
            int x=dis.readInt();
            System.out.println(x);
            ArrayList<String> videosADescargar=new ArrayList<String>();
            String lecturaList[];
            String lectura;
            for(int i=0; i<x; i++){
                lectura=dis.readUTF();
                lecturaList= lectura.split("\\.",3);
                if ("fin".equals(lecturaList[0])){break; }
                    videosADescargar.add("d-"+lecturaList[0]+"-"+lecturaList[1]);
                    System.out.printf( "%-40s %-20s %-30s %n", lecturaList[0], lecturaList[1],lecturaList[2]);
            }
            lecturaList=null;
            for(int i=0; i<videosADescargar.size(); i++){

                dos.writeUTF(videosADescargar.get(i));
                String respues=dis.readUTF();
                if (respues.equals("ok")){
                    int indiceServ=dis.readInt();
                    int puerto=dis.readInt();
                    String Ip=dis.readUTF();
                    lecturaList=videosADescargar.get(i).split("-",3);
                    System.out.println(lecturaList[1]+" "+lecturaList[2]);

                    SyncConnection cnd=new SyncConnection(0,indiceServ,Ip,puerto,lecturaList[1],lecturaList[2],String.valueOf(idServidorD));
                    downloadList.add(cnd);
                    cnd.start();
                }else { System.out.println("El video no esta dataFromServerponible"+lecturaList[1]);}

            }
            dos.writeUTF("fin");
        }
        System.out.println(dis.readUTF());
        SDDaC.start();

        String comando;
        String autor;
        String nombreVideo;
 
        Thread.sleep(1500);

        do {
            System.out.println("Opciones:");
            System.out.println("1: VIDEOS_DESCARGANDO");
            System.out.println("2: VIDEOS_DESCARGADOS");
            //System.out.println("3: CLIENTES_FIELES");
            System.out.println("4: Salir");
            comando=(teclado.nextLine());
            if (comando.equals("VIDEOS_DESCARGANDO") ||comando.equals("1") ){

                System.out.printf( "%-30s  %-6s %n", "Video" , "# descargando");
                System.out.println("-----------------------------------------------------------------");
                for (int i=0; i <ListaVideos.size();i++ ){
                    nombreVideo=ListaVideos.get(i).getNombre();
                    autor=ListaVideos.get(i).getAutor();
                     if (ListaVideos.get(i).getDescargando()>0){
                        System.out.printf( "%-30s %-6s %n", nombreVideo , ListaVideos.get(i).getDescargando());
                    }
                }
            }else if (comando.equals("VIDEOS_DESCARGADOS") ||comando.equals("2")) {

                System.out.printf( "%-30s  %-6s %n", "Video" , "# descargado");
                System.out.println("-----------------------------------------------------------------");
                for (int i=0; i <ListaVideos.size();i++ ){
                    nombreVideo=ListaVideos.get(i).getNombre();
                    autor=ListaVideos.get(i).getAutor();
                     if (ListaVideos.get(i).getDescargas()>0){
                        System.out.printf( "%-30s  %-6s %n", nombreVideo , ListaVideos.get(i).getDescargas());
                    }
                }
            }
            
            /*else if (comando.equals("CLIENTES_FIELES")||comando.equals("3")){
                System.out.printf( "%-30s  %-15s %n", "Nombre" , "# de descargas");
                System.out.println("---------------------------------------------------");
                for (int i=0; i <ListaClientes.size();i++ ){
                    nombreVideo=ListaClientes.get(i).getNombre();
                     if (ListaClientes.get(i).getVisitas()>0){
                        System.out.printf( "%-30s  %-15s %n", nombreVideo,ListaClientes.get(i).getVisitas());
                    }
                }            
            }*/
        } while(! ("adios".equals(comando)|| comando.equals("4")));

           
            
        } catch (IOException ex) {
            System.err.println("Error de conexion IOException");
            SDDaC.Stop();
            Thread.sleep(1000); 
            
        } 
        catch (InterruptedException e) {
             System.err.println("Error de conexion InterruptedException");
        }
        catch (Exception e) {
             System.err.println("Error de conexion Exception");
        }
        
          
    }
    
    public static String CargarVideosCarpeta (ArrayList<Video> ListaVideos, String path){

        Terminal t = new Terminal();
        //t.executeCommand("ls");

        System.out.println("Hola");
        System.out.println(path);
        //String ls= t.executeCommand("ls -1B "+path); //LINUX
        //String ls= t.executeCommand("dir /B " + path); //WINDOWS
        String ls= t.executeCommand("cmd /c dir /B " + path); //WINDOWS

        String [] Ls=ls.split("\n");
        String[] temp;
        ArrayList <String> temp2;
        int i =0;  //Empieza en 1 dado que en 0 lo que hay es /path/videos:
        int j=0;
        Video Video;
        //System.out.println(Ls.length);
        
        while(i< Ls.length){
            temp=((String)Ls[i]).split("\\.");
            System.out.println(Ls[i]);
            j=2;
            temp2=new ArrayList <String>();
            //System.out.println(temp.length);
            while(j<temp.length-1){
                System.out.println(temp[j]);
                temp2.add(temp[j]);
                j++;
            }
            if (temp.length>2){
                Video=new Video(temp[0],temp[1], temp2, Ls[i] );
                int indiceVideo =ListaVideos.indexOf(Video);
                if (indiceVideo==-1){ListaVideos.add(Video);
                System.out.println("              Se registro el video: "+ temp[0]);} //si el video no esta en la lista lo agrega
                else{ System.out.println("              Video ya registrado: "+ temp[0]);}
            }
            i++;
        }
        System.out.println("Cantidad de videos: "+ListaVideos.size());
        return ls;
    }
    
}
