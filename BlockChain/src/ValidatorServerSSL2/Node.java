package ValidatorServerSSL2;

// Se importan los recursos necesarios
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

/**
 *
 * @author Javier Juarros Huerga
 */
/* Nodo con rol Servidor Multihilo, recibe las conexiones de los clientes y abre un hilo opara cada conexion
 * la conexión con los clientes es con Socket seguro SSL, utiliza un certificado dle fichero "RSA_StoreSSL"
 * la Ip de este nodo es la 192.168.1.21
 */
public class Node {

    // IP del nodo en formato String
    private String hostIP = "";

    // Se prepara la IP para el socket SSL del nodo
    private InetAddress IP = null;

    // Para iniciar el Nodo
    public static void main(String[] args) {
        new Node();
    }

    // Constructor por defecto
    public Node() {

        // Pares clave,valor
        // Localizacion del certificado, la clave "keyStore" contiene la ruta del certificado
        System.setProperty("javax.net.ssl.keyStore", "src\\ValidatorServerSSL2\\RSA_StoreSSL");

        // La clave "keyStorePassword" contieen la clave del certificado
        System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

        /* Se lee la Ip del fichero nodes.decrypt descifrandolo con la clave del
         * certificado 3 veces ya que hacen falta 16 caracteres
         */
        setNodesFromEncrypedFile(
                System.getProperty("javax.net.ssl.keyStorePassword")
                + System.getProperty("javax.net.ssl.keyStorePassword")
                + System.getProperty("javax.net.ssl.keyStorePassword"));

        // Unico método de la clase, lanza el Nodo para la espera de conexiones
        loadServer();
    }

    // método que lanza el servidor
    public void loadServer() {
        try {

            // Constante con el puerto de conexion
            final int SERVER_PORT = 6000;

            // Número máximo de conexiones en cola
            final int MAX_CON = 100;

            // Se declaran e inician los objetos para la conexión SSL
            SSLServerSocketFactory sfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            // Se prepara el socket seguro del server, se le pasa, puerto, numero máximo de conexiones en cola e IP
            SSLServerSocket SocketServidor = (SSLServerSocket) sfact.createServerSocket(SERVER_PORT, MAX_CON, IP);

            // Se declara el socket del cliente que posteriormente se castea como seguro con (SSLSocket)
            Socket client;

            System.out.println(" ***** NODO/SERVIDOR 2 *****");
            System.out.println("\nAbre puerto: " + SERVER_PORT + "\nNúmero max de conexiones en cola: " + MAX_CON + "\nIP: " + hostIP);

            // Bucle que espera las conexiones, lleva la cuenta de las conexiones
            for (int i = 1; i < Integer.MAX_VALUE; i++) {

                // Bloqueante. Espera una conexión, se castea el socket como socket seguro SSL
                client = (SSLSocket) SocketServidor.accept();

                // Se muestra el nuemro de conexion
                System.out.println("\nConexión #" + i);

                // Se instancia un hilo por conexión 
                NodeListener thread = new NodeListener(client, i);

                // Se inicia el hilo
                thread.start();
            }

            //Captura de excepciones
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo que descifra el contenido del fichero de IP de nodos
    public void setNodesFromEncrypedFile(String AESpass) {

        try {
            // Se utiliza AES
            NodeAdvancedEncryptionStandard aes = new NodeAdvancedEncryptionStandard(AESpass);

            /* Se descifra el fichero encriptado AES y se introduce en un array de Strings,
            * el fichero tiene los nodos separados por almohadilla # y la Ip para smartcontract
             */
            String arrNodesIP[] = (aes.decrypt()).split("#");

            // Se asigna la IP al nodo desde el fichero encriptado
            hostIP = "192.168.0." + arrNodesIP[1];

            // Se prepara la IP para el socket SSL del nodo
            IP = InetAddress.getByName(hostIP);

            System.out.println("Se ha leido la IP de un fichero encriptado:\n"
                    + "nodes.encrypt\n"
                    + "Las Ip de cada nodo son:\n"
                    + "Nodo2 = " + hostIP);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
