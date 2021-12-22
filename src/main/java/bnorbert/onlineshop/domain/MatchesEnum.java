package bnorbert.onlineshop.domain;

public enum MatchesEnum {
    _49INCHES(1),
    _59INCHES(2),
    _70INCHES(3),
    _70INCHES_UP(4),
    TEST(5);

    int number;

    MatchesEnum(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


}
