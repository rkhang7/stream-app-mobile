package com.iuh.stream.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecallLineRequest {
    private String chatId;
    private String messageId;
    private String lineId;
}
