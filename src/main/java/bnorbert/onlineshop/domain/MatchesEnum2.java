package bnorbert.onlineshop.domain;

public enum MatchesEnum2 {
    _1080P(1),
    _4K(2),
    _8K(3),
    TEST(4),
    _TEST(5);

    int number;

    MatchesEnum2(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
