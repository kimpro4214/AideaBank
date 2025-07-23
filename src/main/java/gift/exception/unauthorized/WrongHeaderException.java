package gift.exception.unauthorized;

public class WrongHeaderException extends RuntimeException {
    public WrongHeaderException() {
        super("잘못된 인증 헤더입니다.");
    }
}
