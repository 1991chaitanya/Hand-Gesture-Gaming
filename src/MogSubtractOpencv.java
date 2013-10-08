
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_video.BackgroundSubtractorMOG2;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.SubtractDescriptor;

public class MogSubtractOpencv {

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

//   cvSmooth(grabbedImage, grabbedImage, CV_MEDIAN, 3, 3, 2, 2);
            mog.apply(grabbedImage, foreground, -1);

//BufferedImage bi2=subtractImage(bi , grabbedImage.getBufferedImage());
//IplImage biIpl=IplImage.createFrom(bi2);
//
//cvCvtColor(biIpl, foreground, CV_BGR2GRAY);
            cvThreshold(foreground, foreground, 50, 100, CV_THRESH_BINARY);
            cvDilate(foreground, foreground, null, 1);
            cvErode(foreground, foreground, null, 3);

//                        cvDilate(foreground, foreground, null, 3);

//                        cvSmooth(foreground, foreground, CV_MEDIAN, 3, 3, 2, 2);


//   if ( contour != null && !contour.isNull() )
//      contour =cvApproxPoly( contour, Loader.sizeof( CvContour.class ), storage, CV_POLY_APPROX_DP,1,8);

            cvFindContours(foreground, storage, contour,
                    Loader.sizeof(CvContour.class), CV_RETR_LIST,
                    CV_CHAIN_APPROX_SIMPLE);
            // cvDilate(diff, diff, null, 3);
            // cvErode(diff, diff, null, 3);
//cvDrawContours(grabbedImage, contour, CvScalar.YELLOW, CvScalar.RED, 1, 1, 8);

            CvRect boundbox;
            try {
                int cnt = 0;
                System.out.println("Contours-" + contour.total());
                for (ptr = contour; ptr != null; ptr = ptr.h_next()) {
                    System.out.println("inside for");
                    boundbox = cvBoundingRect(ptr, 0);
                    count++;
                    if (boundbox.width() > 80 && boundbox.height() > 80 && boundbox.width() < 500 && boundbox.height() < 400) {
                        cvRectangle(
                                grabbedImage,
                                  cvPoint(boundbox.x(), boundbox.y()),
                                cvPoint(boundbox.x() + boundbox.width(), boundbox.y() + boundbox.height()), CV_RGB(255, 0, 0), 1, 8,
                                0);
                        cvPutText(grabbedImage, "Contour-" + count, cvPoint(boundbox.x(), boundbox.y()), cvFont(1, 1), CvScalar.RED);
                    }


                }

            } catch (Exception e) {
            }
            frameOutput.showImage(foreground);
            frameInput.showImage(grabbedImage);
//          if(count%10==0)
//                      bi=frame.getBufferedImage();

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
