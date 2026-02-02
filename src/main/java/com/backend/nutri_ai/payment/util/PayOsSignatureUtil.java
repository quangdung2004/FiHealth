    package com.backend.nutri_ai.payment.util;

    import com.backend.nutri_ai.payment.dto.request.PayOsCreatePaymentRequest;

    import javax.crypto.Mac;
    import javax.crypto.spec.SecretKeySpec;
    import java.nio.charset.StandardCharsets;
    import java.util.HexFormat;
    import java.util.Map;
    import java.util.TreeMap;
    import java.util.stream.Collectors;

    public class PayOsSignatureUtil {

        /**
         * Tạo Signature cho việc TẠO LINK THANH TOÁN (Create Payment Link)
         * Lưu ý: Chỉ ký 5 trường cụ thể này.
         */
        public static String createPaymentSignature(PayOsCreatePaymentRequest req, String key) {
            Map<String, Object> params = new TreeMap<>();
            params.put("amount", req.getAmount());
            params.put("cancelUrl", req.getCancelUrl());
            params.put("description", req.getDescription());
            params.put("orderCode", req.getOrderCode());
            params.put("returnUrl", req.getReturnUrl());

            String raw = params.entrySet()
                    .stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            return hmacSha256(raw, key);
        }

        /**
         * Hàm băm HMAC SHA256 dùng chung
         */
        public static String hmacSha256(String data, String key) {
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(
                        new SecretKeySpec(
                                key.getBytes(StandardCharsets.UTF_8),
                                "HmacSHA256"
                        )
                );
                byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
                return HexFormat.of().formatHex(rawHmac);
            } catch (Exception e) {
                throw new RuntimeException("Cannot generate HMAC", e);
            }
        }
    }