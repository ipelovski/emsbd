package sasj.teacher;

import sasj.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private UserService userService;

    public Optional<Teacher> getCurrentTeacher() {
        return userService.getCurrentUser()
            .flatMap(user -> teacherRepository.findByUserId(user.getId()));
    }
}
