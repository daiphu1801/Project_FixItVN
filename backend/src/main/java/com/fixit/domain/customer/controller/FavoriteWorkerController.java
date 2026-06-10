package com.fixit.domain.customer.controller;

import com.fixit.domain.customer.dto.response.FavoriteWorkerResponse;
import com.fixit.domain.customer.service.FavoriteWorkerService;
import com.fixit.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/favorites")
@RequiredArgsConstructor
public class FavoriteWorkerController {

    private final FavoriteWorkerService favoriteWorkerService;

    // 1. Thêm thợ vào danh sách yêu thích
    @PostMapping("/{workerId}")
    public ApiResponse<Void> addFavorite(@PathVariable UUID workerId) {
        favoriteWorkerService.addFavorite(workerId);
        return ApiResponse.success(null, "Thêm thợ yêu thích thành công");
    }

    // 2. Xóa thợ khỏi danh sách yêu thích
    @DeleteMapping("/{workerId}")
    public ApiResponse<Void> removeFavorite(@PathVariable UUID workerId) {
        favoriteWorkerService.removeFavorite(workerId);
        return ApiResponse.success(null, "Xóa thợ yêu thích thành công");
    }

    // 3. Kiểm tra xem thợ này đã nằm trong danh sách yêu thích chưa
    @GetMapping("/{workerId}/status")
    public ApiResponse<Boolean> isFavorite(@PathVariable UUID workerId) {
        boolean isFav = favoriteWorkerService.isFavorite(workerId);
        return ApiResponse.success(isFav);
    }

    // 4. Lấy toàn bộ danh sách thợ yêu thích của khách hàng hiện tại
    @GetMapping
    public ApiResponse<List<FavoriteWorkerResponse>> getFavorites() {
        return ApiResponse.success(favoriteWorkerService.getFavorites());
    }
}
