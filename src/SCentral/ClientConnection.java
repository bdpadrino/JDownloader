
package SCentral;
import Model.DownloadServer;
import Model.VideoForDownloadServer;
import Model.Video;
import Model.Client;
import java.io.*;
import java.net.*;
import java.util.logging.*;
import java.util.ArrayList;

/**
 * CLASE QUE MANEJA EL PROTOCOLO DE COMUNICACION CON EL CLIENTE 
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class ClientConnection extends Thread {
    
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    private int idSessio;
    private String idUsuario;
    private ArrayList<Client> Clientes ;
    private ArrayList<DownloadServer> DatosServer;
    private ArrayList<VideoForDownloadServer> ListaVideosYSusServidores;
    public ArrayList<Integer> DescargasEnServidor;

    public ClientConnection(Socket socket, int id,ArrayList<Client> Clientes, ArrayList<DownloadServer> DatosServer,ArrayList<VideoForDownloadServer> ListaVideosYSusServidores) {
        this.socket = socket;
        this.idSessio = id;
        this.Clientes=Clientes;
        this.DatosServer=DatosServer;
        this.ListaVideosYSusServidores =ListaVideosYSusServidores;
        this.DescargasEnServidor=new ArrayList<Integer> ();
        for (int i =0; i< DatosServer.size(); i++){
            DescargasEnServidor.add(0);
        }
    
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * METODO PARA CERRAR CONEXIONES
     */
    public void Stop() {
        try {
            if (socket!=null){socket.close();}
            int t;
            for (int i=0; i<DescargasEnServidor.size();i++){
                t=this.DatosServer.get(i).getClientesActuales()-DescargasEnServidor.get(i);
                if (t>=0){
                this.DatosServer.get(i).restarCliente(DescargasEnServidor.get(i));
                }
            }
        } catch (IOException ex) {
            
        }
    }
    
    //HILO PARA CONEXIONES CON EL CLIENTE
    @Override
    public void run() {
        String instruccion="";
        String [] Instrucciones;
        
        int indiceCliente;
        try {
           
            this.idUsuario=dis.readUTF();
            Client Id= new Client(this.idUsuario);
            indiceCliente=Clientes.indexOf(Id);
            // Se usa el nombre del usuario para registrarlo o sumar a su contador de visitas
            if (indiceCliente== -1) {
                Clientes.add(Id);
                System.out.println("Se registro: "+this.idUsuario+" Bienvenido al sistema");
                try{ dos.writeUTF("Bienvenido al sistema ");} catch(EOFException e){System.out.println("Error: Bienvenida ");};}
            else {
                System.out.println("Se conecto "+this.idUsuario+" # de visitas anteriores: "+(Clientes.get(indiceCliente)).getVisitas());
                Clientes.get(indiceCliente).sumarCliente();
                try{ dos.writeUTF("Bienvenido al Sistema "+this.idUsuario+" # de visitas anteriores: "+(Clientes.get(indiceCliente)).getVisitas());} catch(EOFException e){System.out.println("Error: Bienvenida ");};

            }
            VideoForDownloadServer libroTemp; 
            int x;
            int y;
            String STemp="";
            
            do {
                instruccion= dis.readUTF();
                System.out.println(instruccion);
                Instrucciones= instruccion.split("-");
                if ("a".equals(Instrucciones[0]) ){
                    System.out.println("paso");
                    x=ListaVideosYSusServidores.size();
                    dos.writeInt(x);
                    for(int i=0; i<x; i++){
                        libroTemp=ListaVideosYSusServidores.get(i);
                        if ((libroTemp.getAutor()).equals(Instrucciones[1])){
                            STemp=libroTemp.getNombre()+"."+libroTemp.getAutor()+".";
                            for(int j=0; j<libroTemp.getGenerosSize(); j++){
                                STemp=STemp+libroTemp.getGenero(j)+" ";
                            }
                        dos.writeUTF(STemp);
                        }

                    }
                    dos.writeUTF("fin");
                }else if ( "l".equals(instruccion) ){
                    x=ListaVideosYSusServidores.size();
                    dos.writeInt(x);
                    for(int i=0; i<x; i++){
                        libroTemp=ListaVideosYSusServidores.get(i);
                        STemp=libroTemp.getNombre()+"."+libroTemp.getAutor()+".";       
                        for(int j=0; j<libroTemp.getGenerosSize(); j++){
                            STemp=STemp+libroTemp.getGenero(j)+" ";
                        }
                        
                        dos.writeUTF(STemp);
                    }
                } else if ( "d".equals(Instrucciones[0]) || "descarga".equals(Instrucciones[0]) ) {
                    x=ListaVideosYSusServidores.indexOf((new VideoForDownloadServer(new Video(Instrucciones[1],Instrucciones[2]))));
                    if (x>=0){
                        
                        String IpSD="";
                        int puertoSD=0;
                        int indiceServidorD;
                        y=0;
                        int indiceServidorDConMenosClientes=-1;
                        int menorCantidadDeClientes=Integer.MAX_VALUE;
                        int lenghtListaDeServidoresDelLibro=ListaVideosYSusServidores.get(x).getServidoresSize();
                        int numClientesEnServidorActual;
                        while(!(menorCantidadDeClientes==0) && y<lenghtListaDeServidoresDelLibro ){
                            indiceServidorD=ListaVideosYSusServidores.get(x).getServidor(y);

                            numClientesEnServidorActual=DatosServer.get(indiceServidorD).getClientesActuales();
                            if (numClientesEnServidorActual<menorCantidadDeClientes && DatosServer.get(indiceServidorD).getEstado()){
                                menorCantidadDeClientes=numClientesEnServidorActual;
                                indiceServidorDConMenosClientes=indiceServidorD;

                            }
                            y++;
                        }
                        if (indiceServidorDConMenosClientes>=0){
                            DatosServer.get(indiceServidorDConMenosClientes).sumarCliente(); //Se agrega un cliente al contador de clientes del ServidorD
                            //ListaLibrosYSusServidores.get(x).sumarDescarga(indiceServidorDConMenosClientes); // Al libro se le agrega uno al contador descargando
                            ListaVideosYSusServidores.get(x).sumarDescarga(indiceServidorDConMenosClientes); // Al libro se le agrega uno al contador descargando
                            System.out.println(idUsuario+" descarga "+ Instrucciones[1]+" con el server # "+ indiceServidorDConMenosClientes);
                            
                            if (DescargasEnServidor.size()<DatosServer.size()){
                                int m=DescargasEnServidor.size();
                                for (int i=m;i < DatosServer.size();i++ )
                                    DescargasEnServidor.add(0);
                            }
                            int m=DescargasEnServidor.get(indiceServidorDConMenosClientes);
                            DescargasEnServidor.set(indiceServidorDConMenosClientes,m+1);
                            dos.writeUTF("ok");
                            dos.writeInt(indiceServidorDConMenosClientes);
                            dos.writeInt(DatosServer.get(indiceServidorDConMenosClientes).getPuerto());
                            dos.writeUTF(DatosServer.get(indiceServidorDConMenosClientes).getIp());
                        } else {
                            dos.writeUTF("No hay servidores de descarga con el libro");
                        }
                    } else {
                        dos.writeUTF("El Libro solicitado no esta en la libreria");
                    }                
                } else if ( "dr".equals(Instrucciones[0]) || "descarga r".equals(Instrucciones[0]) ){
                    x=ListaVideosYSusServidores.indexOf((new VideoForDownloadServer(new Video(Instrucciones[1],Instrucciones[2]))));
                    if (x>=0){
                        int IndiceServidorFallido=dis.readInt();
                        if ((DescargasEnServidor.get(IndiceServidorFallido)>0)){
                            int m=DescargasEnServidor.get(IndiceServidorFallido);
                            DescargasEnServidor.set(IndiceServidorFallido,m-1);
                        }
                        String IpSD="";
                        int puertoSD=0;
                        int indiceServidorD;
                        y=0;
                        int indiceServidorDConMenosClientes=-1;
                        int menorCantidadDeClientes=Integer.MAX_VALUE;
                        int lenghtListaDeServidoresDelLibro=ListaVideosYSusServidores.get(x).getServidoresSize();
                        int numClientesEnServidorActual;
                        while(!(menorCantidadDeClientes==0) && y<lenghtListaDeServidoresDelLibro ){
                            indiceServidorD=ListaVideosYSusServidores.get(x).getServidor(y);

                            numClientesEnServidorActual=DatosServer.get(indiceServidorD).getClientesActuales();
                            if (numClientesEnServidorActual<menorCantidadDeClientes && DatosServer.get(indiceServidorD).getEstado()){
                                menorCantidadDeClientes=numClientesEnServidorActual;
                                indiceServidorDConMenosClientes=indiceServidorD;

                            }
                            y++;
                        }
                        if (DescargasEnServidor.size()<DatosServer.size()){
                                int m=DescargasEnServidor.size();
                                for (int i=m;i < DatosServer.size();i++ )
                                    DescargasEnServidor.add(0);
                            }
                        if (indiceServidorDConMenosClientes>=0){
                            DatosServer.get(indiceServidorDConMenosClientes).sumarCliente(); //Se agrega un cliente al contador de clientes del ServidorD
                           
                            ListaVideosYSusServidores.get(x).sumarDescarga(indiceServidorDConMenosClientes); // Al libro se le agrega uno al contador descargando
                            System.out.println(idUsuario+" recupera descarga de "+ Instrucciones[1]+" con el server # "+ indiceServidorDConMenosClientes);
                            int m=DescargasEnServidor.get(indiceServidorDConMenosClientes);
                            DescargasEnServidor.set(indiceServidorDConMenosClientes,m+1);
                            dos.writeUTF("ok");
                            dos.writeInt(indiceServidorDConMenosClientes);
                            dos.writeInt(DatosServer.get(indiceServidorDConMenosClientes).getPuerto());
                            dos.writeUTF(DatosServer.get(indiceServidorDConMenosClientes).getIp());
                        }else {
                            dos.writeUTF("No hay servidores de descarga con el libro");
                        }
                    } else {
                        dos.writeUTF("El Libro solicitado no esta en la libreria");
                    }
                } else if ("actualizar".equals(Instrucciones[0])){ 
                
                    STemp=dis.readUTF();

                    int puertoTemp =dis.readInt();
                    int indiceServidorD= DatosServer.indexOf(new DownloadServer(STemp,puertoTemp));
                    String nombreL=dis.readUTF();
                    String autorL=dis.readUTF();
                    int indiceLibro= ListaVideosYSusServidores.indexOf(new VideoForDownloadServer(new Video(nombreL,autorL)));
                    ListaVideosYSusServidores.get(indiceServidorD).sumarDescarga( indiceServidorD);

                    DatosServer.get(indiceServidorD).restarCliente();
                    
                    //System.out.println("Actualizo=======================================================");
                }
            } 
            while(!("adios".equals(Instrucciones[0]) || "fin".equals(Instrucciones[0]) || "exit".equals(Instrucciones[0]) ));
            
            System.out.println(" P ");
        } 
        catch (IOException ex) {
            System.out.println("Se callo la conexion: "+ this.idUsuario+" session "+idSessio+". ");
        }
        finally {
            Stop();
        }
        System.out.println("Se desconecto "+ this.idUsuario+" session "+idSessio+". ");

        
    }  
}
