package com.fixit.feature.customer.complaint.domain.repository;

import com.fixit.core.common.ResultCallback;
import com.fixit.feature.customer.complaint.domain.model.Complaint;
import java.util.List;

public interface ComplaintRepository {
    void createComplaint(String bookingId, String reason, List<String> evidenceUrls, ResultCallback<Complaint> callback);
    void getBookingComplaint(String bookingId, ResultCallback<Complaint> callback);
    void cancelComplaint(String bookingId, String complaintId, ResultCallback<Void> callback);
}
