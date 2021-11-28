package com.iuh.stream.models.chat;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("_id")
    private String id;
    private String type;
    private String content;
    private boolean recall;
    private List<String> readedUsers;
    private boolean received;
    private List<String> deletedByUsers;
    private Date createdAt;
    private List<Reaction> reactions;

}
