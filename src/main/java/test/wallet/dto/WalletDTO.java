package test.wallet.dto;

import lombok.Data;
import test.wallet.enums.OperationTypeEnum;

@Data
public class WalletDTO {
    private Long walletId;
    private OperationTypeEnum operationType;
    private Long amount;
}
