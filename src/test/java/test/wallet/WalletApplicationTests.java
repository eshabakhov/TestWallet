package test.wallet;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import test.wallet.controller.WalletController;
import test.wallet.dto.Wallet;
import test.wallet.dto.WalletDTO;
import test.wallet.enums.OperationTypeEnum;
import test.wallet.exception.WalletConflictException;
import test.wallet.exception.WalletNotFoundException;
import test.wallet.service.WalletService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WalletApplicationTests extends DatabaseIntegrationTest {

	@Autowired
	private WalletController walletController;

	@Autowired
	private WalletService walletService;

	@Test
	@Order(1)
	public void testCreateWallet() {
		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setAmount(100000L);
		Wallet wallet = walletController.create(walletDTO);
		Assertions.assertEquals(100000L, wallet.getAmount());
	}

	@Test
	@Order(2)
	public void testGetWallet() {
		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setAmount(100000L);
		Wallet walletCreated = walletController.create(walletDTO);
		Wallet walletGot = walletController.get(walletCreated.getId());
		Assertions.assertEquals(walletGot.getId(), walletCreated.getId());
	}

	@Test
	@Order(3)
	public void testDepositWallet() {
		WalletDTO walletDTOCreate = new WalletDTO();
		walletDTOCreate.setAmount(100000L);

		Wallet prevWallet = walletController.create(walletDTOCreate);
		long prevSum = prevWallet.getAmount();

		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setWalletId(prevWallet.getId());
		walletDTO.setAmount(10000L);
		walletDTO.setOperationType(OperationTypeEnum.DEPOSIT);

		walletController.update(walletDTO);
		Wallet nextWallet = walletController.get(prevWallet.getId());
		long nextSum = nextWallet.getAmount();

		Assertions.assertEquals(prevSum + 10000L, nextSum);
	}

	@Test
	@Order(4)
	public void testWithdrawWallet() {
		WalletDTO walletDTOCreate = new WalletDTO();
		walletDTOCreate.setAmount(100000L);

		Wallet prevWallet = walletController.create(walletDTOCreate);
		long prevSum = prevWallet.getAmount();

		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setWalletId(prevWallet.getId());
		walletDTO.setAmount(10000L);
		walletDTO.setOperationType(OperationTypeEnum.WITHDRAW);

		walletController.update(walletDTO);
		Wallet nextWallet = walletController.get(prevWallet.getId());
		long nextSum = nextWallet.getAmount();

		Assertions.assertEquals(prevSum - 10000L, nextSum);
	}

	@Test
	@Order(5)
	public void testDeleteWallet() {
		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setAmount(100000L);
		Wallet wallet = walletController.create(walletDTO);

		String responseMessage = walletController.delete(wallet.getId()).getMessage();

		Assertions.assertEquals(String.format("Wallet %d deleted successfully", wallet.getId()), responseMessage);
	}

	@Test
	@Order(6)
	public void testNotEnoughFundsWallet() {
		WalletDTO walletDTOCreate = new WalletDTO();
		walletDTOCreate.setAmount(100000L);

		Wallet prevWallet = walletController.create(walletDTOCreate);

		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setWalletId(prevWallet.getId());
		walletDTO.setAmount(1000000L);
		walletDTO.setOperationType(OperationTypeEnum.WITHDRAW);

		WalletConflictException thrown = Assertions
				.assertThrows(WalletConflictException.class, () -> {
					walletController.update(walletDTO);
				}, "WalletConflictException error was expected");

		Assertions.assertEquals("There are not enough funds on the wallet balance", thrown.getMessage());
	}

	@Test
	@Order(7)
	public void testWalletBalanceExceeded() {
		WalletDTO walletDTOCreate = new WalletDTO();
		walletDTOCreate.setAmount(100000L);

		Wallet prevWallet = walletController.create(walletDTOCreate);

		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setWalletId(prevWallet.getId());
		walletDTO.setAmount(9223372036854775807L);
		walletDTO.setOperationType(OperationTypeEnum.DEPOSIT);

		WalletConflictException thrown = Assertions
				.assertThrows(WalletConflictException.class, () -> {
					walletController.update(walletDTO);
				}, "WalletConflictException error was expected");

		Assertions.assertEquals("The wallet balance has been exceeded.", thrown.getMessage());
	}

	@Test
	@Order(8)
	public void testNonExistID() {
		long id = -1L;
		WalletNotFoundException thrown = Assertions
				.assertThrows(WalletNotFoundException.class, () -> {
					walletController.get(id);
				}, "WalletNotFoundException error was expected");

		Assertions.assertEquals(String.format("Wallet %d not found", id), thrown.getMessage());
	}

	@Test
	@Order(9)
	public void testCreateWalletWithNegativeAmount() {
		WalletDTO walletDTOCreate = new WalletDTO();
		walletDTOCreate.setAmount(-100000L);
		WalletConflictException thrown = Assertions
				.assertThrows(WalletConflictException.class, () -> {
					walletController.create(walletDTOCreate);
				}, "WalletConflictException error was expected");

		Assertions.assertEquals("Can't create a wallet with a negative balance", thrown.getMessage());
	}

	@Test
	@Order(10)
	public void testMissingId() {
		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setOperationType(OperationTypeEnum.WITHDRAW);
		walletDTO.setAmount(10000L);

		WalletConflictException thrown = Assertions
				.assertThrows(WalletConflictException.class, () -> {
					walletController.update(walletDTO);
				}, "WalletConflictException error was expected");

		Assertions.assertEquals("The given id must not be null", thrown.getMessage());
	}

	@Test
	@Order(11)
	public void testMissingOperationType() {
		WalletDTO walletDTOCreate = new WalletDTO();
		walletDTOCreate.setAmount(100000L);

		Wallet prevWallet = walletController.create(walletDTOCreate);

		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setWalletId(prevWallet.getId());
		walletDTO.setAmount(10L);

		WalletConflictException thrown = Assertions
				.assertThrows(WalletConflictException.class, () -> {
					walletController.update(walletDTO);
				}, "WalletConflictException error was expected");

		Assertions.assertEquals("The given operation type must not be null", thrown.getMessage());
	}

	@Test
	@Order(12)
	public void testMissingAmount() {
		WalletDTO walletDTOCreate = new WalletDTO();
		walletDTOCreate.setAmount(100000L);

		Wallet prevWallet = walletController.create(walletDTOCreate);

		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setWalletId(prevWallet.getId());
		walletDTO.setOperationType(OperationTypeEnum.DEPOSIT);

		WalletConflictException thrown = Assertions
				.assertThrows(WalletConflictException.class, () -> {
					walletController.update(walletDTO);
				}, "WalletConflictException error was expected");

		Assertions.assertEquals("The given amount must not be null", thrown.getMessage());
	}

	@Test
	@Order(13)
	public void testHighRps() {
		WalletDTO walletDTO = new WalletDTO();
		walletDTO.setAmount(1000L);
		Wallet wallet = walletController.create(walletDTO);

		ExecutorService executor = Executors.newFixedThreadPool(100);
		try {
			for (int i = 0; i < 10000; i++) {
				executor.submit(() -> {
					WalletDTO walletDTO1 = new WalletDTO();
					walletDTO1.setWalletId(wallet.getId());
					walletDTO1.setOperationType(OperationTypeEnum.DEPOSIT);
					walletDTO1.setAmount(1000L);
					walletController.update(walletDTO1);
				});
			}
		} finally {
			executor.shutdown();
			try {
				executor.awaitTermination(1, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				Assertions.fail();
			}
			long finalSum = walletController.get(wallet.getId()).getAmount();
			Assertions.assertEquals(10001000L, finalSum);
		}
	}
}
