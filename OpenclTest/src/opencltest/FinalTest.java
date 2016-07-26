/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencltest;

import org.opencv.core.Core;
import static org.opencv.core.CvType.CV_8UC1;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;

/**
 *
 * @author simon
 */
public class FinalTest {

    public static void main(String[] args) {// Load the library

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat source = Imgcodecs.imread("camtest.small.png");
        Mat gauss = new Mat(source.width(), source.height(), source.type());
        Imgproc.GaussianBlur(source, gauss, new Size(3, 3), 0);
        Mat gray = new Mat(source.width(), source.height(), CV_8UC1);

        Imgproc.cvtColor(gauss, gray, Imgproc.COLOR_BGR2GRAY);
        Mat outerBox = new Mat(gauss.size(), CV_8UC1);
        Imgproc.adaptiveThreshold(gray, outerBox, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 9, 2);
        Core.bitwise_not(outerBox, outerBox);
        Mat kernel = new Mat(3, 3, CV_8UC1);
        kernel.put(0, 0, new double[]{0, 1, 0, 1, 1, 1, 0, 1, 0});
        Imgproc.dilate(outerBox, outerBox, kernel);
        Mat lines = new Mat();
        Imgproc.HoughLinesP(outerBox, lines, 1, Math.PI / 180, 300);

        Mat img = new Mat();
        Imgproc.cvtColor(outerBox, img, Imgproc.COLOR_GRAY2BGR);
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            System.out.println("line " + start + " " + end);
            Imgproc.line(img, start, end, new Scalar(255,0,0), 1);

        }
        System.out.println(lines);
        Imgcodecs.imwrite("test_1.png", img);
    }
}
