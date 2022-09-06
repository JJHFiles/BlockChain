package BOE;

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
/* BoeServer con rol Servidor Multihilo, recibe las conexiones de los clientes y abre un hilo para cada conexión
 * la conexión con los clientes es con Socket seguro SSL, utiliza un certificado dle fichero "RSA_StoreSSL"
 * la Ip de este nodo es la 192.168.1.233
 */
public class BoeServer {

    // IP del nodo en formato String
    private String hostIP = "";

    // Se prepara la IP para el socket SSL
    InetAddress IP = null;

    // Para iniciar el Nodo
    public static void main(String[] args) {
        new BoeServer();
    }

    // Constructor por defecto
    public BoeServer() {

            // Pares clave,valor
            // Localización del certificado, la clave "keyStore" contiene la ruta del certificado
            System.setProperty("javax.net.ssl.keyStore", "src\\BOE\\RSA_StoreSSL");

            // La clave "keyStorePassword" contieen la clave del certificado
            System.setProperty("javax.net.ssl.keyStorePassword", "1234567");
        
            setNodesFromEncrypedFile(
                    System.getProperty("javax.net.ssl.keyStorePassword")
                    + System.getProperty("javax.net.ssl.keyStorePassword")
                    + System.getProperty("javax.net.ssl.keyStorePassword"));

            // Único método de la clase, lanza el Servidor para la espera de conexiones
            loadServer();
       
    }

    // metodo que lanza el servidor
    public void loadServer() {
        try {

            // Constante con el puerto de conexión
            final int SERVER_PORT = 6000;

            // Número máximo de conexiones en cola
            final int MAX_CON = 100;

            // Se declaran e inician los objetos para la conexión SSL
            SSLServerSocketFactory sfact = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            // Se prepara el socket seguro del server, se le pasa, puerto, numero maximo de conexiones en cola e IP
            SSLServerSocket SocketServidor = (SSLServerSocket) sfact.createServerSocket(SERVER_PORT, MAX_CON, IP);

            // Se declara el socket del cliente que posteriormente se castea como seguro con (SSLSocket)
            Socket client;

            System.out.println(" ***** BOE SERVER *****");
            System.out.println("\nAbre puerto: " + SERVER_PORT + "\nNumero max de conexiones en cola: " + MAX_CON + "\nIP: " + hostIP);

            // Bucle que espera las conexiones, lleva la cuenta de las conexiones
            for (Integer i = 1; i < Integer.MAX_VALUE; i++) {

                // Bloqueante. ,espera una conexión, se castea el socket como socket seguro SSL
                client = (SSLSocket) SocketServidor.accept();

                // Se muestra el número de conexión
                System.out.println("\nConexion #" + i);

                // Se instancia un hilo por conexión 
                BoeServerListener thread = new BoeServerListener(client, i);

                // Se inicia el hilo
                thread.start();
            }

            //Captura de excepciones
        } catch (IOException ex) {
            Logger.getLogger(BoeServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo que descifra el contenido del fichero de IP de nodos
    public void setNodesFromEncrypedFile(String AESpass) {

        try {
            // Se utiliza AES
            BOEAdvancedEncryptionStandard aes = new BOEAdvancedEncryptionStandard(AESpass);

            /* Se descifra el fichero encriptado AES y se introduce en un array de Strings,
            * el fichero tiene los nodos separados por almohadilla # y la Ip para smartcontract
             */
            String arrNodesIP[] = (aes.decrypt()).split("#");

            // Se asigna la IP al nodo desde el fichero encriptado
            hostIP = "192.168.0." + arrNodesIP[2];

            // Se prepara la IP para el socket SSL del nodo
            IP = InetAddress.getByName(hostIP);

            System.out.println("Se ha leido la IP de un fichero encriptado:\n"
                    + "nodes.encrypt\n"
                    + "Las Ip del Servidor BOE es:\n"
                    + "Nodo1 = " + hostIP);
        } catch (UnknownHostException ex) {
            Logger.getLogger(BoeServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
