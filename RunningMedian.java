import java.util.TreeMap;
import java.util.*;

//
// The job of the RunningMedian model
// is to compute the running median of a
// never ending series, without storing all the
// values of the series
//

//
// This class denotes the values
// or the range of values in the
// incoming series.
//
class Range
{
    //
    // High and Low end of the range
    // (both inclusive). Range can also
    // represent single value with low ==
    // high
    //
    public int low;
    public int high;

    //
    // Constructor
    //
    public Range(int low, int high){
        this.low = low;
        this.high = high;
    }

    //
    // If a number falls in the range
    // High and Low inclusive.
    //
    public boolean contains(int number){
        return (number >= low && number <= high);
    }

    @Override
    public String toString() {
        return (this.low + "-" + this.high);
    }

    @Override
    public int hashCode() {
        return Objects.hash(low, high);
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Range)) {
            return false;
        }

        Range range = (Range) o;
        return range.low == low &&
                range.high == high;
    }
}

class RangeCompare implements Comparator<Range>
{
    @Override
    public int compare(Range r1, Range r2) {

        //
        // Ranges will never overlap
        // for this problem. Disjoint
        //
        if (r1.low == r2.low && r1.high == r2.high) {
            return 0;
        } else if (r1.high < r2.low) {
            return -1;
        } else {
            return 1;
        }
    }
}

//
// For a given value or range of
// values in a series this class
// stores all the occurrence stats.
//
class Probablity
{
    //
    // Number of times a value or
    // range of values has occurred
    //
    public int occurrenceCount;

    //
    // occurrenceCount / Total count of series
    //
    public double probabilityValue;

    //
    // Stores sum of probability of all
    // the numbers less than or equal to
    // that of a given value or range of values
    //
    public double probabilityOfNumbersLTE;

    //
    // Stores sum of probability of all
    // the numbers greater than or equal to
    // that of a given value or range of values
    //
    public double probabilityOfNumbersGTE;

    //
    // How far is this value/range from being
    // the median.
    //
    public double distanceFromMedian;

    //
    // Constructor
    //
    Probablity(int occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    @Override
    public String toString() {
        return (this.occurrenceCount
                + " " + this.probabilityValue
                + " " + this.probabilityOfNumbersLTE
                + " " + this.probabilityOfNumbersGTE
                + " " + this.distanceFromMedian);
    }
}

class ProbablityCompare implements Comparator<Probablity>
{
    //
    // We use this comparator to detect if a range/value
    // is close to being a median.
    //
    @Override
    public int compare(Probablity p1, Probablity p2) {
       if (p1.distanceFromMedian < p2.distanceFromMedian) {
           return -1;
       } else if (p1.distanceFromMedian > p2.distanceFromMedian) {
           return 1;
       } else {
           return 0;
       }
    }
}

//
// This class calculates the running
// median for a infinitely long series
// with space constraints of not storing more
// than N unique entries.
//
public class RunningMedian
{
    //
    // TreeMap to store a value or range of values
    // and its count. Tree Map sorts entries
    // by key.
    //
    private TreeMap<Range, Probablity> CountMap;

    //
    // Total values in the series
    //
    private int totalValues;

    //
    // Memory Constraint
    // How many entries can the count map hold
    //
    private static int maxEntries;

    //
    // Constructor
    //
    RunningMedian() {
        CountMap = new TreeMap<Range, Probablity>(new RangeCompare());
        totalValues = 0;
        maxEntries = 4;
    }

    //
    // This function says if a range/value
    // can be clubbed with other range/value
    // These are usually the candidates which are
    // far from being a median.
    //
    private boolean isMergeCandidate(Range r) {
        Probablity p = CountMap.get(r);
        if (p.distanceFromMedian == 0) {
            return false;
        }

        return true;
    }

    //
    // Utility to round the double to given precision
    //
    private double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    //
    // This function combines two disjoint ranges
    //
    private Range clubRanges(Range range1, Range range2) {
        Range range = new Range(Math.min(range1.low, range2.low),
                                Math.max(range1.high, range2.high));

        return range;
    }

    //
    // This function clubs the neighboring entries in the CountMap
    // which are the farthest from the median. This is what we
    // call range creation by zooming out on certain range of values
    // to make way for new entries that are closer to the median.
    // It just clubs once and then exists. Meaning at the max only
    // the first two best candidates will be clubbed
    //
    public void merge() {

        //
        // Sort entries in descending order of their
        // distance from median
        //
        List<Map.Entry<Range, Probablity>> listDescDistanceFromMedian =
                new ArrayList<Map.Entry<Range, Probablity>>(CountMap.entrySet());
        Collections.sort(listDescDistanceFromMedian,
                new Comparator<Map.Entry<Range, Probablity>>() {
            public int compare(Map.Entry<Range, Probablity> entry1,
                               Map.Entry<Range, Probablity> entry2)
            {
                Probablity p1 = entry1.getValue();
                Probablity p2 = entry2.getValue();
                if (p1.distanceFromMedian < p2.distanceFromMedian) {
                    return 1;
                } else if (p1.distanceFromMedian > p2.distanceFromMedian) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        //
        // Try all the candidates from the
        // above sorted list whose distance from
        // median is > 0 and see if we can club a candidate with
        //  its fellow neighbor whose distance from median is also
        // > 0.
        //
        List<Range> keyList = new ArrayList<Range>(CountMap.keySet());
        int i = 0;
        while(true) {
            Map.Entry<Range, Probablity> mergeeEntry = listDescDistanceFromMedian.get(i);
            Range mergee = mergeeEntry.getKey();
            Probablity mergeeProbability = mergeeEntry.getValue();

            if (round(mergeeProbability.distanceFromMedian, 1) == 0) {

                //
                // We have reached the median candidates
                // which cannot be clubbed. Exit
                //
                break;
            }

            int index = keyList.indexOf(mergee);

            //
            // We expect the mergee to be either at the last
            // or the beginning of the series, i.e at the edge
            //
            if (index == 0) {
                index++;
            } else {
                index--;
            }

            Range mergerer = keyList.get(index);

            //
            // Check if the mergerer is ready
            // to be merged
            //
            if (isMergeCandidate(mergerer) == false) {
                continue;
            }

            //
            // Compute the new range.
            // Remove mergee and mergerer
            //
            Range newRange = clubRanges(mergee, mergerer);
            Probablity newP = new Probablity(CountMap.get(mergee).occurrenceCount +
                    CountMap.get(mergerer).occurrenceCount);
            CountMap.remove(mergee);
            CountMap.remove(mergerer);
            CountMap.put(newRange, newP);
            break;
        }
    }

    //
    // Given all the entries, and their num occurrences
    // this function goes and computes probability distribution
    // of the all the entries and their distances from the mean.
    //
    public void computeProbablityDistribution() {
        double totalProbab = 0;
        double probabilityOfNumbersLTE = 0;
        for (Map.Entry<Range, Probablity> entry : CountMap.entrySet()) {
            Probablity p = entry.getValue();
            p.probabilityValue = p.occurrenceCount / (double)this.totalValues;
            probabilityOfNumbersLTE = totalProbab + p.probabilityValue;
            p.probabilityOfNumbersLTE = probabilityOfNumbersLTE;
            totalProbab += p.probabilityValue;
        }

        //
        // For probabilityOfNumbersGTE we will have to do
        // O(n^2). However as know we are only iterating over unique
        // values of the series and limited by maxEntries.
        // Hence it should be computationally less severe
        //
        List<Map.Entry<Range, Probablity>> entryList = new ArrayList<Map.Entry<Range, Probablity>>
                                                           (CountMap.entrySet());
        for (int i = 0; i < entryList.size(); i++) {
            Probablity p = entryList.get(i).getValue();
            double probabilityOfNumbersGTE = 0;
            for (int j = i; j < entryList.size(); j++) {
                probabilityOfNumbersGTE += entryList.get(j).getValue().probabilityValue;
            }

            p.probabilityOfNumbersGTE = probabilityOfNumbersGTE;
        }

        //
        // A number is truly a median if the sum of probabilities
        // of all the numbers smaller than or equal to it is >= 0.5
        // AND for all the numbers greater than or equal to it.
        //
        double distanceFromMedian;
        for (int i = 0; i < entryList.size(); i++) {
            Probablity p = entryList.get(i).getValue();
            distanceFromMedian = 0;
            distanceFromMedian += p.probabilityOfNumbersGTE < 0.5 ?
                    (0.5 - p.probabilityOfNumbersGTE) : 0;
            distanceFromMedian += p.probabilityOfNumbersLTE < 0.5 ?
                    (0.5 - p.probabilityOfNumbersLTE) : 0;
            p.distanceFromMedian = distanceFromMedian;
        }
    }

    public void displayEntryCountMap() {
        for (Map.Entry<Range, Probablity> entry : CountMap.entrySet()) {
            System.out.println(entry.getKey()+" : "+entry.getValue());
        }

        System.out.println("Total Entries:" + totalValues);
    }

    //
    // Internal to getMedian
    //
    private Range getMedianInternal() {
        int positionOfMedian = (int)Math.ceil(this.totalValues / 2.0);

        //
        // Find the entry in the CountMap that represents
        // the position of this median
        //
        int index = 0;
        for (Map.Entry<Range, Probablity> entry : CountMap.entrySet()) {
            Probablity p = entry.getValue();
            index += p.occurrenceCount;

            if (positionOfMedian <= index) {
                //
                // This is our median
                //
                return entry.getKey();
            }
        }

        return null;
    }

    //
    // Computes the current running median of the series
    //
    public double getMedian() {
        double returnValue;
        Range r = getMedianInternal();
        if (r == null) {
            return Integer.MIN_VALUE;
        }

        if (r.low < r.high) {
            returnValue = (r.low + r.high)/2;

            //
            // Seems a range is coming out as a median
            // We need to start zooming in into this range
            // Lets start by splitting it into two.
            //
            int diff = r.high - r.low;
            diff = diff / 2;
            Range newRange1 = new Range(r.low, r.low + diff);
            Range newRange2 = new Range(r.low + diff + 1, r.high);

            //
            // Remove original range and insert the
            // split ranges.
            // We split the occurrence count since we have no way
            // of knowing who had how many occurrences
            //
            Probablity p = CountMap.get(r);
            CountMap.remove(r);
            CountMap.put(newRange1, new Probablity((int)Math.floor(p.occurrenceCount / 2.0)));
            CountMap.put(newRange2, new Probablity((int)Math.ceil(p.occurrenceCount / 2.0)));

            //
            // Check if we have exceeded the max entries
            //
            if (CountMap.size() > RunningMedian.maxEntries) {
                computeProbablityDistribution();
                 merge();
            }

        } else {
            returnValue = r.low;
        }

        return returnValue;
    }

    //
    // Insert value from the incoming series
    //
    public void insert(int value) {

        Range range = new Range(value, value);

        //
        // First see if there already an entry for this
        // value
        //
        if(CountMap.containsKey(range) == true) {
            Probablity p = CountMap.get(range);
            p.occurrenceCount += 1;
            this.totalValues += 1;
            return;
        }

        //
        // If not then we need to check if it belongs to
        // an existing range. Walk thru all the entries.
        //
        for (Map.Entry<Range, Probablity> entry : CountMap.entrySet()) {
            range = entry.getKey();
            if (value >= range.low && value <= range.high) {
                Probablity p = entry.getValue();
                p.occurrenceCount += 1;
                this.totalValues += 1;
                return;
            }
        }

        //
        // There is no entry representing this value
        // we have to insert a new entry presenting this value
        // If we have reached the max entry limit then we have
        // to club some values which are farthest from median
        // into ranges
        //
        range = new Range(value, value);
        CountMap.put(range, new Probablity(1));
        this.totalValues += 1;
        if (CountMap.size() > RunningMedian.maxEntries) {
            computeProbablityDistribution();
            merge();
            return;
        }
    }
}
