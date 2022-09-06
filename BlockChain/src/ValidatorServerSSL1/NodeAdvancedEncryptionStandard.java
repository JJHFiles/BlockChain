package ValidatorServerSSL1;

// Se importan los recursos necesarios
import java.security.*;
import javax.crypto.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Javier Juarros Huerga
 *
 * Mediante secretkey privada o simétrica y utilizando el algoritmo AES,
 * se lee fichero encriptado.
 */
public class NodeAdvancedEncryptionStandard {
 
    
    
    /* Main para cifrar descifrar ficheros, solo se habilita para generar nuevos 
     * ficheros encriptados si cambian los nodos. Mientras tanto queda comentado
     */
//    public static void main(String [] args) {  
//   
//    NodeAdvancedEncryptionStandard aes = new NodeAdvancedEncryptionStandard("1234567123456712");
//    aes.encrypt();
//    //aes.decrypt();
//    }
    
     /* Clave usada para encriptado y desencriptado, de 16 caracteres
      * La contraseña se recibe desde GUILogin a través del constructor
      * es la clave de acceso 2 veces seguidas cogiendo solo los 16 primeros caracteres                 
      */      
    String clave = null; 
    

    //Declara el objeto tipo secretkey secreta
    SecretKey secretkey =null;

    // Consructor que asigna la clave de GUILogin
    public NodeAdvancedEncryptionStandard(String clave){
    
    /*  Se asigna la clave recibida de GUILogin, solo necesita los 16
     *  primeros caracteres de la cadena, de0 a 16
     */
    this.clave= clave.substring(0, 16);
    
        System.out.println("Clave que aplica: "+ this.clave);
    
    // Inicializa al obj. SecretKey con la clave AES
    secretkey = new SecretKeySpec(this.clave.getBytes(), "AES");
    
    }
    
    
    // Método para el encriptado AES
    public void encrypt() {
        try {
            //Llama al método que encripta el fichero que se pasa como parametro
            secretkey = cifrarFichero("src\\ValidatorServerSSL1\\nodes");

            //Captura de excepciones
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException
                | BadPaddingException | IllegalBlockSizeException
                | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    // Método para el desencriptado
    public String decrypt() {

        // Devuelve un string con el contenido descifrado del fichero encriptado
        String ipPort = null;

        try {
            // Llama al método que desencripta el fichero
            ipPort = descifrarFichero("src\\ValidatorServerSSL1\\nodes.encrypt", secretkey,
                    "src\\ValidatorServerSSL1\\nodes.decrypt");

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(NodeAdvancedEncryptionStandard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(NodeAdvancedEncryptionStandard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NodeAdvancedEncryptionStandard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(NodeAdvancedEncryptionStandard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(NodeAdvancedEncryptionStandard.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(NodeAdvancedEncryptionStandard.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ipPort;
    }

    // Método que encripta el fichero de entrada , el resultado lo deja en fichero.encrypt
    private SecretKey cifrarFichero(String file)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            FileNotFoundException, IOException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {

        // Crea secretkey simétrica
        // Crea un objeto KeyGenerator para generar la secretkey simétrica para algoritmo AES
        System.out.println("1.- Utiliza clave simétrica AES");

        SecretKey claveSimetrica = new SecretKeySpec(clave.getBytes(), "AES"); //genera la secretkey simétrica

        // Muestra la secretkey aplicada, solo durante el cifrado      
        System.out.print("Clave simétrica AES :");
        System.out.println(claveSimetrica.toString() + "    " + claveSimetrica.getEncoded() + " Lengt: " + claveSimetrica.getEncoded().length);

        // Crea el objeto Cipher para cifrar/descifrar, utilizando el algoritmo AES y modo de cifrado 
        Cipher cifrador = Cipher.getInstance("AES");

        // Inicializa el objeto Cipher en modo de operacion ENCRIPTACIÓN con claveSimetrica simétrica
        cifrador.init(Cipher.ENCRYPT_MODE, claveSimetrica);

        // Declaracion  de objetos
        System.out.println("\n2.- Cifra con AES el fichero: " + file
                + "\ny deja resultado en " + file + ".encrypt");

        FileInputStream fe; // Fichero de entrada
        FileOutputStream fs; // Fichero de salida

        int bytesLeidos;

        byte[] buffer = new byte[1000];
        byte[] bufferCifrado;

        fe = new FileInputStream(file);                 // Objeto fichero de entrada
        fs = new FileOutputStream(file + ".encrypt");   // Fichero de salida

        // Lee el fichero de mil en mil y pasa los fragmentos leídos al cifrador
        bytesLeidos = fe.read(buffer, 0, 1000);

        // Mientras no se llegue al final del fichero        
        while (bytesLeidos != -1) {

            // Pasa texto claro al cifrador y lo cifra, asignándolo a bufferCifrado
            bufferCifrado = cifrador.update(buffer, 0, bytesLeidos);
            fs.write(bufferCifrado); // Graba el texto cifrado en fichero
            bytesLeidos = fe.read(buffer, 0, 1000);
        }
        //Completa el cifrado        
        bufferCifrado = cifrador.doFinal();

        //Graba el final del texto cifrado, si lo hay        
        fs.write(bufferCifrado);

        //Cierra ficheros abiertos
        fe.close();
        fs.close();

        return claveSimetrica;
    }

    // Método que desencripta el fichero de entrada retornado el resultado en String
    // Pasandole también la secretkey simétrica 
    private String descifrarFichero(String fileEntrada, Key claveSimetrica, String fileSalida)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
            FileNotFoundException, IOException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {

        // Crea el objeto Cipher para cifrar/descifrar, utilizando el algoritmo AES y modo de cifrado 
        Cipher cifrador = Cipher.getInstance("AES");

        // Inicializa el objeto Cipher en modo de operacion DESENCRIPTACI“N con secretkey simétrica
        cifrador.init(Cipher.DECRYPT_MODE, claveSimetrica);

        // Declaracion de  objetos        
        System.out.println("\u001B[34mDescifra con AES el fichero:\n"
                + fileEntrada
                + "\n\u001B[34mEl resultado lo retorna como un String"
                + "\nAES Provider: \u001B[35m" + cifrador.getProvider() + "\u001B[30m\n");
        FileInputStream fe;     //fichero de entrada

        int bytesLeidos;
        fe = new FileInputStream(fileEntrada);

        byte[] bufferClaro = null;
        byte[] buffer = new byte[1000]; //array de bytes

        // Lee el fichero de 1k en 1k y pasa los fragmentos leídos al objeto Cipher
        bytesLeidos = fe.read(buffer, 0, 1000);

        // Mientras no se llegue al final del fichero
        while (bytesLeidos != -1) {

            // Pasa texto cifrado al objeto Cipher y lo descifra, asignándolo a bufferClaro
            bufferClaro = cifrador.update(buffer, 0, bytesLeidos);

            bytesLeidos = fe.read(buffer, 0, 1000);
        }

        //Completa el descifrado        
        bufferClaro = cifrador.doFinal();

        // Objeto String que se devuelve a la GUI 
        String ip = new String(bufferClaro, "UTF-8");

        // Cierra archivos abiertos
        fe.close();

        //Devuelve los datos contenidos en el fichero cifrado,, eliminando salto de línea final
        return ip.trim();
    }
}
