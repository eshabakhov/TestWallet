package test.wallet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class WalletConflictException extends IllegalArgumentException {
    public WalletConflictException(String message) {
        super(message);
    }
}
