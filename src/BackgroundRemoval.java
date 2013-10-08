/*
 * ImgGrabber.java
 *
 * Created on July 6, 2012, 8:25 PM
 */


import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import static com.googlecode.javacv.cpp.opencv_highgui.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.SubtractDescriptor;
import javax.swing.ImageIcon;
import javax.swing.SwingWorker;

/**
 *
 * @author  rajesh
 */
public class BackgroundRemoval extends javax.swing.JFrame {
//CvCapture capture=cvCreateCameraCapture(0);
        
  OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
    IplImage frame = null;
      IplImage frame2 = null;
      BufferedImage bi=null;
    SwingWorker sw = null;
       public static int i=0;
    /** Creates new form ImgGrabber */
    public BackgroundRemoval() {

        initComponents();
  

        sw = new SwingWorker() {   

            @Override
            protected Object doInBackground()  throws Exception {
                
             
                        grabber.start();
                         frame2=grabber.grab();
                         bi=frame2.getBufferedImage();
                         grabber.stop();
                         grabber.release();
                          grabber.start();
                while (true) {
                    System.out.println("Grabbing Image .... ");
//                            cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, 1280);
//                            cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, 1024);
                                    
//             /       final IplImage img=cvRetrieveFrame(capture);
                    
                    frame=grabber.grab();
            
                    Image img = frame.getBufferedImage();
                 System.out.println("old"+jLabel1.getWidth()+""+jLabel1.getHeight());
/*
                        Image imgtemp = createResizedcopy(img,
                jLabel1.getWidth(),jLabel1.getHeight(),
                true);    */   
//                     new Thread(){
//
//                        @Override
//                        public void run() {
                                           //      cvSaveImage("capture"+i+".jpg", img);
                        i++;
                        if(frame!=null || frame2!=null)
                        {

                            //BufferedImage b2=imageToBufferedImage( makeColorTransparent( trans(subtractImage(bi , frame.getBufferedImage() )), Color.BLACK));
BufferedImage b2=subtractImage(bi , frame.getBufferedImage() );
               jLabel1.setIcon(new ImageIcon(  b2 ));
//  try {
//                ImageIO.write(b2, "png", new File("c:/"+i+i+".png"));
//            } catch (IOException ex) {
//                Logger.getLogger(ImgGrabber.class.getName()).log(Level.SEVERE, null, ex);
//            }
                    }
//        if(i%20==0)
//                        bi=frame.getBufferedImage();
//                        }
//                         
//                         
//                     }.start();
//System.out.println("new"+jLabel1.getWidth()+""+jLabel1.getHeight());
                }
                        

                
       
//                return null;
                
            }};
        sw.execute();




    };
        private static BufferedImage imageToBufferedImage(Image image) {

        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();  

        return bufferedImage;

    }

public BufferedImage trans( BufferedImage bi2)
    {
    
    BufferedImage img_logo = new BufferedImage(bi2.getWidth(), bi2.getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img_logo.createGraphics();
    g.drawImage(bi2, 0, 0, null);
    if (g != null) {
            try {
                ImageIO.write(img_logo, "png", new File("c:/"+i+".png"));
            } catch (IOException ex) {
             System.out.println("Error Saving");
            }

        g.dispose();
    }
return img_logo;
}
 public static Image makeColorTransparent(BufferedImage im, final Color color) {
        ImageFilter filter = new RGBImageFilter() {

                // the color we are looking for... Alpha bits are set to opaque
                public int markerRGB = color.getRGB() | 0xFF000000;

                public final int filterRGB(int x, int y, int rgb) {
                        if ((rgb | 0xFF000000) == markerRGB) {
                                // Mark the alpha bits as zero - transparent
                                return 0x00FFFFFF & rgb;
                        } else {
                                // nothing to do
                                return rgb;
                        }
                }
        };

        ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }

     public BufferedImage createResizedcopy(Image originalImage,
                int scaledWidth,int scaledHeight,
                boolean preserveAlpha)
    {

     System.out.println(scaledWidth+"x"+scaledHeight);


        System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth,scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
                g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        System.out.println("resize image sent..");  
        return scaledBI;
    }
    public BufferedImage subtractImage(BufferedImage img1, BufferedImage img2) {

BufferedImage imageOut;
RenderedOp op = SubtractDescriptor.create(img1, img2, null);
imageOut = op.getAsBufferedImage();
return imageOut;
}
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jLabel1.setOpaque(true);

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 797, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(168, 168, 168)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
                .addGap(29, 29, 29))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed


//            cvReleaseCapture(capture);


    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new BackgroundRemoval().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
