package io.github.puh0v.youtube.enums;

public enum BroadcastStatus {
    NONE, LIVE, UPCOMING;

    public static BroadcastStatus getBroadcastStatus(String status) {
        if (status.equalsIgnoreCase("none")) {
            return NONE;
        }

        switch (status.toLowerCase()) {
            case "live": return LIVE;
            case "upcoming": return UPCOMING;
            default: return NONE;
        }
    }
}
