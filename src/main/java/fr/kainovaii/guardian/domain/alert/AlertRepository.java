package fr.kainovaii.guardian.domain.alert;

import fr.kainovaii.guardian.core.DB;
import org.javalite.activejdbc.LazyList;

public class AlertRepository
{
    public void create(String memberId, String memberName, String word, String message,  String channel, String timestamp, double toxicity)
    {
        DB.withConnection(() -> {
            Alert alert = new Alert();
            alert.set(
                "member_id", memberId,
                "member_name", memberName,
                "word", word,
                "message", message,
                "channel", channel,
                "timestamp", timestamp,
                "toxicity", toxicity
            );
            return alert.saveIt();
        });
    }

    public LazyList<Alert> getAll() { return Alert.findAll(); }

    public LazyList<Alert> findByMember(String memberId) { return Alert.where("member_id = ?", memberId); }
}
