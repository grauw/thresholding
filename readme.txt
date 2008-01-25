Thresholding
============

Introduction to Image Processing individual assignment 1
By Laurens Holst, 0122173

Manual
======

You can start this program by double-clicking on the thresholding.jar file, or via the command line:
java -jar thresholding.jar Vlieland_Objects.jpg

When opening via the command line, you will see some information messages as you navigate the program.
As you can see above, you can pass the image that you want to process via the command line. You can also
open an image using the GUI. Some demonstration pictures are included.

The program should be pretty self-explanatory, but I’ll explain the menus:

In the <File> menu you can use <Open> to select a new image to process. When you have an image loaded,
you can use the <Save images> menu item to save four processed copies of the file, the type of processing
is appended to the filename:

    <filename>.grayscale.png  - A grayscale version (in case the original file was in colour)
    <filename>.blurred.png    - A blurred version, used by the improved thresholding
    <filename>.standard.png   - The image processed with standard thresholding
    <filename>.improved.png   - The image processed with improved thresholding

In the <View> menu you can use the <Set number of segments...> option to change the number of segments
the program should use for the thresholding. The default value is 4.

The next group of options in the <View> menu are used to control which representation of the processed
image is shown:

    Original   - The original image as-is
    Grayscale  - A grayscale version of the original image (in case the original file was in colour)
    Blurred    - A blurred version, used by the improved thresholding
    Standard   - The image processed with standard thresholding
    Improved   - The image processed with improved thresholding

Finally, you can select whether the program should overlay the image with the histogram and the
thresholds in the histogram using the <Overlay histogram and thresholds> option. Whether it shows the
histogram and thresholds of the standard thresholding method (without blur) or those of the improved
thresholding method (after blurring the image first) depends on the current view:

    Original   - Standard
    Grayscale  - Standard
    Blurred    - Improved
    Standard   - Standard
    Improved   - Improved

Improvement
===========

My improvement applies a low-pass filter (a.k.a. a blur) to the image before thresholding it. This removes
some high-frequency that decrease the accuracy of the thresholding.

The blurring is done based on the blurMatrix variable in the ImageCanvas class. This matrix can be tweaked
to try out different masks and their effects on the thresholding.


~Grauw