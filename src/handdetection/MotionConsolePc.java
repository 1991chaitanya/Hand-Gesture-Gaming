/*
 * ImgGrabber.java
 *
 * Created on July 6, 2012, 8:25 PM
 */
package handdetection;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvConvexityDefect;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_video.BackgroundSubtractorMOG2;
import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import imagehelper.Picture;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.SubtractDescriptor;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSliderUI;
import util.StringHelper;


public class MotionConsolePc extends javax.swing.JFrame {

    String listofCamera = null;
    public static CvCapture capture = null;
    boolean breakLoop = false;
    boolean detectionBusy = false;
    public IplImage frame = null;
    IplImage frame2 = new IplImage();
    IplImage frm = new IplImage();
    IplImage edge = new IplImage();
    int width = 640;
    int height = 480;
    SwingWorker sw = null;
    public static int i = 0;
    Picture pbox, pbox_jjil;
    long startTime = 0, endTime = 0;
    BufferedImage buffimg = null;
    ServerSocket servsock = null;
    String[] listcam = new String[100];
    StringBuffer sb = new StringBuffer();
    ServerSocket servelist = null;
    int line_count = 0;
    int selectedcam = 0;
    Socket camsock = null;
    int num_cams;
    int wid = 0, hei = 0;
    boolean clicked = false;
    CvPoint defect_top = new CvPoint();
    CvPoint defect_left = new CvPoint();
    CvPoint defect_lowleft = new CvPoint();
    CvPoint defect_lowright = new CvPoint();
    CvPoint defect_right = new CvPoint();
    Timer cont = new Timer();
    BufferedImage bi = null;
    boolean once = true;
    int points = 0;
    boolean automode = true;
    boolean handmode = false;
    boolean staticBackMode = false;
    boolean pauseRight = false;
    boolean pauseLeft = false;
    int thresh = 40;
    //Server serveImage=null;
    /** Creates new form ImgGrabber */
    Controls controls = new Controls();
    SaveControls makeControls=new SaveControls();
        LoadControls loadControls=new LoadControls();
    int prevPositionX = 0;
    int prevPositionY = 0;
    boolean racemode = false;
    boolean firstmode = false;

    public MotionConsolePc() {
        controls.setVisible(false);
        controls.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        initComponents();
        jPanel1.setVisible(false);
        jPanel2.setVisible(false);
//        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/streetback.jpg"))); // NOI18N
        cont.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (controls.saved == true) {
                    key1 = controls.key1;
                    key2 = controls.key2;
                    key3 = controls.key3;
                    key4 = controls.key4;
                    key5 = controls.key5;
                }

            }
        }, 1, 1);
    }

    public MotionConsolePc(int cameraId) {
        initComponents();
        startCamera(cameraId);

    }

    public void startCamera(int cameraId) {

        breakLoop = false;
        capture = cvCreateCameraCapture(cameraId);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_WIDTH, 640);
        cvSetCaptureProperty(capture, CV_CAP_PROP_FRAME_HEIGHT, 480);
        System.out.println("Camera  ===> " + cameraId);

        sw = new SwingWorker() {
  
            @Override
            protected Object doInBackground() {

                try {
                    final Timer time = new Timer();
                    time.scheduleAtFixedRate(new TimerTask() {

                        @Override
                        public void run() {

                            if (breakLoop || racemode == true) {
                                time.cancel();
                            }

                            if (capture != null) {
                                frame = cvRetrieveFrame(capture);
                                if (once == true) {
                                    frame2 = frame.clone();
                                    bi = frame2.getBufferedImage();
                                    once = false;
                                }
                                //frame= hand_detect(frame);
                                //frame=detectObjects(frame);
                                color_detect();
//                                cvFlip(frame, frame, 1);//i used this to flip the captured image
                                // frame=finger_detect(frame);
                                if (frame != null) {

                                    buffimg = frame.getBufferedImage();
//              buffimg= haar_Video(frame);
                                    jLabel1.setIcon(new ImageIcon(buffimg));
                                    cvReleaseImage(frame2);
                                }
                            }

//                            jLabel1.repaint();





                        }
                    }, 10, 10);

                } catch (Exception e) {
                    e.printStackTrace();

                    try {
//                        cvReleaseImage(jjil);
                        cvReleaseImageHeader(frame);
                        cvReleaseImage(frame);
                        cvReleaseCapture(capture);
                    } catch (Exception ex) {
                        System.out.println("Release error handled.");
                        ex.printStackTrace();

                    }
                } finally {
                    System.out.println("Finally here .");
                    return (null);
                }
            }

            @Override
            protected void done() {
                System.out.println("Swingworker Job Done...............");
            }
        };
        sw.execute();


    }

    public void race_mode() {
        try {

            // Preload the opencv_objdetect module to work around a known

            //File f = new File("lib/squash1.avi");
            final FrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();

            // BackgroundSubtractorMOG2 mog = null;


            final Timer tm = new Timer();
            tm.scheduleAtFixedRate(new TimerTask() {

                int count = 0;
                CvSeq contour = new CvSeq(null);
                CvSeq ptr = new CvSeq();
//                Loader.load(opencv_objdetect.class);
                CvMemStorage storage = CvMemStorage.create();
                BackgroundSubtractorMOG2 mog = new BackgroundSubtractorMOG2(200, 10,
                        true);
                IplImage grabbedImage = grabber.grab();
                IplImage foreground = null;

                @Override
                public void run() {
                    try {
                        while ((grabbedImage = grabber.grab()) != null && racemode == true) {
                            count = 0;
                            System.out.println("Inside while");

                            if (foreground == null) {
                                foreground = IplImage.create(grabbedImage.width(), grabbedImage.height(),
                                        IPL_DEPTH_8U, 1);
                            }
                            mog.apply(grabbedImage, foreground, -1);
                            cvThreshold(foreground, foreground, 50, 100, CV_THRESH_BINARY);
                            cvDilate(foreground, foreground, null, 1);
                            cvErode(foreground, foreground, null, 3);

                            cvFindContours(foreground, storage, contour,
                                    Loader.sizeof(CvContour.class), CV_RETR_LIST,
                                    CV_CHAIN_APPROX_SIMPLE);
                            CvRect boundbox;
                            try {
                                int cnt = 0;
                                System.out.println("Contours-" + contour.total());
                                for (ptr = contour; ptr != null && racemode == true; ptr = ptr.h_next()) {
                                    System.out.println("inside for");
                                    boundbox = cvBoundingRect(ptr, 0);
                                    count++;
                                    if (boundbox.width() > 100 && boundbox.height() > 200 && boundbox.width() < 500 && boundbox.height() < 400) {

                                        new Thread(new Runnable() {

                                            public void run() {
                                                try {
                                                    new Robot().keyPress(KeyEvent.VK_UP);
                                                    new Robot().delay(800);
                                                    new Robot().keyRelease(KeyEvent.VK_UP);
                                                } catch (AWTException ex) {
                                                    Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                                }

                                            }
                                        }).start();
                                        cvRectangle(
                                                grabbedImage,
                                                cvPoint(boundbox.x(), boundbox.y()),
                                                cvPoint(boundbox.x() + boundbox.width(), boundbox.y()
                                                + boundbox.height()), CV_RGB(255, 0, 0), 1, 8,
                                                0);
//                                              cvPutText(grabbedImage,"Contour-"+count,  cvPoint(boundbox.x(), boundbox.y()), cvFont(1, 1), CvScalar.RED);
                                        final int currentPositionX = boundbox.x() + (boundbox.width() / 2);
                                        int currentPositionY = boundbox.y() + (boundbox.height() / 2);

                                        if (prevPositionX != 0 && racemode == true && (currentPositionX > prevPositionX + 65 || currentPositionX < prevPositionX - 65)) {


                                            if (currentPositionX > prevPositionX)//SlideLeftDown
                                            {
                                                new Robot().mouseMove(MouseInfo.getPointerInfo().getLocation().x - 40, MouseInfo.getPointerInfo().getLocation().y);
                                                new Thread(new Runnable() {

                                                    public void run() {
                                                        try {
                                                            new Robot().keyPress(KeyEvent.VK_LEFT);
                                                            new Robot().delay(550);
                                                            new Robot().keyRelease(KeyEvent.VK_LEFT);
                                                        } catch (AWTException ex) {
                                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                                        }

                                                    }
                                                }).start();


                                            } else if (currentPositionX < prevPositionX)//slideRightUp
                                            {
                                                new Robot().mouseMove(MouseInfo.getPointerInfo().getLocation().x + 40, MouseInfo.getPointerInfo().getLocation().y);
                                                new Thread(new Runnable() {

                                                    public void run() {
                                                        try {
                                                            new Robot().keyPress(KeyEvent.VK_RIGHT);
                                                            new Robot().delay(550);
                                                            new Robot().keyRelease(KeyEvent.VK_RIGHT);
                                                        } catch (AWTException ex) {
                                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                                        }

                                                    }
                                                }).start();

                                            }
//   else if(currentPositionX < prevPositionX && currentPositionY > prevPositionY)//right
//     new Robot().mouseMove(MouseInfo.getPointerInfo().getLocation().x+20,MouseInfo.getPointerInfo().getLocation().y);
//    else if(currentPositionX > prevPositionX && currentPositionY > prevPositionY)//left
//     new Robot().mouseMove(MouseInfo.getPointerInfo().getLocation().x-20,MouseInfo.getPointerInfo().getLocation().y);
//         else if (currentPositionX > prevPositionX && currentPositionY < prevPositionY)//up
//     new Robot().mouseMove(MouseInfo.getPointerInfo().getLocation().x,MouseInfo.getPointerInfo().getLocation().y-20);
//             else if (currentPositionX > prevPositionX && currentPositionY > prevPositionY)//down
//     new Robot().mouseMove(MouseInfo.getPointerInfo().getLocation().x,MouseInfo.getPointerInfo().getLocation().y+20);
                                        }
                                        prevPositionX = boundbox.x() + (boundbox.width() / 2);
                                        prevPositionY = boundbox.y() + (boundbox.height() / 2);

                                    }
                                    if (boundbox.y() < 80) {
                                        new Thread(new Runnable() {

                                            public void run() {
                                                try {
                                                    new Robot().keyPress(KeyEvent.VK_R);
                                                    new Robot().delay(100);
                                                    new Robot().keyRelease(KeyEvent.VK_R);
                                                } catch (AWTException ex) {
                                                    Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                                }

                                            }
                                        }).start();
                                    }

                                }
//                         new Robot().keyRelease(KeyEvent.VK_UP);

                            } catch (Exception e) {
                                System.out.println("Error in Detection");
                                if (racemode == false) {
                                    System.out.println("Stopping While..");
                                    grabber.stop();
                                    break;
                                }
                            }
                            cvFlip(grabbedImage, grabbedImage, 1);
                            jLabel1.setIcon(new ImageIcon(grabbedImage.getBufferedImage()));
                            jLabel1.repaint();
                            if (racemode == false) {
                                System.out.println("Stopping grabber..");
                                grabber.stop();
                                break;
                            }
                        }

                        if (racemode == false) {
                            System.out.println("Stopping Timer..");
                            tm.cancel();

                        }


                    } catch (Exception e) {
                        System.out.println("Error in race mode");
                    }

                    if (racemode == false) {
                        System.out.println("Stopping Timer..");
                        tm.cancel();

                    }
                }
            }, 10, 10);




        } catch (Exception e) {
        }

    }

    public void first_mode() {
        try {

            // Preload the opencv_objdetect module to work around a known

            //File f = new File("lib/squash1.avi");
            final FrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();

            // BackgroundSubtractorMOG2 mog = null;


            final Timer tm = new Timer();
            tm.scheduleAtFixedRate(new TimerTask() {

                int count = 0;
                CvSeq contour = new CvSeq(null);
                CvSeq ptr = new CvSeq();
//                Loader.load(opencv_objdetect.class);
                CvMemStorage storage = CvMemStorage.create();
                BackgroundSubtractorMOG2 mog = new BackgroundSubtractorMOG2(200, 10,
                        true);
                IplImage grabbedImage = grabber.grab();
                IplImage foreground = null;

                @Override
                public void run() {
                    try {
                        while ((grabbedImage = grabber.grab()) != null && firstmode == true) {
                            count = 0;
                            System.out.println("Inside while");

                            if (foreground == null) {
                                foreground = IplImage.create(grabbedImage.width(), grabbedImage.height(),
                                        IPL_DEPTH_8U, 1);
                            }
                            mog.apply(grabbedImage, foreground, -1);
                            cvThreshold(foreground, foreground, 50, 100, CV_THRESH_BINARY);
                            cvDilate(foreground, foreground, null, 1);
                            cvErode(foreground, foreground, null, 3);

                            cvFindContours(foreground, storage, contour,
                                    Loader.sizeof(CvContour.class), CV_RETR_LIST,
                                    CV_CHAIN_APPROX_SIMPLE);
                            CvRect boundbox;
                            try {
                                int cnt = 0;
                                System.out.println("Contours-" + contour.total());
                                for (ptr = contour; ptr != null && firstmode == true; ptr = ptr.h_next()) {
                                    System.out.println("inside for");
                                    boundbox = cvBoundingRect(ptr, 0);
                                    count++;
                                    if(boundbox.y()>280 && boundbox.y()<380 &&boundbox.width() > 100 && boundbox.height() > 100)
                                        {        cvPutText(grabbedImage, "Crouch", cvPoint(boundbox.x() + (boundbox.width() / 2),  boundbox.y() + (boundbox.height() / 2)), cvFont(2, 5), CV_RGB(205, 230, 127));
                                            cvRectangle(
                                                    grabbedImage,
                                                    cvPoint(boundbox.x(), boundbox.y()),
                                                    cvPoint(boundbox.x() + boundbox.width(), boundbox.y()
                                                    + boundbox.height()), CV_RGB(255, 0, 0), 1, 8,
                                                    0);
                                             new Thread(new Runnable() {

                                                public void run() {
                                                    try {
                                                        new Robot().keyPress(KeyEvent.VK_CONTROL);
//                                                        new Robot().delay(300);
                                                      
                                                    } catch (AWTException ex) {
                                                        Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                                    }

                                                }
                                            }).start();
                                          
                                        }

                                    if (boundbox.width() > 40 && boundbox.height() > 40 && boundbox.width() < 100 && boundbox.height() < 100 && boundbox.y() > 300 && boundbox.x() < 80) {
                                        cvRectangle(
                                                grabbedImage,
                                                cvPoint(boundbox.x(), boundbox.y()),
                                                cvPoint(boundbox.x() + boundbox.width(), boundbox.y()
                                                + boundbox.height()), CV_RGB(255, 0, 0), 1, 8,
                                                0);
                                        new Thread(new Runnable() {

                                            public void run() {
                                                try {
                                                    new Robot().keyPress(KeyEvent.VK_ENTER);
                                                    new Robot().delay(50);
                                                    new Robot().keyRelease(KeyEvent.VK_ENTER);
                                                } catch (AWTException ex) {
                                                    Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                                }

                                            }
                                        }).start();
                                       
                                    }
                                    if (boundbox.width() > 40 && boundbox.height() > 40 && boundbox.width() < 100 && boundbox.height() < 100 && boundbox.y() > 380 && boundbox.x() >540) {
                                        cvRectangle(
                                                grabbedImage,
                                                cvPoint(boundbox.x(), boundbox.y()),
                                                cvPoint(boundbox.x() + boundbox.width(), boundbox.y()
                                                + boundbox.height()), CV_RGB(255, 0, 0), 1, 8,
                                                0);
                                  new Thread(new Runnable() {

                                                public void run() {
                                                    try {
                                                        new Robot().keyPress(KeyEvent.VK_W);
                                                        new Robot().delay(300);
                                                        new Robot().keyRelease(KeyEvent.VK_W);
                                                    } catch (AWTException ex) {
                                                        Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                                    }

                                                }
                                            }).start();
                                        break;
                                    }
                                    if (boundbox.width() > 60 && boundbox.height() > 200 && boundbox.width() < 500 && boundbox.height() < 440 &&boundbox.y()>30) {


                                        cvRectangle(
                                                grabbedImage,
                                                cvPoint(boundbox.x(), boundbox.y()),
                                                cvPoint(boundbox.x() + boundbox.width(), boundbox.y()
                                                + boundbox.height()), CV_RGB(255, 0, 0), 1, 8,
                                                0);
//                                              cvPutText(grabbedImage,"Contour-"+count,  cvPoint(boundbox.x(), boundbox.y()), cvFont(1, 1), CvScalar.RED);
                                        final int currentPositionX = boundbox.x() + (boundbox.width() / 2);
                                        int currentPositionY = boundbox.y() + (boundbox.height() / 2);
                                        System.out.println("Resolution=" + Toolkit.getDefaultToolkit().getScreenSize());
                                              if (boundbox.y() > 30 && boundbox.y() < 130) {
                                            cvPutText(grabbedImage, "up|Down", cvPoint(currentPositionX, currentPositionY), cvFont(2, 5), CV_RGB(205, 230, 127));
                                            if (prevPositionX != 0 && firstmode == true && (currentPositionX > prevPositionX + 10 || currentPositionX < prevPositionX - 10)) {
                                                if(currentPositionX<prevPositionX)
                                                new Robot().mouseMove((int) MouseInfo.getPointerInfo().getLocation().x, (int)  MouseInfo.getPointerInfo().getLocation().y - 20);
                                                else if(currentPositionX>prevPositionX)
                                                new Robot().mouseMove((int) MouseInfo.getPointerInfo().getLocation().x, (int)  MouseInfo.getPointerInfo().getLocation().y + 20);

                                            }
                                            break;
                                        }
                                        
                                         else if (boundbox.y() > 130 && boundbox.y() < 280)
                                        {
                                            try {
                                                new Robot().keyRelease(KeyEvent.VK_CONTROL);
                                            } catch (Exception e) {
                                            }
                                              cvPutText(grabbedImage, "<- ->", cvPoint(currentPositionX, currentPositionY), cvFont(2, 5), CV_RGB(205, 230, 127));
                                            new Robot().mouseMove((int) ((Toolkit.getDefaultToolkit().getScreenSize().width - currentPositionX * 2.8)), MouseInfo.getPointerInfo().getLocation().y);
                                      // break;
                                        }
                                     
                                        if (prevPositionX != 0 && firstmode == true && (currentPositionX > prevPositionX + 20 || currentPositionX < prevPositionX - 20) && boundbox.y()<280 && boundbox.y()>130) {
                                           

                                        }
                                        prevPositionX = boundbox.x() + (boundbox.width() / 2);
                                        prevPositionY = boundbox.y() + (boundbox.height() / 2);

                                    }
                                    if (boundbox.y() < 30) {
                                        new Thread(new Runnable() {

                                            public void run() {
                                                try {
                                                    new Robot().keyPress(KeyEvent.VK_S);
                                                    new Robot().delay(100);
                                                    new Robot().keyRelease(KeyEvent.VK_S);
                                                } catch (AWTException ex) {
                                                    Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                                }

                                            }
                                        }).start();
                                    }

                                }
//                         new Robot().keyRelease(KeyEvent.VK_UP);

                            } catch (Exception e) {
                                System.out.println("Error in Detection");
                                if (firstmode == false) {
                                    System.out.println("Stopping While..");
                                    grabber.stop();
                                       tm.cancel();
                                    break;
                                }
                            }
                            cvFlip(grabbedImage, grabbedImage, 1);
                            jLabel1.setIcon(new ImageIcon(grabbedImage.getBufferedImage()));
                            jLabel1.repaint();
                            if (firstmode == false) {
                                System.out.println("Stopping grabber..");
                               
                                  tm.cancel();
                                break;
                            }
                        }

                        if (firstmode == false) {
                            System.out.println("Stopping Timer..");
                            grabber.stop();
                            tm.cancel();
                    
                        }


                    } catch (Exception e) {
                        System.out.println("Error in race mode");
                    }

                    if (firstmode == false) {
                        System.out.println("Stopping Timer..");
                      
                        tm.cancel();

                    }
                }
            }, 10, 10);




        } catch (Exception e) {
        }

    }
            CanvasFrame frameInput = new CanvasFrame("Original");
    public void color_detect() {
        if (frame != null) {
            frm = frame.clone();
            final int width = frame.width();
            final int height = frame.height();
            CvSize sz = cvGetSize(frame);
            CvSeq hull3 = new CvSeq();
            CvSeq contour = new CvSeq(null);
            CvSeq ptr = new CvSeq();
            Loader.load(opencv_objdetect.class);
            edge = IplImage.create(width, height, IPL_DEPTH_8U, 0);
            final CvMemStorage storage = CvMemStorage.create();
            int person = 0;
            cvClearMemStorage(storage);

            // Convert to grayscale image...
            if (staticBackMode == false) {
                bi = subtractImage(bi, frame.getBufferedImage());
                frameInput.showImage(IplImage.createFrom(bi));
                cvCvtColor(IplImage.createFrom(bi), edge, CV_BGR2GRAY);

            } else {
                BufferedImage bi2 = subtractImage(bi, frame.getBufferedImage());
                cvCvtColor(IplImage.createFrom(bi2), edge, CV_BGR2GRAY);
            }
            if (automode) {
                cvThreshold(edge, edge, thresh, 255, CV_THRESH_BINARY);

            } else {
                cvThreshold(edge, edge, jSlider1.getValue(), 255, CV_THRESH_BINARY);
            }
            cvFindContours(edge, storage, contour,
                    Loader.sizeof(CvContour.class), CV_RETR_LIST,
                    CV_CHAIN_APPROX_SIMPLE);
            //
            if (contour != null && !contour.isNull()) {
                contour = cvApproxPoly(contour, Loader.sizeof(CvContour.class), storage, CV_POLY_APPROX_DP, 1, 1);
            }
            cvDrawContours(frame, contour, CvScalar.YELLOW, CvScalar.RED, 1, 1, 8);
            CvRect boundbox = null;
            if (contour != null && !contour.isNull()) //      contour =cvApproxPoly( contour, Loader.sizeof( CvContour.class ), storage, CV_POLY_APPROX_DP,1,1);
            {
                try {
                    int cnt = 0;
                    for (ptr = contour; ptr != null && ptr.total() < 30; ptr = ptr.h_next()) {
                        System.out.println("inside for");

                        hull3 = cvConvexHull2(ptr, null, CV_COUNTER_CLOCKWISE, 0);
                        CvSeq defectSeq = cvConvexityDefects(ptr, hull3, storage);//changes made to cvcreatememstorage()
                        System.out.println(cnt+" Defects "+defectSeq.total());
cnt++;
                        for (int r = 0; r < (defectSeq.total() - 1) && defectSeq.total() < 50; r++) {
                            person++;
                            System.out.println("inside for for");
                            final CvConvexityDefect dp = new CvConvexityDefect(cvGetSeqElem(defectSeq, r));//(CvConvexityDefect);
//                            cvCircle(frame, dp.end(), 5, CV_RGB(255, 0, 0), -1, 8, 0);
//                            cvCircle(frame, dp.start(), 5, CV_RGB(0, 255, 0), -1, 8, 0);
                            cvCircle(frame, dp.depth_point(), 5, CV_RGB(0, 0, 255), -2, 5, 0);
                            cvPutText(frame, "||", cvPoint(40, 15), cvFont(1, 1), CvScalar.RED);
                            cvPutText(frame, "||", cvPoint(width - 40, 15), cvFont(1, 1), CvScalar.RED);
                            cvRectangle(frame, cvPoint(0, 0), cvPoint(80, 20), CV_RGB(255, 0, 0), 1, 8, 0);
                            cvRectangle(frame, cvPoint(width, 0), cvPoint(width - 80, 20), CV_RGB(255, 0, 0), 1, 8, 0);
                            //Following are the Bounding areas which will be used to detect body part and simulate keyEvent.
//                            cvRectangle(frame, cvPoint(0, 100), cvPoint(150, 300), CV_RGB(200, 0, 200), 1, 8, 0);//Right Rectangle for Right key
//                            //cvRectangle(grabbedImage, cvPoint(0,200), cvPoint(200,400),  CV_RGB(200, 0, 200), 1, 8, 0);//Right Rectangle for Right key
//                            cvRectangle(frame, cvPoint(width, 100), cvPoint(width - 150, 300), CV_RGB(200, 0, 200), 1, 8, 0);//left rectangle
//                            cvRectangle(frame, cvPoint(width - 200, 0), cvPoint(width - 440, 50), CV_RGB(200, 0, 200), 1, 8, 0);//center up rectangle
//                            cvRectangle(frame, cvPoint(width, height), cvPoint(width - 150, height - 180), CV_RGB(200, 0, 200), 1, 8, 0);//lower left rectangle
//                            cvRectangle(frame, cvPoint(150, 300), cvPoint(0, height), CV_RGB(200, 0, 200), 1, 8, 0);//lower right Rectangle
                            cvRectangle(frame, cvPoint(width - 150, 50), cvPoint(150, height), CV_RGB(205, 255, 127), 1, 8, 0);//center
                            cvRectangle(frame, cvPoint(width - 150, 50), cvPoint(width - 250, 100), CV_RGB(205, 230, 127), 1, 8, 0);//center left rectangle
                            cvRectangle(frame, cvPoint(250, 50), cvPoint(150, 100), CV_RGB(205, 230, 127), 1, 8, 0);//center right rectangle
                            cvPutText(frame, "->", cvPoint(width - 230, 88), cvFont(3, 5), CV_RGB(205, 230, 127));
                            cvPutText(frame, "<-", cvPoint(170, 88), cvFont(3, 5), CV_RGB(205, 230, 127));
                            if (dp.depth_point().x() < 80 && dp.depth_point().y() < 80) {
                                pauseRight = true;
                                cvRectangle(frame, cvPoint(0, 0), cvPoint(80, 20), CV_RGB(0, 0, 255), 1, 8, 0);
                                cvPutText(frame, "||", cvPoint(40, 15), cvFont(4, 2), CvScalar.BLUE);
                                cvPutText(frame, "||", cvPoint(width - 40, 15), cvFont(4, 2), CvScalar.BLUE);
                                if (pauseRight == true && pauseLeft == true) {
                                    JOptionPane.showMessageDialog(null, "Game Paused");
                                    pauseLeft = false;
                                    pauseRight = false;
                                }

                            }
                            if (dp.depth_point().x() > width - 80 && dp.depth_point().y() < 80) {
                                pauseLeft = true;
                                cvRectangle(frame, cvPoint(0, 0), cvPoint(80, 20), CV_RGB(0, 0, 255), 1, 8, 0);
                                cvPutText(frame, "||", cvPoint(40, 15), cvFont(1, 1), CvScalar.BLUE);
                                cvPutText(frame, "||", cvPoint(width - 40, 15), cvFont(1, 1), CvScalar.BLUE);
                                if (pauseLeft == true && pauseRight == true) {
                                    JOptionPane.showMessageDialog(null, "Game Paused");
                                    pauseLeft = false;
                                    pauseRight = false;

                                }

                            }
                            if ((dp.depth_point().x() < width - 200 && dp.depth_point().y() < 50 && dp.depth_point().x() > width - 440) ||
                                    (dp.start().x() < width - 200 && dp.start().y() < 50 && dp.start().x() > width - 440) ) {
                                // Key Event is simulated if body part hits the top of the Green area.
                                new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            cvRectangle(frame, cvPoint(width - 200, 0), cvPoint(width - 440, 50), CV_RGB(205, 230, 127), 2, 8, 0);//center up rectangle
                                            new Robot().keyPress(key5);
                                            new Robot().delay(200);
                                            new Robot().keyRelease(key5);
                                        } catch (AWTException ex) {
                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                }).start();
                            }
                            if ((dp.depth_point().y() > 100 && dp.depth_point().y() < 300 && dp.depth_point().x() > width - 150) || (dp.start().y() > 100 && dp.start().y() < 300 && dp.start().x() > width - 150)) {
                                //Key Event is simulated if body part hits middle left outside the Green area.
                                new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            cvRectangle(frame, cvPoint(width, 100), cvPoint(width - 150, 300), CV_RGB(205, 230, 127), 2, 8, 0);//left rectangle
                                              new Robot().keyPress(key4);
                                            new Robot().delay(200);
                                            new Robot().keyRelease(key4);
                                        } catch (AWTException ex) {
                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                }).start();
                            }
                            if ((dp.depth_point().x() < 150 && dp.depth_point().y() < 300 && dp.depth_point().x() > 0 && dp.depth_point().y() > 100) || (dp.start().x() < 150 && dp.start().y() < 300 && dp.start().x() > 0 && dp.start().y() > 100)) {
                                //Key Event is simulated if body part hits middle right outside the Green area.
                                new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            cvRectangle(frame, cvPoint(0, 100), cvPoint(150, 300), CV_RGB(205, 230, 127), 2, 8, 0);//Right Rectangle for Right key
                                            new Robot().keyPress(key1);
                                            new Robot().delay(200);
                                            new Robot().keyRelease(key1);
                                        } catch (AWTException ex) {
                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                }).start();
                            }
                            if ((dp.depth_point().x() > width - 150 && dp.depth_point().y() > height - 180 && dp.depth_point().x() > width - 150) || (dp.start().x() > width - 150 && dp.start().y() > height - 180 && dp.start().x() > width - 150)) {
                                //Key Event is simulated if body part hits lower left outside the Green area.
                                new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            cvRectangle(frame, cvPoint(width, height), cvPoint(width - 150, height - 180), CV_RGB(205, 230, 127), 2, 8, 0);//lower left rectangle
                                            new Robot().keyPress(key3);
                                            new Robot().delay(200);
                                            new Robot().keyRelease(key3);
                                        } catch (AWTException ex) {
                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                }).start();
                            }
                            if ((dp.depth_point().x() < 150 && dp.depth_point().y() > 300 && dp.depth_point().y() < height) || (dp.start().x() < 150 && dp.start().y() > 300 && dp.start().y() < height)) {
                                //Key Event is simulated if body part hits lower right outside the Green area.
                                new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            cvRectangle(frame, cvPoint(150, 300), cvPoint(0, height), CV_RGB(205, 230, 127), 2, 8, 0);//lower right Rectangle
                                            new Robot().keyPress(key2);
                                            new Robot().delay(200);
                                            new Robot().keyRelease(key2);
                                        } catch (AWTException ex) {
                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                }).start();
                            }
                            if ((dp.depth_point().y() > 50 && dp.depth_point().y() < 100 && dp.depth_point().x() > width - 250) || (dp.start().y() > 50 && dp.start().y() < 100 && dp.start().x() > width - 250)) {
                                //Key Event is simulated if body part hits left box inside the Green area.
                                new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            cvPutText(frame, "->", cvPoint(width - 230, 88), cvFont(3, 5), CV_RGB(0, 255, 0));
                                            new Robot().keyPress(37);
                                            new Robot().delay(550);
                                            new Robot().keyRelease(37);
                                        } catch (AWTException ex) {
                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                }).start();
                            }
                            if ((dp.depth_point().x() > 150 && dp.depth_point().y() < 100 && dp.depth_point().x() < 250 && dp.depth_point().y() > 50) || (dp.start().x() > 150 && dp.start().y() < 100 && dp.start().x() < 250 && dp.start().y() > 50)) {
                                //Key Event is simulated if body part hits right box inside the Green area.
                                new Thread(new Runnable() {

                                    public void run() {
                                        try {
                                            cvPutText(frame, "<-", cvPoint(170, 88), cvFont(3, 5), CV_RGB(0, 255, 0));
                                            new Robot().keyPress(39);
                                            new Robot().delay(550);
                                            new Robot().keyRelease(39);
                                        } catch (AWTException ex) {
                                            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                    }
                                }).start();
                            }
//                        try {
//                            new Thread(new Runnable() {
//
//                                public void run() {
//                                    try {
//                                        Thread.sleep(1);
//                                    } catch (InterruptedException ex) {
//                                        Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
//                                    }
//                                    defect_reset();
//                                }
//                            }).start();
//                        } catch (Exception e) {
//                        }

                            //System.out.println(dp.depth_point());



                        }
                    }
                } catch (Exception e) {
                }
            }
            points = 0;

            if (staticBackMode == false) {
                bi = frm.getBufferedImage();
            }
            System.out.println("Person Edges-" + person);
            edge.release();
            frm.release();
            frm = null;
            edge = null;
            storage.release();

         }
    }


    private BufferedImage subtractImage(BufferedImage img1, BufferedImage img2) {

        BufferedImage imageOut;
        RenderedOp op = SubtractDescriptor.create(img1, img2, null);
        imageOut = op.getAsBufferedImage();
        return imageOut;
    }

    public BufferedImage createResizedcopy(Image originalImage,
            int scaledWidth, int scaledHeight,
            boolean preserveAlpha) {

        System.out.println(scaledWidth + "x" + scaledHeight);


        System.out.println("resizing...");
        int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
        Graphics2D g = scaledBI.createGraphics();
        if (preserveAlpha) {
            g.setComposite(AlphaComposite.Src);
        }
        g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        g.dispose();
        System.out.println("resize image sent..");
        return scaledBI;
    }

    private void exitApp() {
        // TODO add your handling code here:
        firstmode=false;
        racemode=false;
        System.out.println("Window Closing Event.....");
        stopCamera();
        breakLoop = true;

        cvReleaseCapture(capture);
        ig.setVisible(false);
        ig.dispose();
        System.exit(0);
        // need to be removed when used in application
//        System.exit(0);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jButton4 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTextField3 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Window Capture");
        setBackground(new java.awt.Color(205, 230, 127));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));

        jButton2.setText("Auto mode");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Accuracy Settings");

        jSlider1.setBackground(new java.awt.Color(0, 153, 204));
        jSlider1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jSlider1.setForeground(new java.awt.Color(51, 0, 51));
        jSlider1.setPaintLabels(true);
        jSlider1.setPaintTicks(true);
        jSlider1.setFocusable(false);
        jSlider1.setOpaque(false);
        jSlider1.setUI(new MySliderUI(jSlider1));
        jSlider1.setPaintTicks(true);
        jSlider1.setMajorTickSpacing(10);

        jButton4.setText("Manual mode");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton7.setText("X");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Game Mode only");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(125, 125, 125)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                        .addComponent(jButton7))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(51, 51, 51))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton2)
                    .addComponent(jLabel4))
                .addContainerGap())
        );

        getContentPane().add(jPanel1);
        jPanel1.setBounds(460, 0, 370, 120);

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));

        jTextField3.setText("0");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel5.setText("Camera No.");

        jButton6.setText("X");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Detection Settings");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)
                        .addComponent(jButton6)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel2);
        jPanel2.setBounds(0, 0, 456, 120);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/stop2.png"))); // NOI18N
        jButton8.setBorder(null);
        jButton8.setBorderPainted(false);
        jButton8.setContentAreaFilled(false);
        jButton8.setFocusPainted(false);
        jButton8.setRolloverEnabled(true);
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton8MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton8MouseReleased(evt);
            }
        });
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton8);
        jButton8.setBounds(360, 640, 100, 70);

        jLabel1.setBackground(new java.awt.Color(205, 230, 127));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Hwoarang.jpg"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 690, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(74, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3);
        jPanel3.setBounds(0, 130, 820, 510);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 18));
        jLabel6.setForeground(new java.awt.Color(204, 255, 51));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Mode Status");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(129, Short.MAX_VALUE)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel4);
        jPanel4.setBounds(0, 660, 370, 36);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 12));
        jLabel8.setForeground(new java.awt.Color(204, 255, 51));
        jLabel8.setText("Works best in white or bright background, with less objects.");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(250, 730, 350, 30);

        jMenuBar1.setBackground(new java.awt.Color(205, 230, 127));

        jMenu1.setBackground(new java.awt.Color(205, 230, 127));
        jMenu1.setText("Game");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/startgame.png"))); // NOI18N
        jMenuItem1.setText("General Mode");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/race.jpg"))); // NOI18N
        jMenuItem3.setText("Race Mode");
        jMenuItem3.setDoubleBuffered(true);
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/SniperScope1.png"))); // NOI18N
        jMenuItem5.setText("First Person Shooter Mode");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);

        jMenuBar1.add(jMenu1);

        jMenu2.setBackground(new java.awt.Color(205, 230, 127));
        jMenu2.setText("Preferences");

        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Preferences_Script.png"))); // NOI18N
        jMenuItem6.setText("Accuracy Settings");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Preferences_Script.png"))); // NOI18N
        jMenuItem7.setText("Detection Settings");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Preferences_Script.png"))); // NOI18N
        jMenuItem4.setText("Select Game Controls");
        jMenuItem4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);

        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Preferences_Script.png"))); // NOI18N
        jMenuItem2.setText("SetControls");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem2);

        jMenuItem8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/Preferences_Script.png"))); // NOI18N
        jMenuItem8.setText("Make Savable Game Controls");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuBar1.add(jMenu2);

        jMenu3.setBackground(new java.awt.Color(205, 230, 127));
        jMenu3.setText("About");
        jMenu3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu3ActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        automode = true;        // TODO add your handling code here:
        jSlider1.setValue(thresh);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

        // need to be removed when used in application
        System.out.println("Closing window");
        ig.dispose();
        exitApp();


    }//GEN-LAST:event_formWindowClosing

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        jLabel6.setText("Game Mode");        // TODO add your handling code here:
        firstmode = false;
        racemode = false;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println("Starting Camera ");
            if (capture != null) {
                stopCamera();
            }



            startCamera(StringHelper.n2i(jTextField3.getText()));

//            initDetector();
        } catch (Exception e) {
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenu3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu3ActionPerformed
        JOptionPane.showMessageDialog(null, "This Gaming Console is A Wonderful way to play our games. Happy Gaming!!");        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu3ActionPerformed

    private void jMenuItem1ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed1
    }//GEN-LAST:event_jMenuItem1ActionPerformed1

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        automode = false;        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        controls.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        jPanel1.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        jPanel2.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jButton8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MousePressed
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/stoppushed.png")));        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8MousePressed

    private void jButton8MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MouseReleased
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/stop2.png")));     // TODO add your handling code here:
    }//GEN-LAST:event_jButton8MouseReleased

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        jLabel6.setText("Mode Status");

        racemode = false;
        if (capture != null) {
            stopCamera();

        }


        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        jPanel2.setVisible(false);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        jPanel1.setVisible(false);        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        jLabel6.setText("Race Mode");
        firstmode = false;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {

        if (capture != null) {
                stopCamera();

            }
        } catch (Exception e) {
            System.out.println("Error");
        }


        racemode = true;
        race_mode();

        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        racemode = false;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MotionConsolePc.class.getName()).log(Level.SEVERE, null, ex);
        }

        jLabel6.setText("First Person Shooter Mode");

        try {

            if (capture != null) {
                stopCamera();

            }
        } catch (Exception e) {
            System.out.println("Error");
        }


        firstmode = true;
        first_mode();
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed

makeControls.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
loadControls.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem4ActionPerformed
    public void getcontrols() {
        key1 = controls.key1;
        key2 = controls.key2;
        key3 = controls.key3;
        key4 = controls.key4;
        key5 = controls.key5;
    }
    /**
     * @param args the command line arguments
     */
    int key1 = 39;
    int key2 = 40;
    int key3 = 40;
    int key4 = 37;
    int key5 = 38;
    static MotionConsolePc ig;

    /** Stroke size. it is recommended to set it to 1 for better view */
    //FOLLOWING CODES GOES HERE
    public static void main(String args[]) {
//        try {
//            // Set cross-platform Java L&F (also called "Metal")
//            Color foregroundColor = new Color(205, 230, 127);
//
//            //Set JButton foreground color
//// UIManager.put("Button.foreground",new ColorUIResource(foregroundColor));
//            UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
//
//        } catch (Exception e) {
//            // handle exception
//        }
        ig = new MotionConsolePc();
        ig.setPreferredSize(new Dimension(870, 820));
        ig.setSize(ig.getPreferredSize());
        ig.setBackground(new Color(205, 230, 127));
        ig.setVisible(true);


    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    public static javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    public javax.swing.JSlider jSlider1;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables

    public void stopCamera() {
        // TODO add your handling code here:  
        try {
            firstmode = false;
            racemode = false;
            System.out.println("In here stop");
            breakLoop = true;
            if (sw != null) {
                sw.cancel(true);
            }
//            try {
//
//                cvReleaseImage(jjil);
//                cvReleaseImage(frame);
//            } catch (Exception e) {
//                System.out.println("image release not possible.. error handled");
//            }
            buffimg = null;
            sw = null;
            if (pbox != null) {
              //  pbox.destroy();
                pbox = null;
            }
            if (pbox_jjil != null) {
               // pbox_jjil.destroy();
                pbox_jjil = null;
            }

            //sw.cancel(true);

        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
    protected int _strokeSize = 1;
    protected Color _shadowColor = Color.BLACK;
    protected boolean _shadowed = true;
    protected boolean _highQuality = true;
    protected Dimension _arcs = new Dimension(30, 30);
    protected int _shadowGap = 5;
    protected int _shadowOffset = 4;
    protected int _shadowAlpha = 150;
    protected Color _backgroundColor = Color.LIGHT_GRAY;
}

class MySliderUI extends BasicSliderUI {

    private static float[] fracs = {0.0f, 0.2f, 0.4f, 0.6f, 0.8f, 1.0f};
    private LinearGradientPaint p;

    public MySliderUI(JSlider slider) {
        super(slider);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Rectangle t = trackRect;

        Point2D start = new Point2D.Float(t.x, t.y);
        Point2D end = new Point2D.Float(t.width, t.height);
        Color[] colors = {Color.BLACK, Color.BLACK, Color.DARK_GRAY,
            Color.LIGHT_GRAY, Color.WHITE, Color.WHITE};
        p = new LinearGradientPaint(start, end, fracs, colors);
        g2d.setPaint(p);
        g2d.fillRect(t.x, t.y, t.width, t.height);
    }

    @Override
    public void paintThumb(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle t = thumbRect;
        g2d.setColor(Color.RED);
        int tw2 = t.width / 2;
        g2d.drawLine(t.x, t.y, t.x + t.width - 1, t.y);
        g2d.drawLine(t.x, t.y, t.x + tw2, t.y + t.height);
        g2d.drawLine(t.x + t.width - 1, t.y, t.x + tw2, t.y + t.height);
    }
}
