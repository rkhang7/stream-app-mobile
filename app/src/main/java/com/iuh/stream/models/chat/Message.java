package com.iuh.stream.models.chat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
    private List<Line> lines;
    private String sender;
    private List<String> deletedByUsers;
    private String _id;
}
