package Blockchain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier Juarros huerga
 */
// Clase que introduce los bloques en la blockchain
public class Blockchain implements Serializable {

    // Número que identifica el objeto creado con la clase cuando se serializa
    private static final long serialVersionUID = 1010011010L;

    // Número de ceros necesarios al comienzo del hash minado
    private String difficulty;
    private ArrayList<Block> blocks;

    // Contructor para agregar bloques nuevos
    public Blockchain(String difficulty, String bankBalance, String entity,
            String entityCif, String password) {
        this.difficulty = difficulty;
        blocks = new ArrayList<>();

        Block b = new Block(
                0 + "",
                System.currentTimeMillis() + "",
                null,
                0 + "",
                bankBalance + "",
                entity,
                entityCif,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                password);

        b.mineBlock(Integer.parseInt(this.difficulty));
        blocks.add(b);
        System.out.println(""
                + "\u001B[35mEntidad nueva creada: " + entity
                + "\n\u001B[35mSaldo inicial: #" + bankBalance + "# Euros"
                + "\n\u001B[35mGenerado bloque: Inicial\u001B[30m\n");
    }

    // Método para agregar los bloques en la blockchain
    public Block newBlock(String transactionAmount, String entity, String entityCif,
            String secondEntity, String secondEntityCif, String senderCif,
            String receiverCif, String senderEmail, String receiverEmail,
            String offer, String deadline, String password) {

        Block currentBlock = null;
        Block latestBlock = latestBlock();

        // Se comprueba si el crédito en banco es menor que la sustracción
        if ((Double.valueOf(latestBlock.getBankBalance()) + Double.valueOf(transactionAmount)) < 0) {
            System.out.println(""
                    + "\u001B[31mNo hay crédito suficiente en banco."
                    + "\nEntidad que realiza la transacción: " + entity
                    + "\nIntentando sacar: " + transactionAmount
                    + "\nCredito actual: " + latestBlock.getBankBalance()
                    + "\nFin  de transacción");

        } else if (!latestBlock.getEntityCif().equals(entityCif)) {
            System.out.println(""
                    + "\n\u001B[31mEntidad incorrecta, se ha introducido un CIF erróneo"
                    + "\n\u001B[31mNo se puede agregar el nuevo bloque #"
                    + (Double.valueOf(latestBlock.getIndex()) + 2));

        } else {

            // Se comprueba si en la transacción se agregan o extraen euros.
            String amountSign = "";
            String balanceResult = "";

            // Signo de valor +
            if (entityCif.equals(receiverCif)) {
                amountSign = "+";
                balanceResult = (Double.valueOf(latestBlock.getBankBalance()) + Double.valueOf(transactionAmount)) + "";

                // signo de valor negativo -
            } else {
                amountSign = "-";
                balanceResult = (Double.valueOf(latestBlock.getBankBalance()) - Double.valueOf(transactionAmount)) + "";

            }

            currentBlock = new Block(
                    (Integer.parseInt(latestBlock.getIndex()) + 1) + "",
                    System.currentTimeMillis() + "",
                    latestBlock.getHash(),
                    amountSign + transactionAmount,
                    // (Double.valueOf(latestBlock.getBankBalance()) - Double.valueOf(transactionAmount)) + "", 
                    balanceResult,// Acumula las operaciones bancarias de la entidad
                    entity,
                    entityCif,
                    secondEntity,
                    secondEntityCif,
                    senderCif,
                    receiverCif,
                    senderEmail,
                    receiverEmail,
                    offer,
                    deadline,
                    password);

            // Se validan los atributos del bloque
            if (!isBlockChainValid()) {
                currentBlock = null;

                // bloque validado positivamente
            } else {

                System.out.println(""
                        + "\u001B[35mTransacción #" + currentBlock.getIndex() + " admitida: "
                        + transactionAmount + " Euros"
                        + "\n\u001B[35mSaldo actual: " + currentBlock.getBankBalance()
                        + " Euros"
                        + "\nGenerado nuevo bloque\u001B[30m\n");
            }
        }
        return currentBlock;
    }

    // Para agregar bloques nuevos, se realiza el minado
    public void addBlock(Block b) {

        if (b != null) {
            b.mineBlock(Integer.parseInt(difficulty));
            blocks.add(b);
        } else {

            System.out.println("\u001B[31mBloque descartado.");

        }
    }

    // 
    //Se valida si el primer bloque cumple unas condiciones unicas del bloque incial
    public boolean isFirstBlockValid() {
        Block firstBlock = blocks.get(0);

        // Si entra alguno de los siguientes if, el bloque inicial se da como inválido y no se añadiran bloques sobre esta blockchain
        // Su índice ha de ser 0
        if (Integer.parseInt(firstBlock.getIndex()) != 0) {
            System.out.println("\nSe descartara bloque, bloque inicial con índice distino de 0");
            return false;
        
        // No puede tener hash previo
        } else if (firstBlock.getPreviousHash() != null) {

            System.out.println("\nSe descartara bloque, bloque inicial con previoushash:\n" + blocks.get(0).getPreviousHash());
            return false;
        
        // Su hash no puede ser nulo
        } else if (firstBlock.getHash() == null) {
            System.out.println("\nSe descartara bloque, bloque inicial sin hash");
            return false;
        
        // Su hash se recalcula y se valida resultado con el actual
        } else if (!Block.calculateHash(firstBlock).equals(firstBlock.getHash())) {
            System.out.println("\nSe descartara bloque, el hash del bloque inicial no coincide con el generado:"
                    + "\n hash inicial :" + Block.calculateHash(firstBlock)
                    + "\n hash generado:" + firstBlock.getHash());
            return false;
        }

        // Bloque inicial valido
        return true;
    }

    // Se valida el nuevo bloque
    public boolean isValidNewBlock(Block newBlock, Block previousBlock) {

        System.out.println("\u001B[34mValidando nuevo bloque con índice #" + newBlock.getIndex() + "\u001B[30m");

        // Nuevo bloque y bloque previo no pueden ser nulos
        if (newBlock != null && previousBlock != null) {
            
            // Se comprueba que los índices sean consecutivos
            if ((Integer.parseInt(previousBlock.getIndex()) + 1) != Integer.parseInt(newBlock.getIndex())) {
                System.out.println("\nBloque nuevo inválido,índice del nuevo bloque no correlaciona con índice bloque anterior");
                return false;
                
            // El hash previo ha de ser distinto de nulo
            } else if (newBlock.getPreviousHash() == null) {

                System.out.println("\nBloque nuevo inválido, previoushash del nuevo bloque nulo");
                return false;
            
            // El hash del bloque previo debe estar contenido como hash de bloque previo en el nuevo bloque coincide con el hash del bloque nuevo
            } else if (!newBlock.getPreviousHash().equals(previousBlock.getHash())) {
                System.out.println("Validando nuevo bloque con índice #" + newBlock.getIndex());
                System.out.println("\nBloque nuevo inválido, previoushash del nuevo bloque no coincide con el hash del bloque previo");
                return false;
                
            // El hash del nuevo bloque no puede ser nulo
            } else if (newBlock.getHash() == null) {

                System.out.println("\nBloque nuevo inválido, no tiene hash");
                return false;
            // Se recalcula el hash del nuevo bloque, debe coincdir con el hash actual del bloque
            } else if (!Block.calculateHash(newBlock).equals(newBlock.getHash())) {

                System.out.println("\nBloque nuevo inválido, hash del bloque nuevo no coincide con el hash generado:"
                        + "\nHash actual  : " + newBlock.getHash()
                        + "\nHash generado: " + Block.calculateHash(newBlock));
                return false;
            }
            
            // Bloque  nuevo valido
            return true;
        }
        System.out.println("Bloque nuevo inválido, bloque nuevo y previo igual a null");
        return false;
    }

    // Se validan la blockchain bloque a bloque
    public boolean isBlockChainValid() {
        
        System.out.println("\n\u001B[32mValidando la cadena de bloques");
        if (!isFirstBlockValid()) {
            System.out.println("\u001B[32m\n bloque inicial validado negativamente");
            return false;
        }
        
        for (int i = 1; i < blocks.size(); i++) {
            Block currentBlock = blocks.get(i);
            Block previousBlock = blocks.get(i - 1);

            if (!isValidNewBlock(currentBlock, previousBlock)) {
                System.out.println("\u001B[31mHash inválido, bloque descartado\u001B[30m");
                return false;
            }
        }
        System.out.println("\n\u001B[32mCadena de bloques validada positivamente");
        return true;
    }


    public String getDifficulty() {
        return difficulty;
    }

    public Block latestBlock() {
        return blocks.get(blocks.size() - 1);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Block block : blocks) {
            builder.append(block).append("\n");
        }

        return builder.toString();
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

}
