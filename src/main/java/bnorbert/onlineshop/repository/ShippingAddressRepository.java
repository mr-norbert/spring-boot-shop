package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.ShippingAddress;
import bnorbert.onlineshop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {
    Optional<ShippingAddress> findTopByIdAndUser(long address_id, User user);
}
