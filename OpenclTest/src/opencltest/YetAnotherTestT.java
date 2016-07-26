/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencltest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import static org.opencv.core.CvType.CV_8UC1;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author simon
 */
public class YetAnotherTestT {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat kernel = new Mat(3, 3, CV_8UC1);
        kernel.put(0, 0, new double[]{0, 1, 0, 1, 1, 1, 0, 1, 0});

        Mat source = Imgcodecs.imread("test.smaller.png");
        Mat blur = new Mat();
        Mat edges = new Mat();
        Mat dilated = new Mat();

        Imgproc.GaussianBlur(source, blur, new Size(13, 13), 0);
        Imgproc.Canny(blur, edges, 10, 30, 5, false);
//        Imgproc.cvtColor(edges, edges, Imgproc.COLOR_RGB2GRAY);
//        Imgproc.adaptiveThreshold(edges, edges, 255, ADAPTIVE_THRESH_MEAN_C, THRESH_BINARY, 5, 2);
//        Core.bitwise_not(edges, edges);
//        Imgproc.dilate(edges, edges, kernel);
//        Imgproc.dilate(edges, dilated, kernel);
        dilated = edges;
//        Core.bitwise_not(edges, edges);

        Mat lines = new Mat();
        Imgproc.HoughLinesP(dilated, lines, 1, Math.PI / 180, 300, 10, 70);

        Mat empty = new Mat(source.height(), source.width(), source.type());
//        paintLines(empty, lines);

        List<MatOfPoint> contours = new ArrayList<>();
        Mat hier = new Mat();
        Imgproc.findContours(edges, contours, hier, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat foundSquare = new Mat(source.height(), source.width(), CvType.CV_8UC4);
        source.copyTo(foundSquare);

        List<Double> hor = new ArrayList<>();
        for (Iterator<MatOfPoint> iterator = contours.iterator(); iterator.hasNext();) {
            MatOfPoint next = iterator.next();
            Rect bounding = Imgproc.boundingRect(next);
            int tr = 20;
            if (diffLessThan(bounding.size().width - 40, tr)
                    && diffLessThan(bounding.size().height - 40, tr)) {
                Imgproc.rectangle(empty, bounding.tl(), bounding.br(), randomColor(), 3);
//                hor.add(bounding.x + 0.0);
                hor.add(bounding.x + bounding.width/2.0 + 0.0);
                drawRect(bounding, foundSquare);
            }
        }
        

        Imgcodecs.imwrite("test_2.png", source);
        Imgcodecs.imwrite("test_3.png", dilated);
        Imgcodecs.imwrite("test_4.png", empty);
        Imgcodecs.imwrite("test_h.png", foundSquare);
        
        
        hor.sort(Double::compare);
        double low = hor.get(0);
        double hih = hor.get(hor.size() - 1);
        double n = hor.size();
        Function<Double, Double> K = (d) -> (Math.abs(d) <= 1) ? ((3.0 / 4.0) * (1 - (d * d))) : 0;//epanechnikov kernel
        List<Double> result = new ArrayList<>();
        double h = 10;
        for(int i = 0; i < source.width() + 1;i++)
            result.add(0.0);
        for (double d = low; d <= hih; d += 1) {
            double sum = 0;
            for (Double di : hor) {
                sum += K.apply((d - di) / h);
            }
            result.set(
                    (int)d,
                    sum / (n * h)
            );
            System.out.println(sum / (n * h));
        }
        normalize(result, 255);
        Mat test = new Mat(source.height(), source.width(), source.type());
        source.copyTo(test);
        draw(result, test);
        Imgcodecs.imwrite("test_uwot.png", test);
    }

    private static boolean diffLessThan(double val, double compare) {
        return Math.abs(val) < compare;
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

    private static Scalar randomColor() {
        return new Scalar(Math.random() * 255, Math.random() * 255, Math.random() * 255);
    }

    private static void drawRect(Rect bounding, Mat foundSquare) {
//        drawCross(bounding.x, bounding.y, foundSquare);
        drawCross(bounding.tl().x, bounding.tl().y, foundSquare);
        drawCross(bounding.br().x, bounding.br().y, foundSquare);
    }

    private static void normalize(List<Double> data, double max) {
        double mx = data.stream().max(Double::compare).orElse(new Double(0));
        if (mx == 0) {
            return;
        }
        for (int i = 0; i < data.size(); i++) {
            data.set(i, (max * data.get(i)) / mx);
        }
    }

    private static void drawCross(double x, double y, Mat foundSquare) {
        Imgproc.line(foundSquare,
                new Point(x, 0), new Point(x, foundSquare.height()),
                new Scalar(255, 0, 0, 180), 1);

        Imgproc.line(foundSquare,
                new Point(0, y), new Point(foundSquare.width(), y),
                new Scalar(255, 0, 0, 180), 1);
    }

    private static void draw(List<Double> result, Mat test) {
        for (int i = 1; i < result.size() - 1; i++) {
            if (result.get(i) > result.get(i + 1) && result.get(i) > result.get(i - 1)) {
                Imgproc.line(test,
                        new Point(i, 0), new Point(i, test.height()),
//                        new Scalar(result.get(i), 0, 0), 1
                        new Scalar(0,0,255),1
                );
            }
            
//            Imgproc.line(test,
//                        new Point(i, 0), new Point(i, test.height()),
//                        new Scalar(result.get(i), 0, 0), 1
////                        new Scalar(0,0,255),1
//                );
        }
    }
}
