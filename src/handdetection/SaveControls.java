/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Options.java
 *
 * Created on Aug 30, 2012, 4:49:33 PM
 */
package handdetection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author technowings
 */
public class SaveControls extends javax.swing.JFrame {

    public static int key1 = 39;
    public static int key2 = 40;
    public static int key3 = 40;
    public static int key4 = 37;
    public static int key5 = 38;

    /** Creates new form Options */
    public SaveControls() {
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(697, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });
        getContentPane().setLayout(null);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/streetback.jpg"))); // NOI18N
        jLabel1.setDoubleBuffered(true);
        getContentPane().add(jLabel1);
        jLabel1.setBounds(79, 107, 515, 390);

        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField1MouseClicked(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });
        getContentPane().add(jTextField1);
        jTextField1.setBounds(310, 80, 70, 20);

        jTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField2MouseClicked(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField2KeyPressed(evt);
            }
        });
        getContentPane().add(jTextField2);
        jTextField2.setBounds(16, 246, 60, 20);

        jTextField3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField3MouseClicked(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
        });
        getContentPane().add(jTextField3);
        jTextField3.setBounds(16, 459, 60, 20);

        jTextField4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField4MouseClicked(evt);
            }
        });
        jTextField4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField4KeyPressed(evt);
            }
        });
        getContentPane().add(jTextField4);
        jTextField4.setBounds(598, 244, 60, 20);

        jTextField5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField5MouseClicked(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField5KeyPressed(evt);
            }
        });
        getContentPane().add(jTextField5);
        jTextField5.setBounds(598, 452, 60, 20);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Preferences_Script.png"))); // NOI18N
        getContentPane().add(jLabel2);
        jLabel2.setBounds(200, 40, 290, 50);

        jLabel3.setFont(new java.awt.Font("Segoe Script", 1, 36));
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("CONTROLS");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(240, 0, 220, 50);

        jLabel4.setText("Click on the Boxes to set your desired Key.");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(40, 500, 250, 20);

        jButton1.setText("Save");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(290, 500, 100, 23);

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Controls Id Name");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(40, 50, 130, 15);
        getContentPane().add(jTextField6);
        jTextField6.setBounds(40, 70, 140, 20);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyPressed
        jTextField2.setText(evt.getKeyText(evt.getKeyCode()));
        key4 = evt.getKeyCode();
    //    jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/streetback.jpg"))); // NOI18N
        JOptionPane.showMessageDialog(null, "Key Accepted");// TODO add your handling code here:
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2KeyPressed

    private void jTextField2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField2MouseClicked
        jLabel1.setIcon(new ImageIcon("/img/back_controlsTop.jpg"));
   //     JOptionPane.showMessageDialog(null, "Enter your new key now.");
        key4 = 0;
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back_controlsLeft.jpg"))); // NOI18N
        jLabel1.repaint();// TODO add your handling code here:
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2MouseClicked

    private void jTextField3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField3MouseClicked
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back_controlsLowLeft.jpg"))); // NOI18N
        jLabel1.repaint();
    //    JOptionPane.showMessageDialog(null, "Enter your new key now.");
        key3 = 0;      // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3MouseClicked

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        jTextField3.setText(evt.getKeyText(evt.getKeyCode()));
        key3 = evt.getKeyCode();
        //jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/streetback.jpg"))); // NOI18N
        JOptionPane.showMessageDialog(null, "Key Accepted");// TODO add your handling code here:
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jTextField4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField4KeyPressed
        jTextField4.setText(evt.getKeyText(evt.getKeyCode()));
        key1 = evt.getKeyCode();
       // jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/streetback.jpg"))); // NOI18N
        JOptionPane.showMessageDialog(null, "Key Accepted");// TODO add your handling code here:
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4KeyPressed

    private void jTextField4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField4MouseClicked
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back_controlsRight.jpg"))); // NOI18N
        jLabel1.repaint();
  //      JOptionPane.showMessageDialog(null, "Enter your new key now.");
        key1 = 0;        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4MouseClicked

    private void jTextField5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyPressed
        jTextField5.setText(evt.getKeyText(evt.getKeyCode()));
        key2 = evt.getKeyCode();
  //      jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/streetback.jpg"))); // NOI18N
        JOptionPane.showMessageDialog(null, "Key Accepted");// TODO add your handling code here:
    }//GEN-LAST:event_jTextField5KeyPressed

    private void jTextField5MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField5MouseClicked
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back_controlsLowRight.jpg"))); // NOI18N
        jLabel1.repaint();
      //  JOptionPane.showMessageDialog(null, "Enter your new key now.");
        key2 = 0;        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5MouseClicked

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        jTextField1.setText(evt.getKeyText(evt.getKeyCode()));
        key5 = evt.getKeyCode();
       // jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/streetback.jpg"))); // NOI18N
        JOptionPane.showMessageDialog(null, "Key Accepted");
// TODO add your handling code here:
    }//GEN-LAST:event_jTextField1KeyPressed

    private void jTextField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseClicked
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/back_controlsTop.jpg"))); // NOI18N
        jLabel1.repaint();
     //   JOptionPane.showMessageDialog(null, "Enter your new key now.");
        key5 = 0;         // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        try {

            Class.forName("com.mysql.jdbc.Driver");

            Connection c = DriverManager.getConnection("jdbc:mysql://localhost/gamecontrols", "root", "");
            PreparedStatement ps = c.prepareStatement("INSERT into controls(controlsId,center, left , right, lowleft,lowright) values ('"+jTextField6.getText()+"',"+key5+","+key4+","+key1+","+key3+","+key2) ;
           System.out.println(ps.toString());
            ps.execute();
c.close();
        } catch (Exception ex) {
            Logger.getLogger(SaveControls.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JOptionPane.showMessageDialog(null, "Controls Changed. Now you can Close this window and proceed with Your Gaming!");
        
        saved = true;
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        saved = false;
        System.out.println("Saved.");// TODO add your handling code here:
    }//GEN-LAST:event_formComponentHidden
    /**
     * @param args the command line arguments
     */
    public static boolean saved = false;

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new SaveControls().setVisible(true);

            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    // End of variables declaration//GEN-END:variables
}
