# image-color
Async Image Analyzer

## Building
Depends on Gradle. Instructions to install: https://gradle.org/install/#install

`./gradlew clean build`

## Running

`java-Xmx512m -Dinput=urls.txt -Doutput=dominantColors.txt -DinputBufferSize=64 -DoutputBufferSize=64 -jar build/libs/image-color.jar`

## Parameters
`input`: AbsolutePath to input file.

`output`: AbsolutePath to output file.

`inputBufferSize`: Power of 2 integer to size the input RingBuffer. The larger this is, the quicker Http requests will execute, but the more image data that will be saved in memory (as bytes).

`outputBufferSize`:  Power of 2 integer to size the output RingBuffer. The larger this is, the more output Strings can be saved. This should be large enough to not block the processors working on the input. Because the output handler is simply writing to file, this should be empty most of time.

## Notes
Application make use of:
* Apache NIO: https://hc.apache.org/httpcomponents-core-ga/tutorial/html/nio.html
* LMAX Disruptor: https://github.com/LMAX-Exchange/disruptor/wiki/Introduction
* Image analysis alogrithm is very crude and has much opporunity for optimization: loops through each pixel, keeping track of the number of occurrences of any given RGB value. It currently excludes white.

## Testing
jvisualvm output of CPU usage and Heap usage during run of urls.txt:
![alt text](https://github.com/rene-m-hernandez/image-color/blob/master/CPUandMEMusage.png)
