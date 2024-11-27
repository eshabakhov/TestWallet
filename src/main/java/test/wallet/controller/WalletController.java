package test.wallet.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import test.wallet.dto.ResponseDTO;
import test.wallet.dto.Wallet;
import test.wallet.dto.WalletDTO;
import test.wallet.exception.WalletNotFoundException;
import test.wallet.service.WalletService;

@RestController
@RequestMapping(value = "/v1")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping(value = "/wallets")
    public Wallet create(@RequestBody WalletDTO walletDTO) {
        return walletService.create(walletDTO);
    }

    @GetMapping(value = "/wallets/{id}")
    public Wallet get(@PathVariable Long id) {
        Wallet wallet = walletService.get(id);
        if (wallet.isExist()) {
            return walletService.get(id);
        } else {
            throw new WalletNotFoundException(id);
        }
    }

    @PostMapping(value = "/wallet")
    public ResponseDTO update(@RequestBody WalletDTO walletDTO) {
        walletService.update(walletDTO);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setHttpCode((short) HttpStatus.OK.value());
        responseDTO.setMessage(String.format("Wallet %d updated successfully", walletDTO.getWalletId()));
        return responseDTO;
    }

    @DeleteMapping(value = "/wallets/{id}")
    public ResponseDTO delete(@PathVariable Long id) {
        walletService.deleteWalletById(id);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setHttpCode((short) HttpStatus.OK.value());
        responseDTO.setMessage(String.format("Wallet %d deleted successfully", id));
        return responseDTO;
    }
}
