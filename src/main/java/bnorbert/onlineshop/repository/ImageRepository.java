package bnorbert.onlineshop.repository;

import bnorbert.onlineshop.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByNameAndPhoto(String name, byte[] photo);
}
