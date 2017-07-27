package Model;

/**
 * Clase Cliente.
 *
 * @author Atahualpa Silva F. <https://github.com/atahualpasf>
 */
public class Client extends Object {

         private String IdNombre;
         private int numeroDeConecciones;

         /**
          * Contructor de clase. Utilizado para regsitrar los datos del cliente
          * al momento de su conexión.
          *
          * @param Nombre ID del cliente que se esta registrando.
          */
         public Client(String Nombre) {
                  this.IdNombre = Nombre;
                  this.numeroDeConecciones = 1;
         }

         /**
          * Cinstructor de clase. Utilizado para regsitrar los datos del cliente
          * al momento de su conexión.
          *
          * @param Nombre
          * @param conecciones
          */
         public Client(String Nombre, int conecciones) {
                  this.IdNombre = Nombre;
                  this.numeroDeConecciones = conecciones;
         }

         /**
          * Método utilizado para obtener el nombre del cliente.
          *
          * @return Nombre del cliente.
          */
         public String getNombre() {
                  return this.IdNombre;
         }

         /**
          * Método utilizado para otener el número de visitas realizado por un
          * cliente.
          *
          * @return Número de visitas.
          */
         public int getVisitas() {
                  return numeroDeConecciones;
         }

         /**
          * Método utilizado para llevar un conteo de las conexiones del
          * cliente.
          */
         public void sumarCliente() {
                  this.numeroDeConecciones++;
         }

         @Override
         public String toString() {
                  return this.getNombre() + "." + this.getVisitas();
         }

         @Override
         public boolean equals(Object o) {
                  if (o instanceof Client) {
                           String Temp = ((Client) o).IdNombre;
                           return ((this.IdNombre).equals(Temp));
                  } else if (o instanceof String) {
                           String Temp = (String) o;
                           return ((this.IdNombre).equals((String) o));
                  }
                  return false;
         }
}
