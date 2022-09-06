import Blockchain.Block;
import Blockchain.Blockchain;
import java.util.ArrayList;

/**
 *
 * @author Javier Juarros Huerga
 */
public class TestGUI extends javax.swing.JFrame {

    // Blockchain
    ArrayList<Blockchain> blockchain = null;

    // Constructor
    public TestGUI() {
        initComponents();

        this.jComboBox1.addItem("Seleccione una opcion");
        this.jComboBox1.addItem("Agrega un bloque con un hash que coincide con el anterior");
        this.jComboBox1.addItem("Inserta un bloque entre bloques generados, indices no sonsecutivos");
        
        this.jTextArea1.setText("\nTest de Blockchain\n");
    }

    // Se agregan 3 bloques correctamente para efectuar las pruebas
    public void correctBlockchain() {
        blockchain = new ArrayList<Blockchain>();

        blockchain.add(new Blockchain("1", "1000000", "Ayuntamiento de Collado Villalba", "A44376411", "password1"));

        blockchain.get(0).newBlock("150", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021");
        blockchain.get(0).newBlock("6790", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021");

        System.out.println(blockchain);
        System.out.println("\n\u001B[30mEs válida la blockchain generada? => " + blockchain.get(0).isBlockChainValid() + "\n");
        this.jTextArea1.setText(jTextArea1.getText()+blockchain);
    }

// Agrega un bloque con un hash que coincide con el anterior
    public void invalidHash() {

        System.out.println("Se agrega un bloque que corrompe la blockchain, tiene un hash válido pero"
                + "\nque no pertenece a la sucesión de hashes de la blockchain\n\n");

        this.jTextArea1.setText(jTextArea1.getText()+"\n\n**************************\n"
                + "Se agrega un bloque que corrompe la blockchain, tiene un hash válido "
                + "\nque no pertenece a la sucesión de hashes de la blockchain.\n\n");
        
        blockchain.get(0).addBlock(new Block("3", "123456789013", "0b932409941d21a6f8fbde6f78e64aa3bd15519e204933e89dc516f5fe9b784a", "0d332409941d21a6f8fbde6f78e64aa3bd15519e204933e89dc516f5fe9b783a", "1000000", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021"));

        System.out.println("\n\u001B[30mPara comprobarlo se valida la blockchain con le metodo isBlockChainValid()"
                + "\nEs válida la blockchain generada? => \u001B[31m" + blockchain.get(0).isBlockChainValid() + "\u001B[30m\n");

        this.jTextArea1.setText(jTextArea1.getText()+"Para comprobarlo se valida la blockchain con le metodo isBlockChainValid()"
                + "\nEs válida la blockchain generada? => " + blockchain.get(0).isBlockChainValid() + "\n");
    }

    // Inserta un bloque entre bloques generados, rompiendo la secuencia logica de indices de la blockchain
    public void insertNewBlock() {

        System.out.println("\nSe inserta nuevo bloque entre la posicion 1 y la 2\n");

        this.jTextArea1.setText(jTextArea1.getText()+"\n\n**************************\nSe inserta nuevo bloque entre la posicion 1 y la 2\n");
        
        blockchain.get(0).getBlocks().add(1, blockchain.get(0).newBlock("1000000", "Ayuntamiento de Collado Villalba", "A44376411", "BBVA", "P44376411", "A44376411", "P44376411", "A44376411", "nosotros@es.com", "ellos@son.es", "OF-Pago-Tasas", "13/06/2021"));

        System.out.println("\n\u001B[30mPara comprobarlo se valida la blockchain con le metodo isBlockChainValid()"
                + "\nEs válida la blockchain generada? => \u001B[31m" + blockchain.get(0).isBlockChainValid() + "\u001B[30m\n");

        this.jTextArea1.setText(jTextArea1.getText()+"\nPara comprobarlo se valida la blockchain con le metodo isBlockChainValid()"
                + "\nEs válida la blockchain generada? => " + blockchain.get(0).isBlockChainValid() + "\n");
        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TESTING BLOCKCHAIN INTEGRITY");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 7)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("Javier Juarros Huerga  -  TFG");
        jLabel13.setToolTipText("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(161, 161, 161)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel13)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Lanza las pruebas
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
    
        if (this.jComboBox1.getSelectedIndex() == 2) {

            correctBlockchain();
            invalidHash();
   
        } else if (this.jComboBox1.getSelectedIndex() == 1) {

            correctBlockchain();
            insertNewBlock();
        }
    }//GEN-LAST:event_jComboBox1ActionPerformed

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
            java.util.logging.Logger.getLogger(TestGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
