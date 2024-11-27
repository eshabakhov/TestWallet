package test.wallet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import test.wallet.dto.Wallet;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE Wallet w SET w.isExist = FALSE WHERE w.id = :wallet_id AND w.isExist != FALSE")
    Integer deleteWalletById(@Param("wallet_id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Wallet w SET w.amount = w.amount + :amount WHERE w.id = :wallet_id AND w.isExist != FALSE")
    Integer depositWalletById(@Param("wallet_id") Long id, @Param("amount") Long amount);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Wallet w SET w.amount = amount - :amount WHERE w.id = :wallet_id AND w.isExist != FALSE")
    Integer withdrawWalletById(@Param("wallet_id") Long id, @Param("amount") Long amount);

}
