package GUI3;

// Se importan los recursos necesarios
import EntityClientSSL.GUIClient;
import TemporalObjects.TempBlock;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingConstants;

/**
 *
 * @author Javier Juarros Huerga
 */

/* Frame/Ventana desde donde el usuario solicita las transacciones al nodo a través de un cliente Socket SSL 
 * El cliente Socket, los datos de la entidad, e IPs vienen de la GUILogin a través del constructor
 */
public class GUITransactions extends javax.swing.JFrame {

    // Datos reutilizables necesarios para efectuar las transacciones
    private String entityCif = "Unknow";
    private String entity = "Unknow";
    private String receiverEntityCif = "Unknow";
    private double balance = 0;

    // Cliente que se comunica con el nodo
    private GUIClient client;

    // IPs que entran por el constructor
    private String nodeIp1 = "";
    private String nodeIp2 = "";
    private String smartcontractIp = "";

    // Contiene el nodo que esta disponible, se busca con el metodo setAliveNode()
    private String aliveNode = "nodeIp1";

    // Bloque temporal para enviar y recibir las consultas al nodo
    private TempBlock tb = new TempBlock();

    // Array de bloques temporales para enviar y recibir las consultas al nodo, es una imagen a su blockchain
    private ArrayList<TempBlock> arrTb = new ArrayList<TempBlock>();

    // Cliente para conectar con el BoeServer utiliza la IP .233
    GUIClient boeClient = null;

    // Constructor invocado por GUILogin
    public GUITransactions(TempBlock tb, GUIClient c, String nodeIp1, String nodeIp2, String smartcontractIp) {

        // Se inician los componentes, generado por Netbeans
        initComponents();
        
        // IPs de los nodos y smartcontract
        this.nodeIp1 = nodeIp1;
        this.nodeIp2 = nodeIp2;
        this.smartcontractIp = smartcontractIp;

        // Datos de la entidad, vienen desde el contructor a través del TempBlock  tb
        this.entityCif = tb.getEntityCif();
        this.entity = tb.getEntity();

        // Se reciben los datos del cliente a través del constructor
        this.tb = tb;

        // Recibe el objeto GUIClient de la GUILogin, cliente que se ocmunica con el nodo
        this.client = c;

        // Se muestra la informacion en la ventana, cargando la informacion en los componentes visuales
        fillTopJLabel();
        fillJComboBox();
        fillJTextArea();

        // Se muestra la IP del nodo y URL smartcontract al que esta conectado GUIClient
        this.jLabelCurrentNodeIP.setText("Conexión realizada con nodo: " + ((c.getClientSocket().getLocalAddress())+"").substring(1, ((c.getClientSocket().getLocalAddress())+"").length()));
        this.jLabelSmartContractURL.setText("Conexión a Smartcontract: " + this.smartcontractIp);

    }

    // Se muestra la entidad y el entityCif del usuario
    public void fillTopJLabel() {
        this.jLabelEntityName.setText(entity + " - " + entityCif);
      //  this.jLabelEntityName.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // Se carga el ComboBox con las entdades destino con las que hacer transacciones
    public void fillJComboBox() {

        // Se preopara un bloque temporal para hacer la consulta al nodo
        tb = new TempBlock();

        // Se envia al nodo, pide todas las entidades y cifs.
        tb.setTask("receiveEntitiesAndCifs");

        // Array recibido del nodo con las entidades y CIFs
        arrTb = client.getArrTempBlockFromServer(tb);

        this.jComboBox1.removeAllItems();
        // Se itera el arrayrecibido para cargar las enidades y cifs en el COmboBox
        for (TempBlock iTb : arrTb) {

            // Para eliminar la entidad del usuario de la lista de destinatarios, no se pagara a si mismo
            if (!iTb.getEntity().equals(this.entity)) {
                this.jComboBox1.addItem(iTb.getEntity() + "   (CIF)   " + iTb.getEntityCif());
            }
        }
    }

    // Rellena la zona de texto con el conjunto de transacciones que le afectan
    public void fillJTextArea() {

        tb = new TempBlock();

        // Recibe una replica de su blockchain en un array temporal de bloques
        tb.setTask("receiveBlockchain");
        tb.setEntityCif(entityCif);

        // Se hace la petición y se recibe un Array de bloques
        arrTb = client.getArrTempBlockFromServer(tb);

        // Se copia el balance actual de la entidad emisora (cliente).
        this.balance = Double.valueOf(arrTb.get(arrTb.size() - 1).getBankBalance());
        this.jLabelBalance.setText("+" + balance + " Euros");

        // Color del texto del balance, rojo si es negativo y azul si es postivo
        if (balance > 0) {
            this.jLabelBalance.setForeground(Color.BLUE);
        } else {
            this.jLabelBalance.setForeground(Color.GRAY);
        }
        System.out.println("Balance actual: " + balance + " Euros\n");

        this.jTextArea1.setText(""
                + " #    TRANSACTIONS                                       ENTITY:   " + arrTb.get(0).getEntity().toUpperCase() + "    -    CIF:   " + arrTb.get(0).getEntityCif().toUpperCase() + "                                           CURRENT DATE:  " + new Date()
                + "\n---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        String text = "";

        // Se cargan las transacciones en la zona de texto para su visualizacion
        for (int x = 1; x < arrTb.size(); x++) {
            TempBlock tb = new TempBlock();
            TempBlock tb1 = new TempBlock();

            // Para pedir la blockchain de emisor de la transaccion
            tb.setTask("receiveEntityAndEntityCif");

            // Para pedir la blockchain del receptor de la transaccion
            tb1.setTask("receiveEntityAndEntityCif");

            // Se pide la blochain del emisor al nodo
            tb.setEntityCif(arrTb.get(x).getSenderCif());
            tb = client.getBlockFromServer(tb);

            // Se pide la blockchain del receptor al nodo
            tb1.setEntityCif(arrTb.get(x).getReceiverCif());
            tb1 = client.getBlockFromServer(tb1);

            text = (""
                    + text + "\n"
                    + "#" + arrTb.get(x).getIndex()
                    + "   Invoice: " + arrTb.get(x).getOffer()
                    + "   Sender ( " + arrTb.get(x).getSenderCif() + " - " + tb.getEntity() + " )"
                    + "  ->  "
                    + "Receiver ( " + arrTb.get(x).getReceiverCif() + " - " + tb1.getEntity() + " )"
                    + ",  Amount: " + arrTb.get(x).getTransactionAmount() + " Euros"
                    + ",  Balance: " + arrTb.get(x).getBankBalance() + " Euros"
                    + ",  Timestamp: " + new Date(Long.valueOf(arrTb.get(x).getTimestamp()))
                    + ",  Transacction ID: " + arrTb.get(x).getTransactionId() + "\n");

        }

        this.jTextArea1.setText(this.jTextArea1.getText()
                + "\n\n"
                + "#0   Initial balance: " + arrTb.get(0).getBankBalance() + " Euros\n"
                + text);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDialog1 = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldMonto = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel3 = new javax.swing.JLabel();
        jButtonSend = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabelBalance = new javax.swing.JLabel();
        jLabelEntityName = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldOffer = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldSenderEmail = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextFieldReceiverEmail = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldDeadline = new javax.swing.JTextField();
        jLabelCurrentNodeIP = new javax.swing.JLabel();
        jButtonRefreshWindow = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabelSmartContractURL = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LIBRO DE CUENTAS Y TRANSACCIONES");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(640, 400));
        setPreferredSize(new java.awt.Dimension(1366, 768));
        setSize(new java.awt.Dimension(1366, 768));
        addHierarchyListener(new java.awt.event.HierarchyListener() {
            public void hierarchyChanged(java.awt.event.HierarchyEvent evt) {
                formHierarchyChanged(evt);
            }
        });

        jLabel1.setText("Importe: ............");
        jLabel1.setToolTipText("");

        jLabel2.setText("Receptor:");
        jLabel2.setToolTipText("");

        jTextFieldMonto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldMonto.setText("0");
        jTextFieldMonto.setToolTipText("");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 10)); // NOI18N
        jTextArea1.setRows(5);
        jScrollPane2.setViewportView(jTextArea1);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("CUADERNO CONTABLE");
        jLabel3.setToolTipText("");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jButtonSend.setBackground(new java.awt.Color(0, 204, 51));
        jButtonSend.setText("EJECUTAR TRANSACCIÓN");
        jButtonSend.setToolTipText("");
        jButtonSend.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonSendMousePressed(evt);
            }
        });
        jButtonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendActionPerformed(evt);
            }
        });

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel4.setText("Balance de la cuenta:");
        jLabel4.setToolTipText("");

        jLabelBalance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelBalance.setText("Euros");
        jLabelBalance.setToolTipText("");

        jLabelEntityName.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabelEntityName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabelEntityName.setText("Nombre de Entidad:");
        jLabelEntityName.setToolTipText("");

        jLabel7.setText("Euros");
        jLabel7.setToolTipText("");

        jLabel8.setText("Factura: ...........");
        jLabel8.setToolTipText("");

        jTextFieldOffer.setText("F-xxxxxxxx-xxx");
        jTextFieldOffer.setToolTipText("");
        jTextFieldOffer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldOfferActionPerformed(evt);
            }
        });

        jLabel9.setText("Correo de emisor:");
        jLabel9.setToolTipText("");

        jTextFieldSenderEmail.setText("xxxxx@xxxxx.com");

        jLabel10.setText("Correo de receptor:");
        jLabel10.setToolTipText("");

        jTextFieldReceiverEmail.setText("xxxxx@xxxxx.com");
        jTextFieldReceiverEmail.setToolTipText("");

        jLabel11.setText("Fecha límite:");
        jLabel11.setToolTipText("");

        jTextFieldDeadline.setText("08/09/2021");
        jTextFieldDeadline.setToolTipText("");

        jLabelCurrentNodeIP.setText("Conexión realizada con nodo: ");
        jLabelCurrentNodeIP.setToolTipText("");

        jButtonRefreshWindow.setBackground(new java.awt.Color(0, 51, 153));
        jButtonRefreshWindow.setForeground(new java.awt.Color(255, 255, 255));
        jButtonRefreshWindow.setText("Actualizar información de pantalla");
        jButtonRefreshWindow.setActionCommand("Refresh  Transacions  List");
        jButtonRefreshWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshWindowActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("Javier Juarros Huerga");
        jLabel13.setToolTipText("");

        jLabelSmartContractURL.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelSmartContractURL.setText("Smartcontract utilizado: ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldOffer, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldSenderEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jLabelEntityName, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelBalance, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldMonto)
                                    .addComponent(jTextFieldReceiverEmail, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldDeadline, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 56, Short.MAX_VALUE)
                                .addComponent(jButtonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButtonRefreshWindow, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabelCurrentNodeIP, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(167, 167, 167)
                        .addComponent(jLabelSmartContractURL, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1286, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 732, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 15, Short.MAX_VALUE))
                    .addComponent(jSeparator2))
                .addGap(34, 34, 34))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRefreshWindow, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabelBalance)
                    .addComponent(jLabelEntityName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10)
                        .addComponent(jTextFieldReceiverEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextFieldSenderEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(jTextFieldMonto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextFieldOffer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jTextFieldDeadline, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonSend, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelSmartContractURL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelCurrentNodeIP))
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Aqui se seleciona la entidad receptora del monto, recieverEntity
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        System.out.println("Receptor de transaccion seleccionado:\n"
                + this.jComboBox1.getSelectedItem() + "\n");

        // Sol oselecciona cuando esten dadas de alta as entidades
        if (this.jComboBox1.getSelectedItem() != null) {
            this.receiverEntityCif = (this.jComboBox1.getSelectedItem() + "").split("   ")[2];
        }

    }//GEN-LAST:event_jComboBox1ActionPerformed

    // Envia bloque al servidor para su validacion y adicion a la bockchain, tambien es validado un Smartcontract primero en el cliente y luego en el server. Doble validacion
    private void jButtonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendActionPerformed

        // Se busca BoeServer y se conecta a el a través de GUIClient
        setAliveBoeServerIP();

        // Se busca un nodo disponible y se conecta a el a través de GUIClient
        setAliveNode();

        // Se prepara el bloque temporal para hacer la validacion Smartcontract
        tb = new TempBlock();
        tb.setOffer(this.jTextFieldOffer.getText().trim());
        tb.setTransactionAmount(this.jTextFieldMonto.getText().trim());
        tb.setDeadline(this.jTextFieldDeadline.getText().trim());
        tb.setEntityCif(this.entityCif);
        tb.setSenderEmail(this.jTextFieldSenderEmail.getText().trim());
        tb.setReceiverCif(receiverEntityCif);
        tb.setReceiverEmail(this.jTextFieldReceiverEmail.getText().trim());
        tb.setTask("receiveSmartContractValidation");

        // Validacion de smartcontract     
        if (this.smartContractValidation()) { 
            System.out.println("\u001B[32mSmartContract ha sido validado en el BOE\n\u001B[30m");

            // Se restablece el botón;
            this.jButtonSend.setText("EJECUTAR TRANSACCIÓN");
            this.jButtonSend.setBackground(Color.GRAY);

            // Se piden al servidor los pares Entidades y cifs dados de alta.
            tb = new TempBlock();
            tb.setTask("receiveEntitiesAndCifs");
            arrTb = client.getArrTempBlockFromServer(tb);

            String secondEntityCif = "Unknow";

            // Aqui se busca el cif del receptor comparando el item del combobox selecionado con los datos recibidos del nodo
            for (TempBlock tbi : arrTb) {
                // Split para coger solo el string antes de "   " que es la entidad
                if (tbi.getEntity().equals((this.jComboBox1.getSelectedItem() + "").split("   ")[0])) {
                    secondEntityCif = tbi.getEntityCif();
                    break; //Encontrado CIF y fin del for.
                }

            }

            System.out.println("Destinatario de transaccion:\n"
                    + secondEntityCif + "  " + this.jComboBox1.getSelectedItem() + "\n");

            // Trim para eliminar espacios en blanco al ppio y fin de la cadena
            double amount = Double.valueOf(this.jTextFieldMonto.getText().trim());

            this.jLabelBalance.setText("");

            // Valida el balance disponible y que la transaccion sea mayor de 0: Si no hay suficiente saldo no se efectuara la transaccion
            if ((balance - amount) >= 0 && amount > 0) {
                this.jLabelBalance.setForeground(Color.BLUE); // Color si el balance resultante es positivo
                this.jLabelBalance.setText("Se efectuara la transaccion, hay suficiente saldo.");
                System.out.println("\nSe efectuara la transaccion, hay suficiente saldo.");

                // Envia al server el CIF emisor, CIF destinatario y el monto, servidor efectua transaccion
                // Genera el bloque del emisor y del receptor y se recibe la blockchain actualizada del emisor con el nuevo bloque agregado. 
                System.out.println("\nAgregando nuevo bloque en el emisor: " + entity + ", " + entityCif);

                // Preparando bloque temporal de la entidad emisora para enviar al nodo del EMISOR
                tb = new TempBlock();
                tb.setTask("addNewBock");
                tb.setTransactionAmount(amount + "");
                tb.setEntity(entity);
                tb.setEntityCif(entityCif);
                tb.setSecondEntity((this.jComboBox1.getSelectedItem() + "").split("   ")[0]);
                tb.setSecondEntityCif(secondEntityCif);
                tb.setSenderCif(entityCif);
                tb.setReceiverCif(secondEntityCif);
                tb.setOffer(this.jTextFieldOffer.getText().trim());
                tb.setDeadline(this.jTextFieldDeadline.getText().trim());
                tb.setSenderEmail(this.jTextFieldSenderEmail.getText().trim());
                tb.setReceiverEmail(this.jTextFieldReceiverEmail.getText().trim());

                // Se efectua la transaccion
                client.getArrTempBlockFromServer(tb);

                System.out.println("\nAgregando nuevo bloque en el destinatario: "
                        + (this.jComboBox1.getSelectedItem() + "").split("   ")[0] + ""
                        + ", " + secondEntityCif + "\n");

                // Preparando bloque temporal de la entidad receptora para enviar al nodo RECEPTOR
                tb = new TempBlock();
                tb.setTask("addNewBock");
                tb.setTransactionAmount(amount + "");
                tb.setEntity((this.jComboBox1.getSelectedItem() + "").split("   ")[0]);
                tb.setEntityCif(secondEntityCif);
                tb.setSecondEntity(entity);
                tb.setSecondEntityCif(entityCif);
                tb.setSenderCif(entityCif);
                tb.setReceiverCif(secondEntityCif);
                tb.setOffer(this.jTextFieldOffer.getText().trim());
                tb.setDeadline(this.jTextFieldDeadline.getText().trim());
                tb.setSenderEmail(this.jTextFieldSenderEmail.getText().trim());
                tb.setReceiverEmail(this.jTextFieldReceiverEmail.getText().trim());

                // Se efectua la transaccion
                client.getArrTempBlockFromServer(tb);

                System.out.println("\nMonto = " + amount);
                this.jLabelBalance.setText("\nBalance final: " + (balance - amount));

                // Se vuenve a cargar la informacion actualizada en el cuadro de texto con el historial de transacciones
                fillJTextArea();

                // Hilo para poder bloquear el botón durante su pulsación. de esa manera se esperan 10s entre transaccion y transaccion
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // Dehabilita el botón mientras dura la propagacion de datos
                        jButtonSend.setEnabled(false);

                        // Método de espera tras 10 segundos, muestra el tiempo dentro del texto del botón de envío "EJECUTAR TRANSACCIÓN"
                        waitingSync();

                        // Vuelve a habilitarse el botón para efectuar la siguiente transaccion
                        jButtonSend.setEnabled(true);
                    }
                });
                thread.start();
                // Fin del hilo, solo se ejecuta una vez por transaccion

            } else { // No hay saldo suficiente para la transaccion
                this.jLabelBalance.setForeground(Color.RED);

                this.jLabelBalance.setText(
                        "No se efectuará la transacción, no hay suficiente saldo, o monto = 0: "
                        + balance + " - " + amount + " = " + (balance - amount));

                System.out.println(""
                        + "No se efectuará la transacción, no hay suficiente saldo, o monto = 0:\n"
                        + balance + " - " + amount + " = \u001B[31m" + (balance - amount) + "\u001B[30m");
            }
        this.jButtonSend.setBackground(Color.GREEN);
        } else {// Smart Contract validation = false
            System.out.println("\n\u001B[31mSmartContract NO ha sido validado en el BOE\u001B[30m\n");

            // Se restablece el botón;
            this.jButtonSend.setText("EJECUTAR TRANSACCIÓN");
            this.jButtonSend.setBackground(Color.GREEN);
        }
    }//GEN-LAST:event_jButtonSendActionPerformed

    // Boton para refescar el historial de transacciones y el combobox manualmente
    private void jButtonRefreshWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshWindowActionPerformed

        // Se actualiza Combobox e historial e transacciones.
        fillJComboBox();
        fillJTextArea();

        // Para actualizar el balance, se consulta el balance actual al nodo
        tb = new TempBlock();
        tb.setTask("receiveBlockchain");
        tb.setEntityCif(entityCif);

        //se consulta yrecibe petición del nodo
        arrTb = client.getArrTempBlockFromServer(tb);

        // Se pinta el balance actual en la ventana
        this.jLabelBalance.setText("+" + arrTb.get(arrTb.size() - 1).getBankBalance() + " Euros");

    }//GEN-LAST:event_jButtonRefreshWindowActionPerformed

    // Método para poder camiar color y texto del botón, se activa cuando se pulsa
    private void jButtonSendMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonSendMousePressed
        this.jButtonSend.setText("Buscando nodos . . .");
        this.jButtonSend.setBackground(Color.lightGray);
    }//GEN-LAST:event_jButtonSendMousePressed

    private void jTextFieldOfferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldOfferActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldOfferActionPerformed

    private void formHierarchyChanged(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_formHierarchyChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_formHierarchyChanged

    // Método de espera tras 10 segundos, muestra el tiempo dentro del texto del botón de envío "EJECUTAR TRANSACCIÓN"
    public void waitingSync() {
        System.out.println("\n10 segundos de espera hasta que se propague la sincronización.\n");

        // Tiempo de espera
        int waitTime = 10;

        // Bucle que segundo a segundo muestra el tiempo en el botón de envío
        for (int x = waitTime; x >= 0; x--) {

            try {
                System.out.println("\nFaltan " + x + " segundos...\n");
                this.jButtonSend.setText("Propagando, espere... " + x);

                // Espera de 1 segundo entre iteracion e iteracion
                sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(GUITransactions.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // Al finalizar los 10 segundo se vuelve a escribir en el botón su texto de envío
        this.jButtonSend.setText("EJECUTAR TRANSACCIÓN");
        System.out.println("Ya puede introducir un nuevo bloque con seguridad");
    }

    // Método que busca nodos disponibles en la red
    public void setAliveNode() {

        //Si el nodo disponible es el primero se conecta al cliente con su IP
        if (this.aliveNode.equals("nodeIp1")) {
            client = new GUIClient(nodeIp1);
            System.out.println("GUIClient intentando conectar con Nodo1: \u001B[34m" + nodeIp1 + "\u001B[30m");

            // Si el nodo disponible es el segundo se conecta al cliente con su IP
        } else {
            client = new GUIClient(nodeIp2);
            System.out.println("GUIClient intentando conectar con Nodo2: \u001B[34m" + nodeIp2 + "\u001B[30m");

        }

        // Se comprueba si en isConnected salta una excepción.
        // La excepción provoca que se cambie de nodo
        try {
            client.getClientSocket().isConnected();
        } catch (Exception e) {

            System.out.println("GUIClient: Nodo caido");

            // Cambio de nodo
            if (aliveNode.equals("nodeIp1")) {
                aliveNode = "nodeIp2";
                this.jLabelCurrentNodeIP.setText("Current Node IP:  " + nodeIp2);

                // Cambio de nodo
            } else {
                aliveNode = "nodeIp1";
                this.jLabelCurrentNodeIP.setText("Current Node IP:  " + nodeIp1);
            }

            // Redundacia del metodo, se ha cambiado la IP del nodo disponible
            setAliveNode();
        }
        System.out.println("");
    }

    // Comprueba si el servidor BOE esta levantado, y reintenta conexion.
    public void setAliveBoeServerIP() {

        boeClient = new GUIClient(smartcontractIp);
        System.out.println("GUIClient intentando conectar con BoeClient: \u001B[34m" + smartcontractIp + "\u001B[30m");

        // Se comprueba si el servidor está caido, si es asi salta una excepción y se vuelve a intentar conectar
        try {
            boeClient.getClientSocket().isConnected();
            
        } catch (Exception e) {

            System.out.println("GUIClient: BOE Server caído");

            // Recurrencia del metodo, hasta que no salte la excepción porque conecto con un nodo disponible.
            setAliveBoeServerIP();
        }
        System.out.println("");
    }

    // Método  que devuelve true o false dependiendo si el smartcontract ha sido validado correctamente
    private boolean smartContractValidation() {

        boolean isScValid = false; // Por defecto false, antes de la validación

        tb = new TempBlock();

        // Se envían los datos de Smart Contract al servidor BOE
        tb.setOffer(this.jTextFieldOffer.getText().trim());
        tb.setTransactionAmount(this.jTextFieldMonto.getText().trim());
        tb.setDeadline(this.jTextFieldDeadline.getText().trim());
        tb.setEntityCif(this.entityCif);
        tb.setSenderEmail(this.jTextFieldSenderEmail.getText().trim());
        tb.setReceiverCif(receiverEntityCif);
        tb.setReceiverEmail(this.jTextFieldReceiverEmail.getText().trim());
        tb.setTask("*");

        tb = boeClient.getBlockFromServer(tb);

        // Si el Smartcontract ha sido validado correctamnete
        if (tb.getTask().equals("true")) {

            // Dato que se retorna
            isScValid = true;     
        } 
        
        // Se retorna el resultado de la validación
        return isScValid;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonRefreshWindow;
    private javax.swing.JButton jButtonSend;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelBalance;
    private javax.swing.JLabel jLabelCurrentNodeIP;
    private javax.swing.JLabel jLabelEntityName;
    private javax.swing.JLabel jLabelSmartContractURL;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextFieldDeadline;
    private javax.swing.JTextField jTextFieldMonto;
    private javax.swing.JTextField jTextFieldOffer;
    private javax.swing.JTextField jTextFieldReceiverEmail;
    private javax.swing.JTextField jTextFieldSenderEmail;
    // End of variables declaration//GEN-END:variables

}
