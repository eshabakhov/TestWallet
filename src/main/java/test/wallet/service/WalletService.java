package test.wallet.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import test.wallet.enums.OperationTypeEnum;
import test.wallet.dto.Wallet;
import test.wallet.dto.WalletDTO;
import test.wallet.exception.WalletConflictException;
import test.wallet.exception.WalletNotFoundException;
import test.wallet.repository.WalletRepository;

import java.util.Objects;

@Service
@AllArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public Wallet create(WalletDTO walletDTO) {
        if (walletDTO.getAmount() < 0) {
            throw new WalletConflictException("Can't create a wallet with a negative balance");
        } else {
            Wallet wallet = new Wallet();
            wallet.setAmount(walletDTO.getAmount());
            wallet.setExist(true);
            return walletRepository.save(wallet);
        }

    }

    public Integer update(WalletDTO walletDTO) {
        if (Objects.isNull(walletDTO.getWalletId())) {
            throw new WalletConflictException("The given id must not be null");
        }
        if (Objects.isNull(walletDTO.getOperationType())) {
            throw new WalletConflictException("The given operation type must not be null");
        }
        if (Objects.isNull(walletDTO.getAmount())) {
            throw new WalletConflictException("The given amount must not be null");
        }

        Wallet currentWallet = get(walletDTO.getWalletId());

        if (currentWallet.isExist()) {
            long sum = 0;
            if (walletDTO.getOperationType() == OperationTypeEnum.DEPOSIT) {
                sum = currentWallet.getAmount() + walletDTO.getAmount();
            }
            if (walletDTO.getOperationType() == OperationTypeEnum.WITHDRAW) {
                sum = currentWallet.getAmount() - walletDTO.getAmount();
            }

            if (sum >= 0) {
                if (walletDTO.getOperationType() == OperationTypeEnum.DEPOSIT) {
                    return walletRepository.depositWalletById(currentWallet.getId(), walletDTO.getAmount());
                }
                return walletRepository.withdrawWalletById(currentWallet.getId(), walletDTO.getAmount());
            } else {
                if (walletDTO.getOperationType() == OperationTypeEnum.DEPOSIT) {
                    throw new WalletConflictException("The wallet balance has been exceeded.");
                }
                throw new WalletConflictException("There are not enough funds on the wallet balance");
            }
        } else {
            throw new WalletNotFoundException(currentWallet.getId());
        }
    }

    public Wallet get(Long id) {
        return walletRepository.findById(id).orElseThrow(() -> new WalletNotFoundException(id));
    }

    public Integer deleteWalletById(Long id) {
        Integer updatedWallet = walletRepository.deleteWalletById(id);
        if (updatedWallet == 1) {
            return updatedWallet;
        } else {
            throw new WalletNotFoundException(id);
        }
    }
}
