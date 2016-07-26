/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opencltest;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author simon
 */
public class VideoTest {
    public static void main(String[] args) {
        //http://192.168.43.1:4747/mjpegfeed?640x480;
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture cap = new VideoCapture("http://192.168.43.1:4747/mjpegfeed?640x480");
        Mat mat = new Mat();
        System.out.println(cap.read(mat));
        Imgcodecs.imwrite("test_cam_.png", mat);
    }
   
}
