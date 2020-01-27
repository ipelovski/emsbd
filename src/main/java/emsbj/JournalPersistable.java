package emsbj;

import org.springframework.data.domain.Persistable;

public interface JournalPersistable extends Persistable<Long> {
    @Override
    default boolean isNew() {
        return getId() == null;
    }
}
