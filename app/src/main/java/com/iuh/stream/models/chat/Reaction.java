package com.iuh.stream.models.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reaction {
    private String senderId;
    private String emoji;
}
