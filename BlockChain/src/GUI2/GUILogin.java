package GUI2;

// Se importan los recursos necesarios
import EntityClientSSL.GUIClient;
import TemporalObjects.TempBlock;
import java.awt.Color;

/**
 *
 * @author Javier Juarros Huerga
 *
 * GUILogin: Se encarga de logar a las entidades o de dar de alta entidades
 * nuevas . Para conectar con el servidor utiliza un cliente con socket seguro
 * SSL Se conecta automaticamente a uno de los 2 nodos disponibles, si uno esta
 * caído se conecta con el alternativo y así sucesivamente. Las IP de los nodos
 * las copia de un fichero encriptado con AES, esas IP las pasa al constructor
 * del cliente que se comunica con el Nodo disponible
 *
 */
public class GUILogin extends javax.swing.JFrame {

    // Las IP de los nodos las lee de un fichero cifrado
    private String nodeIp1 = "";
    private String nodeIp2 = "";

    // IP de conexión con la WEB del smartContract
    private String smartcontractIp = "";

    // Contiene el nodo que esta disponible, se busca con el método setAliveNode()
    private String aliveNode = "nodeIp1";

    // Objeto cliente para comunicarse con los nodos y BOE Server
    private GUIClient nodeClient = null;

    // Constructor
    public GUILogin() {
        System.out.println(" ***** CLIENTE *****\n");

        // Inicia los componentes, generado por Netbeans
        initComponents();

     

    }

    // Método que descifra el contenido del fichero de IP de nodos
    public void setNodesFromEncrypedFile(String AESpass) {

        // Se utiliza AES
        GUIAdvancedEncryptionStandard aes = new GUIAdvancedEncryptionStandard(AESpass);

        /* Se descifra el fichero encriptado AES y se introduce en un array de Strings,
         * el fichero tiene los nodos separados por almohadilla # y la Ip para smartcontract
         */
        String arrNodesIP[] = (aes.decrypt()).split("#");

        // Se asignan las IP
        nodeIp1 = "192.168.0." + arrNodesIP[0];
        nodeIp2 = "192.168.0." + arrNodesIP[1];
        smartcontractIp = "192.168.0." + arrNodesIP[2];

        System.out.println("Se han leido las IP de un fichero encriptado:\n"
                + "nodes.encrypt\n"
                + "Las Ip de cada nodo son:\n"
                + "Nodo1 IP = " + nodeIp1 + "\n"
                + "Nodo2 IP = " + nodeIp2 + "\n"
                + "BOE Server smartcontract IP = " + smartcontractIp);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButtonLoad = new javax.swing.JButton();
        jTextFieldEntityCif = new javax.swing.JTextField();
        jPasswordField1 = new javax.swing.JPasswordField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldBalance = new javax.swing.JTextField();
        jTextFieldEntity = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("   CONTROL  DE  ACCESO");
        setAlwaysOnTop(true);
        setResizable(false);

        jLabel1.setText("CIF");
        jLabel1.setToolTipText("");
        jLabel1.setRequestFocusEnabled(false);

        jLabel2.setText("Contraseña");
        jLabel2.setToolTipText("");
        jLabel2.setRequestFocusEnabled(false);

        jButtonLoad.setText("Cargar datos");
        jButtonLoad.setToolTipText("");
        jButtonLoad.setActionCommand("E n t r a r");
        jButtonLoad.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButtonLoadMousePressed(evt);
            }
        });
        jButtonLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonLoadActionPerformed(evt);
            }
        });

        jTextFieldEntityCif.setText("B12345678");
        jTextFieldEntityCif.setToolTipText("");

        jPasswordField1.setText("12345678");
        jPasswordField1.setToolTipText("");

        jCheckBox1.setText("Alta de nueva Entidad");
        jCheckBox1.setToolTipText("");
        jCheckBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCheckBox1ItemStateChanged(evt);
            }
        });

        jLabel3.setText("Balance inicial");
        jLabel3.setToolTipText("");

        jLabel4.setText("Nombre de la Entidad");
        jLabel4.setToolTipText("");

        jTextFieldBalance.setText("0.00");
        jTextFieldBalance.setEnabled(false);

        jTextFieldEntity.setToolTipText("");
        jTextFieldEntity.setEnabled(false);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 7)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 153, 153));
        jLabel5.setText("Javier Juarros Huerga");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addComponent(jButtonLoad, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jTextFieldEntity, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldBalance, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                    .addComponent(jTextFieldEntityCif, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldEntityCif, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextFieldBalance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jTextFieldEntity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox1)
                    .addComponent(jButtonLoad, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addGap(4, 4, 4))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Botón que envia al nodo los datos del cliente Entidad y password, o bien registra una entdad nueva en el nodo
    private void jButtonLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLoadActionPerformed

        // Se genera un bloque temporal tb (temporary block)
        TempBlock tb = new TempBlock();

        // Se recogen los valores escritos en los TextBoxes que con comunes en las dos opciones de checkbox
        // Trim() para quitar espacios en blanco en inicio y fin de cadena
        tb.setEntityCif(this.jTextFieldEntityCif.getText().trim());

        // getPassword() devuelve char[] y se convierte a String
        tb.setPassword(String.valueOf(this.jPasswordField1.getPassword()));

        /*  Ejecuta el método de descifrado de fichero de nodos "nodes,encrypt", 
         *  Se pasa la clave 2 veces, se eliminan espacios en blanco en principio y
         *  fin de cadena "trim()", y se convierte a String ya que "getPassword()"
         *  devueve un array de char "char[]"
         */
        setNodesFromEncrypedFile(
                String.valueOf(this.jPasswordField1.getPassword()).trim()
                + String.valueOf(this.jPasswordField1.getPassword()).trim() + "");

        // Busca un nodo disponible y se conecta, IPs de conexión: "192.168.0.231" o "192.168.0.232"
        this.setAliveNode();

        // Si la entidad ya existe y queremos efectuar transacciónes, se selecciona CheckBox
        if (!this.jCheckBox1.isSelected()) {

            // Envia petición a nodo de validación si existen cif y password en la blockchain, devuelve true o false
            tb.setTask("receiveClientValidation");

            // Enviando petición y recibiendo respuesta en bloque temporal (tb)
            tb = nodeClient.getBlockFromServer(tb);

            // Si la respuesta es true, el cif de la entidad es correcto y la password también
            if (tb.getTask().equals("true")) {
                System.out.println(""
                        + "Recibida respuesta del servidor:"
                        + "\n\u001B[32mDatos de la entidad enviados al servidor y validados correctamente."
                        + "\nCliente validado.\u001B[30m\n");

                // Se cierra la ventana de login y se abrira la de efectuar transacciónes
                this.dispose();

                // Abre la ventana de transacciónes, le pasa los datos del cliente validado (TempBlock) y la conexion con el nodo (cliente)
                GUITransactions c2 = new GUITransactions(tb, nodeClient, this.nodeIp1, this.nodeIp2, this.smartcontractIp);

                // La hace visible
                c2.setVisible(true);

                // Si la respuesta fue false. O bien el CIF no existe en la blockchain,  o bien el usuario o el password son incorrectos
            } else {
                System.out.println(""
                        + "Recibida respuesta del servidor:"
                        + "\n\u001B[31mDatos de la entidad enviados al servidor erroneos o no existe la entidad en la blockchain.\n"
                        + "Introduzca correctamente CIF y password o bien cree una Entidad nueva seleccionando (New Entity registry)\u001B[30m");

            }

            // El checkbox de creación de nueva entidad ha sido seleccionado, se creará una entidad nueva y despues se podran efectuar transacciónes
        } else {

            // Se agregan al bloque temporal los datos que faltan de recoger de los textfield y se envian al nodo para que cree la nueva entidad
            tb.setBankBalance(this.jTextFieldBalance.getText().trim());
            tb.setEntity(this.jTextFieldEntity.getText().trim());

            // Registra la en el nodo la nueva blockchain
            tb.setTask("registryNewBlockchain");

            // Envia la petición y recibe true si la ha creado y false si el el CIF de la entidad ya existia.
            tb = nodeClient.getBlockFromServer(tb);

            // Crea la nueva entidad 
            if (tb.getTask().equals("true")) {
                System.out.println(""
                        + "Recibida respuesta del servidor:"
                        + "\n\u001B[32mDatos de la entidad enviados al servidor y Creados correctamente.");

                // Cierra la ventana de login
                this.dispose();  // Cierra ventana

                // Abre la ventana de transacciónes le envia los datos de la entidad (tb) y el cliente del nodo (nodeClient)
                GUITransactions c2 = new GUITransactions(tb, nodeClient, this.nodeIp1, this.nodeIp2, this.smartcontractIp);

                // Se visibiliza el frame
                c2.setVisible(true);

                // Si la entidad ya existe no se creará una nueva
            } else {
                System.out.println(""
                        + "Recibida respuesta del servidor:"
                        + "\n\u001B[31mCIF de entidad ya existe, introduzca un CIF válido.\u001B[30m");

            }

        }
    }//GEN-LAST:event_jButtonLoadActionPerformed

    // Se utiliza para elegir si es un login con una entidad existente o si es la creación de entidad nueva
    private void jCheckBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCheckBox1ItemStateChanged

        // Creando nueva entidad
        if (this.jCheckBox1.isSelected()) {
            System.out.println("Seleccionado CheckBox para crear una nueva entidad");
            this.jTextFieldBalance.setEnabled(true);
            this.jTextFieldEntity.setEnabled(true);

            // Aceso con autenticacion de CIF y password
        } else {
            System.out.println("CheckBox no seleccionado");
            this.jTextFieldBalance.setEnabled(false);
            this.jTextFieldEntity.setEnabled(false);
        }
    }//GEN-LAST:event_jCheckBox1ItemStateChanged

    // Método para poder cambiar color y texto del botón, se activa cuando se pulsa
    private void jButtonLoadMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonLoadMousePressed
        this.jButtonLoad.setText("Buscando nodos . . .");
        this.jButtonLoad.setBackground(Color.lightGray);
    }//GEN-LAST:event_jButtonLoadMousePressed

    // Método recurrente para seleccionar un nodo disponible.
    public void setAliveNode() {

        // Si el nodo disponible es igual a  "nodeIp1" 
        if (this.aliveNode.equals("nodeIp1")) {

            // Ejecuta el cliente con Ip 21
            nodeClient = new GUIClient(nodeIp1);
            System.out.println("GUIClient intentando conectar con Nodo1: \u001B[34m" + nodeIp1 + "\u001B[30m");

            // Ejecura el segundo nodo, ip 22
        } else {
            nodeClient = new GUIClient(nodeIp2);
            System.out.println("GUIClient intentando conectar con Nodo2: \u001B[34m" + nodeIp2 + "\u001B[30m");

        }
        // Se comprueba si el servidor está caído, si es asi salta una excepción y se vuelve a intentar conectar
        try {
            nodeClient.getClientSocket().isConnected();
            
            // Se vuelve a poner el botón como estaba al inicio, ha conectado con un nodo disponible
            this.jButtonLoad.setText("CARGAR DATOS");
            this.jButtonLoad.setBackground(Color.GRAY);
        } catch (Exception e) {

            System.out.println("GUIClient: Nodo caído");

            // Si salta la excepción poruqe no encuentra el nodo en esa ip trata de conectar con la otra.
            if (aliveNode.equals("nodeIp1")) {
                aliveNode = "nodeIp2";
            } else {
                aliveNode = "nodeIp1";
            }

            // Recurrencia del método, hasta que no salte la excepción porque conecto con un nodo disponible.
            setAliveNode();
        }
        System.out.println("");
    }

    // Inicia la clase
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUILogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUILogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUILogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUILogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
      
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUILogin().setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonLoad;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextFieldBalance;
    private javax.swing.JTextField jTextFieldEntity;
    private javax.swing.JTextField jTextFieldEntityCif;
    // End of variables declaration//GEN-END:variables
}
