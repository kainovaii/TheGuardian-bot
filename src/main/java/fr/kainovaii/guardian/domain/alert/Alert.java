package fr.kainovaii.guardian.domain.alert;

import org.javalite.activejdbc.Model;

public class Alert extends Model
{
    public Long getMemberId() {
        return getLong("member_id");
    }

    public void setMemberId(Long memberId) {
        set("member_id", memberId);
    }

    public String getMemberName() {
        return getString("member_name");
    }

    public void setMemberName(String memberName) {
        set("member_name", memberName);
    }

    public String getWord() {
        return getString("word");
    }

    public void setWord(String word) {
        set("word", word);
    }

    public String getMessage() {
        return getString("message");
    }

    public void setMessage(String message) {
        set("message", message);
    }

    public String getChannel() {
        return getString("channel");
    }

    public void setChannel(String channel) {
        set("channel", channel);
    }

    public Long getTimestamp() {
        return getLong("timestamp");
    }

    public void setTimestamp(Long timestamp) {
        set("timestamp", timestamp);
    }

    public Double getToxicity() {
        return getDouble("toxicity");
    }

    public void setToxicity(Double toxicity) {
        set("toxicity", toxicity);
    }
}
