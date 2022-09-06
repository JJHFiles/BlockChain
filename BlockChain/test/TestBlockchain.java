
import Blockchain.Block;
import Blockchain.Blockchain;

import java.util.ArrayList;

/**
 *
 * @author Javier Juarros huerga
 */
public class TestBlockchain {

    ArrayList<Blockchain> blockchain_1 = null;


    public static void main(String[] args) {
        TestBlockchain test = new TestBlockchain();
        
        test.correctBlockchain();
        test.invalidHash();
        
        test.correctBlockchain();
        test.insertNewBlock();
        


    }

    public void correctBlockchain() {
        blockchain_1 = new ArrayList<Blockchain>();

        blockchain_1.add(new Blockchain("1", "1000000", "Ayuntamiento de Collado Villalba", "A44376411", "password1"));

        blockchain_1.get(0).newBlock("1000000", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021");
        blockchain_1.get(0).newBlock("1000000", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021");
        //  blockchain_1.get(0).newBlock("1000000", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021");
        //  blockchain_1.get(0).newBlock("1000000", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021");

        System.out.println(blockchain_1);
        System.out.println("\n\u001B[30mEs válida la blockchain generada? => " + blockchain_1.get(0).isBlockChainValid() + "\n");

    }

// mete un bloque con un hash que coincide con el anterior
    public void invalidHash() {

        System.out.println("Se agrega un bloque que corrompe la blockchain, tiene un hash válido pero"
                + "\nque no pertenece a la sucesión de hashes de la blockchain\n\n");

        blockchain_1.get(0).addBlock(new Block("3", "123456789013", "0b932409941d21a6f8fbde6f78e64aa3bd15519e204933e89dc516f5fe9b784a", "0d332409941d21a6f8fbde6f78e64aa3bd15519e204933e89dc516f5fe9b783a", "1000000", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021"));

        System.out.println("\n\u001B[30mPara comprobarlo se valida la blockchain con le metodo isBlockChainValid()"
                + "\nEs válida la blockchain generada? => \u001B[31m" + blockchain_1.get(0).isBlockChainValid() + "\u001B[30m\n");

    }

    // mete un bloque entre bloques generados, rompiendo la secuencia logica de indices de la blockchain_1
    public void insertNewBlock() {

        System.out.println("Se inserta nuevo bloque entre la posicion 1 y la 2\n");

        blockchain_1.get(0).getBlocks().add(1, blockchain_1.get(0).newBlock("1000000", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021"));

        System.out.println("\n\u001B[30mPara comprobarlo se valida la blockchain con le método isBlockChainValid()"
                + "\nEs válida la blockchain generada? => \u001B[31m" + blockchain_1.get(0).isBlockChainValid() + "\u001B[30m\n");

    }

    
}
