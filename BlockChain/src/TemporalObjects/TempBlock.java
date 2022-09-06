package TemporalObjects;

import java.io.Serializable;
/**
 *
 * @author Javier Juarros Huerga
 */

/* Clase que replica un bloque de la blockchain, se utilizan de forma temporal 
 * en los algoritmos que interactuan con las blockchains.
 *  También se utilizan para el envio de información entre cliente-Servidor
 */
public class TempBlock implements Serializable {

    // Numero que identifica el objeto creado con la clase a la que pertenece
    private static final long serialVersionUID = 1010011010L;
    
    /* Atributo para labores variadas como transmision de información
     * que no depende del bloque, como respuestas y preguntas entre
     * cliente y servidor
     */ 
    private String task;

    // Atributos que coinciden con los de la blockchain
    private String password;

    private String nodeIp;
    private String nodePort;

    private String difficulty;

    private String index;
    private String timestamp;
    private String hash;
    private String previousHash;
    private String nonce;

    private String transactionId;
    private String transactionAmount;
    private String bankBalance;

    private String entity;
    private String entityCif;
    private String secondEntity;
    private String secondEntityCif;
    private String senderCif;
    private String receiverCif;

    private String senderEmail;
    private String receiverEmail;
    private String offer;
    private String deadline;

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public String getNodePort() {
        return nodePort;
    }

    public void setNodePort(String nodePort) {
        this.nodePort = nodePort;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(String transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(String bankBalance) {
        this.bankBalance = bankBalance;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getEntityCif() {
        return entityCif;
    }

    public void setEntityCif(String entityCif) {
        this.entityCif = entityCif;
    }

    public String getSecondEntity() {
        return secondEntity;
    }

    public void setSecondEntity(String secondEntity) {
        this.secondEntity = secondEntity;
    }

    public String getSecondEntityCif() {
        return secondEntityCif;
    }

    public void setSecondEntityCif(String secondEntityCif) {
        this.secondEntityCif = secondEntityCif;
    }

    public String getSenderCif() {
        return senderCif;
    }

    public void setSenderCif(String senderCif) {
        this.senderCif = senderCif;
    }

    public String getReceiverCif() {
        return receiverCif;
    }

    public void setReceiverCif(String receiverCif) {
        this.receiverCif = receiverCif;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}
