package com.fixit.global.config;

import com.fixit.domain.service_categories.entity.ServiceCategory;
import com.fixit.domain.service_categories.entity.ServiceItem;
import com.fixit.domain.service_categories.repository.ServiceCategoryRepository;
import com.fixit.domain.service_categories.repository.ServiceItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final ServiceItemRepository serviceItemRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedServiceCategories();
    }

    private void seedServiceCategories() {
        if (serviceCategoryRepository.count() <= 3) {
            log.info("Bắt đầu tạo dữ liệu mẫu cho danh mục dịch vụ...");

            // Xóa dữ liệu cũ (nếu có lỗi khóa ngoại thì có thể cần xóa bookings trước)
            try {
                serviceItemRepository.deleteAll();
                serviceCategoryRepository.deleteAll();
            } catch (Exception e) {
                log.warn("Không thể xóa dữ liệu cũ, bỏ qua seed mới.");
                return;
            }

            String[][] categories = {
                {"Xây dựng & Kiến trúc", "https://cdn-icons-png.flaticon.com/512/3069/3069411.png"},
                {"Cơ khí & Nhôm kính", "https://cdn-icons-png.flaticon.com/512/3201/3201509.png"},
                {"Điện lạnh", "https://cdn-icons-png.flaticon.com/512/2990/2990352.png"},
                {"Điện nước", "https://cdn-icons-png.flaticon.com/512/1973/1973998.png"},
                {"Thợ mộc", "https://cdn-icons-png.flaticon.com/512/3131/3131599.png"},
                {"Đồ gỗ nội thất", "https://cdn-icons-png.flaticon.com/512/2610/2610360.png"},
                {"Vệ sinh", "https://cdn-icons-png.flaticon.com/512/995/995055.png"},
                {"Thông nghẹt", "https://cdn-icons-png.flaticon.com/512/1000/1000003.png"},
                {"Chuyển nhà", "https://cdn-icons-png.flaticon.com/512/2760/2760124.png"},
                {"Giặt ghế", "https://cdn-icons-png.flaticon.com/512/2822/2822238.png"},
                {"Xe cẩu", "https://cdn-icons-png.flaticon.com/512/2621/2621172.png"},
                {"Dịch vụ khác", "https://cdn-icons-png.flaticon.com/512/1043/1043444.png"}
            };

            for (String[] cat : categories) {
                ServiceCategory sc = ServiceCategory.builder()
                        .serviceName(cat[0])
                        .iconUrl(cat[1])
                        .build();
                sc = serviceCategoryRepository.save(sc);
                
                // Thêm 1 dịch vụ con mẫu cho mỗi danh mục
                serviceItemRepository.save(ServiceItem.builder()
                        .serviceCategory(sc)
                        .itemName("Dịch vụ " + cat[0])
                        .suggestedPrice(java.math.BigDecimal.valueOf(150000.0))
                        .build());
            }

            log.info("Tạo dữ liệu mẫu danh mục dịch vụ thành công!");
        }
    }
}
