# image-color
Async Image Analyzer

## Building
Depends on Gradle. Instructions to install: https://gradle.org/install/#install

`./gradlew clean build`

## Running

`java-Xmx512m -Dinput=urls.txt -Doutput=dominantColors.txt -DinputBufferSize=64 -DoutputBufferSize=64 -jar build/libs/image-color.jar`

Parameters
`input`: AbsolutePath to input file.
`output`: AbsolutePath to output file.
`inputBufferSize`: Power of 2 integer to size the input RingBuffer. The larger this is, the quicker Http requests will execute and the more image data that will be saved in memory (as bytes).
`outputBufferSize`:  Power of 2 integer to size the output RingBuffer. The larger this is, the more output Strings can be saved. This should be large enough to not block the processors working on the input.