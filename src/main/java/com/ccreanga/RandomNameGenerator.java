package com.ccreanga;

import java.io.*;
import java.util.ArrayList;

public class RandomNameGenerator {

    Sylabs pre, mid, sur;

    final private static char[] VOWELS = {'a', 'e', 'i', 'o', 'u', 'ä', 'ö', 'õ', 'ü', 'y'};
    final private static char[] CONSONANTS = {'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y'};

    private InputStream in;

    private class Sylabs {
        private String[] syl = null;
        private String[] pureSyl = null;
        private boolean containsConsFirst;
        private boolean containsVowelFirst;
        private boolean allowCons;
        private boolean allowVowel;

        private boolean[] expectsVowel;
        private boolean[] expectsConsonant;
        private boolean[] hatesPreviousVowels;
        private boolean[] hatesPreviousConsonants;
        private boolean[] vowelFirst;
        private boolean[] consonantFirst;
        private boolean[] vowelLast;
        private boolean[] consonantLast;


        public Sylabs(ArrayList<String> arrayList) {
            syl = new String[arrayList.size()];
            syl = arrayList.toArray(syl);

            pureSyl = new String[arrayList.size()];
            pureSyl = arrayList.toArray(pureSyl);

            expectsVowel = new boolean[arrayList.size()];
            expectsConsonant = new boolean[arrayList.size()];
            hatesPreviousVowels = new boolean[arrayList.size()];
            hatesPreviousConsonants = new boolean[arrayList.size()];

            vowelFirst = new boolean[arrayList.size()];
            consonantFirst = new boolean[arrayList.size()];
            vowelLast = new boolean[arrayList.size()];
            consonantLast = new boolean[arrayList.size()];

            for (int i = 0; i < syl.length; i++) {
                pureSyl[i] = pureSyl(syl[i]);
                expectsVowel[i] = expectsVowel(syl[i]);
                expectsConsonant[i] = expectsConsonant(syl[i]);
                hatesPreviousVowels[i] = hatesPreviousVowels(syl[i]);
                hatesPreviousConsonants[i] = hatesPreviousConsonants(syl[i]);

                vowelFirst[i] = vowelFirst(pureSyl[i]);
                consonantFirst[i] = consonantFirst(pureSyl[i]);
                vowelLast[i] = vowelLast(pureSyl[i]);
                consonantLast[i] = consonantLast(pureSyl[i]);
            }

            containsConsFirst = containsConsFirst(syl);
            containsVowelFirst = containsVowelFirst(syl);
            allowCons = allowCons(syl);
            allowVowel = allowVowels(syl);
        }

        private String pureSyl(String s) {
            s = s.trim();
            if (s.charAt(0) == '+' || s.charAt(0) == '-') s = s.substring(1);
            return s.split(" ")[0];
        }

        public int length() {
            return syl.length;
        }

        private boolean expectsVowel(String s) {
            return s.indexOf("+v", 1) > -1;
        }

        private boolean expectsConsonant(String s) {
            return s.indexOf("+c", 1) > -1;
        }

        private boolean hatesPreviousVowels(String s) {
            return s.indexOf("-c", 1) > -1;
        }

        private boolean hatesPreviousConsonants(String s) {
            return s.indexOf("-v", 1) > -1;
        }

        private boolean vowelFirst(String s) {
            return (String.copyValueOf(VOWELS).contains(String.valueOf(s.charAt(0))));
        }

        private boolean consonantFirst(String s) {
            return (String.copyValueOf(CONSONANTS).contains(String.valueOf(s.charAt(0))));
        }

        private boolean vowelLast(String s) {
            return (String.copyValueOf(VOWELS).contains(String.valueOf(s.charAt(s.length() - 1))));
        }

        private boolean consonantLast(String s) {
            return (String.copyValueOf(CONSONANTS).contains(String.valueOf(s.charAt(s.length() - 1))));
        }

        private boolean containsConsFirst(String[] array) {
            for (String s : array) {
                if (consonantFirst(s)) return true;
            }
            return false;
        }

        private boolean containsVowelFirst(String[] array) {
            for (String s : array) {
                if (vowelFirst(s)) return true;
            }
            return false;
        }

        private boolean allowCons(String[] array) {
            for (String s : array) {
                if (hatesPreviousVowels(s) || !hatesPreviousConsonants(s)) return true;
            }
            return false;
        }

        private boolean allowVowels(String[] array) {
            for (String s : array) {
                if (hatesPreviousConsonants(s) || !hatesPreviousVowels(s)) return true;
            }
            return false;
        }
    }


    public RandomNameGenerator(InputStream in) throws IOException {
        this.in = in;
        refresh();
    }

    public void refresh() throws IOException {
        try {
            BufferedReader bufRead = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = "";
            ArrayList<String> preList = new ArrayList<>();
            ArrayList<String> surList = new ArrayList<>();
            ArrayList<String> midList = new ArrayList<>();
            while (line != null) {
                line = bufRead.readLine();
                if (line != null && !line.equals("")) {
                    if (line.charAt(0) == '-') {
                        preList.add(line.substring(1).toLowerCase());
                    } else if (line.charAt(0) == '+') {
                        surList.add(line.substring(1).toLowerCase());
                    } else {
                        midList.add(line.toLowerCase());
                    }
                }
            }
            pre = new Sylabs(preList);
            mid = new Sylabs(midList);
            sur = new Sylabs(surList);
        }finally{
            in.close();
        }
    }

    public String compose(int syls) {
        if (syls > 2 && mid.length() == 0)
            throw new RuntimeException("invalid sylable files");
        if (pre.length() == 0)
            throw new RuntimeException("invalid sylable files");
        if (sur.length() == 0)
            throw new RuntimeException("invalid sylable files");
        if (syls < 1)
            throw new RuntimeException("compose(int syls) can't have less than 1 syllable");
        int expecting = 0; // 1 for vocal, 2 for consonant
        int last = 0; // 1 for vocal, 2 for consonant
        int a = (int) (Math.random() * pre.length());

        if (pre.vowelLast[a]) last = 1;
        else last = 2;

        if (syls > 2) {
            if (pre.expectsVowel[a]) {
                expecting = 1;
                if (!(mid.containsVowelFirst))
                    throw new RuntimeException("invalid sylable files");
            }
            if (pre.expectsConsonant[a]) {
                expecting = 2;
                if (!(mid.containsConsFirst))
                    throw new RuntimeException("invalid sylable files");
            }
        } else {
            if (pre.expectsVowel[a]) {
                expecting = 1;
                if (!(sur.containsVowelFirst))
                    throw new RuntimeException("invalid sylable files");
            }
            if (pre.expectsConsonant[a]) {
                expecting = 2;
                if (!(sur.containsConsFirst))
                    throw new RuntimeException("invalid sylable files");
            }
        }
        if (((pre.vowelLast[a])) && !(mid.allowVowel))
            throw new RuntimeException("invalid sylable files");
        if (((pre.consonantLast[a])) && !(mid.allowCons))
            throw new RuntimeException("invalid sylable files");
        int b[] = new int[syls];
        for (int i = 0; i < b.length - 2; i++) {
            do {
                b[i] = (int) (Math.random() * mid.length());
            }
            while (expecting == 1 && !mid.vowelFirst[b[i]] ||
                    expecting == 2 && !mid.consonantFirst[b[i]] ||
                    last == 1 && mid.hatesPreviousVowels[b[i]] ||
                    last == 2 && mid.hatesPreviousConsonants[b[i]]);

            expecting = 0;
            if (mid.expectsVowel[b[i]]) {
                expecting = 1;
                if (i < b.length - 3 && !(mid.containsVowelFirst))
                    throw new RuntimeException("invalid sylable files");
                if (i == b.length - 3 && !(sur.containsVowelFirst))
                    throw new RuntimeException("invalid sylable files");
            }
            if (mid.expectsConsonant[b[i]]) {
                expecting = 2;
                if (i < b.length - 3 && !(mid.containsConsFirst))
                    throw new RuntimeException("invalid sylable files");
                if (i == b.length - 3 && !(sur.containsConsFirst))
                    throw new RuntimeException("invalid sylable files");
            }
            if ((mid.vowelLast[b[i]]) && !(mid.allowVowel) && syls > 3)
                throw new RuntimeException("invalid sylable files");
            if ((mid.consonantLast[b[i]]) && !(mid.allowCons) && syls > 3)
                throw new RuntimeException("invalid sylable files");
            if (i == b.length - 3) {
                if ((mid.vowelLast[b[i]]) && !(sur.allowVowel))
                    throw new RuntimeException("invalid sylable files");
                if ((mid.consonantLast[b[i]]) && !(sur.allowCons))
                    throw new RuntimeException("invalid sylable files");
            }
            if (mid.vowelLast[b[i]]) last = 1;
            else last = 2;
        }

        int c;
        do {
            c = (int) (Math.random() * sur.length());
        }
        while (expecting == 1 && !((sur.vowelFirst[c])) ||
                expecting == 2 && !((sur.consonantFirst[c])) ||
                last == 1 && (sur.hatesPreviousVowels[c]) ||
                last == 2 && (sur.hatesPreviousConsonants[c]));

        StringBuilder sb = new StringBuilder();
        String first = (pre.pureSyl[a]);
        sb.append(Character.toUpperCase(first.charAt(0)));
        for (int i = 1; i < first.length(); i++) {
            sb.append(first.charAt(i));
        }

        for (int i = 0; i < b.length - 2; i++) {
            sb.append((mid.pureSyl[b[i]]));
        }
        if (syls > 1)
            sb.append((sur.pureSyl[c]));
        return sb.toString();
    }


}

