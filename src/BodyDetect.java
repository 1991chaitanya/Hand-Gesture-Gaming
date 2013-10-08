
import static com.googlecode.javacv.cpp.opencv_core.CV_FONT_HERSHEY_PLAIN;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvPutText;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MEDIAN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvBoundingRect;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvDilate;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvErode;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvSmooth;
import java.io.File;
import com.googlecode.javacv.cpp.videoInputLib.videoInput;
import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvConvexityDefect;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvFont;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvConvexityDefect;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_video.BackgroundSubtractorMOG2;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.SubtractDescriptor;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class BodyDetect {

    private IplImage prevImg,  currImg,  diffImg;

    public static void main(String[] args) throws Exception {
        // Preload the opencv_objdetect module to work around a known
        int count = 0;
        CvSeq contour = new CvSeq(null);
        CvSeq ptr = new CvSeq();
        Loader.load(opencv_objdetect.class);
        CvMemStorage storage = CvMemStorage.create();
        CanvasFrame frameInput = new CanvasFrame("Original");
        CanvasFrame frameOutput = new CanvasFrame("Foregroung");
        //File f = new File("lib/squash1.avi");
        FrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        IplImage grabbedImage = grabber.grab();
        grabber.start();
        IplImage frame2 = null;
        BufferedImage bi = null;
        frame2 = grabber.grab();
        bi = frame2.getBufferedImage();
        grabber.stop();
        grabber.release();
        IplImage foreground = null;

        grabber.start();
        // BackgroundSubtractorMOG2 mog = null;
        BackgroundSubtractorMOG2 mog = new BackgroundSubtractorMOG2(200, 10,
                true);
        IplImage frame = grabbedImage.clone();
        CvSize sz = cvGetSize(frame);
  
        IplImage hsv_mask = cvCreateImage(sz, 8, 1);
        CvScalar hsv_min = cvScalar(0, 30, 80, 0);
        CvScalar hsv_max = cvScalar(20, 150, 255, 0);
        while (frameInput.isVisible() && (grabbedImage = grabber.grab()) != null) {
            count = 0;
            System.out.println("Inside while");
            // cvSmooth(grabbedImage, grabbedImage, CV_GAUSSIAN, 9, 9, 2, 2);
            frame = grabbedImage.clone();
            if (foreground == null) {
                foreground = IplImage.create(frame.width(), frame.height(),
                        IPL_DEPTH_8U, 1);
            }
//mog.apply(grabbedImage, foreground, -1);
            BufferedImage bi2 = subtractImage(bi, grabbedImage.getBufferedImage());
            count++;
            cvCvtColor(IplImage.createFrom(bi2), foreground, CV_BGR2GRAY);
            cvThreshold(foreground, foreground, 50, 255, CV_THRESH_BINARY_INV);
//mog.apply(grabbedImage, foreground,-1);
//mog.apply(foreground, foreground, -1);
//                        cvDilate(foreground, foreground, null, 3);
//                        cvErode(foreground, foreground, null, 5);
//
            cvSmooth(foreground, foreground, CV_MEDIAN, 3, 3, 2, 2);

//       cvThreshold(foreground,foreground,128,255,CV_THRESH_BINARY);
            frameOutput.showImage(foreground);
            if (contour != null && !contour.isNull()) {
                contour = cvApproxPoly(contour, Loader.sizeof(CvContour.class), storage, CV_POLY_APPROX_DP, 1, 1);
            }

            cvFindContours(foreground, storage, contour,
                    Loader.sizeof(CvContour.class), CV_RETR_LIST,
                    CV_CHAIN_APPROX_SIMPLE);
            // cvDilate(diff, diff, null, 3);
            // cvErode(diff, diff, null, 3);
            cvDrawContours(grabbedImage, contour, CvScalar.YELLOW, CvScalar.RED, 1, 1, 8);

            CvRect boundbox;
            CvSeq hull3 = new CvSeq();
//if(contour!=null)
//{
////     CvPoint[] pt=new CvPoint[5];
////            for(int o=0;o<4;o++)
////            {
////                pt[o] = new CvPoint(cvGetSeqElem(points, o));// Can cast..
////                System.out.println(pt[o].x()+"-"+pt[o].y());
////            }
////         cvLine(grabbedImage, pt[0], pt[1],CvScalar.RED,1,1,0);
////         cvLine(grabbedImage, pt[1], pt[2], CvScalar.RED,1,1,0);
////       cvLine(grabbedImage, pt[2], pt[3],CvScalar.RED,1,1,0);
////           cvLine(grabbedImage, pt[3], pt[0], CvScalar.RED,1,1,0);
//
////cvDrawContours(grabbedImage, contour2, CvScalar.YELLOW, CvScalar.RED, 1, 1, CV_AA);
//       hull3 = cvConvexHull2(contour, null, CV_COUNTER_CLOCKWISE, 0);
//        CvSeq defectSeq = cvConvexityDefects(contour, hull3, storage);//changes made to cvcreatememstorage()
//
//
//        for (int r=0;r<defectSeq.total();r++){
//   CvConvexityDefect dp=new CvConvexityDefect(cvGetSeqElem(defectSeq,r));//(CvConvexityDefect);
////  cvDrawContours(grabbedImage,points, CvScalar.GREEN, CvScalar.MAGENTA, 1, 1, 8,dp.depth_point());
//  // cvLine(grabbedImage,(dp.start()),(dp.end()),CvScalar.BLUE,-1,8,0);
//        //      cvLine(grabbedImage,(dp.start()),(dp.depth_point()),CvScalar.GREEN,-1,8,0);
////                cvLine(grabbedImage,(dp.depth_point()),(dp.end()),CvScalar.RED,-1,8,0);
//               // cvCircle(grabbedImage,(dp.depth_point()),2,CvScalar.YELLOW,-1,8,0);
//         cvCircle(grabbedImage, dp.end(), 5, CV_RGB(255, 0, 0), -1, 8, 0);
//         cvCircle(grabbedImage, dp.start(), 5, CV_RGB(0,255, 0), -1, 8, 0);
//         cvCircle(grabbedImage, dp.depth_point(), 5, CV_RGB(0, 0,255), -2,5, 0);
//         cvPutText(grabbedImage,"Edge", dp.depth_point(), cvFont(1, 1), CvScalar.RED);
//    }}
            try {
                int cnt = 0;
                System.out.println("Contours-" + contour.total());

                boundbox = cvBoundingRect(contour, 0);
                int mycounter=0;
                CvPoint start,end;
                start=new CvPoint();
                end=new CvPoint();
                



                for (ptr = contour; ptr != null; ptr = ptr.h_next()) {
                    System.out.println("inside for");
                    boundbox = cvBoundingRect(ptr, 0);
                    count++;
//                    if (boundbox.width() > 80 && boundbox.height() > 80 && boundbox.width() < 600 && boundbox.height() < 400) {
                        cvRectangle(
                                grabbedImage,
                                cvPoint(boundbox.x(), boundbox.y()),
                                  cvPoint(boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()), CV_RGB(255, 0, 0), 1, 8,
                                0);

//                        cvPutText(grabbedImage, "Contour-" + count, cvPoint(boundbox.x(), boundbox.y()), cvFont(1, 1), CvScalar.RED);
//                    }
//                if(mycounter==0){
//                    start.put(boundbox.x()+(boundbox.width()/2) , boundbox.y());
//                }else if(mycounter==1){
//
//                    end.put(boundbox.x()+(boundbox.width()/2) , boundbox.y());
//                     cvLine(grabbedImage, start, end, CvScalar.YELLOW, 3, 8, 0);
//                }else if(mycounter>1){
//                    start=end;
//                    end.put(boundbox.x()+(boundbox.width()/2) , boundbox.y());
//                     cvLine(grabbedImage, start, end, CvScalar.YELLOW, 3, 8, 0);
//                  }
//                        mycounter++;


//                                cvPutText(grabbedImage, " " + cnt,
//                                               cvPoint(boundbox.x(), boundbox.y()), font, CvScalar.RED);
                    hull3 = cvConvexHull2(ptr, null, CV_COUNTER_CLOCKWISE, 0);
                    CvSeq defectSeq = cvConvexityDefects(ptr, hull3, storage);//changes made to cvcreatememstorage()

                    System.out.println("Defects Count - " + defectSeq.total());
                    for (int r = 0; r < defectSeq.total(); r++) {

                        CvConvexityDefect dp = new CvConvexityDefect(cvGetSeqElem(defectSeq, r));//(CvConvexityDefect);
//  cvDrawContours(grabbedImage,points, CvScalar.GREEN, CvScalar.MAGENTA, 1, 1, 8,dp.depth_point());
                        cvLine(grabbedImage, (dp.start()), (dp.end()), CvScalar.BLUE, 3, 8, 0);
//cvLine(grabbedImage,(dp.start()),(dp.depth_point()),CvScalar.GREEN,3,8,0);
//cvLine(grabbedImage,(dp.depth_point()),(dp.end()),CvScalar.RED,3,8,0);
                    // cvCircle(grabbedImage,(dp.depth_point()),2,CvScalar.YELLOW,-1,8,0);
//        cvCircle(grabbedImage, dp.end(), 5, CV_RGB(255, 0, 0), -1, 8, 0);
//        cvCircle(grabbedImage, dp.start(), 5, CV_RGB(0, 255, 0), -1, 8, 0);
//        cvCircle(grabbedImage, dp.depth_point(), 5, CV_RGB(0, 0, 255), -2, 5, 0);
//        cvPutText(grabbedImage, "" + r, dp.depth_point(), cvFont(1, 1), CvScalar.RED);
                    // Color randomColor = new Color(rand.nextFloat(),
                    // rand.nextFloat(), rand.nextFloat());
                    // CvScalar color = CV_RGB(randomColor.getRed(),
                    // randomColor.getGreen(), randomColor.getBlue());
                    // cvDrawContours(diff, ptr, color, CV_RGB(0, 0, 0), -1,
                    // CV_FILLED, 8, cvPoint(0, 0));

                    }


                }

            } catch (Exception e) {
            }

            frameInput.showImage(grabbedImage);

//          if(count%10==0)
            bi = frame.getBufferedImage();

        }
        grabber.stop();
        frameInput.dispose();
        frameOutput.dispose();
    }

    public static BufferedImage subtractImage(BufferedImage img1, BufferedImage img2) {

        BufferedImage imageOut;
        RenderedOp op = SubtractDescriptor.create(img1, img2, new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED));
        imageOut = op.getAsBufferedImage();
        return imageOut;
    }
}
