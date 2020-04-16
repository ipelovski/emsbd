package sasj.subject;

import sasj.grade.Grade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SubjectNameRepository subjectNameRepository;

    public Subject create(String name, Grade grade) {
        Optional<SubjectName> optionalSubjectName = subjectNameRepository.findByValue(name);
        SubjectName subjectName = optionalSubjectName.orElseGet(() -> {
            SubjectName newSubjectName = new SubjectName(name);
            subjectNameRepository.save(newSubjectName);
            return newSubjectName;
        });
        return subjectRepository.save(new Subject(subjectName, grade));
    }
}
