package com.fixit.domain.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String toEmail, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("[FixIt VN] Mã xác thực OTP đặt lại mật khẩu");

            String htmlContent = """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;">
                    <h2 style="color: #42c2ff; text-align: center;">Yêu Cầu Đặt Lại Mật Khẩu</h2>
                    <p>Xin chào,</p>
                    <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản FixIt VN của bạn. Vui lòng sử dụng mã OTP dưới đây để hoàn tất quá trình:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <span style="font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #0f172a; background-color: #f1f5f9; padding: 10px 20px; border-radius: 8px; border: 1px dashed #cbd5e1;">
                            %s
                        </span>
                    </div>
                    <p style="color: #64748b; font-size: 14px;">Mã OTP này có hiệu lực trong vòng 2 phút. Nếu bạn không yêu cầu thay đổi này, bạn có thể bỏ qua email này một cách an toàn.</p>
                    <hr style="border: 0; border-top: 1px solid #e2e8f0; margin: 20px 0;">
                    <p style="text-align: center; color: #94a3b8; font-size: 12px;">© 2026 FixIt VN. All rights reserved.</p>
                </div>
                """.formatted(otpCode);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Email OTP sent successfully to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}", toEmail, e);
            throw new RuntimeException("Không thể gửi email OTP. Vui lòng thử lại sau.");
        }
    }
}
