package com.fixit.domain.booking.service.matching;

import com.fixit.domain.booking.repository.projection.PendingBookingProjection;
import com.fixit.domain.booking.service.dto.matching.BookingMatchingContext;
import com.fixit.domain.booking.service.dto.matching.MatchingResult;
import com.fixit.domain.booking.service.dto.matching.WorkerMatchingCandidate;
import com.fixit.domain.booking.service.matching.algorithm.HungarianAlgorithmSolver;
import com.fixit.domain.worker.repository.WorkerRepository;
import com.fixit.domain.worker.repository.projection.WorkerCandidateProjection;
import com.fixit.global.config.WorkerMatchingProperties;
import com.fixit.global.util.HaversineUtil;
import com.fixit.infrastructure.maps.GoogleMapsClient;
import com.fixit.infrastructure.maps.dto.GoogleMapsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * NHẠC TRƯỞNG (Orchestrator) của hệ thống.
 * Kết nối tất cả các Tầng (DB -> Google Maps -> Tính Cost -> Hungarian Algorithm).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WorkerMatchingServiceImpl implements WorkerMatchingService {

    private final WorkerRepository workerRepository;
    private final GoogleMapsClient googleMapsClient;
    private final CostMatrixBuilder costMatrixBuilder;
    private final WorkerMatchingProperties properties;

    @Override
    public List<MatchingResult> performBatchMatching(List<PendingBookingProjection> pendingBookings) {
        if (pendingBookings.isEmpty()) return Collections.emptyList();

        log.info("Bắt đầu chạy Batch Matching cho {} đơn hàng.", pendingBookings.size());

        List<BookingMatchingContext> contexts = new ArrayList<>();
        Set<UUID> allUniqueWorkerIds = new HashSet<>();
        List<WorkerCandidateProjection> allUniqueWorkers = new ArrayList<>();

        // BƯỚC 1: LẤY ỨNG VIÊN & ĐO KHOẢNG CÁCH (Gọi DB + Google Maps)
        for (PendingBookingProjection booking : pendingBookings) {
            
            // 1.1 Tìm 5 thợ gần nhất (Lọc thô bằng SQL - Tầng 3)
            List<WorkerCandidateProjection> candidates = workerRepository.findCandidatesNearby(
                    booking.getServiceId(),
                    booking.getDestinationLat().doubleValue(),
                    booking.getDestinationLng().doubleValue(),
                    properties.getCandidateRadiusKm(),
                    properties.getMaxWorkersPerBooking()
            );

            List<WorkerMatchingCandidate> workerCandidates = new ArrayList<>();
            for (WorkerCandidateProjection worker : candidates) {
                // Gom tất cả Thợ vào 1 mảng chung để lát tạo Ma trận
                if (allUniqueWorkerIds.add(worker.getWorkerId())) {
                    allUniqueWorkers.add(worker);
                }

                double distanceKm;
                double durationMins;
                boolean isGoogleMapsUsed = false;

                // 1.2 Gọi Google Maps đo ETA (Tầng 2)
                GoogleMapsResponse mapsResponse = googleMapsClient.getDistanceMatrix(
                        worker.getLatitude().doubleValue(), worker.getLongitude().doubleValue(),
                        booking.getDestinationLat().doubleValue(), booking.getDestinationLng().doubleValue()
                );

                if (mapsResponse != null && "OK".equals(mapsResponse.getStatus()) &&
                        !mapsResponse.getRows().isEmpty() && !mapsResponse.getRows().get(0).getElements().isEmpty() &&
                        "OK".equals(mapsResponse.getRows().get(0).getElements().get(0).getStatus())) {
                    
                    distanceKm = mapsResponse.getRows().get(0).getElements().get(0).getDistance().getValue() / 1000.0;
                    durationMins = mapsResponse.getRows().get(0).getElements().get(0).getDuration().getValue() / 60.0;
                    isGoogleMapsUsed = true;
                } else {
                    // CƠ CHẾ FALLBACK (DỰ PHÒNG CHỐNG SẬP HỆ THỐNG)
                    distanceKm = HaversineUtil.calculateDistanceKm(
                            worker.getLatitude().doubleValue(), worker.getLongitude().doubleValue(),
                            booking.getDestinationLat().doubleValue(), booking.getDestinationLng().doubleValue()
                    );
                    durationMins = HaversineUtil.estimateDurationMins(distanceKm);
                }

                // 1.3 Tạo cái túi đựng Thợ
                WorkerMatchingCandidate candidateDTO = WorkerMatchingCandidate.builder()
                        .workerInfo(worker)
                        .distanceKm(distanceKm)
                        .durationMins(durationMins)
                        .isGoogleMapsUsed(isGoogleMapsUsed)
                        .build();

                // 1.4 Chấm điểm Cost (Tầng 5)
                costMatrixBuilder.calculateAndSetCost(candidateDTO);
                workerCandidates.add(candidateDTO);
            }

            // Gói đơn hàng và các anh thợ lại
            contexts.add(BookingMatchingContext.builder()
                    .booking(booking)
                    .candidates(workerCandidates)
                    .build());
        }

        // BƯỚC 2: TẠO MA TRẬN NxM ĐỂ CHUẨN BỊ GIẢI
        int N = allUniqueWorkers.size(); // N hàng (Thợ)
        int M = pendingBookings.size();  // M cột (Đơn hàng)
        
        if (N == 0) {
            log.warn("Không tìm thấy bất kỳ thợ nào rảnh cho {} đơn hàng.", M);
            return pendingBookings.stream()
                    .map(b -> new MatchingResult(b.getBookingId(), null))
                    .collect(Collectors.toList());
        }

        double[][] costMatrix = new double[N][M];
        double MAX_COST = 999999.0; // Điểm cost vô cực (Dành cho thợ không biết làm dịch vụ đó hoặc ở quá xa)

        for (int i = 0; i < N; i++) {
            WorkerCandidateProjection worker = allUniqueWorkers.get(i);
            for (int j = 0; j < M; j++) {
                BookingMatchingContext context = contexts.get(j);
                
                // Lục trong cái túi xem anh thợ i có ứng tuyển vào đơn j không?
                Optional<WorkerMatchingCandidate> candidateOpt = context.getCandidates().stream()
                        .filter(c -> c.getWorkerInfo().getWorkerId().equals(worker.getWorkerId()))
                        .findFirst();

                if (candidateOpt.isPresent()) {
                    costMatrix[i][j] = candidateOpt.get().getMatchingCost();
                } else {
                    costMatrix[i][j] = MAX_COST; // Phạt điểm vô cực
                }
            }
        }

        // BƯỚC 3: GIẢI MÃ BẰNG CỖ MÁY HUNGARIAN
        HungarianAlgorithmSolver solver = new HungarianAlgorithmSolver(costMatrix);
        int[] assignment = solver.execute();

        // BƯỚC 4: RÁP KẾT QUẢ VÀ TRẢ VỀ CHO HỆ THỐNG GỬI THÔNG BÁO
        List<MatchingResult> results = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            int assignedBookingIndex = assignment[i];
            if (assignedBookingIndex != -1) {
                // Thợ i được Cỗ máy chia cho đơn j
                UUID workerId = allUniqueWorkers.get(i).getWorkerId();
                UUID bookingId = pendingBookings.get(assignedBookingIndex).getBookingId();
                
                // Loại trừ những cặp bị ép duyên (Cost = 999999)
                if (costMatrix[i][assignedBookingIndex] < MAX_COST) {
                    results.add(new MatchingResult(bookingId, workerId));
                }
            }
        }

        // Lọc lại những đơn hàng đen đủi bị bỏ rơi (WorkerId = null)
        for (PendingBookingProjection booking : pendingBookings) {
            boolean matched = results.stream().anyMatch(r -> r.getBookingId().equals(booking.getBookingId()));
            if (!matched) {
                results.add(new MatchingResult(booking.getBookingId(), null)); 
            }
        }

        log.info("Batch Matching hoàn tất. Ghép thành công {}/{} đơn.", 
                results.stream().filter(MatchingResult::isMatched).count(), M);
        return results;
    }
}
