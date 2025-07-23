package gift.exception.forbidden;

public class WrongPasswordException extends RuntimeException {
    public WrongPasswordException() {
        super("비밀번호가 일치하지 않습니다.");
    }
}
