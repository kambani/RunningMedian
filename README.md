# RunningMedian
Finding a median of an incoming stream of data is relatively simple if you can store all the values in the memory/storage. However, if we are imposed with memory/storage constraints, finding an exact solution is difficult. If the data has some characteristics we can exploit and develop efficient specialized solutions. For example if the stream type is integral, we can find an approximate solution if we can leverage lessons from counting sort and probability density function. This code is an attempt to do the same

# Usage

C:\Users\kaushala\IdeaProjects\RunningMedian\src>set "PATH=%PATH%;C:\Program Files\Java\jdk-13.0.1\bin"

C:\Users\kaushala\IdeaProjects\RunningMedian\src>javac Main.java RunningMedian.java

C:\Users\kaushala\IdeaProjects\RunningMedian\src>"C:\Program Files\Java\jdk-13.0.1\bin\java.exe" Main C:\temp\series.txt
Expected,Actual
1.0,1.0
1.0,1.0
1.0,1.0
1.0,1.0
1.0,1.0
1.0,1.0
1.0,1.0
1.5,1.0
2.0,2.0
2.0,2.0
2.0,2.0
2.0,2.0
2.0,2.0
2.5,2.0
3.0,3.0
3.5,3.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
4.0,4.0
Input Series:
[1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 7, 7, 7, 34]
