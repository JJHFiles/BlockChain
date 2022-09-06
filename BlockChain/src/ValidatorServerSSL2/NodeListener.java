package ValidatorServerSSL2;

// Se importan los recursos necesarios
import Blockchain.Block;
import Blockchain.Blockchain;
import TemporalObjects.TempBlock;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.*;
/**
 *
 * @author Javier Juarros Huerga
 */
/* Hilo que cera el nodo para comunicarse con la GUI, recibe y respeonde a peticiones
 * Hay un hilo por conexión con el nodo
 */
public class NodeListener extends Thread {

    // Número de la conexión a la que corresponde el hilo
    private int connNum;

    // Socket SSL recibido por el contructor desde el nodo
    private Socket client;

    /* Flujos de entrada y salida
     * Para la lectura y envío de información a la GUI. 
     * También escucha al cliente que le envía las blockchains a sincronizar desde el otro nodo
     */
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    // Flujo para la lectura y escritura del flujo de datos de las blockchains
    private ObjectOutputStream salida = null;
    private ObjectInputStream entrada = null;

    // Objeto para la lectura y escritura del fichero de blockchains
    private FileOutputStream fos = null;
    private FileInputStream fis = null;

    // Bloque temporal utilizado para las comunicaciones Cliente-Servidor. 
    private TempBlock tb = new TempBlock();

    // Blockchains almacenazas hasta su serialización
    private ArrayList<Blockchain> blockchain = null;

    // Blockchain temporales recibidas de la GUI y del cliente de sincronización del otro nodo
    private ArrayList<Blockchain> tempBChain = new ArrayList<Blockchain>();

    // Constante, Url del BOE para smartcontract String 
    private final String BOE_URL_ADDRESS = "http://192.168.0.233/smartcontract.txt";

    // Constante que contiene la dirección del fichero con el objeto serializado de blockchains
    private final String BLOCKCHAIN_FILE = "src\\ValidatorServerSSL2\\persistentBlockchain.dat";

    // Constructor, recibe el socket SSL y el número de conexión
    public NodeListener(Socket client, int connNum) {
        System.out.println("\n\u001B[34mIniciado hilo de echucha para la conexión: " + connNum + "\u001B[30m\n");

        // Se copian el cliente o el número de conexión a atributos locales
        this.client = client;
        this.connNum = connNum;
    }

    // Hilo de escucha de la GUI y del cliente sincronizador del otro nodo
    public void run() {

        // Objeto que captura el Stream recibido
        Object obj = null;

        try {

            // Flujos de entrada y salida de Streams de datos
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

            /* Bucle infito de escucha, lee órdenes atributo task del bloque temporal
             * y lee las blockchains recibidas del otro nodo  a través de su cliente
             * para su sincronización.
             */
            while (true) {

                // Se inicializan los objetos de entrada
                // Si el objeto que viene de la GUI
                tb = new TempBlock(); 
                
                
                obj = new Object();

                // Se recibe el objeto bloqueante
                obj = (Object) in.readObject();

                // Se comprueba que el objeto recibido es de tipo TembBlock para su correcto casting, viene de la GUI
                if (obj.getClass().getName().contains("TempBlock")) {

                    // Se recibe el bloqueTemporal y se castea
                    tb = (TempBlock) obj;

                    /* Los siguientes "if" reciben de las comunicaciones entrantes de los clientes
                 * Las órdenes se leen a través String leído en tb.getTask()
                     */
                    // Si se solicita una validación de Login desde GUI
                    if (tb.getTask().equals("receiveClientValidation")) {
                        getClientValidation();

                        // La GUI solicita todas las entidades y cifs
                    } else if (tb.getTask().equals("receiveEntitiesAndCifs")) {
                        getEntitiesAndCifs();

                        // GUI pide una entidad a partir de un entityCIF
                    } else if (tb.getTask().equals("receiveEntityAndEntityCif")) {
                        getEntityAndEntityCif();

                        // GUI recibe una blockchain a partir de un entityCIF
                    } else if (tb.getTask().equals("receiveBlockchain")) {
                        getBlockchainByEntityCif();

                        // La GUI solicita agregar un nuevo bloque
                    } else if (tb.getTask().equals("addNewBock")) {
                        addNewBlock(tb);

                        // La GUI solicita crear una nueva blockchain y agregar su primer bloque
                    } else if (tb.getTask().equals("registryNewBlockchain")) {
                        registryNewBlockchain(tb);

                        // La GUI solicita ejecutar una validación de smartcontract
                    } else if (tb.getTask().equals("receiveSmartContractValidation")) {
                        smartcontractValidation(tb);
                    }

                    // recibe las blockchains del otro nodo cliente para su sincronización, viene de SpreadProcess
                } else if (obj.getClass().getName().contains("ArrayList")) {
                    tempBChain = (ArrayList<Blockchain>) obj;
                    syncBlockchains();

                    // En caso de recibir algun objeto que no entiende el Nodo, se descarta y no hace nada
                } else {
                    System.out.println("Servidor ha recibido un Stream que no entiende, solo acepta Objetos");
                }
            }

            // Captura de excepciones
        } catch (IOException ex) {
            System.out.println("\u001B[35mEl cliente se ha desconectado, excepcion generada java.net. SocketException( Connection reset)\u001B[30m");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método para validar el smart contract, recibe un bloque temporal con los datos a contrastar
    public void smartcontractValidation(TempBlock tb) {

        // Por defecto se da supuesto false
        tb.setTask("false");

        // URL del BOE
        URL url = null;

        try {

            // Se asigna la URL
            url = new URL(BOE_URL_ADDRESS);

            // Se conecta con el servidor WEB del BOE para leer el fichero "smartcontract.txt"
            URLConnection URLc = url.openConnection();

            // Flujos de entrada de datos
            InputStreamReader inputStreamReader = new InputStreamReader(URLc.getInputStream());
            BufferedReader in = new BufferedReader(inputStreamReader);

            // Contiene la línea leída del fichero
            String inputLine = null;

            // array que contiene el total de las líneas leídas
            String inputArr[];

            // Se itera enlas líneas del array leído del fichero y se comparan con los datos introducidos desde la GUI 
            while ((inputLine = in.readLine()) != null) {
                inputArr = inputLine.split(",");

                if (inputArr[0].equals(tb.getOffer())
                        && inputArr[1].equals(tb.getTransactionAmount())
                        && inputArr[2].equals(tb.getDeadline())
                        && inputArr[3].equals(tb.getEntityCif())
                        && inputArr[4].equals(tb.getSenderEmail())
                        && inputArr[5].equals(tb.getReceiverCif())
                        && inputArr[6].equals(tb.getReceiverEmail())) {
                    System.out.println("\n\u001B[32mSmart Contract validado:\n" + inputLine + "\u001B[30m");

                    // Se devolverá true
                    tb.setTask("true");

                    // se deja de iterar
                    break;

                    // En caso de no coincidir la línea leída con los datos introducidos desde la GUI
                } else {
                    System.out.println("\nLinea leída del BOE no válida para Smartcontract."
                            + "\nRemote(BOE):" + inputLine
                            + "\nLocal (GUI):"
                            + tb.getOffer() + ","
                            + tb.getTransactionAmount() + ","
                            + tb.getDeadline() + ","
                            + tb.getEntityCif() + ","
                            + tb.getSenderEmail() + ","
                            + tb.getReceiverCif() + ","
                            + tb.getReceiverEmail());

                    // Se devolverá false
                    tb.setTask("false");
                }
            }

            // se cierran los recursos abiertos
            in.close();

            // Se envía la contestación a la GUI, true o false
            out.writeObject(tb);

        } catch (MalformedURLException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Servidor BOE no disponible(XAMPP): " + BOE_URL_ADDRESS);
        }
    }

    // Método para registrar nuevas blockchain y agregar el primer bloque
    public void registryNewBlockchain(TempBlock tb) {

        try {

            // Primero se comprueba si existe un fichero serializado de blockchains, si no es así se crea uno.
            File file = new File(BLOCKCHAIN_FILE);

            // Se crea el fichero de blockchain ya que no existe ningúna blockchain previa
            if (!file.exists()) {
                System.out.println("Fichero blockchain no existe se creará uno nuevo\n");

                // Se prepara el objeto de blockchains
                blockchain = new ArrayList<Blockchain>();

                // Agregando nueva blockhain
                blockchain.add(new Blockchain("1", tb.getBankBalance(), tb.getEntity(), tb.getEntityCif(), tb.getPassword())); // difficulty = Zeros = 1
                System.out.println("\n"
                        + "Nueva blockchain creada:\n"
                        + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getEntity() + "\n"
                        + "CIF: " + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getEntityCif() + "\n"
                        + "Initial Balance: " + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getBankBalance());

                // Se devuelve a la GUI que la blockchain ha sido agregada correctamente.
                tb.setTask("true");

                // se serializa la blockchain en el fichero
                writePersistenceBlockchain();

                System.out.println("\nSe ha creado un nuevo fichero para blockchains\n");

                // En este caso si existe un fichero serializado con las blockchains
            } else {
                System.out.println("Fichero blockchain ya existe");

                // Se leen las blockchains actuales para comprobar si ya existe otra con el cif de la nueva blockchain a crear
                readAllBlockchainsFromFile();

                // Si no hay ningúna blockchain creada con anterioridad
                if (blockchain.size() == 0) {
                    System.out.println("Blockchain local de size " + blockchain.size() + " se agregará la primera");

                    // Se agrega la blockchain al array de blockchains
                    blockchain.add(new Blockchain("1", tb.getBankBalance(), tb.getEntity(), tb.getEntityCif(), tb.getPassword())); // difficulty = Zeros = 1
                    System.out.println("\n"
                            + "Nueva blockchain creada:\n"
                            + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getEntity() + "\n"
                            + "CIF: " + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getEntityCif() + "\n"
                            + "Initial Balance: " + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getBankBalance());

                    // Se devolverá true,  blockchain creada
                    tb.setTask("true");

                    // Se serializa el fichero con la nueva blockchain
                    writePersistenceBlockchain();

                    // Si existen blockchains previas
                } else {

                    // se iteran las blockchains previas para comprobar si ya existe una con ese entityCIF
                    for (Blockchain bc : blockchain) {
                        Block b = bc.getBlocks().get(0);

                        // Si ya existe una no se agrega el bloque y se comunica a la GUI
                        if (b.getEntityCif().equals(tb.getEntityCif())) {
                            System.out.println("\u001B[31mNo se creará la nueva Blockchain, ya existe una con ese CIF: " + tb.getEntityCif() + "\u001B[30m");

                            // devuelve a la GUI false
                            tb.setTask("false");

                            // En caso de no existir una blockchain con ese cif se agrega
                        } else {

                            // Agregando nueva blockhain
                            blockchain.add(new Blockchain("1", tb.getBankBalance(), tb.getEntity(), tb.getEntityCif(), tb.getPassword())); // difficulty = Zeros = 1
                            System.out.println("\n"
                                    + "Nueva blockchain creada:\n"
                                    + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getEntity() + "\n"
                                    + "CIF: " + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getEntityCif() + "\n"
                                    + "Initial Balance: " + blockchain.get(blockchain.size() - 1).getBlocks().get(0).getBankBalance());

                            // Se devuelve a la GUI true
                            tb.setTask("true");

                            // Se serializa la blockchain con el nuevobloque agregado
                            writePersistenceBlockchain();

                            //Fin del for, bloque agregado
                            break;
                        }
                    }
                }
            }

            // Se envía respuesta a la GUI, true o false si no se agrego el bloque
            out.writeObject(tb);

        } catch (IOException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Validación del login de la entidad en la GUI
    public void getClientValidation() {

        try {

            // Para comprobar si existe fichero serializado previo con blockchains
            File file = new File(BLOCKCHAIN_FILE);

            // Si no existe el fichero se comunica a la GUI que se debe generar una nueva entidad
            if (!file.exists()) {
                System.out.println("\u001B[31mNo existe un archivo de blockchains.\n"
                        + "La entidad no esta registrada, debe crear desde GUI una Entidad nueva, debe seleccionar (New Entity Registry)\u001B[30m");

                // respuesta a la GUI
                tb.setTask("false");

                /* Si existe fichero serialidado de blockchains, se comprueba
             * si coinciden los datos introducidos en la GUI con alguna blockchain
                 */
            } else {

                // Se leen todas las blockchains
                this.readAllBlockchainsFromFile();

                // Se itera comprobando todos los entityCIF con el entityCif recibido
                for (Blockchain bc : blockchain) {
                    Block b = bc.getBlocks().get(0);

                    // Si se encuentra un cif igual se valida el acceso para efectuar transacciones en la GUI
                    if (b.getEntityCif().equals(tb.getEntityCif())) {
                        System.out.println("CIF del usuario correcto");

                        //La password se valida del último bloque introducido
                        b = bc.getBlocks().get(bc.getBlocks().size() - 1);
                        if (b.getPassword().equals(tb.getPassword())) {
                            System.out.println("Password del usuario correcta\nLogin valido.");

                            tb.setTask("true");
                            tb.setEntity(b.getEntity());

                            break; //Dejar de iterar

                            // La password no ha sido validad correctamente, no coinciden
                        } else {
                            tb.setTask("false");
                            System.out.println("Password del usuario erróneo");

                        }

                        // En caso de que ningún cif coincida con el cif recibido
                    } else {
                        tb.setTask("false");
                        System.out.println("entityCif de la entidad no existe");
                    }
                }
            }

            // respuesta envíada a la GUI, true o false si la entidad no ha sido validada correctamente
            out.writeObject(tb);
        } catch (IOException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Devuelve un nombre de entidad a partir de su entityCif
    private void getEntityAndEntityCif() {

        // Se lee el fichero serializado de blockchains
        this.readAllBlockchainsFromFile();

        try {

            // Se busca en todas las blockchains si existe ese entityCif
            for (int x = 0; x < blockchain.size(); x++) {

                // Se comparan los cifs
                if (blockchain.get(x).getBlocks().get(0).getEntityCif().equals(tb.getEntityCif())) {

                    // Se inicializa el bloque temporal para la respuesta ala GUI
                    tb = new TempBlock();

                    // Se guardan en tb entidad y cif
                    tb.setEntity(blockchain.get(x).getBlocks().get(0).getEntity());
                    tb.setEntityCif(blockchain.get(x).getBlocks().get(0).getEntityCif());
                }
            }
            // Se envía la respuesta a la GUI
            out.writeObject(tb);
        } catch (IOException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Devuelve un array con todas las entidades y cifs existentes
    public void getEntitiesAndCifs() {

        // Se lee el fichero con las blockchains
        this.readAllBlockchainsFromFile();

        try {

            // Se inicializa el objeto de respuesta
            ArrayList<TempBlock> arrTb = new ArrayList<TempBlock>();

            // Se leen todas las blockchains y se copian sus nombres de entidad y sus cifs
            for (int x = 0; x < blockchain.size(); x++) {
                TempBlock tb = new TempBlock();

                tb.setEntity(blockchain.get(x).getBlocks().get(0).getEntity());
                tb.setEntityCif(blockchain.get(x).getBlocks().get(0).getEntityCif());

                // Se agregan en el array entidad y cif una a una
                arrTb.add(tb);
            }

            // Se envía array de respuesta a la GUI
            out.writeObject(arrTb);
        } catch (IOException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // devuelve un array con la blockchain solicitada por entityCif 
    public void getBlockchainByEntityCif() {

        // se lee el fichero de blockchains
        this.readAllBlockchainsFromFile();

        try {

            // Array que devolverá a la GUI con la blockchain solicitada
            ArrayList<TempBlock> arrTb = new ArrayList<TempBlock>();

            // Se itera buscando la blockchain
            for (Blockchain bc : blockchain) {

                /*  Si la entityCif del primer bloque coincide con la entityCif
                 *  que se busca se copiarán los bloques restantes de esa blockchain
                 */
                if (bc.getBlocks().get(0).getEntityCif().equals(tb.getEntityCif())) {

                    // Copiando bloque a bloque y agregándose al array que se retornará
                    for (Block b : bc.getBlocks()) {
                        TempBlock tb = new TempBlock();
                        tb.setEntity(b.getEntity());
                        tb.setEntityCif(b.getEntityCif());
                        tb.setSecondEntity(b.getSecondEntity());
                        tb.setSecondEntityCif(b.getSecondEntityCif());
                        tb.setSenderCif(b.getSenderCif());
                        tb.setReceiverCif(b.getReceiverCif());
                        tb.setBankBalance(b.getBankBalance());
                        tb.setIndex(b.getIndex());
                        tb.setTransactionAmount(b.getTransactionAmount());
                        tb.setTransactionId(b.getTransactionId());
                        tb.setSenderEmail(b.getSenderEmail());
                        tb.setReceiverEmail(b.getReceiverEmail());
                        tb.setOffer(b.getOffer());
                        tb.setDeadline(b.getDeadline());
                        tb.setTimestamp(b.getTimestamp());

                        // Se agrega el bloque al array
                        arrTb.add(tb);
                    }
                }
            }

            // Se devuelve el array de bloques, blockchain solicitada
            out.writeObject(arrTb);

        } catch (IOException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Para agregar un nuevo bloque. El bloque se pasa como parámetro tb
    public void addNewBlock(TempBlock tb) {
        try {

            // Se crea un array de bloques temporal  par devolverlo posteriormente
            ArrayList<TempBlock> arrTb = new ArrayList<TempBlock>();

            // Se lee el fichero con las blockchains, y se copia suu contenido en el objeto blockchain
            readAllBlockchainsFromFile();

            // Se itera hasta encontrar la blockchain en la que se agregará el bloque, se busca por entityCif
            for (int x = 0; x < blockchain.size(); x++) {

                // Se compara la entity cif recibida con las del primer bloque de todas las blockchains
                if (blockchain.get(x).getBlocks().get(0).getEntityCif().equals(tb.getEntityCif())) {

                    // // se agrega el bloque validandose y minandose
                    blockchain.get(x).addBlock(blockchain.get(x).newBlock(
                            tb.getTransactionAmount(),
                            tb.getEntity(),
                            tb.getEntityCif(),
                            tb.getSecondEntity(),
                            tb.getSecondEntityCif(),
                            tb.getSenderCif(),
                            tb.getReceiverCif(),
                            tb.getSenderEmail(),
                            tb.getReceiverEmail(),
                            tb.getOffer(),
                            tb.getDeadline(),
                            // Se copia la password del último bloque
                            blockchain.get(x).getBlocks().get(blockchain.get(x).getBlocks().size() - 1).getPassword()
                    ));
                }
            }

            // Se serializa la blockchain con el nuevo bloque
            writePersistenceBlockchain();

            // Se muestra las blockchains almacenadas
            System.out.println(""
                    + "\n=============================================================================================="
                    + blockchain
                    + "==============================================================================================");

            // Es necesario devolver un objeto para que continue el hilo de ejecución correctamente
            out.writeObject(arrTb);

        } catch (IOException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    // Método que lee la blockchain del fichero serializado y lo carga en el objeto blockchain
    public void readAllBlockchainsFromFile() {
        try {

            // Flujos de datos para la lectura del fichero
            FileInputStream fis = null;
            ObjectInputStream entrada = null;

            // Se inicializan los objetos para el flujo
            fis = new FileInputStream(BLOCKCHAIN_FILE);
            entrada = new ObjectInputStream(fis);

            // Se castea el objeto leído del fichero copiandose en la blockchain de la clase
            blockchain = (ArrayList<Blockchain>) entrada.readObject();

            // Se cierran los recursos abiertos
            fis.close();
            entrada.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método para la persistencia de la blockchain serializándola en el fichero "persistentBlockchain.dat"
    private void writePersistenceBlockchain() {
        try {

            // Flujos de datos para la lectura del fichero
            FileOutputStream fos = null;
            ObjectOutputStream salida = null;

            // Se inicializan los objetos para el flujo
            fos = new FileOutputStream(BLOCKCHAIN_FILE);
            salida = new ObjectOutputStream(fos);

            // Se serializa el objeto Blockchain y se graba en el fichero
            salida.writeObject(blockchain);

            // Se cierran los recursos abiertos
            fos.close();
            salida.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NodeListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Método que sincroniza la blockchain local con la blockchain recibida (TempBChain)
    public void syncBlockchains() {
        //   try {
        // Se comprueba si existe el fichero de blockchains
        File file = new File(BLOCKCHAIN_FILE);

        //Se comprueba si existe fichero blockchain si es asi se leen las blockchains
        if (file.exists()) {

            // Se leen las blockchain sel fichero serializado y se carga en el objeto blockchain
            readAllBlockchainsFromFile();

            /* Se copian los bloques iniciales que falten              
            * Se itera la blockchain local y se compara con la recibida para no duplicar blockchains
            * Primero se copian y generan los primeros boques, el bloque inicial tienen index 0
             */
            for (Blockchain iTb : tempBChain) {

                // Se buscan si ya existen si no es asi se copian
                boolean isExistBc = false;
                for (Blockchain iBc : blockchain) {
                    if (iTb.getBlocks().get(0).getEntityCif().equals(iBc.getBlocks().get(0).getEntityCif())) {
                        isExistBc = true;
                    }
                }
                if (!isExistBc) {
                    tb = new TempBlock();
                    tb.setBankBalance(iTb.getBlocks().get(0).getBankBalance());
                    tb.setEntity(iTb.getBlocks().get(0).getEntity());
                    tb.setEntityCif(iTb.getBlocks().get(0).getEntityCif());
                    tb.setPassword(iTb.getBlocks().get(0).getPassword());
                    System.out.println("\nSe escribira bloque inicial (index = 0): " + tb.getEntity() + "\n");

                    // Se graba ese bloque inicial
                    this.registryNewBlockchain(tb);

                    System.out.println("\nCreado Bloque inicial (index = 0): " + tb.getEntity() + "\n");
                }
            }

            // Si el fichero de blockchains no existe se crea pero sin blockchains, Arraylist se size() == 0
        } else {

            // Se crea el array de blockchains
            blockchain = new ArrayList<Blockchain>();

            // Se graba el arrayList de blockchains vacio
            writePersistenceBlockchain();

            // Primero se copian y generan los primeros boques, el bloque inicial tienen index 0
            for (Blockchain iTb : tempBChain) {
                tb = new TempBlock();
                tb.setBankBalance(iTb.getBlocks().get(0).getBankBalance());
                tb.setEntity(iTb.getBlocks().get(0).getEntity());
                tb.setEntityCif(iTb.getBlocks().get(0).getEntityCif());
                tb.setPassword(iTb.getBlocks().get(0).getPassword());
                System.out.println("\nSe escribira bloque inicial (index = 0): " + tb.getEntity() + "\n");

                // Se graba ese bloque inicial
                this.registryNewBlockchain(tb);

                System.out.println("\nCreado Bloque inicial (index = 0): " + tb.getEntity() + "\n");

            }

        }

        /*  Se iteran las blockchains recibidas y se van copiando a la 
         *  blockchain local, se hace bloque a bloque minando y validandolas
         */
        // Si la blockchain recibida tiene blockchains estas se copiarán
        if (tempBChain.size() > 0) {

            // Se itera en la blockchain local
            for (int x = 0; x < blockchain.size(); x++) {

                /* Se comprueba el size de la blockchain local con la
                 * recibida si es menor se copian los bloques que faltan
                 * uno a uno minandose y validandose .
                 */
                if ((blockchain.get(x).getBlocks().size() < tempBChain.get(x).getBlocks().size())) {
                    int substract = 0;

                    //Se calculan los bloques que faltan para mostrarlo por consola
                    substract = tempBChain.get(x).getBlocks().size() - blockchain.get(x).getBlocks().size();

                    // Se muestra informacion sobre l ablockchain a sincronizar y el número de bloques que le faltan
                    System.out.println("\n"
                            + "BLOCKCHAIN NO SINCRONIZADA, Faltan " + substract + " bloques"
                            + "\nA la blockchain perteneceinte a: "
                            + blockchain.get(x).getBlocks().get(0).getEntity()
                            + "le faltan bloques, se procede a su sincronización"
                            + " con los nuevos bloques recibidos\n"
                            + "\u001B[35mLocal: " + blockchain.get(x).getBlocks().get(0).getEntity()
                            + "=" + blockchain.get(x).getBlocks().size()
                            + "\n\u001B[35mRecibido: " + tempBChain.get(x).getBlocks().get(0).getEntity()
                            + "=" + tempBChain.get(x).getBlocks().size() + ""
                            + "\n\nSe procede a su sincronización\n");

                    // Se procede a la sincronización bloque a bloque
                    for (int y = blockchain.get(x).getBlocks().size(); y < tempBChain.get(x).getBlocks().size(); y++) {

                        // Bloque nuevo a sincronizar
                        TempBlock tb = new TempBlock();

                        // Datos del bloque nuevo que se copiará en la blockchain local
                        tb.setTransactionAmount(tempBChain.get(x).getBlocks().get(y).getTransactionAmount());
                        tb.setEntity(tempBChain.get(x).getBlocks().get(y).getEntity());
                        tb.setEntityCif(tempBChain.get(x).getBlocks().get(y).getEntityCif());
                        tb.setSenderCif(tempBChain.get(x).getBlocks().get(y).getSenderCif());
                        tb.setSecondEntityCif(tempBChain.get(x).getBlocks().get(y).getSecondEntityCif());
                        tb.setSenderCif(tempBChain.get(x).getBlocks().get(y).getSenderCif());
                        tb.setReceiverCif(tempBChain.get(x).getBlocks().get(y).getReceiverCif());
                        tb.setSenderEmail(tempBChain.get(x).getBlocks().get(y).getSenderEmail());
                        tb.setReceiverEmail(tempBChain.get(x).getBlocks().get(y).getReceiverEmail());
                        tb.setOffer(tempBChain.get(x).getBlocks().get(y).getOffer());
                        tb.setDeadline(tempBChain.get(x).getBlocks().get(y).getDeadline());

                        // Se agrega el nuevo bloque
                        addNewBlock(tb);
                    }

                    // En este caso no se hace nada, la bloackchain esta sincronizada, se comunica por consola.
                } else {
                    System.out.println("\u001B[32mLa blockchain " + blockchain.get(x).getBlocks().get(0).getEntity() + " esta sincronizada\u001B[30m");
                }
            }
        }
    }
}
