package com.fixit.feature.customer.review.data.remote.mapper;

import com.fixit.feature.customer.review.data.remote.dto.ReviewResponseDto;
import com.fixit.feature.customer.review.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewMapper {
    public static Review toDomain(ReviewResponseDto dto) {
        if (dto == null) return null;
        return new Review(
                dto.getId(),
                dto.getCustomerName(),
                dto.getCustomerAvatar(),
                dto.getRating(),
                dto.getComment(),
                dto.getCreatedAt()
        );
    }

    public static List<Review> toDomainList(List<ReviewResponseDto> dtoList) {
        List<Review> list = new ArrayList<>();
        if (dtoList != null) {
            for (ReviewResponseDto dto : dtoList) {
                list.add(toDomain(dto));
            }
        }
        return list;
    }
}
