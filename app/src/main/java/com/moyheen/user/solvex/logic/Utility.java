package com.moyheen.user.solvex.logic;

import java.util.Random;

/**
 * Created by moyheen on 23-Dec-14.
 */
public class Utility {
    private int first_num;
    private int second_num;
    private char sign;
    private int score;
    private int randomNumber;

    public Utility() {
    }

    public void setFirstNum(int num) {
        this.first_num = num+1;
    }

    public int getFirstNum() {
        return first_num;
    }

    public void setSecondNum(int num) {
        this.second_num = num+1;
    }

    public int getSecondNum() {
        return second_num;
    }

    public void setSign(int num) {
        switch (num) {
            case 10:
                sign = '-';
                break;
            case 11:
                sign = '+';
                break;
            case 12:
                sign = '*';
                break;
            case 13:
                sign = '/';
                break;
        }
    }

    public char getSign() {
        return sign;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void calculateScore() {
        int result;

        switch (getSign()) {

            case '-':
                result = first_num - second_num;
                if (result == randomNumber) {
                    score += 5;
                }
                setScore(score);
                break;

            case '+':
                result = first_num + second_num;
                if (result == randomNumber) {
                    score += 4;
                }
                setScore(score);
                break;

            case '*':
                result = first_num * second_num;
                if (result == randomNumber) {
                    score += 3;
                }
                setScore(score);
                break;

            case '/':
                result = first_num / second_num;
                if (result == randomNumber) {
                    score += 2;
                }
                setScore(score);
                break;
        }
    }

    public int generateRandom(int min, int max) {

        Random random = new Random();

        randomNumber = random.nextInt((max - min) + 1) + min;

        return getRandomNumber();
    }

}
