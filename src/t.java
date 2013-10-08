/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Administrator
 */
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

public class t {

    public static void main(String[] args) {
    CanvasFrame frameInput = new CanvasFrame("CameraPreview");
        CanvasFrame frameOutput = new CanvasFrame("ForegroundContour");
        CanvasFrame frameEdge = new CanvasFrame("Edge");
        try {
            FrameGrabber grabber = new OpenCVFrameGrabber(0);
            while (true) {

                grabber.start();
                IplImage grabbedImage = grabber.grab();
                                frameInput.showImage(grabbedImage);

                IplImage foreground = null;
                if (foreground == null) {
                    foreground = IplImage.create(grabbedImage.width(), grabbedImage.height(),
                            IPL_DEPTH_8U, 1);
                }
                cvCvtColor(grabbedImage, foreground, CV_BGR2GRAY);
//                blur
                IplImage canny_output = foreground.clone();
                int thresh = 80;
                cvCanny(foreground, canny_output, thresh, thresh * 2, 3);
                frameEdge.showImage(canny_output);
                CvMemStorage storage = CvMemStorage.create();
                CvSeq first_contour = new CvSeq();
                CvSeq contour = new CvSeq(null);
                //public static int cvFindContours(CvArr image, CvMemStorage storage, CvSeq first_contour, int header_size, int mode, int method)
                cvFindContours(canny_output, storage, first_contour, Loader.sizeof(CvContour.class), CV_RETR_TREE, CV_CHAIN_APPROX_SIMPLE);
//                  cvFindContours(foreground, storage, contour,
//                    Loader.sizeof(CvContour.class), CV_RETR_LIST,
//                    CV_CHAIN_APPROX_SIMPLE);
IplImage cont=grabbedImage.clone();
//                frameOutput.showImage(grabbedImage);
//for( int i = 0; i< first_contour.total(); i++ )
                for (CvSeq c = first_contour; c != null; c = c.h_next()) {
                    {


//     first_contour.
//       Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
//public static void cvDrawContours(CvArr img, CvSeq contour, CvScalar external_color, CvScalar hole_color, int max_level, int thickness, int line_type)
// public static void cvDrawContours(CvArr arg0, CvSeq arg1, CvScalar arg2, CvScalar arg3, int arg4, int arg5, int arg6, CvPoint arg7)
                        //   cvDrawContours( grabbedImage, first_contour, i, CvScalar.YELLOW,2,8,cvPoint(0, 0));

                        
                        cvDrawContours(
                                cont,
                                c,
                                CvScalar.RED, // Red
                                CvScalar.BLUE, // Blue
                                1, // Vary max_level and compare results
                                2,
                                8);

                        frameOutput.showImage(cont);

                    } 

//                    cvReleaseImage(cont);
                    
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
