package com.github.Viduality.VSkyblock.Utilitys;

import com.github.Viduality.VSkyblock.VSkyblock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Locale;

public class ChallengesCache {


    private VSkyblock plugin = VSkyblock.getInstance();

    private int c1 = 0;
    private int c2 = 0;
    private int c3 = 0;
    private int c4 = 0;
    private int c5 = 0;
    private int c6 = 0;
    private int c7 = 0;
    private int c8 = 0;
    private int c9 = 0;
    private int c10 = 0;
    private int c11 = 0;
    private int c12 = 0;
    private int c13 = 0;
    private int c14 = 0;
    private int c15 = 0;
    private int c16 = 0;
    private int c17 = 0;
    private int c18 = 0;


    public int getc1() {
        return c1;
    }
    public void setc1(int c1) {
        this.c1 = c1;
    }

    public int getc2() {
        return c2;
    }
    public void setc2(int c2) {
        this.c2 = c2;
    }

    public int getc3() {
        return c3;
    }
    public void setc3(int c3) {
        this.c3 = c3;
    }

    public int getc4() {
        return c4;
    }
    public void setc4(int c4) {
        this.c4 = c4;
    }

    public int getc5() {
        return c5;
    }
    public void setc5(int c5) {
        this.c5 = c5;
    }

    public int getc6() {
        return c6;
    }
    public void setc6(int c6) {
        this.c6 = c6;
    }

    public int getc7() {
        return c7;
    }
    public void setc7(int c7) {
        this.c7 = c7;
    }

    public int getc8() {
        return c8;
    }
    public void setc8(int c8) {
        this.c8 = c8;
    }

    public int getc9() {
        return c9;
    }
    public void setc9(int c9) {
        this.c9 = c9;
    }

    public int getc10() {
        return c10;
    }
    public void setc10(int c10) {
        this.c10 = c10;
    }

    public int getc11() {
        return c11;
    }
    public void setc11(int c11) {
        this.c11 = c11;
    }

    public int getc12() {
        return c12;
    }
    public void setc12(int c12) {
        this.c12 = c12;
    }

    public int getc13() {
        return c13;
    }
    public void setc13(int c13) {
        this.c13 = c13;
    }

    public int getc14() {
        return c14;
    }
    public void setc14(int c14) {
        this.c14 = c14;
    }

    public int getc15() {
        return c15;
    }
    public void setc15(int c15) {
        this.c15 = c15;
    }

    public int getc16() {
        return c16;
    }
    public void setc16(int c16) {
        this.c16 = c16;
    }

    public int getc17() {
        return c17;
    }
    public void setc17(int c17) {
        this.c17 = c17;
    }

    public int getc18() {
        return c18;
    }
    public void setc18(int c18) {
        this.c18 = c18;
    }

    /**
     * Gets the challenge count for the given challenge.
     *
     * @param challenge
     * @return Integer challengecount
     */
    public int getCurrentChallengeCount(int challenge) {
        int challengeCount = 0;
        switch (challenge) {
            case 1: challengeCount = getc1();
                    break;
            case 2: challengeCount = getc2();
                    break;
            case 3: challengeCount = getc3();
                    break;
            case 4: challengeCount = getc4();
                    break;
            case 5: challengeCount = getc5();
                    break;
            case 6: challengeCount = getc6();
                    break;
            case 7: challengeCount = getc7();
                    break;
            case 8: challengeCount = getc8();
                    break;
            case 9: challengeCount = getc9();
                    break;
            case 10: challengeCount = getc10();
                    break;
            case 11: challengeCount = getc11();
                break;
            case 12: challengeCount = getc12();
                break;
            case 13: challengeCount = getc13();
                break;
            case 14: challengeCount = getc14();
                break;
            case 15: challengeCount = getc15();
                break;
            case 16: challengeCount = getc16();
                break;
            case 17: challengeCount = getc17();
                break;
            case 18: challengeCount = getc18();
                break;
        }
        return challengeCount;
    }
}
