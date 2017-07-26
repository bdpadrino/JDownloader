/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.util.ArrayList;

/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class VideoForDownloadServer {
    private Video Video;
    private ArrayList <Integer> ListaServidoresD; //contiene los indices de los servidores que tienen el video
    private ArrayList <Integer> CantidadDeDescargasPorServidor; //contiene en la posicion i la cantidad de descargas que tiene de este Video el servidor en la posicion i de ListaServidoresD
    private int numeroDescargas;

    
    
    public VideoForDownloadServer(){}
    public VideoForDownloadServer(Video Video ){
        this.Video= Video;
        this.ListaServidoresD=new ArrayList <Integer>();

        this.numeroDescargas=0;   
    }
    
        /**
     * Crea un VideoYServidores recien iniciado (las estadisticas estan en 0)
     * @param Video     video en forma Video
     * @param ListaServidoresD  Lista de los indices de los servidores que tienen al video
     */
    public VideoForDownloadServer(Video Video,ArrayList <Integer> ListaServidoresD ){
        this.Video= Video;
        this.ListaServidoresD=ListaServidoresD;
        this.CantidadDeDescargasPorServidor=new ArrayList <Integer>();
        for (int i=0;i<ListaServidoresD.size();i++ ){
            CantidadDeDescargasPorServidor.add((Integer)0);
        }
        this.numeroDescargas=0;   
    }
    /**
     * Crea un VideoYServidores con datos estadisticos pre exixtentes
     * @param Video     video en forma Video
     * @param ListaServidoresD  Lista de los indices de los servidores que tienen al video
     * @param CantidadDeDescargasPorServidor contiene en la posicion i la cantidad de descargas que tiene de este Video el servidor en la posicion i de ListaServidoresD
     * @param numeroDescargando Numero de descargas vigentes del video
     * @param numeroDescargas   Numero de descargas realizadas a este video
     */
        public VideoForDownloadServer(Video Video,ArrayList <Integer> ListaServidoresD,ArrayList <Integer> CantidadDeDescargasPorServidor,int numeroDescargas ){
            this.Video= Video;
            this.ListaServidoresD=ListaServidoresD;
            this.CantidadDeDescargasPorServidor=CantidadDeDescargasPorServidor;
   
            this.numeroDescargas=numeroDescargas;   
        }
    public String getAutor(){return Video.getAutor() ;}
    public String getNombre(){return Video.getNombre() ;}
    public Video getVideo(){return Video;}
    /**
     * Agrega el servidor a la lista de servidores que tienen el video solo si este no estaba en la lista antes.
     * @param ServerD es el servidor de descargado a la lista de servers que tienen el video  
     */
    public void addServidorD(Integer ServerD){
        if (this.ListaServidoresD.indexOf(ServerD) ==-1){
            this.ListaServidoresD.add(ServerD);
            this.CantidadDeDescargasPorServidor.add(0);
        };
        
    }
    public int getServidor(int i){return this.ListaServidoresD.get(i);}
    public int getDescargasServidor(int i){return this.CantidadDeDescargasPorServidor.get(i);}
    public int getServidoresSize(){return this.ListaServidoresD.size();}
    
    public String getGenero(int i){

        return this.Video.getGenero(i);}
    public int getGenerosSize(){return this.Video.getGenerosSize();}

    
    /* BEGIN SYNC DOWNLOAD SERVERS */
    public ArrayList<Integer> getListaServidoresD() {
        return ListaServidoresD;
    }
    
    /* END SYNC DOWNLOAD SERVERS */
    
    

 /**
 * devuelve el numero de visitas
 */   
    public int  getDescargas(){
        return numeroDescargas;}    
    
    

 /**
 * Una descarga activa pasa de activa a completada. 
 * @param servidor es el numero de identidad del servidor 
 */       
    public void sumarDescarga(int servidor){
        this.numeroDescargas++; 
        int indiceServidor = ListaServidoresD.indexOf(servidor);
        int temp=CantidadDeDescargasPorServidor.get(indiceServidor)+1;
        CantidadDeDescargasPorServidor.set(indiceServidor,temp);
        
    }
       @Override
    public boolean equals(Object o){
        if (o instanceof VideoForDownloadServer){
            String TempNombre =  ( ((VideoForDownloadServer)o).Video).getNombre();
            String TempAutor = ( ((VideoForDownloadServer) o).Video).getAutor();
    //        System.out.println("-----------------"+this.Video.getNombre()+" "+this.Video.getAutor()+" "+TempNombre+" "+TempAutor+" "+((this.Video.getNombre()).equals(TempNombre)&&(this.Video.getAutor()).equals(TempAutor) ));
    //        System.out.println("                        "+TempNombre+" "+TempAutor+" "+((this.Video.getNombre()).equals(TempNombre)&&(this.Video.getAutor()).equals(TempAutor) ));
            return ((this.Video.getNombre()).equals(TempNombre)&&(this.Video.getAutor()).equals(TempAutor) );
        }
        return false;
    }
    /*
        private Video Video;
    private ArrayList <Integer> ListaServidoresD; //contiene los indices de los servidores que tienen el video
    private ArrayList <Integer> CantidadDeDescargasPorServidor; //contiene en la posicion i la cantidad de descargas que tiene de este Video el servidor en la posicion i de ListaServidoresD
    private int numeroDescargas;
    
    */
        @Override
    public String toString(){
        String S="";
        String sep=".";
        S=numeroDescargas+"";
        for (int i=0;i<ListaServidoresD.size(); i++   ){
            S=S+sep+ListaServidoresD.get(i);
        }
        S=S+"-";
        for (int i=0;i<CantidadDeDescargasPorServidor.size(); i++   ){
            S=S+CantidadDeDescargasPorServidor.get(i)+sep;
        }
        S=S+"fin";
        return S;
    }
    
    
}
