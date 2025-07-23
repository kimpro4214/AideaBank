package gift.exception.forbidden;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException() {
        super("이미 존재하는 이메일입니다.");
    }
}
