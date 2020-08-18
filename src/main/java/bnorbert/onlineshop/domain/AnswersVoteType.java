package bnorbert.onlineshop.domain;

public enum AnswersVoteType {
    UPVOTE(1),
    DOWNVOTE(-1),
    ;

    private int direction;

    AnswersVoteType(int direction) {
    }
}
