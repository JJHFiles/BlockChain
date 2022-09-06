package BOE;

// Se importan los recursos necesarios
import TemporalObjects.TempBlock;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.*;

/**
 *
 * @author Javier Juarros Huerga
 */
/* Hilo que cera el nodo para comunicarse con la GUI, recibe y responde a peticiones
 * Hay un hilo por conexión con el BoeServer
 */
public class BoeServerListener extends Thread {

    // Numero de la conexión a la que corresponde el hilo
    private int connNum;

    // Socket SSL recibido por el contructor desde el BoeServer
    private Socket client;

    /* Flujos de entrada y salida
     * Para la lectura y envio de información a la GUI. 
     */
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;

    // Bloque temporal utilizado para las comunicaciones Cliente-Servidor. 
    private TempBlock tb = new TempBlock();

    // ArrayList con las líneas leidas del fichero smartcontract.txt
    ArrayList<String> lines = new ArrayList<String>();

    // Constructor, recibe el socket SSL y el numero de conexión
    public BoeServerListener(Socket client, int connNum) {
        System.out.println("\n\u001B[34mIniciado hilo de echucha para la conexión: " + connNum + "\u001B[30m\n");

        // Se copian el cliente u el numero de conexión a atributos locales
        this.client = client;
        this.connNum = connNum;
    }

    // Hilo de escucha de la GUI para validar smartcontracts
    public void run() {

        try {

            // Flujos de entrada y salida de Streams de datos
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());

            // Bucle infito de escucha
            while (true) {
                // Se inicializa el objeto de entrada
                tb = new TempBlock();

                // Se recibe el objeto
                tb = (TempBlock) in.readObject();

                // Se lanza el metodo que inicia la validación
                this.smartcontractValidation(tb);
            }

            // Captura de excepciones
        } catch (IOException ex) {
            System.out.println("\u001B[35mCliente con conexión #" + this.connNum + ", se ha desconectado\u001B[30m");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BoeServerListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo para validar el smart contract, recibe un bloque temporal con los datos a contrastar
    public void smartcontractValidation(TempBlock tb) {

        // Por defecto, se da supuesto false
        tb.setTask("false");

        try {

            // Lee el fichero smartcontracts.txt
            this.readFile();

            // Array que contiene el total de las lineas leídas
            String inputArr[];

            // Solo se usa para mostrar por pantalla la linea completa leída
            String checkLine = "";

            // Se itera enlas líneas del array leído del fichero y se comparan con los datos introducidos desde la GUI 
            for (int x = 0; x < lines.size(); x++) {

                // Se usa en las iteraciones para comparar lo recibido con lo local
                inputArr = lines.get(x).split(",");

                // Solo se usa para mostrar por pantalla la línea completa leida
                checkLine = lines.get(x);

                // Itera comparando los smartcontract que manda la GUI con los del archivo del servidor BOE
                if (inputArr[0].equals(tb.getOffer())
                        && inputArr[1].equals(tb.getTransactionAmount())
                        && inputArr[2].equals(tb.getDeadline())
                        && inputArr[3].equals(tb.getEntityCif())
                        && inputArr[4].equals(tb.getSenderEmail())
                        && inputArr[5].equals(tb.getReceiverCif())
                        && inputArr[6].equals(tb.getReceiverEmail())
                        && inputArr[7].equals("*")) {

                    // Se marca como validada la linea * se pasa a $
                    lines.set(x, lines.get(x).replace('*', '$'));

                    System.out.println("\n\u001B[32mSmart Contract validado:\n" + lines.get(x) + "\u001B[30m");

                    // Se devolverá true
                    tb.setTask("true");

                    // Se guardan los cambios con la linea validada
                    writeFile();

                    // Se deja de iterar, encontrado smartcontract coincidente
                    break;

                    // En caso de no coincidir la línea leída con los datos introducidos desde la GUI
                } else {
                    System.out.println("\n\u001B[31mLinea #" + x + " leida del BOE NO valida para Smartcontract.\u001B[30m"
                            + "\nlocal (BOE):" + checkLine
                            + "\nRemote(GUI):"
                            + tb.getOffer() + ","
                            + tb.getTransactionAmount() + ","
                            + tb.getDeadline() + ","
                            + tb.getEntityCif() + ","
                            + tb.getSenderEmail() + ","
                            + tb.getReceiverCif() + ","
                            + tb.getReceiverEmail()
                            + ",*\n");

                    // Se devolverá false
                    tb.setTask("false");
                }
            }

            // Se envia la contestación a la GUI, true o false
            out.writeObject(tb);

        } catch (MalformedURLException ex) {
            Logger.getLogger(BoeServerListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BoeServerListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Metodo para la lectura del fichero smartcontracts.txt
    public void readFile() {

        try {

            // Apertura del fichero y lectura
            BufferedReader br = new BufferedReader(new FileReader(new File("src\\BOE\\smartcontract.txt")));
            String line;

            // Lectura del fichero, se carga linea a linea en al ArrayList
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

            // Se cierra el recurso
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BoeServerListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BoeServerListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void writeFile() {

        try {
            // Apertura del fichero sobre el que se escriben las líneas con "*" y "$"
            FileWriter fw = new FileWriter("src\\BOE\\smartcontract.txt");
            PrintWriter pw = new PrintWriter(fw);

            // Se graban todas las líneas del ArrayList
            for (String writeLine : lines) {
                pw.println(writeLine);
            }

            // Se cierran los recursos para grabar el fichero
            fw.close();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
