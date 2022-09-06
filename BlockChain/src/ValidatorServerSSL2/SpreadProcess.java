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
/* Se encarga de la propagaci�n de la blockchain del nodo local al nodo remoto
*  Copia todas las blockchains y las propaga de Ip 192.168.1.21 -> 192.168.1.22
*  Utiliza socket seguro SSL para conectarse con  el nodo remoto
*  Cada 5 segundos efectua una propagaci�n.  El destino copiar� los bloques que
*  le falten sincronizando las blockchains.
 */
public class SpreadProcess extends Thread {

    // Flujo de  salida, solo env�a datos, no necestita flujo de input 
    private ObjectOutputStream out = null;

    // Declaraci�n de constantes IP y puerto de comunicaciones remotos    
    private String remoteNoideIp = "";
    private final int REMOTE_NODE_PORT = 6000;//puerto remoto

    // Declaraci�n de client SSL
    private SSLSocket client = null;

    // Conjunto de blockchains locales que env�a al nodo remoto para su sincronizaci�n
    private ArrayList<Blockchain> blockchain = null;

    // Constante que contiene la direccion del fichero con el objeto serializado de blockchains
    private final String BLOCKCHAIN_FILE = "src\\ValidatorServerSSL2\\persistentBlockchain.dat";

    // Hilo que controla si el nodo esta ca�do y reintenta la conexi�n
    //   private Thread t = null;
    // M�todo que lanza la clase
    public static void main(String[] args) {
        SpreadProcess sp = new SpreadProcess();
        sp.nodeConnection();
    }

    // Establece la conexi�n con el nodo destino
    public void nodeConnection() {

        try {

            System.out.println("\nINICIANDO LA PROPAGACION DE BLOCKCHAINS....\n");

            // Par de claves - valor
            // Clave TrustStore, Valor localizaci�n del fichero de certificado "RSA_StoreSSL"
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

            // Los  datos de la conexi�n se visualizan por consola
            System.out.println("\u001B[34mHost/Nodo remoto: " + session.getPeerHost());
            System.out.println("\u001B[34mTipo de Cifrado de la conexi�n: " + session.getCipherSuite());
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

            //Flujo de Stream objetos que se env�an
            out = new ObjectOutputStream(client.getOutputStream());

            System.out.println("Conexi�n establecida con el Nodo");

            start();

            // Captura de excepciones
        } catch (Exception ex) {
            System.out.println("\u001B[31mEl nodo no esta disponible reintentando conexi�n en 5 segundos\u001B[31m");

            // Espera entre intento de conexi�n con el nodo
            try {
                sleep(5000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(SpreadProcess.class.getName()).log(Level.SEVERE, null, ex1);
            }

            // Tras 5 segundos vuelve a intentar la conexi�n, sellama a si mismo, m�todo redundante.
            nodeConnection();
        }
    }

    // Hilo que env�a las blockchains al nodo remoto cada 5 sec 
    public void run() {

        // Bucle que se ejecuta cada 5 sec
        while (true) {
            try {

                // Se comprueba si el fichero serializado de blockchain existe en local, si es asi env�a las blockchains
                File file = new File(BLOCKCHAIN_FILE);

                // Comprobacion de existencia del fichero
                if (file.exists()) {

                    // Como existe fichero lee las blockchain y las carga en el atributo blockchain "Arraylist"
                    readAllBlockchainsFromFile();

                    // Env�a las blockchains
                    out.writeObject(blockchain);

                    // El fichero serializado "persistentBlockchain.dat" no existe
                } else {
                    System.out.println("Fichero blockchain no ha sido creado no se pueden enviar cambios\n");
                }

                // Tiempo entre env�o y env�o al nodo
                int waitTime = 5; // tiempo de espera entre sincronizaciones

                System.out.println("Esperando al siguiente env�o, durar� " + waitTime + " segundos\n");

                // Espera de un segundo por iteraci�n hasta completar waitTime
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

    // M�todo que lee las blockchains del fichero local
    public void readAllBlockchainsFromFile() {
        try {

            // Flujo de string para la lectura del fichero
            FileInputStream fis = null;

            // Flujo de lectura se deja en "input"
            ObjectInputStream input = null;

            // Se prepara la lectura
            fis = new FileInputStream(BLOCKCHAIN_FILE);

            System.out.println("Env�ando blockchains a Servidor: " + this.remoteNoideIp);

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

    // M�todo que descifra el contenido del fichero de IP de nodos
    public void setNodesFromEncrypedFile(String AESpass) {

        // Se utiliza AES
        NodeAdvancedEncryptionStandard aes = new NodeAdvancedEncryptionStandard(AESpass);

        /* Se descifra el fichero encriptado AES y se introduce en un array de Strings,
            * el fichero tiene los nodos separados por almohadilla # y la Ip para smartcontract
         */
        String arrNodesIP[] = (aes.decrypt()).split("#");

        // Se asigna la IP del nodo destino a sincronizar desde el fichero encriptado
        remoteNoideIp = "192.168.0." + arrNodesIP[0];

        System.out.println("Se ha le�do la IP del nodo remoto un fichero encriptado:\n"
                + "nodes.encrypt\n"
                + "La Ip del nodo es:\n"
                + "Nodo1 = " + remoteNoideIp);
    }
}
