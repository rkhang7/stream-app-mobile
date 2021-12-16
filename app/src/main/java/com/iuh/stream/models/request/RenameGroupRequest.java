package com.iuh.stream.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenameGroupRequest {
    private String groupId;
    private String name;
}
