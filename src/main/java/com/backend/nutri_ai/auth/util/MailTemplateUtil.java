package com.backend.nutri_ai.auth.util;


import java.text.NumberFormat;
import java.util.Locale;

public class MailTemplateUtil {

    public static String otpTemplate(String otp) {
        return """
        <div style="font-family:Arial;max-width:600px;margin:auto">
            <h2>Xác thực tài khoản</h2>
            <p>Mã OTP của bạn:</p>
            <h1 style="color:#e74c3c">%s</h1>
            <p>Mã có hiệu lực trong 5 phút.</p>
        </div>
        """.formatted(otp);
    }

    public static String resetPasswordTemplate(String otp) {
        return """
        <div style="font-family:Arial">
            <h2>Reset mật khẩu</h2>
            <p>OTP của bạn:</p>
            <h1>%s</h1>
        </div>
        """.formatted(otp);
    }

    public static String activationTemplate(String link) {
        return """
        <div style="font-family:Arial">
            <h2>Kích hoạt tài khoản</h2>
            <p>Click link bên dưới:</p>
            <a href="%s">%s</a>
        </div>
        """.formatted(link, link);
    }
    private static String safe(String s) {
        return s == null ? "" : s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String formatVnd(Long amount) {
        if (amount == null) return "";
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + " ₫";
    }

    private static String packageName(int durationDays) {
        if (durationDays >= 365) return "Premium (1 năm)";
        if (durationDays >= 180) return "Premium (6 tháng)";
        if (durationDays >= 90)  return "Premium (3 tháng)";
        if (durationDays >= 30)  return "Premium (1 tháng)";
        return "Premium";
    }

    // ✅ NEW
    public static String premiumThankYouTemplate(
            String fullName,
            int durationDays,
            Long amount,
            String orderCode
    ) {
        String name = safe(fullName);
        String pkg = packageName(durationDays);
        String money = formatVnd(amount);
        String order = safe(orderCode);

        return ""
                + "<div style='margin:0;padding:0;background:#f6f8fb;'>"
                + "  <div style='max-width:680px;margin:0 auto;padding:24px;'>"
                + "    <div style='background:#ffffff;border:1px solid #e9edf3;border-radius:14px;overflow:hidden;'>"
                + "      <div style='padding:22px 24px;background:linear-gradient(90deg,#ff7a00,#ff9a3c);color:#fff;'>"
                + "        <div style='font-size:18px;font-weight:700;letter-spacing:.2px;'>FiHealth</div>"
                + "        <div style='margin-top:6px;font-size:14px;opacity:.95;'>Xác nhận đăng ký gói Premium</div>"
                + "      </div>"
                + ""
                + "      <div style='padding:24px;color:#1f2a37;font-family:Arial,sans-serif;'>"
                + "        <p style='margin:0 0 12px;font-size:15px;line-height:1.6;'>"
                + "          Xin chào <b>" + (name.isEmpty() ? "bạn" : name) + "</b>,"
                + "        </p>"
                + "        <p style='margin:0 0 12px;font-size:15px;line-height:1.6;'>"
                + "          FiHealth chân thành cảm ơn bạn đã tin tưởng và đăng ký <b>" + pkg + "</b>. "
                + "          Thanh toán của bạn đã được ghi nhận thành công và quyền lợi Premium sẽ được kích hoạt ngay."
                + "        </p>"
                + ""
                + "        <div style='margin:18px 0;padding:16px 18px;border:1px solid #eef2f7;border-radius:12px;background:#fbfcfe;'>"
                + "          <div style='font-size:14px;color:#6b7280;margin-bottom:10px;font-weight:600;'>Thông tin giao dịch</div>"
                + "          <table style='width:100%;border-collapse:collapse;font-size:14px;'>"
                + "            <tr>"
                + "              <td style='padding:6px 0;color:#6b7280;width:40%;'>Mã đơn hàng</td>"
                + "              <td style='padding:6px 0;color:#111827;font-weight:600;'>" + order + "</td>"
                + "            </tr>"
                + "            <tr>"
                + "              <td style='padding:6px 0;color:#6b7280;'>Gói đăng ký</td>"
                + "              <td style='padding:6px 0;color:#111827;font-weight:600;'>" + pkg + "</td>"
                + "            </tr>"
                + "            <tr>"
                + "              <td style='padding:6px 0;color:#6b7280;'>Thời hạn</td>"
                + "              <td style='padding:6px 0;color:#111827;font-weight:600;'>" + durationDays + " ngày</td>"
                + "            </tr>"
                + "            " + (amount != null ? ""
                + "            <tr>"
                + "              <td style='padding:6px 0;color:#6b7280;'>Số tiền</td>"
                + "              <td style='padding:6px 0;color:#111827;font-weight:700;'>" + money + "</td>"
                + "            </tr>" : "")
                + "          </table>"
                + "        </div>"
                + ""
                + "        <p style='margin:0 0 12px;font-size:15px;line-height:1.6;'>"
                + "          Với Premium, bạn sẽ nhận được trải nghiệm đầy đủ hơn như: nội dung và tính năng nâng cao, "
                + "          ưu tiên hỗ trợ, và các cải tiến mới nhất ngay khi được phát hành."
                + "        </p>"
                + ""
                + "        <p style='margin:0 0 12px;font-size:15px;line-height:1.6;'>"
                + "          Nếu bạn có bất kỳ thắc mắc nào về hoá đơn, quyền lợi gói, hoặc cần hỗ trợ kỹ thuật, "
                + "          vui lòng phản hồi trực tiếp email này. Đội ngũ FiHealth luôn sẵn sàng hỗ trợ."
                + "        </p>"
                + ""
                + "        <div style='margin-top:18px;padding-top:16px;border-top:1px solid #eef2f7;color:#6b7280;font-size:12.5px;line-height:1.6;'>"
                + "          <div><b>Lưu ý bảo mật:</b> FiHealth không bao giờ yêu cầu bạn cung cấp mật khẩu qua email.</div>"
                + "          <div style='margin-top:6px;'>Email này được gửi tự động. Nếu bạn không thực hiện giao dịch này, vui lòng liên hệ ngay với chúng tôi.</div>"
                + "        </div>"
                + ""
                + "        <p style='margin:18px 0 0;font-size:15px;line-height:1.6;'>"
                + "          Trân trọng,<br/>"
                + "          <b>Đội ngũ FiHealth</b>"
                + "        </p>"
                + "      </div>"
                + "    </div>"
                + "    <div style='text-align:center;color:#9aa3af;font-family:Arial,sans-serif;font-size:12px;margin-top:14px;'>"
                + "      © FiHealth. All rights reserved."
                + "    </div>"
                + "  </div>"
                + "</div>";
    }
}
