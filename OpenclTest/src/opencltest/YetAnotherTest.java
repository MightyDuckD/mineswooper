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

/**
 *
 * @author simon
 */
public class YetAnotherTest {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat kernel = new Mat(3, 3, CV_8UC1);
        kernel.put(0, 0, new double[]{0, 1, 0, 1, 1, 1, 0, 1, 0});

        Mat source = Imgcodecs.imread("camtest.small.jpg");
        Mat edges = new Mat();

        Imgproc.GaussianBlur(source, source, new Size(13, 13), 0);
        Imgproc.Canny(source, edges, 10, 30,3,false);
//        Imgproc.dilate(edges, edges, kernel);
//        Imgproc.dilate(edges, edges, kernel);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 300, 50, 16);

        Mat empty = new Mat(source.height(),source.width(),source.type());
        paintLines(empty, lines);
        Imgproc.dilate(empty, empty, kernel);
        Imgproc.dilate(empty, empty, kernel);

        Imgcodecs.imwrite("test_2.png", source);
        Imgcodecs.imwrite("test_3.png", edges);
        Imgcodecs.imwrite("test_4.png", empty);
    }

    private static void paintLines(Mat targetImg, Mat lines) {
        for (int x = 0; x < lines.rows(); x++) {
            double[] vec = lines.get(x, 0);
            double x1 = vec[0],
                    y1 = vec[1],
                    x2 = vec[2],
                    y2 = vec[3];
            Point start = new Point(x1, y1);
            Point end = new Point(x2, y2);
            Imgproc.line(targetImg, start, end, new Scalar(255, 0, 0), 1);
        }
    }
}
