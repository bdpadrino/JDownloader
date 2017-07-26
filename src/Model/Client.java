
package Model;

/**
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class Client extends Object {
    private String IdNombre ;
    private int numeroDeConecciones;


    /**
    * crea un DatosCliente recien inicializado (para cuando se crea un registro nuevo)
    * @param IdNombre es el nombre del cliente 
    */      
    public Client(String Nombre) {
        this.IdNombre=Nombre;
        this.numeroDeConecciones=1;   
    }
    
    /**
    * crea un DatosCliente con los datos (para cuando se cargan los datos de un cliente)
    * @param IdNombre es el nombre del cliente 
    * @param numeroDeConecciones es el numero de conecciones del cliente
    */    
    public Client(String Nombre, int conecciones) {
        this.IdNombre=Nombre;
        this.numeroDeConecciones=conecciones;
    }
    
    /**
    * devuelve el nombre del cliente
    */   
    public String  getNombre(){
        return this.IdNombre;}
 
    /**
     * devuelve el numero de visitas
     */   
    public int  getVisitas(){
        return numeroDeConecciones;}    
    
    /**
    * Se agrega un cliente al historial y a clientes conectados
    */    
    public void sumarCliente(){
        this.numeroDeConecciones++;   
    }
    
    @Override
    public String toString(){
        return this.getNombre()+"."+this.getVisitas();
    }

   @Override
    public boolean equals(Object o){
        if (o instanceof Client){
            String Temp = ((Client) o).IdNombre;
            return ((this.IdNombre).equals(Temp));}
        else if (o instanceof String) {
            String Temp = (String) o;
            return ((this.IdNombre).equals((String)o));}
        return false;
    }
    
        
}