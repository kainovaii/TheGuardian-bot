package fr.kainovaii.guardian.domain.penalty;

import fr.kainovaii.guardian.core.database.DB;
import org.javalite.activejdbc.LazyList;

public class PenaltyRepository
{
    public void create(String memberId, String memberName, String reason, String author, String timestamp, String type, String status, double toxicity)
    {
        DB.withConnection(() -> {
            Penalty penalty = new Penalty();
            penalty.set(
                "member_id", memberId,
                "member_name", memberName,
                "reason", reason,
                "author", author,
                "timestamp", timestamp,
                "type", type,
                "status", status,
                "toxicity", toxicity
            );
            return penalty.saveIt();
        });
    }

    public LazyList<Penalty> getAll() { return Penalty.findAll(); }

    public LazyList<Penalty> findByMember(String memberId) { return Penalty.where("member_id = ?", memberId); }
}
