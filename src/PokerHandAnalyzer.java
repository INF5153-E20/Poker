import java.util.Arrays;
import java.util.HashSet;

public class PokerHandAnalyzer {

    final static String faces = "AKQJT98765432";
    final static String suits = "HDSC";
    final static String[] deck = buildDeck();

    public static Hand analyzeHand(final String[] hand) {
        if (hand.length != 5)
            throw new IllegalArgumentException("Wrong number of cards in hand.");
        if (new HashSet<>(Arrays.asList(hand)).size() != hand.length)
            throw new IllegalArgumentException("Duplicate cards in hand.");

        int[] faceCount = new int[faces.length()];
        long straight = 0, flush = 0;
        for (String card : hand) {

            int face = faces.indexOf(card.charAt(0));
            if (face == -1)
                throw new IllegalArgumentException("Non-existing face.");
            straight |= (1 << face);

            faceCount[face]++;

            if (suits.indexOf(card.charAt(1)) == -1)
                throw new IllegalArgumentException("Non-existing suit.");
            flush |= (1 << card.charAt(1));
        }

        // shift the bit pattern to the right as far as possible
        while (straight % 2 == 0)
            straight >>= 1;

        // straight is 00011111; A-2-3-4-5 is 1111000000001
        boolean hasStraight = straight == 0b11111 || straight == 0b1111000000001;

        // unsets right-most 1-bit, which may be the only one set
        boolean hasFlush = (flush & (flush - 1)) == 0;

        if (hasStraight && hasFlush)
            return Hand.StraightFlush;

        int total = 0;
        for (int count : faceCount) {
            if (count == 4)
                return Hand.FourOfAKind;
            if (count == 3)
                total += 3;
            else if (count == 2)
                total += 2;
        }

        if (total == 5)
            return Hand.FullHouse;

        if (hasFlush)
            return Hand.Flush;

        if (hasStraight)
            return Hand.Straight;

        if (total == 3)
            return Hand.ThreeOfAKind;

        if (total == 4)
            return Hand.TwoPair;

        if (total == 2)
            return Hand.Pair;

        return Hand.HighCard;
    }

    private static String[] buildDeck() {
        String[] dck = new String[suits.length() * faces.length()];
        int i = 0;
        for (char s : suits.toCharArray()) {
            for (char f : faces.toCharArray()) {
                dck[i] = "" + f + s;
                i++;
            }
        }
        return dck;
    }
}