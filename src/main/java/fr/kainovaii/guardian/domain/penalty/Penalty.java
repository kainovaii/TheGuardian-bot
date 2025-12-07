package fr.kainovaii.guardian.domain.penalty;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("penalty")
public class Penalty extends Model
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

    public String getReason() {
        return getString("reason");
    }

    public void setReason(String reason) {
        set("reason", reason);
    }

    public String getAuthor() {
        return getString("author");
    }

    public void setAuthor(String author) {
        set("author", author);
    }

    public String getTimestamp() {
        return getString("timestamp");
    }

    public void setTimestamp(Long timestamp) {
        set("timestamp", timestamp);
    }

    public String getType() {
        return getString("type");
    }

    public void setType(String type) {
        set("type", type);
    }

    public String getStatus() {
        return getString("status");
    }

    public void setStatus(String status) {
        set("status", status);
    }

    public Double getToxicity() {
        return getDouble("toxicity");
    }

    public void setToxicity(Double toxicity) {
        set("toxicity", toxicity);
    }
}
