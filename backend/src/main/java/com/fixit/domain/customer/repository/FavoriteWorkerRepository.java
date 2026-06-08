package com.fixit.domain.customer.repository;

import com.fixit.domain.customer.entity.FavoriteWorker;
import com.fixit.domain.customer.entity.FavoriteWorkerId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FavoriteWorkerRepository extends JpaRepository<FavoriteWorker, FavoriteWorkerId> {

    // Tìm tất cả thợ yêu thích theo ID của khách hàng
    @Query("SELECT fw FROM FavoriteWorker fw WHERE fw.id.customerId = :customerId")
    List<FavoriteWorker> findAllByCustomerId(@Param("customerId") UUID customerId);
}
