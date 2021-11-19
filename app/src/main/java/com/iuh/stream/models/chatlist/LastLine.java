package com.iuh.stream.models.chatlist;

import com.iuh.stream.models.chat.Line;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LastLine {
    private Line line;
    private String senderId;
}
