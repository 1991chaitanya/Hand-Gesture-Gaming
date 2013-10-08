
import javax.swing.JOptionPane;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
public class Test {
    public static void main(String[] args) {
        System.out.println("Start");
        new Thread(){

            @Override
            public void run() {
                super.run();
                JOptionPane.showMessageDialog(null, "Alert");
            }
            
        }.start();

        System.out.println("End");
    }
}
