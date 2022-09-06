package ValidatorServerSSL2;

// Se importan los recursos necesarios
import Blockchain.Blockchain;
import java.io.*;
import static java.lang.Thread.sleep;
import java.math.BigInteger;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.*;
import javax.net.ssl.*;

/**
 *
 * @author Javier Juarros Huerga
 */
/* Se encarga de la propagación de la blockchain del nodo local al nodo remoto
*  Copia todas las blockchains y las propaga de Ip 192.168.1.21 -> 192.168.1.22
*  Utiliza socket seguro SSL para conectarse con  el nodo remoto
*  Cada 5 segundos efectua una propagación.  El destino copiará los bloques que
*  le falten sincronizando las blockchains.
 */
public class SpreadProcess extends Thread {

    // Flujo de  salida, solo envía datos, no necestita flujo de input 
    private ObjectOutputStream out = null;

    // Declaración de constantes IP y puerto de comunicaciones remotos    
    private String remoteNoideIp = "";
    private final int REMOTE_NODE_PORT = 6000;//puerto remoto

    // Declaración de client SSL
    private SSLSocket client = null;

    // Conjunto de blockchains locales que envía al nodo remoto para su sincronización
    private ArrayList<Blockchain> blockchain = null;

    // Constante que contiene la direccion del fichero con el objeto serializado de blockchains
    private final String BLOCKCHAIN_FILE = "src\\ValidatorServerSSL2\\persistentBlockchain.dat";

    // Hilo que controla si el nodo esta caído y reintenta la conexión
    //   private Thread t = null;
    // Método que lanza la clase
    public static void main(String[] args) {
        SpreadProcess sp = new SpreadProcess();
        sp.nodeConnection();
    }

    // Establece la conexión con el nodo destino
    public void nodeConnection() {

        try {

            System.out.println("\nINICIANDO LA PROPAGACION DE BLOCKCHAINS....\n");

            // Par de claves - valor
            // Clave TrustStore, Valor localización del fichero de certificado "RSA_StoreSSL"
            System.setProperty("javax.net.ssl.trustStore", "src\\ValidatorServerSSL2\\RSA_StoreSSL");

            // Clave trustStorePassword contiene el valor de la clave del certificado
            System.setProperty("javax.net.ssl.trustStorePassword", "1234567");

            /* Se lee la Ip del fichero nodes.decrypt descifrandolo con la clave del
         * certificado 3 veces ya que hacen falta 16 caracteres
             */
            setNodesFromEncrypedFile(
                    System.getProperty("javax.net.ssl.trustStorePassword")
                    + System.getProperty("javax.net.ssl.trustStorePassword")
                    + System.getProperty("javax.net.ssl.trustStorePassword"));

            // IP Remota
            InetAddress IP = InetAddress.getByName(remoteNoideIp);

            // Se declara el socket seguro SSL
            SSLSocketFactory sfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            client = (SSLSocket) sfact.createSocket(IP, REMOTE_NODE_PORT);
            SSLSession session = ((SSLSocket) client).getSession();

            // Los  datos de la conexión se visualizan por consola
            System.out.println("\u001B[34mHost/Nodo remoto: " + session.getPeerHost());
            System.out.println("\u001B[34mTipo de Cifrado de la conexión: " + session.getCipherSuite());
            System.out.println("\u001B[34mProtocolo utilizado: " + session.getProtocol());
            System.out.println("\u001B[34mIdentificador de sesion:" + new BigInteger(session.getId()));
            System.out.println("\u001B[34mCreacion de la sesion: " + session.getCreationTime());

            // Aqui se declara el certificado siguiendo el formato X509
            X509Certificate certificate = (X509Certificate) session.getPeerCertificates()[0];
            System.out.println("\u001B[34mPropietario del certificado: " + certificate.getSubjectDN());
            System.out.println("\u001B[34mAlgoritmo del certificado: " + certificate.getSigAlgName());
            System.out.println("\u001B[34mTipo de certificado: " + certificate.getType());
            System.out.println("\u001B[34mEmisor del certificado: " + certificate.getIssuerDN());
            System.out.println("\u001B[34mNumero Serie del certificado: " + certificate.getSerialNumber() + "\n");

            //Flujo de Stream objetos que se envían
            out = new ObjectOutputStream(client.getOutputStream());

            System.out.println("Conexión establecida con el Nodo");

            start();

            // Captura de excepciones
        } catch (Exception ex) {
            System.out.println("\u001B[31mEl nodo no esta disponible reintentando conexión en 5 segundos\u001B[31m");

            // Espera entre intento de conexión con el nodo
            try {
                sleep(5000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(SpreadProcess.class.getName()).log(Level.SEVERE, null, ex1);
            }

            // Tras 5 segundos vuelve a intentar la conexión, sellama a si mismo, método redundante.
            nodeConnection();
        }
    }

    // Hilo que envía las blockchains al nodo remoto cada 5 sec 
    public void run() {

        // Bucle que se ejecuta cada 5 sec
        while (true) {
            try {

                // Se comprueba si el fichero serializado de blockchain existe en local, si es asi envía las blockchains
                File file = new File(BLOCKCHAIN_FILE);

                // Comprobacion de existencia del fichero
                if (file.exists()) {

                    // Como existe fichero lee las blockchain y las carga en el atributo blockchain "Arraylist"
                    readAllBlockchainsFromFile();

                    // Envía las blockchains
                    out.writeObject(blockchain);

                    // El fichero serializado "persistentBlockchain.dat" no existe
                } else {
                    System.out.println("Fichero blockchain no ha sido creado no se pueden enviar cambios\n");
                }

                // Tiempo entre envío y envío al nodo
                int waitTime = 5; // tiempo de espera entre sincronizaciones

                System.out.println("Esperando al siguiente envío, durará " + waitTime + " segundos\n");

                // Espera de un segundo por iteración hasta completar waitTime
                for (int x = waitTime; x >= 0; x--) {
                    System.out.println("Faltan " + x + " segundos...\n");
                    sleep(1000);

                }

                // Captura de excepciones
            } catch (Exception ex) {
                //System.exit(0);

                Logger.getLogger(SpreadProcess.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Método que lee las blockchains del fichero local
    public void readAllBlockchainsFromFile() {
        try {

            // Flujo de string para la lectura del fichero
            FileInputStream fis = null;

            // Flujo de lectura se deja en "input"
            ObjectInputStream input = null;

            // Se prepara la lectura
            fis = new FileInputStream(BLOCKCHAIN_FILE);

            System.out.println("Envíando blockchains a Servidor: " + this.remoteNoideIp);

            // Se lee el input
            input = new ObjectInputStream(fis);

            // Copiado del input de entrada a un objeto de tipo blockchain, es casteado.
            blockchain = (ArrayList<Blockchain>) input.readObject();

            // Cierre de recursos abiertos
            fis.close();
            input.close();

            // Captura de excepciones
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SpreadProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SpreadProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SpreadProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método que descifra el contenido del fichero de IP de nodos
    public void setNodesFromEncrypedFile(String AESpass) {

        // Se utiliza AES
        NodeAdvancedEncryptionStandard aes = new NodeAdvancedEncryptionStandard(AESpass);

        /* Se descifra el fichero encriptado AES y se introduce en un array de Strings,
            * el fichero tiene los nodos separados por almohadilla # y la Ip para smartcontract
         */
        String arrNodesIP[] = (aes.decrypt()).split("#");

        // Se asigna la IP del nodo destino a sincronizar desde el fichero encriptado
        remoteNoideIp = "192.168.0." + arrNodesIP[0];

        System.out.println("Se ha leído la IP del nodo remoto un fichero encriptado:\n"
                + "nodes.encrypt\n"
                + "La Ip del nodo es:\n"
                + "Nodo1 = " + remoteNoideIp);
    }
}
