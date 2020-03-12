package emsbj;

import emsbj.generation.Generator;
import emsbj.mark.Mark;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(1000000)
public class InitializationFilter implements Filter {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SchoolClassRepository schoolClassRepository;
    @Autowired
    private WeeklySlotRepository weeklySlotRepository;
    @Autowired
    private BlobRepository blobRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private Generator generator;
    private boolean initialized;
    private SchoolYear schoolYear;
    private Map<Integer, Grade> grades = new HashMap<>(4);
    private List<Subject> subjects = new LinkedList<>();
    private List<Teacher> teachers = new LinkedList<>();
    private List<Student> students = new LinkedList<>();
    private List<Room> rooms = new LinkedList<>();
    private List<SchoolClass> schoolClasses = new LinkedList<>();
    private List<WeeklySlot> weeklySlots = new LinkedList<>();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (!initialized) {
            initialize();
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void initialize() {
        User admin = new User("admin");
        admin.setRole(User.Role.admin);
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEmail("admin@admin.admin");
        Blob adminPicture = new Blob();
        byte[] byteArray = readFile("sokka-small.png");
        adminPicture.setData(byteArray);
        adminPicture.setMimeType("image/png");
        blobRepository.save(adminPicture);
        admin.getPersonalInfo().setPicture(adminPicture);
        userRepository.save(admin);
        JournalAuditAware.setCurrentUser(admin);
        createSchoolYear();
        createTerms();
//        createGrades();
//        createSubjects();
        createWeeklySlots();
        createPrincipal();
//        createTeachers();
//        createStudents();
//        createSchoolClasses();
        createRooms();
//        createCourses();
        generator.generate();
        initialized = true;
    }

    private byte[] readFile(String fileName) {
        try (InputStream inputStream = getClass().getClassLoader()
            .getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Cannot load file " + fileName);
            }
            byte[] byteArray = new byte[inputStream.available()];
            int bytesRead = inputStream.read(byteArray, 0, byteArray.length);
            if (bytesRead != byteArray.length) {
                throw new RuntimeException("Could not read the whole file.");
            }
            return byteArray;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createSchoolYear() {
        SchoolYear schoolYear = new SchoolYear(2020, 2021);
        this.schoolYear = schoolYearRepository.save(schoolYear);
    }

    private void createTerms() {
        Term firstTerm = new Term(schoolYear, "I");
        termRepository.save(firstTerm);
        Term secondTerm = new Term(schoolYear, "II");
        termRepository.save(secondTerm);
        schoolYear = schoolYearRepository.findByIdWithAll(schoolYear.getId()).get();
    }

    private void createGrades() {
        for (Integer i = 9; i <= 12; i++) {
            Grade grade = new Grade(i);
            gradeRepository.save(grade);
            grades.put(i, grade);
        }
        {
            Grade grade = new Grade(8);
            gradeRepository.save(grade);
        }
        {
            Grade grade = new Grade("8 prep");
            gradeRepository.save(grade);
        }
    }

    private void createSubjects() {
        Subject subject = subjectService.create("Биология", grades.get(9));
        subjects.add(subject);
    }

    private void createWeeklySlots() {
        Duration lessonDuration = Duration.ofMinutes(40);
        Duration breakDuration = Duration.ofMinutes(10);
        Duration longBreakDuration = Duration.ofMinutes(20);
        int lessonsBeforeLongBreak = 2;
        LocalTime[] shiftsBegin = {LocalTime.of(7, 30), LocalTime.of(13, 30)};
        for (int day = 1; day <= 5; day++) {
            for (int shift = 1; shift <= 2; shift++) {
                LocalTime begin = shiftsBegin[shift - 1];
                LocalTime end = begin.plus(lessonDuration);
                for (int ordinal = 1; ordinal <= 7; ordinal++) {
                    WeeklySlot lessonWeeklySlot = new WeeklySlot(
                        DayOfWeek.of(day), shift, ordinal, begin, end);
                    weeklySlotRepository.save(lessonWeeklySlot);
                    weeklySlots.add(lessonWeeklySlot);
                    if (ordinal == lessonsBeforeLongBreak) {
                        begin = end.plus(longBreakDuration);
                    }
                    else {
                        begin = end.plus(breakDuration);
                    }
                    end = begin.plus(lessonDuration);
                }
            }
        }
    }

    private void createPrincipal() {
        createUser(User.Role.principal, "Станислав", "Игнатиев", "Големанов");
    }

    private void createTeachers() {
        {
            User teacherUser = createUser(User.Role.teacher, "Унуфри", "Методиев", "Харалампиев");
            Teacher teacher = new Teacher();
            teacher.setUser(teacherUser);
            teacher.getSkills().add(subjects.get(0));
            teacherRepository.save(teacher);
            teachers.add(teacher);
        }
        {
            User teacherUser = createUser(User.Role.teacher, "Генади", "Захариев", "Хаджитошев");
            Teacher teacher = new Teacher();
            teacher.setUser(teacherUser);
            teacher.getSkills().add(subjects.get(0));
            teacherRepository.save(teacher);
            teachers.add(teacher);
        }
    }

    private void createStudents() {
        createStudent("Гошко", "Георгиев", "Гошев");
        createStudent("Тошко", "Теодоров", "Тодоров");
        createStudent("Лъв", "Леонидов", "Котов");
    }

    private void createStudent(String firstName, String middleName, String lastName) {
        User studentUser = createUser(User.Role.student, firstName, middleName, lastName);
        Student student = new Student();
        student.setUser(studentUser);
        student.getMarks().add(new Mark(student, subjects.get(0), 599));
        studentRepository.save(student);
        students.add(student);
    }

    private void createSchoolClasses() {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setBeginningSchoolYear(schoolYear);
        schoolClass.setName("А");
        schoolClass.setBeginningGrade(grades.get(9));
        schoolClass.setFormMaster(teachers.get(0));
        schoolClasses.add(schoolClass);
        schoolClassRepository.save(schoolClass);
        students.get(0).setSchoolClass(schoolClass);
        students.get(0).setNumber(1);
        studentRepository.save(students.get(0));
        students.get(1).setSchoolClass(schoolClass);
        students.get(1).setNumber(2);
        studentRepository.save(students.get(1));
    }

    private void createRooms() {
        Room room = new Room();
        room.setName("1");
        room.setFloor(1);
        roomRepository.save(room);
        rooms.add(room);
    }

    private void createCourses() {
        Course course = new Course();
        course.setSubject(subjects.get(0));
        course.setRoom(rooms.get(0));
        course.setTeacher(teachers.get(0));
        course.setSchoolClass(schoolClasses.get(0));
        course.setTerm(schoolYear.getTerms().get(0));
        List<WeeklySlot> courseWeeklySlots = new ArrayList<>(2);
        for (DayOfWeek day : DayOfWeek.values()) {
            List<WeeklySlot> weeklySlots = this.weeklySlots.stream()
                .filter(weeklySlot -> weeklySlot.getDay() == day)
                .collect(Collectors.toList());
            courseWeeklySlots.addAll(weeklySlots);
        }
        course.setWeeklySlots(courseWeeklySlots);
        courseRepository.save(course);
    }

    private User createUser(User.Role role, String firstName, String middleName, String lastName) {
        String username = firstName.substring(0, 1).toLowerCase() + "." + lastName.toLowerCase();
        User user = new User(username);
        user.setPassword(passwordEncoder.encode(username));
        String email = username + "@моето-училище.бг";
        user.setEmail(email);
        user.setRole(role);
        user.getPersonalInfo()
            .setFirstName(firstName)
            .setMiddleName(middleName)
            .setLastName(lastName);
        return userRepository.save(user);
    }
}
