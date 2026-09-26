package io.github.trip.shiv.vcledger.core.utilities;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.codec.language.Soundex;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Generic phonetic + structural name matcher, tuned for Indian names.
 * <p>
 * Usage:
 * <pre>
 *   PhoneticNameMatcher<Customer> matcher =
 *           new PhoneticNameMatcher<>(Customer::getName);
 *   List&lt;Customer&gt; matches =
 *           matcher.findSimilarMatches("Amit", customers, 0.85);
 * </pre>
 * <p>
 * Architecture:
 * <ul>
 *   <li><b>Layer 1 (Phonetic)</b> - Apache Commons Codec (DoubleMetaphone + Soundex).
 *       Produces Set 1 (very strong phonetic matches, auto-included) and
 *       Set 2 (weaker phonetic matches, passed to Layer 2). Anything below the
 *       weak phonetic threshold is discarded before Layer 2 to keep the
 *       structural comparison cheap.</li>
 *   <li><b>Layer 2 (Structural)</b> - weighted blend of Levenshtein similarity,
 *       Jaro-Winkler similarity, bigram Dice similarity and a consonant-skeleton
 *       similarity (vowels stripped, since Indian name transliterations vary a lot
 *       in vowels - e.g. "Amit"/"Amith", "Sumit"/"Sumeet"). Only candidates whose
 *       combined score clears {@code minConfidenceScore} survive.</li>
 * </ul>
 * <p>
 * <b>Multi-token names</b> - a spoken single name (e.g. "Amit") is commonly matched
 * against a stored full name ("Amit Kumar Singh"). Whole-string comparison alone
 * degrades badly here (length mismatch hurts Levenshtein/Dice, and Metaphone/Soundex
 * encode the joined string as one long "word"). Both layers therefore also tokenize
 * both sides on whitespace/punctuation and take the best per-token alignment score
 * (each spoken token matched against its single best-scoring candidate token, then
 * averaged), and the final pair score is the max of the whole-string score and the
 * token-alignment score. This is a greedy, not globally-optimal, assignment - fine
 * for the 1-4 token names this is designed for, but not a strict bipartite matching.
 * Final result = Set 1 (sorted desc) followed by surviving Set 2 (sorted desc).
 *
 * @param <T> the type of object being matched (e.g. a Customer entity)
 */
public class PhoneticNameMatcher<T> {

    // ---- Layer 1 thresholds ----
    /** Phonetic score at/above this => auto-included as a "very strong" match. */
    private static final double STRONG_PHONETIC_THRESHOLD = 0.90;
    /** Phonetic score below this is discarded entirely (not even sent to Layer 2). */
    private static final double WEAK_PHONETIC_THRESHOLD = 0.45;

    // ---- Layer 2 weights (must sum to 1.0) ----
    private static final double W_LEVENSHTEIN = 0.30;
    private static final double W_JARO_WINKLER = 0.35;
    private static final double W_NGRAM_DICE = 0.20;
    private static final double W_CONSONANT = 0.15;

    private final Function<T, String> nameExtractor;

    private final DoubleMetaphone doubleMetaphone;
    private final Soundex soundex;
    private final JaroWinklerSimilarity jaroWinklerSimilarity;
    private final LevenshteinDistance levenshteinDistance;

    public PhoneticNameMatcher(Function<T, String> nameExtractor) {
        if (nameExtractor == null) {
            throw new IllegalArgumentException("nameExtractor must not be null");
        }
        this.nameExtractor = nameExtractor;
        this.doubleMetaphone = new DoubleMetaphone();
        this.doubleMetaphone.setMaxCodeLen(6);
        this.soundex = new Soundex();
        this.jaroWinklerSimilarity = new JaroWinklerSimilarity();
        this.levenshteinDistance = LevenshteinDistance.getDefaultInstance();
    }

    /**
     * Finds candidates whose extracted name phonetically/structurally matches
     * {@code spokenName}, sorted by descending confidence, with Layer-1 "very
     * strong" matches always ranked above Layer-2 matches.
     *
     * @param spokenName        the word as transcribed from speech (e.g. "Amit")
     * @param candidates        the pool of objects to match against
     * @param minConfidenceScore threshold in [0,1] applied only to Layer-2 candidates
     * @return matched candidates, Set-1 (desc) followed by Set-2 (desc)
     */
    public List<T> findSimilarMatches(String spokenName, List<T> candidates, double minConfidenceScore) {
        List<T> result = new ArrayList<>();
        if (spokenName == null || spokenName.isBlank() || candidates == null || candidates.isEmpty()) {
            return result;
        }
        if (minConfidenceScore < 0.0 || minConfidenceScore > 1.0) {
            throw new IllegalArgumentException("minConfidenceScore must be in [0,1]");
        }

        String normalizedSpoken = normalize(spokenName);

        List<ScoredCandidate<T>> strongMatches = new ArrayList<>();
        List<ScoredCandidate<T>> layer2Input = new ArrayList<>();

        for (T candidate : candidates) {
            String candidateName = nameExtractor.apply(candidate);
            if (candidateName == null || candidateName.isBlank()) {
                continue;
            }
            String normalizedCandidate = normalize(candidateName);

            double phoneticScore = computePhoneticScoreCombined(normalizedSpoken, normalizedCandidate);

            if (phoneticScore >= STRONG_PHONETIC_THRESHOLD) {
                strongMatches.add(new ScoredCandidate<>(candidate, phoneticScore));
            } else if (phoneticScore >= WEAK_PHONETIC_THRESHOLD) {
                layer2Input.add(new ScoredCandidate<>(candidate, phoneticScore));
            }
            // else: phonetically unrelated -> discarded, never reaches Layer 2
        }

        List<ScoredCandidate<T>> layer2Matches = new ArrayList<>();
        for (ScoredCandidate<T> sc : layer2Input) {
            String candidateName = nameExtractor.apply(sc.value);
            String normalizedCandidate = normalize(candidateName);
            double structuralScore = computeStructuralScoreCombined(normalizedSpoken, normalizedCandidate);
            if (structuralScore >= minConfidenceScore) {
                layer2Matches.add(new ScoredCandidate<>(sc.value, structuralScore));
            }
        }

        strongMatches.sort((a, b) -> Double.compare(b.score, a.score));
        layer2Matches.sort((a, b) -> Double.compare(b.score, a.score));

        strongMatches.forEach(sc -> result.add(sc.value));
        layer2Matches.forEach(sc -> result.add(sc.value));

        return result;
    }

    // ==================== Layer 1: Phonetic ====================

    /**
     * Whole-string phonetic score, maxed with the best token-level alignment score.
     * Handles "Amit" (spoken) vs "Amit Kumar Singh" (stored) style mismatches.
     */
    private double computePhoneticScoreCombined(String a, String b) {
        double wholeStringScore = phoneticPairScore(a, b);
        double tokenScore = tokenAlignmentScore(a, b, this::phoneticPairScore);
        return Math.max(wholeStringScore, tokenScore);
    }

    private double phoneticPairScore(String a, String b) {
        if (a.equals(b)) {
            return 1.0;
        }

        String dmA = safeEncode(() -> doubleMetaphone.doubleMetaphone(a, false));
        String dmB = safeEncode(() -> doubleMetaphone.doubleMetaphone(b, false));
        String dmAltA = safeEncode(() -> doubleMetaphone.doubleMetaphone(a, true));
        String dmAltB = safeEncode(() -> doubleMetaphone.doubleMetaphone(b, true));

        boolean primaryMatch = !dmA.isEmpty() && dmA.equals(dmB);
        boolean crossMatch = (!dmAltA.isEmpty() && (dmAltA.equals(dmB) || dmAltA.equals(dmAltB)))
                || (!dmAltB.isEmpty() && dmAltB.equals(dmA));

        String sxA = safeEncode(() -> soundex.encode(a));
        String sxB = safeEncode(() -> soundex.encode(b));
        boolean soundexMatch = !sxA.isEmpty() && sxA.equals(sxB);

        double score = 0.0;
        if (primaryMatch) {
            score += 0.75;
        }
        if (crossMatch) {
            score += 0.15;
        }
        if (soundexMatch) {
            score += 0.10;
        }

        // Indian-name calibration: names sharing a phonetic code but differing in
        // their opening sound are common false positives (e.g. "Amit" vs "Sumit").
        // Reward same opening sound, penalize a primary match that differs there.
        if (!a.isEmpty() && !b.isEmpty() && a.charAt(0) == b.charAt(0)) {
            score += 0.05;
        } else if (primaryMatch) {
            score -= 0.20;
        }

        return clamp(score);
    }

    @FunctionalInterface
    private interface EncoderFn {
        String encode();
    }

    private String safeEncode(EncoderFn fn) {
        try {
            String r = fn.encode();
            return r == null ? "" : r;
        } catch (Exception e) {
            return "";
        }
    }

    // ==================== Layer 2: Structural ====================

    /**
     * Whole-string structural score, maxed with the best token-level alignment score.
     */
    private double computeStructuralScoreCombined(String a, String b) {
        double wholeStringScore = structuralPairScore(a, b);
        double tokenScore = tokenAlignmentScore(a, b, this::structuralPairScore);
        return Math.max(wholeStringScore, tokenScore);
    }

    private double structuralPairScore(String a, String b) {
        double levenSim = levenshteinSimilarity(a, b);
        double jaroWinklerSim = jaroWinklerSimilarity.apply(a, b);
        double ngramSim = bigramDiceSimilarity(a, b);
        double consonantSim = consonantSkeletonSimilarity(a, b);

        double combined = (W_LEVENSHTEIN * levenSim)
                + (W_JARO_WINKLER * jaroWinklerSim)
                + (W_NGRAM_DICE * ngramSim)
                + (W_CONSONANT * consonantSim);

        return clamp(combined);
    }

    private double levenshteinSimilarity(String a, String b) {
        int distance = levenshteinDistance.apply(a, b);
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) {
            return 1.0;
        }
        return 1.0 - ((double) distance / maxLen);
    }

    private double bigramDiceSimilarity(String a, String b) {
        Set<String> bigramsA = getBigrams(a);
        Set<String> bigramsB = getBigrams(b);
        if (bigramsA.isEmpty() && bigramsB.isEmpty()) {
            return 1.0;
        }
        if (bigramsA.isEmpty() || bigramsB.isEmpty()) {
            return 0.0;
        }
        Set<String> intersection = new HashSet<>(bigramsA);
        intersection.retainAll(bigramsB);
        return (2.0 * intersection.size()) / (bigramsA.size() + bigramsB.size());
    }

    private Set<String> getBigrams(String s) {
        Set<String> bigrams = new HashSet<>();
        if (s.length() < 2) {
            bigrams.add(s);
            return bigrams;
        }
        for (int i = 0; i < s.length() - 1; i++) {
            bigrams.add(s.substring(i, i + 2));
        }
        return bigrams;
    }

    private String consonantSkeleton(String s) {
        return s.replaceAll("[aeiou]", "");
    }

    private double consonantSkeletonSimilarity(String a, String b) {
        String consA = consonantSkeleton(a);
        String consB = consonantSkeleton(b);
        if (consA.isEmpty() && consB.isEmpty()) {
            return 1.0;
        }
        if (consA.isEmpty() || consB.isEmpty()) {
            return 0.0;
        }
        return jaroWinklerSimilarity.apply(consA, consB);
    }

    // ==================== Token Alignment ====================

    /**
     * Splits both strings into tokens and, for each token on the shorter/spoken
     * side, takes its best score against any token on the other side, then
     * averages those best-scores. This lets a single spoken first name match a
     * multi-word stored name (e.g. "Amit" vs "Amit Kumar Singh") without the
     * length mismatch dragging the whole-string score down.
     * <p>
     * The side with fewer tokens drives the averaging (usually the spoken name),
     * so a short spoken name isn't penalized just because the stored name has
     * extra tokens (middle name, surname, etc.). If either side is a single
     * token, this degenerates to a single pair score - same as whole-string
     * comparison - so it never hurts the common one-word-vs-one-word case.
     */
    private double tokenAlignmentScore(String a, String b, BiFunction<String, String, Double> pairScorer) {
        List<String> tokensA = tokenize(a);
        List<String> tokensB = tokenize(b);
        if (tokensA.isEmpty() || tokensB.isEmpty()) {
            return 0.0;
        }

        List<String> driverTokens = tokensA.size() <= tokensB.size() ? tokensA : tokensB;
        List<String> otherTokens = tokensA.size() <= tokensB.size() ? tokensB : tokensA;

        double sum = 0.0;
        for (String driverToken : driverTokens) {
            double best = 0.0;
            for (String otherToken : otherTokens) {
                best = Math.max(best, pairScorer.apply(driverToken, otherToken));
            }
            sum += best;
        }
        return sum / driverTokens.size();
    }

    private List<String> tokenize(String s) {
        return Arrays.stream(s.split("[^a-z]+"))
                .filter(t -> !t.isBlank())
                .collect(Collectors.toList());
    }

    // ==================== Helpers ====================

    private String normalize(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }

    private double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    private static final class ScoredCandidate<T> {
        final T value;
        final double score;

        ScoredCandidate(T value, double score) {
            this.value = value;
            this.score = score;
        }
    }
}