package gift.repository;

import gift.entity.Member;
import gift.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {
    Page<Wish> findAllByMember(Member member, Pageable pageable);
}

