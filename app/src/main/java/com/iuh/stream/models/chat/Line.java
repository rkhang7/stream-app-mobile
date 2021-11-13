package com.iuh.stream.models.chat;

import java.util.List;

public class Line {
    private String type;
    private String content;
    private boolean seen;
    private List<Reaction> reactions;
    private List<String> deletedBy;

    public Line() {
    }

    public Line(String type, String content, boolean seen, List<Reaction> reactions, List<String> deletedBy) {
        this.type = type;
        this.content = content;
        this.seen = seen;
        this.reactions = reactions;
        this.deletedBy = deletedBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public List<Reaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<Reaction> reactions) {
        this.reactions = reactions;
    }

    public List<String> getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(List<String> deletedBy) {
        this.deletedBy = deletedBy;
    }

    @Override
    public String toString() {
        return "Line{" +
                "type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", seen=" + seen +
                ", reactions=" + reactions +
                ", deletedBy=" + deletedBy +
                '}';
    }
}
