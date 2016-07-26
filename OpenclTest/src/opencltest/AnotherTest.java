/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencltest;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.*;

/**
 *
 * @author simon
 */
public class AnotherTest {

    public static void main(String args[]) {
        // Load the library

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Consider the image for processing
        Mat image = Highgui.imread("camtest.small.png", Imgproc.COLOR_BGR2GRAY);
        Mat imageHSV = new Mat(image.size(), CvType.CV_8UC4);
        Mat imageBlurr = new Mat(image.size(), CvType.CV_8UC4);
        Mat imageA = new Mat(image.size(), CvType.CV_8UC4);
        Imgproc.cvtColor(image, imageHSV, Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(imageHSV, imageBlurr, new Size(5, 5), 0);
        Imgproc.adaptiveThreshold(imageBlurr, imageA, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 7, 5);

        Highgui.imwrite("test1.png", imageBlurr);
        Highgui.imwrite("test3.png", cornerDetection(image));

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(imageA, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(image, contours, -1, new Scalar(0, 0, 255));
        for (int i = 0; i < contours.size(); i++) {
            System.out.println(Imgproc.contourArea(contours.get(i)));
            if (Imgproc.contourArea(contours.get(i)) > 50) {
                Rect rect = Imgproc.boundingRect(contours.get(i));
                System.out.println(rect.height);
                if (rect.height > 28) {
                    System.out.println(rect.x + "," + rect.y + "," + rect.height + "," + rect.width);
                    Imgproc.rectangle(image, new Point(rect.x, rect.height), new Point(rect.y, rect.width), new Scalar(0, 0, 255));

                }
            }
        }
        Highgui.imwrite("test2.png", image);
    }

    public static Mat cornerDetection(Mat inputFrame) {

        MatOfKeyPoint points = new MatOfKeyPoint();

        Mat mat = inputFrame;
        FeatureDetector fast = FeatureDetector.create(FeatureDetector.DYNAMIC_AKAZE);
        fast.detect(mat, points);

        Scalar redcolor = new Scalar(255, 0, 0);
        Mat mRgba = mat.clone();
//        Imgproc.cvtColor(mat, mRgba, Imgproc.COLOR_RGB2BGR, 4);
        Imgproc.line(mRgba, new Point(100, 100), new Point(300, 300), new Scalar(0, 0, 255));

        Features2d.drawKeypoints(mRgba, points, mRgba, redcolor, 3);

        return mRgba;

    }

    private static class Highgui {

        public Highgui() {
        }

        public static void imwrite(String file, Mat imgage) {
            Imgcodecs.imwrite(file, imgage);
        }

        public static Mat imread(String file, int flags) {
            return Imgcodecs.imread("test.png", flags);
        }
    }
}
