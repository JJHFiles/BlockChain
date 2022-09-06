package Blockchain;

import java.io.Serializable;
import static java.lang.Long.toHexString;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 *
 * @author JavierJuarros Huerga
 */

// Clase que contiene todos los datos del bloque para su creación.
public class Block implements Serializable {

    // Numero que identifica el objeto creado con la clase a la que pertenece
    private static final long serialVersionUID = 1010011010L;

    // Datos que se validan para seguridad e integridad de la blockchain
    private String index;
    private String timestamp;
    private String hash;
    private String previousHash;
    private String nonce; // Numero de hases que han sido necesarios generar hasta encontrar el correcto definido por la dificultad ZEROS

    // Datos de la transacción
    private String transactionId; //Concatenación del hash + toHexString(timestamp)
    private String transactionAmount;
    private String bankBalance;

    // Datos de las entidades implicadas en la transacción
    private String entity; // Entidad propietara del Bloque 
    private String entityCif; // CIF del propietario del bloque
    private String secondEntity;
    private String secondEntityCif;
    private String senderCif;
    private String receiverCif;

    // datos necesarios para validar los Smartcontracts
    private String senderEmail;
    private String receiverEmail;
    private String offer;
    private String deadline;

    // password del usuario. Siempre se da como válido el ingresado en el último bloque
    private String password;

    // Constructor para generar un nuevo bloque
    public Block(String index, String timestamp, String previousHash,
            String transactionAmount, String bankBalance, String entity,
            String entityCif, String secondEntity, String secondEntityCif,
            String senderCif, String receiverCif, String senderEmail,
            String receiverEmail, String offer, String deadline, String password) {

        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.nonce = 0 + "";
        this.transactionAmount = transactionAmount;
        this.bankBalance = bankBalance;
        this.entity = entity;
        this.entityCif = entityCif;
        this.secondEntity = secondEntity;
        this.secondEntityCif = secondEntityCif;
        this.senderCif = senderCif;
        this.receiverCif = receiverCif;
        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;
        this.offer = offer;
        this.deadline = deadline;
        this.password = password;

        // Hash del bloque
        this.hash = Block.calculateHash(this);
        
        // La id de la transacción, se obtiene casteando long a String y luego se pasa a hexadecimal
        this.transactionId = hash + toHexString(Long.valueOf(timestamp).byteValue());

    }

    // Retorna la información que se muestra por pantalla tras la creación del bloque
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nBlock #")
                .append((Integer.parseInt(index) + 1) + "º - " + entity)
                .append("\n\n\t  Index             : ")
                .append(index)
                .append("\n\t  PreviousHash      : ")
                .append(previousHash)
                .append("\n\t  CurrentHash       : ")
                .append(hash)
                .append("\n\t  Time Stamp(Date)  : ")
                .append(timestamp + " - " + new Date(Long.valueOf(timestamp)))
                .append("\n\t  Transaction ID    : ")
                .append(transactionId)
                .append("\n\t  Transaction Amount: ")
                .append(transactionAmount + " Euros")
                .append("\n\t  Bank Balance      : ")
                .append(bankBalance + " Euros")
                .append("\n\t  Entity            : ")
                .append(entity)
                .append("\n\t  Entity CIF        : ")
                .append(entityCif)
                .append("\n\t  Second Entity     : ")
                .append(secondEntity)
                .append("\n\t  Second Entity CIF : ")
                .append(secondEntityCif)
                .append("\n\t  SenderCif         : ")
                .append(senderCif)
                .append("\n\t  ReceiverCif       : ")
                .append(receiverCif)
                .append("\n\t  Sender Email      : ")
                .append(senderEmail)
                .append("\n\t  Receiver Email    : ")
                .append(receiverEmail)
                .append("\n\t  Offer             : ")
                .append(offer)
                .append("\n\t  Deadline          : ")
                .append(deadline)
                .append("\n\t  Nonce             : ")
                .append(nonce);
        
        return builder.toString();
    }

    
    //  A partir del un String generado por la concatenación de todos los atributos se calcula el hash
    public static String calculateHash(Block block) {
        if (block != null) {
            
            // Algoritmo de resumen de mensajes, como SHA-1 o SHA-256
            MessageDigest digest = null;

            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }

            String txt = block.str();
            final byte bytes[] = digest.digest(txt.getBytes());
            final StringBuilder builder = new StringBuilder();

            for (final byte b : bytes) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    builder.append('0');
                }

                builder.append(hex);
                System.out.println("builder: " + builder.toString());
            }

            return builder.toString();
        }

        return null;
    }

    // Minado del bloque, búsqueda de un número de Ceros iniciales
    public void mineBlock(int difficulty) {
        nonce = 0 + "";
        System.out.println("\n\u001B[32mComienza el minado");
        
        // Se busca el hash con la dificultad requerida
        while (!getHash().substring(0, difficulty).equals(zeros(difficulty))) {
            nonce = (Integer.parseInt(nonce) + 1) + "";
            hash = Block.calculateHash(this);
            System.out.println("Minando: " + hash);
        }
        System.out.println("\u001B[32mFin del minado\n");
    }
    
    // Devuelve los ceros
    public String zeros(int length) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < length; i++) {
            builder.append('0');
        }
        return builder.toString();
    }

    // Devuelve un String generado por la concatenación de todos los atributos
    public String str() {
        return index + timestamp + previousHash + transactionId + transactionAmount
                + bankBalance
                + entity + entityCif + secondEntity + secondEntityCif + senderCif
                + receiverCif + senderEmail + receiverEmail + offer + deadline + nonce + password;
    }

    // Solo getters
    public String getIndex() {
        return index;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getBankBalance() {
        return bankBalance;
    }

    public String getEntity() {
        return entity;
    }

    public String getEntityCif() {
        return entityCif;
    }

    public String getSecondEntity() {
        return secondEntity;
    }

    public String getSecondEntityCif() {
        return secondEntityCif;
    }

    public String getSenderCif() {
        return senderCif;
    }

    public String getReceiverCif() {
        return receiverCif;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public String getNonce() {
        return nonce;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getOffer() {
        return offer;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public String getDeadline() {
        return deadline;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getPassword() {
        return password;
    }


}
