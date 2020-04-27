package sasj.data;

import org.springframework.data.domain.Persistable;

public abstract class JournalPersistable implements Persistable<Long> {
    @Override
    public boolean isNew() {
        return getId() == null;
    }
}
