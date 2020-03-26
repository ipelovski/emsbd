package emsbj.generation;

import emsbj.util.Pair;
import emsbj.util.Util;
import emsbj.blob.Blob;
import emsbj.blob.BlobRepository;
import emsbj.course.Course;
import emsbj.course.CourseRepository;
import emsbj.grade.Grade;
import emsbj.grade.GradeRepository;
import emsbj.lesson.Lesson;
import emsbj.lesson.LessonRepository;
import emsbj.room.Room;
import emsbj.room.RoomRepository;
import emsbj.School;
import emsbj.schoolclass.SchoolClass;
import emsbj.schoolclass.SchoolClassRepository;
import emsbj.schoolyear.SchoolYear;
import emsbj.schoolyear.SchoolYearRepository;
import emsbj.student.Student;
import emsbj.student.StudentRepository;
import emsbj.subject.Subject;
import emsbj.subject.SubjectName;
import emsbj.subject.SubjectNameRepository;
import emsbj.subject.SubjectRepository;
import emsbj.teacher.Teacher;
import emsbj.teacher.TeacherRepository;
import emsbj.term.Term;
import emsbj.term.TermRepository;
import emsbj.weeklyslot.WeeklySlot;
import emsbj.weeklyslot.WeeklySlotRepository;
import emsbj.mark.Mark;
import emsbj.user.User;
import emsbj.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class Generator {
    private static final int teacherLessonsPerWeek = 34;
    private static final int maxOrdinal = 7;
    private static final int minStudentAge = 6;
    private static final int studentsInSchoolClass = 26;
    static class TeacherTimeTable extends TimeTable<Teacher, CourseValue> {
        TeacherTimeTable(Teacher owner, List<WeeklySlot> weeklySlots) {
            super(owner, weeklySlots);
        }
    }
    static class SchoolClassTimeTable extends TimeTable<SchoolClassValue, String> {
        SchoolClassTimeTable(SchoolClassValue owner, List<WeeklySlot> weeklySlots) {
            super(owner, weeklySlots);
        }
    }

    @Autowired
    private UserGenerator userGenerator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GradeRepository gradeRepository;
    @Autowired
    private SchoolClassRepository schoolClassRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private SubjectNameRepository subjectNameRepository;
    @Autowired
    private WeeklySlotRepository weeklySlotRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private SchoolYearRepository schoolYearRepository;
    @Autowired
    private TermRepository termRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private LessonRepository lessonRepository;
    @Autowired
    private BlobRepository blobRepository;
    @Autowired
    private Util util;

    private List<WeeklySlot> weeklySlots;
    private Map<Integer, Grade> grades;
    private Map<Integer, TeachingProgram> gradePrograms;
    private List<Subject> subjects;
    private Map<SchoolClassValue, SchoolClass> schoolClasses = new LinkedHashMap<>();
    private Map<CourseValue, Course> courses = new HashMap<>();
    private Map<String, List<TeacherTimeTable>> teacherTimeTables;
    private SchoolYear schoolYear;
    private Term term;
    private Random random = new Random(0);

    public void generate() {
        createAdmin();
        createPrincipal();
        schoolYear = createSchoolYear();
        weeklySlots = createWeeklySlots();
        grades = createGrades();

        School.getInstance().reset();
        term = School.getInstance().getTerm();

        TeachingProgram program9 = TeachingProgram.create9();
        TeachingProgram program10 = TeachingProgram.create10();
        TeachingProgram program11 = TeachingProgram.create11();
        TeachingProgram program12 = TeachingProgram.create12();
        gradePrograms = new LinkedHashMap<>(4);
        gradePrograms.put(9, program9);
        gradePrograms.put(10, program10);
        gradePrograms.put(11, program11);
        gradePrograms.put(12, program12);

        subjects = createSubjects();

        Map<SchoolClassValue, TeachingProgram> programs =
            new LinkedHashMap<>();
        String[] schoolClassNames = "АБВГ".split("");
        for (int grade = 9; grade <= 12; grade++) {
            for (int i = 0; i < 4; i++) {
                SchoolClassValue schoolClass = new SchoolClassValue(
                    grade, schoolClassNames[i], i < 2 ? 1 : 2);
                programs.put(schoolClass, gradePrograms.get(grade));
            }
        }

        TeachingProgram programAll = new TeachingProgram();
        for (SchoolClassValue schoolClass : programs.keySet()) {
            programAll = mergePrograms(programAll, programs.get(schoolClass));
        }
        Map<String, List<Teacher>> teachers = createTeachers(programAll);
        teacherTimeTables = createTeacherTimeTables(teachers);
        Map<SchoolClassValue, Map<String, Teacher>> schoolClassTeachers =
            computeSchoolClassesTeachers(programs, teachers);

        List<SchoolClassTimeTable> schoolClassTimeTables = new ArrayList<>(programs.size());
        for (SchoolClassValue schoolClass : programs.keySet()) {
            SchoolClassTimeTable schoolClassTimeTable = createSchoolClassTimeTable(
                schoolClass, programs.get(schoolClass));
            schoolClassTimeTables.add(schoolClassTimeTable);
        }

        populateTeacherTimeTables(schoolClassTimeTables, teacherTimeTables, schoolClassTeachers);

        fixTeacherTimeTables(teacherTimeTables);

        checkAll(teacherTimeTables);

        persistSchoolClasses(schoolClassTimeTables, schoolClassTeachers);

        createLessons();
    }

    private void createAdmin() {
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

    private SchoolYear createSchoolYear() {
        LocalDate now = LocalDate.now();
        int beginYear = now.getYear()
            - (now.isBefore(LocalDate.of(now.getYear(), Month.JULY, 1)) ? 1 : 0);
        SchoolYear schoolYear = new SchoolYear(beginYear, beginYear + 1);
        schoolYearRepository.save(schoolYear);
        Term firstTerm = new Term(schoolYear, "I");
        firstTerm.setBegin(LocalDate.of(schoolYear.getBeginYear(), Month.JULY, 1));
        firstTerm.setEnd(LocalDate.of(schoolYear.getBeginYear(), Month.DECEMBER, 31));
        termRepository.save(firstTerm);
        Term secondTerm = new Term(schoolYear, "II");
        secondTerm.setBegin(LocalDate.of(schoolYear.getEndYear(), Month.JANUARY, 1));
        secondTerm.setEnd(LocalDate.of(schoolYear.getEndYear(), Month.JUNE, 30));
        termRepository.save(secondTerm);
        return schoolYearRepository.save(schoolYear);
    }

    private List<WeeklySlot> createWeeklySlots() {
        Duration lessonDuration = Duration.ofMinutes(40);
        Duration breakDuration = Duration.ofMinutes(10);
        Duration longBreakDuration = Duration.ofMinutes(20);
        int lessonsBeforeLongBreak = 2;
        LocalTime[] shiftsBegin = {LocalTime.of(7, 30), LocalTime.of(13, 30)};
        List<WeeklySlot> weeklySlots = new ArrayList<>(5 * 2 * 7);
        for (int day = 1; day <= 5; day++) {
            for (int shift = 1; shift <= 2; shift++) {
                LocalTime begin = shiftsBegin[shift - 1];
                LocalTime end = begin.plus(lessonDuration);
                for (int ordinal = 1; ordinal <= 7; ordinal++) {
                    WeeklySlot weeklySlot = new WeeklySlot(
                        DayOfWeek.of(day), shift, ordinal, begin, end);
                    weeklySlots.add(weeklySlot);
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
        weeklySlotRepository.saveAll(weeklySlots);
        return weeklySlots;
    }

    private void createPrincipal() {
        userGenerator.createUser(User.Role.principal,40, 60);
    }

    private TeachingProgram mergePrograms(TeachingProgram... programs) {
        assert programs.length > 0;
        TeachingProgram programAll = new TeachingProgram(programs[0]);
        for (int i = 1; i < programs.length; i++) {
            TeachingProgram program = programs[i];
            for (String key : program.keySet()) {
                if (programAll
                    .computeIfPresent(key, (s, integer) ->
                        program.get(s) + integer) == null) {
                    programAll.put(key, program.get(key));
                }
            }
        }
        return programAll;
    }

    private List<Subject> createSubjects() {
        List<Subject> subjects = new ArrayList<>();
        for (int gradeOrdinal : gradePrograms.keySet()) {
            for (String subjectName : gradePrograms.get(gradeOrdinal).keySet()) {
                Subject subject = createSubject(subjectName, gradeOrdinal, subjects);
                subjects.add(subject);
            }
        }
        return subjects;
    }

    private Subject createSubject(String name, int grade, List<Subject> subjects) {
        Optional<Subject> optionalSubject = subjects.stream()
            .filter(subject -> subject.getName().getValue().equals(name)
                && subject.getGrade().getName().equals(Integer.toString(grade)))
            .findAny();
        if (optionalSubject.isPresent()) {
            throw new IllegalArgumentException("Subject is already created.");
        } else {
            Optional<Subject> subjectWithSameName = subjects.stream()
                .filter(subject -> subject.getName().getValue().equals(name))
                .findAny();
            SubjectName subjectName = subjectWithSameName.map(Subject::getName)
                .orElseGet(() -> subjectNameRepository.save(new SubjectName(name)));
            Subject subject = new Subject(subjectName, grades.get(grade));
            return subjectRepository.save(subject);
        }
    }

    private Map<String, List<Teacher>> createTeachers(TeachingProgram subjectsHours) {
        Map<String, List<Teacher>> teachersForSubjects = new HashMap<>();
        for (String subject : subjectsHours.keySet()) {
            int teachersCount = (int) Math.ceil(subjectsHours.get(subject) / 34.0);
            List<Teacher> teachers = new ArrayList<>(teachersCount);
            for (int i = 0; i < teachersCount; i++) {
                teachers.add(createTeacher(subject));
            }
            teachersForSubjects.put(subject, teachers);
        }
        return teachersForSubjects;
    }

    private Teacher createTeacher(String subject) {
        User teacherUser = createTeacherUser();
        Teacher teacher = new Teacher();
        teacher.setUser(teacherUser);
        teacher.getSkills().addAll(getSubjects(subject));
        return teacherRepository.save(teacher);
    }

    private Collection<Subject> getSubjects(String subjectName) {
        return subjects.stream()
            .filter(subject -> subject.getName().getValue().equals(subjectName))
            .collect(Collectors.toList());
    }

    private User createTeacherUser() {
        return userGenerator.createUser(User.Role.teacher, 25, 60);
    }

    private SchoolClassTimeTable createSchoolClassTimeTable(
        SchoolClassValue schoolClass, TeachingProgram program
    ) {
        TeachingProgram programCopy = new TeachingProgram(program);
        SchoolClassTimeTable timeTable = new SchoolClassTimeTable(schoolClass, weeklySlots);
        for (DayOfWeek day : timeTable.getDays()) {
            List<WeeklySlot> dailySlots = timeTable.getDailySlots(day);
            for (WeeklySlot dailySlot : dailySlots) {
                if (!dailySlot.getShift().equals(schoolClass.shift)
                    || dailySlot.getOrdinal().equals(maxOrdinal)) {
                    continue;
                }
                if (programCopy.size() == 0) {
                    break;
                }
                String subject = pickSubject(programCopy);
                timeTable.occupy(dailySlot, subject);
            }
        }
        for (DayOfWeek day : timeTable.getDays()) {
            List<WeeklySlot> dailySlots = timeTable.getDailySlots(day);
            for (WeeklySlot dailySlot : dailySlots) {
                if (!dailySlot.getShift().equals(schoolClass.shift)
                    || !dailySlot.getOrdinal().equals(maxOrdinal)) {
                    continue;
                }
                if (programCopy.size() == 0) {
                    break;
                }
                String subject = pickSubject(programCopy);
                timeTable.occupy(dailySlot, subject);
            }
        }
        return timeTable;
    }

    private Map<String, List<TeacherTimeTable>> createTeacherTimeTables(
        Map<String, List<Teacher>> teachers
    ) {
        Map<String, List<TeacherTimeTable>> teacherTimeTables = new LinkedHashMap<>();
        for (String subject : teachers.keySet()) {
            List<TeacherTimeTable> teachersForSubjectTimeTables =
                teachers.get(subject).stream()
                .map(teacher -> new TeacherTimeTable(teacher, weeklySlots))
                .collect(Collectors.toList());
            teacherTimeTables.put(subject, teachersForSubjectTimeTables);
        }
        return teacherTimeTables;
    }

    private Map<SchoolClassValue, Map<String, Teacher>> computeSchoolClassesTeachers(
        Map<SchoolClassValue, TeachingProgram> programs,
        Map<String, List<Teacher>> teachersForSubject
    ) {
        Map<String, Map<Teacher, Integer>> teachersAvailableLessons = new LinkedHashMap<>();
        for (String subject : teachersForSubject.keySet()) {
            List<Teacher> teachers = teachersForSubject.get(subject);
            Map<Teacher, Integer> teacherAvailableLessons = new LinkedHashMap<>();
            for (Teacher teacher : teachers) {
                teacherAvailableLessons.put(teacher, teacherLessonsPerWeek);
            }
            teachersAvailableLessons.put(subject, teacherAvailableLessons);
        }
        Map<SchoolClassValue, Map<String, Teacher>> schoolClassTeachers = new HashMap<>(programs.size());
        for (Map.Entry<SchoolClassValue, TeachingProgram> program : programs.entrySet()) {
            for (String subject : program.getValue().keySet()) {
                Teacher teacher = pickTeacher(teachersAvailableLessons.get(subject), program.getValue().get(subject));
                Map<String, Teacher> teachers = schoolClassTeachers
                    .computeIfAbsent(program.getKey(), schoolClassValue -> new LinkedHashMap<>());
                teachers.put(subject, teacher);
            }
        }
        return schoolClassTeachers;
    }

    private void populateTeacherTimeTables(
        List<SchoolClassTimeTable> schoolClassTimeTables,
        Map<String, List<TeacherTimeTable>> teacherTimeTables,
        Map<SchoolClassValue, Map<String, Teacher>> schoolClassTeachers
    ) {
        for (SchoolClassTimeTable schoolClassTimeTable : schoolClassTimeTables) {
            SchoolClassValue schoolClass = schoolClassTimeTable.getOwner();
            for (WeeklySlot weeklySlot : schoolClassTimeTable.getWeeklySlots()) {
                if (schoolClassTimeTable.isOccupied(weeklySlot)) {
                    String subject = schoolClassTimeTable.getSlotContent(weeklySlot).get(0);
                    Teacher teacher = schoolClassTeachers.get(schoolClass).get(subject);

                    List<TeacherTimeTable> timeTables = teacherTimeTables.get(subject);
                    TeacherTimeTable timeTable = timeTables.stream()
                        .filter(tt -> tt.getOwner().equals(teacher))
                        .findAny().orElseThrow(() -> new RuntimeException("no time table found"));
                    CourseValue course = new CourseValue(schoolClass, subject);
                    timeTable.occupy(weeklySlot, course);
                }
            }
        }
    }

    private void fixTeacherTimeTables(
        Map<String, List<TeacherTimeTable>> teacherTimeTables
    ) {
        List<TeacherTimeTable> timeTables = teacherTimeTables.values().stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        for (TeacherTimeTable timeTable : timeTables) {
            for (WeeklySlot weeklySlot : timeTable.getWeeklySlots()) {
                List<CourseValue> courses = timeTable.getSlotContent(weeklySlot);
                while (courses.size() > 1) {
                    swapLessons(timeTable, weeklySlot, teacherTimeTables);
                }
            }
        }
    }

    private void swapLessons(
        TeacherTimeTable timeTable, WeeklySlot weeklySlotWithManyLessons,
        Map<String, List<TeacherTimeTable>> teacherTimeTables
    ) {
        for (WeeklySlot weeklySlot : timeTable.getWeeklySlots()) {
            if (!timeTable.isOccupied(weeklySlot)) {
                Pair<TeacherTimeTable, WeeklySlot> result = findAvailableWeeklySlot(
                    teacherTimeTables, timeTable.getOwner(), weeklySlotWithManyLessons);
                List<CourseValue> courses = timeTable.getSlotContent(weeklySlotWithManyLessons);
                result.getFirst().occupy(result.getSecond(), courses.remove(0));
                break;
            }
        }
    }

    private Pair<TeacherTimeTable, WeeklySlot> findAvailableWeeklySlot(
        Map<String, List<TeacherTimeTable>> teacherTimeTables,
        Teacher excludeTeacher, WeeklySlot weeklySlotWithManyLessons
    ) {
        for (String subject : teacherTimeTables.keySet()) {
            for (TeacherTimeTable timeTable : teacherTimeTables.get(subject)) {
                if (!timeTable.getOwner().equals(excludeTeacher)) {
                    for (WeeklySlot weeklySlot : timeTable.getWeeklySlots()) {
                        if (weeklySlotWithManyLessons.getShift().equals(weeklySlot.getShift())
                                && !timeTable.isOccupied(weeklySlot)) {
                            return new Pair<>(timeTable, weeklySlot);
                        }
                    }
                }
            }
        }
        throw new RuntimeException("cannot find available weekly slot");
    }

    private Teacher pickTeacher(Map<Teacher, Integer> teachersAvailableLessons, int weeklyLessons) {
        int max = -1;
        Teacher teacher = null;
        for (Teacher tempTeacher : teachersAvailableLessons.keySet()) {
            Integer count = teachersAvailableLessons.get(tempTeacher);
            if (count > max) {
                max = count;
                teacher = tempTeacher;
            }
        }
        Integer count = teachersAvailableLessons.get(teacher);
        if ((count - weeklyLessons) <= 0) {
            teachersAvailableLessons.remove(teacher);
        } else {
            teachersAvailableLessons.replace(teacher, count - weeklyLessons);
        }
        return teacher;
    }

    private String pickSubject(TeachingProgram programCopy) {
        int index = randomInt(programCopy.size());
        String subject = getKey(programCopy, index);
        Integer count = programCopy.get(subject);
        if (count == 1) {
            programCopy.remove(subject);
        } else {
            programCopy.replace(subject, count - 1);
        }
        return subject;
    }

    private int randomInt(int max) {
        return random.nextInt(max);
    }

    private <T> T getKey(Map<T, ?> map, int index) {
        if (map.size() <= index) {
            throw new IllegalArgumentException("index is greater than the size of the map");
        }
        int i = 0;
        for (T key : map.keySet()) {
            if (i == index) {
                return key;
            } else {
                i += 1;
            }
        }
        throw new RuntimeException();
    }

    private void checkAll(Map<String, List<TeacherTimeTable>> teacherTimeTables) {
        for (String subject : teacherTimeTables.keySet()) {
            for (TeacherTimeTable teacherTimeTable : teacherTimeTables.get(subject)) {
                for (WeeklySlot weeklySlot : teacherTimeTable.getWeeklySlots()) {
                    List<CourseValue> courses = teacherTimeTable.getSlotContent(weeklySlot);
                    assert courses.size() <= 1;
                }
            }
        }
    }

    private Map<Integer, Grade> createGrades() {
        Map<Integer, Grade> grades = new LinkedHashMap<>();
        for (int i = 9; i <= 12; i++) {
            Grade grade = new Grade(i);
            grade.setOrdinal(i);
            grade = gradeRepository.save(grade);
            grades.put(i, grade);
        }
        return grades;
    }

    private void persistSchoolClasses(
        List<SchoolClassTimeTable> schoolClassTimeTables,
        Map<SchoolClassValue, Map<String, Teacher>> schoolClassTeachers
    ) {
        for (SchoolClassTimeTable schoolClassTimeTable : schoolClassTimeTables) {
            SchoolClassValue schoolClass = schoolClassTimeTable.getOwner();
            persistSchoolClass(schoolClass);
            persistTimeTable(schoolClassTimeTable, schoolClassTeachers);
        }
    }

    private void persistSchoolClass(SchoolClassValue schoolClassValue) {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setName(schoolClassValue.name);
        schoolClass.setBeginningGrade(grades.get(schoolClassValue.grade));
        schoolClass.setBeginningSchoolYear(schoolYear);
        Room room = new Room();
        room.setFloor(1);
        room.setName(schoolClass.getGrade().getOrdinal() + " " + schoolClass.getName());
        schoolClass.setClassRoom(roomRepository.save(room));
        schoolClass = schoolClassRepository.save(schoolClass);
        schoolClass.setStudents(createStudents(schoolClass));
        schoolClasses.put(schoolClassValue, schoolClass);
    }

    private List<Student> createStudents(SchoolClass schoolClass) {
        List<Student> students = new ArrayList<>(studentsInSchoolClass);
        int age = minStudentAge + schoolClass.getGrade().getOrdinal();
        for (int i = 0; i < studentsInSchoolClass; i++) {
            students.add(createStudent(schoolClass, age));
        }
        students.sort(
            Comparator
                .comparing((Student student) ->
                    student.getUser().getPersonalInfo().getFirstName())
                .thenComparing((Student student) ->
                    student.getUser().getPersonalInfo().getMiddleName())
                .thenComparing((Student student) ->
                    student.getUser().getPersonalInfo().getLastName()));
        for (int i = 0; i < students.size(); i++) {
            students.get(i).setNumber(i + 1);
        }
        studentRepository.saveAll(students);
        return students;
    }

    private Student createStudent(SchoolClass schoolClass, int age) {
        User studentUser = userGenerator.createUser(User.Role.student, age, age);
        Student student = new Student();
        student.setUser(studentUser);
        student.setSchoolClass(schoolClass);
        setMarks(student);
        return student;
    }

    private void setMarks(Student student) {
        // TODO
        student.getMarks().add(new Mark(student, subjects.get(0), randomInt(2, 7) * 100));
    }

    private void persistTimeTable(
        SchoolClassTimeTable schoolClassTimeTable,
        Map<SchoolClassValue, Map<String, Teacher>> schoolClassTeachers
    ) {
        SchoolClassValue schoolClass = schoolClassTimeTable.getOwner();
        TeachingProgram teachingProgram = gradePrograms.get(schoolClass.grade);
        for (String subjectName : teachingProgram.keySet()) {
            Subject subject = getSubject(subjectName, schoolClass.grade);
            List<WeeklySlot> subjectWeeklySLots = new ArrayList<>(teachingProgram.get(subjectName));
            for (WeeklySlot weeklySlot : schoolClassTimeTable.getWeeklySlots()) {
                List<String> subjectNames = schoolClassTimeTable.getSlotContent(weeklySlot);
                if (subjectNames.size() > 0 && subjectNames.get(0).equals(subjectName)) {
                    subjectWeeklySLots.add(weeklySlot);
                }
            }
            Course course = new Course();
            course.setSubject(subject);
            course.setWeeklySlots(subjectWeeklySLots);
            course.setSchoolClass(schoolClasses.get(schoolClass));
            course.setTeacher(schoolClassTeachers.get(schoolClass).get(subjectName));
            course.setTerm(term);
            course.setRoom(course.getSchoolClass().getClassRoom());
            course = courseRepository.save(course);
            courses.put(new CourseValue(schoolClass, subjectName), course);
        }
    }

    private Subject getSubject(String name, int grade) {
        return subjects.stream()
            .filter(subject -> subject.getName().getValue().equals(name)
                && subject.getGrade().getName().equals(Integer.toString(grade)))
            .findAny()
            .orElseThrow(() ->
                new IllegalArgumentException("Cannot find subject."));
    }

    private void createLessons() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate startOfWeek = util.getStartOfWeek(now);
        createLessonsForThisWeek(now, startOfWeek);
        startOfWeek = startOfWeek.minusWeeks(1);
        while (term.getBegin().isBefore(startOfWeek)) {
            createLessonsForWeek(startOfWeek);
            startOfWeek = startOfWeek.minusWeeks(1);
        }
    }

    private void createLessonsForThisWeek(LocalDateTime now, LocalDate startOfWeek) {
        for (String subjectName : teacherTimeTables.keySet()) {
            for (TeacherTimeTable teacherTimeTable : teacherTimeTables.get(subjectName)) {
                for (WeeklySlot weeklySlot : teacherTimeTable.getWeeklySlots()) {
                    if (weeklySlot.getDay().ordinal() <= now.getDayOfWeek().ordinal()
                            && weeklySlot.getEnd().isBefore(now.toLocalTime())
                            && teacherTimeTable.isOccupied(weeklySlot)
                    ) {
                        Course course = courses.get(teacherTimeTable.getSlotContent(weeklySlot).get(0));
                        Lesson lesson = new Lesson();
                        lesson.setCourse(course);
                        lesson.setWeeklySlot(weeklySlot);
                        LocalDate date = startOfWeek.plusDays(weeklySlot.getDay().ordinal());
                        LocalDateTime begin = LocalDateTime.of(date, weeklySlot.getBegin());
                        lesson.setBegin(begin);
                        lessonRepository.save(lesson);
                    }
                }
            }
        }
    }

    private void createLessonsForWeek(LocalDate startOfWeek) {
        for (String subjectName : teacherTimeTables.keySet()) {
            for (TeacherTimeTable teacherTimeTable : teacherTimeTables.get(subjectName)) {
                for (WeeklySlot weeklySlot : teacherTimeTable.getWeeklySlots()) {
                    if (teacherTimeTable.isOccupied(weeklySlot)) {
                        Course course = courses.get(teacherTimeTable.getSlotContent(weeklySlot).get(0));
                        Lesson lesson = new Lesson();
                        lesson.setCourse(course);
                        lesson.setWeeklySlot(weeklySlot);
                        LocalDate date = startOfWeek.plusDays(weeklySlot.getDay().ordinal());
                        LocalDateTime begin = LocalDateTime.of(date, weeklySlot.getBegin());
                        lesson.setBegin(begin);
                        lessonRepository.save(lesson);
                    }
                }
            }
        }
    }

    private int randomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    static class SchoolClassValue {
        int grade;
        String name;
        int shift;
        public SchoolClassValue(int grade, String name, int shift) {
            this.grade = grade;
            this.name = name;
            this.shift = shift;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            SchoolClassValue that = (SchoolClassValue) obj;
            return grade == that.grade && name == that.name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(grade, name);
        }
    }
    static class CourseValue {
        SchoolClassValue schoolClass;
        String subject;

        public CourseValue(SchoolClassValue schoolClass, String subject) {
            this.schoolClass = schoolClass;
            this.subject = subject;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CourseValue that = (CourseValue) o;
            return schoolClass.equals(that.schoolClass) &&
                subject.equals(that.subject);
        }

        @Override
        public int hashCode() {
            return Objects.hash(schoolClass, subject);
        }
    }
}
