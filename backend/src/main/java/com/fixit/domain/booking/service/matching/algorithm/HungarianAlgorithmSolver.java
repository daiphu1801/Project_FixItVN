package com.fixit.domain.booking.service.matching.algorithm;

import java.util.Arrays;

/**
 * TRÙM CUỐI: Thuật toán Hungarian (Kuhn-Munkres) O(N^3).
 * Được dùng để giải quyết Bài toán Phân công (Assignment Problem).
 * 
 * Đầu vào: Một Ma trận chi phí NxM (N Thợ, M Đơn hàng).
 * Đầu ra: Một sơ đồ ghép cặp sao cho TỔNG CHI PHÍ CỦA TOÀN BỘ HỆ THỐNG LÀ NHỎ NHẤT.
 */
public class HungarianAlgorithmSolver {

    private final double[][] costMatrix;
    private final int rows, cols, dim;
    private final double[] u, v;
    private final int[] p, way;

    /**
     * Khởi tạo Thuật toán. Nếu số lượng Thợ và Đơn hàng không bằng nhau (ma trận không vuông),
     * Thuật toán sẽ tự động tạo thêm các Thợ "ảo" hoặc Đơn "ảo" với chi phí = 0 để cân bằng.
     */
    public HungarianAlgorithmSolver(double[][] costMatrix) {
        this.rows = costMatrix.length;
        this.cols = costMatrix[0].length;
        this.dim = Math.max(rows, cols);
        this.costMatrix = new double[dim][dim];
        
        for (int i = 0; i < dim; i++) {
            if (i < rows) {
                this.costMatrix[i] = Arrays.copyOf(costMatrix[i], dim);
            } else {
                this.costMatrix[i] = new double[dim]; // Padding (Thêm hàng ảo)
            }
        }
        
        u = new double[dim + 1];
        v = new double[dim + 1];
        p = new int[dim + 1];
        way = new int[dim + 1];
    }

    /**
     * Chạy Thuật toán và trả về mảng kết quả.
     * result[i] = j có nghĩa là: Hàng i (Anh Thợ thứ i) được phân công cho Cột j (Đơn hàng thứ j).
     * Nếu kết quả trả về -1 nghĩa là anh Thợ đó bị dư ra (không có đơn).
     */
    public int[] execute() {
        for (int i = 1; i <= dim; i++) {
            p[0] = i;
            int j0 = 0;
            double[] minv = new double[dim + 1];
            Arrays.fill(minv, Double.MAX_VALUE);
            boolean[] used = new boolean[dim + 1];
            
            // Tìm đường tăng luồng (Augmenting path)
            do {
                used[j0] = true;
                int i0 = p[j0], j1 = 0;
                double delta = Double.MAX_VALUE;
                for (int j = 1; j <= dim; j++) {
                    if (!used[j]) {
                        double cur = costMatrix[i0 - 1][j - 1] - u[i0] - v[j];
                        if (cur < minv[j]) {
                            minv[j] = cur;
                            way[j] = j0;
                        }
                        if (minv[j] < delta) {
                            delta = minv[j];
                            j1 = j;
                        }
                    }
                }
                
                // Cập nhật nhãn (Potentials)
                for (int j = 0; j <= dim; j++) {
                    if (used[j]) {
                        u[p[j]] += delta;
                        v[j] -= delta;
                    } else {
                        minv[j] -= delta;
                    }
                }
                j0 = j1;
            } while (p[j0] != 0);
            
            // Cập nhật lại đường đi
            do {
                int j1 = way[j0];
                p[j0] = p[j1];
                j0 = j1;
            } while (j0 != 0);
        }
        
        // Trích xuất kết quả cuối cùng từ mảng p[]
        int[] assignment = new int[rows];
        Arrays.fill(assignment, -1);
        for (int j = 1; j <= dim; j++) {
            if (p[j] > 0 && p[j] <= rows && (j - 1) < cols) {
                assignment[p[j] - 1] = j - 1;
            }
        }
        return assignment;
    }
}
