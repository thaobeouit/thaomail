import java.util.HashMap;
import java.util.Map;

public class EmailDrafts {
    private Map<String, String> drafts = new HashMap<>();

    public void saveDraft(String subject, String message) {
        drafts.put(subject, message);
    }

    public String getDraft(String subject) {
        return drafts.get(subject);
    }

    public void deleteDraft(String subject) {
        drafts.remove(subject);
    }

    public boolean draftExists(String subject) {
        return drafts.containsKey(subject);
    }
}
