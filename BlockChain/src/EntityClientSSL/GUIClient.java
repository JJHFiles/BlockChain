package EntityClientSSL;

import TemporalObjects.TempBlock;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.logging.*;
import javax.net.ssl.*;

/**
 *
 * @author Javier Juarros Huerga
 */
// Clase que inicia el cliente desde la GUI para comunicarse con los nodos y enviar transacciones
public class GUIClient {

    // Fujos de datos de entrada y salida
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    // Las Ips se reciben por el constructores desde la GUI, estas las lee la GUI de un fichero encriptado   
    private String nodeIp = "";
    private InetAddress Ip = null;

    // Constante co el puerto remoto del nodo, comun para los 2 nodos
    private Integer NODES_PORT = 6000;

    // Declaración del socket seguro SSL
    private SSLSocket clientSocket = null;

    // El constructor recibe una IP de la GUI y conecta con el servidor/nodo
    public GUIClient(String IP) {

        System.out.println("\nCLIENTE DE GUI INICIADO....\n");

        try {

            // Se asigna la IP recibida por el constructor
            nodeIp = IP;
            Ip = InetAddress.getByName(nodeIp);

            // Método para conectar con el servidor/nodo
            nodeConnection();

        } catch (IOException ex) {
            Logger.getLogger(GUIClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Contiene la información para conectar con el servidor/nodo, conexión SSL Socket
    public void nodeConnection() {

        try {

            // Pares clave, valor
            // Clave y Dirección del certificado para conexión SSL, RSA_StoreSSL
            System.setProperty("javax.net.ssl.trustStore", "src\\EntityClientSSL\\RSA_StoreSSL");

            // Clave y password para el certificado para conexión SSl, simétrica.
            System.setProperty("javax.net.ssl.trustStorePassword", "1234567");

            // Declaración del socket seguro
            SSLSocketFactory sfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            clientSocket = (SSLSocket) sfact.createSocket(Ip, NODES_PORT);
            SSLSession session = ((SSLSocket) clientSocket).getSession();

            // Los  datos de la conexión se visualizan por consola
            System.out.println("\u001B[34mHost/Nodo remoto: " + session.getPeerHost());
            System.out.println("\u001B[34mTipo de Cifrado de la conexión: " + session.getCipherSuite());
            System.out.println("\u001B[34mProtocolo utilizado: " + session.getProtocol());
            System.out.println("\u001B[34mIdentificador de sesión:" + new BigInteger(session.getId()));
            System.out.println("\u001B[34mCreación de la sesión: " + session.getCreationTime());

            // Aqui se declara el certificado siguiendo el formato X509
            X509Certificate certificate = (X509Certificate) session.getPeerCertificates()[0];
            System.out.println("\u001B[34mPropietario del certificado: " + certificate.getSubjectDN());
            System.out.println("\u001B[34mAlgoritmo del certificado: " + certificate.getSigAlgName());
            System.out.println("\u001B[34mTipo de certificado: " + certificate.getType());
            System.out.println("\u001B[34mEmisor del certificado: " + certificate.getIssuerDN());
            System.out.println("\u001B[34mNúmero Serie del certificado: " + certificate.getSerialNumber() + "\u001B[30m\n");

            //Se establece conexión con con los flujos de datos del servidor/nodo
            //Flujo de entrada y salida para objetos
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());

        } catch (IOException ex) {
            System.out.println("\u001B[31mGUIClient: Nodo no disponible, buscando nodo on-line\u001B[30m");
        }
    }

    // Método que se invoca en la GUI y pide al servidor contenido a través de un bloque temporal y recibe bloque temporal con la respuesta replica del bloque requerido de la blockchain
    public TempBlock getBlockFromServer(TempBlock tb) {

        try {
            // Envía la petición
            out.writeObject(tb);

            // Recibe respuesta
            tb = (TempBlock) in.readObject();

        } catch (Exception ex) {
            System.out.println("GUIClient: No se puede establecer comunicación con servidor");
        }

        // Retorna un bloque temporal a la GUI
        return tb;
    }

    // Método que se invoca en la GUI y pide al servidor contenido a través de un bloque temporal, envía bloque temporal y recibe Array de bloques temporal, replica de su blockchain
    public ArrayList<TempBlock> getArrTempBlockFromServer(TempBlock tb) {
        ArrayList<TempBlock> arrTb = null;
        try {
            // Envía petición
            out.writeObject(tb);

            // Recibe array de bloques replica de su blockchain
            arrTb = (ArrayList<TempBlock>) in.readObject();

            // Captura de excepciones
        } catch (IOException ex) {
            Logger.getLogger(GUIClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GUIClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Retorna ArrayList
        return arrTb;
    }

    //getters y setters
    public SSLSocket getClientSocket() {
        return clientSocket;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public void setOut(ObjectOutputStream out) {
        this.out = out;
    }

    public Integer getNODES_PORT() {
        return NODES_PORT;
    }

    public void setClientSocket(SSLSocket client) {
        this.clientSocket = client;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public InetAddress getIp() {
        return Ip;
    }

    public void setIp(InetAddress Ip) {
        this.Ip = Ip;
    }

    public void setNODES_PORT(Integer NODES_PORT) {
        this.NODES_PORT = NODES_PORT;
    }
}
