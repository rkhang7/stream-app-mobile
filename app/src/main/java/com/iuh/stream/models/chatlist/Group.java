package com.iuh.stream.models.chatlist;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group implements Serializable {
    @SerializedName("_id")
    private String id;
    private String name;
    private String imageURL;
    private List<String> admins;
}