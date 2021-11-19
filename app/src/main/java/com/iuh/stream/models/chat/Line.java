package com.iuh.stream.models.chat;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Line {
    private Date createdAt;
    private String type;
    private String content;
    private double state;
    private List<Reaction> reactions;
    private List<String> deletedBy;
}
