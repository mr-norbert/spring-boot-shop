package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QueriesRepository extends JpaRepository<Query, Long> {

    Optional<Query> findByUserIpAndQuery(String userIp, String query);

    Optional<Query> findTop1ByUserIpAndQuery(String userIp, String query);

}
