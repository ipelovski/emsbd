package emsbj.generation;

import emsbj.Course;
import emsbj.CourseRepository;
import emsbj.Grade;
import emsbj.GradeRepository;
import emsbj.Room;
import emsbj.RoomRepository;
import emsbj.SchoolClass;
import emsbj.SchoolClassRepository;
import emsbj.SchoolYear;
import emsbj.SchoolYearRepository;
import emsbj.Subject;
import emsbj.SubjectName;
import emsbj.SubjectNameRepository;
import emsbj.SubjectRepository;
import emsbj.Teacher;
import emsbj.TeacherRepository;
import emsbj.Term;
import emsbj.TermRepository;
import emsbj.WeeklySlot;
import emsbj.WeeklySlotRepository;
import emsbj.user.User;
import emsbj.user.UserRepository;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
public class Generator {
    private static final int teacherLessonsPerWeek = 34;
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
    private CourseRepository courseRepository;

    private List<WeeklySlot> weeklySlots;
    private Map<Integer, Grade> grades;
    private Map<Integer, TeachingProgram> gradePrograms;
    private List<Subject> subjects = new ArrayList<>();
    private Map<SchoolClassValue, SchoolClass> schoolClasses = new LinkedHashMap<>();
    private SchoolYear schoolYear;
    private Term term;
    private Random random = new Random(0);

    public void generate() {
        weeklySlots = StreamSupport
            .stream(weeklySlotRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());

        schoolYear = StreamSupport.stream(schoolYearRepository.findAllWithAll().spliterator(), false)
            .findFirst().get();
        term = schoolYear.getTerms().get(0);

        TeachingProgram program9 = TeachingProgram.create9();
        TeachingProgram program10 = TeachingProgram.create10();
        TeachingProgram program11 = TeachingProgram.create11();
        TeachingProgram program12 = TeachingProgram.create12();
        gradePrograms = new LinkedHashMap<>(4);
        gradePrograms.put(9, program9);
        gradePrograms.put(10, program10);
        gradePrograms.put(11, program11);
        gradePrograms.put(12, program12);
        Map<SchoolClassValue, TeachingProgram> programs =
            new LinkedHashMap<>();
        for (int grade = 9; grade <= 12; grade++) {
            for (int ordinal = 1; ordinal <= 4; ordinal++) {
                SchoolClassValue schoolClass = new SchoolClassValue(grade, ordinal);
                programs.put(schoolClass, gradePrograms.get(grade));
            }
        }

        TeachingProgram programAll = new TeachingProgram();
        for (SchoolClassValue schoolClass : programs.keySet()) {
            programAll = mergePrograms(programAll, programs.get(schoolClass));
        }
        Map<String, List<Teacher>> teachers = createTeachers(programAll);
        Map<String, List<TeacherTimeTable>> teacherTimeTables = createTeacherTimeTables(teachers);
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

        // TODO
        // fixHolesInSchoolClassTimeTables();

        // TODO fix two subjects in one day

//        printAll();
        checkAll(teacherTimeTables);

        grades = createGrades();
        persistSchoolClasses(schoolClassTimeTables, schoolClassTeachers);
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
        return Collections.emptyList();
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
                    CourseValue course = new CourseValue();
                    course.subject = subject;
                    course.schoolClass = schoolClass;
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
                result.getKey().occupy(result.getValue(), courses.remove(0));
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

    private void fixHolesInSchoolClassTimeTables() {

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

    private Map<String, Course> createCourses(Iterable<String> subjects, Map<String, Subject> subjectObjects) {
        Map<String, Course> courses = new HashMap<>();
        for (String subject : subjects) {
            Course course = new Course();
            course.setSubject(subjectObjects.get(subject));
            courses.put(subject, course);
        }
        return courses;
    }

    private void printAll() {

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
        schoolClass.setName(schoolClassValue.ordinal + "");
        schoolClass.setBeginningGrade(grades.get(schoolClassValue.grade));
        schoolClass.setBeginningSchoolYear(schoolYear);
        Room room = new Room();
        room.setFloor(1);
        room.setName(schoolClass.getGrade().getOrdinal() + " " + schoolClass.getName());
        schoolClass.setClassRoom(roomRepository.save(room));
        schoolClass = schoolClassRepository.save(schoolClass);
        schoolClasses.put(schoolClassValue, schoolClass);
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
            courseRepository.save(course);
        }
    }

    private Subject getSubject(String name, int grade) {
        Optional<Subject> optionalSubject = subjects.stream()
            .filter(subject -> subject.getName().getValue().equals(name)
                && subject.getGrade().getName().equals(Integer.toString(grade)))
            .findAny();
        if (optionalSubject.isPresent()) {
            return optionalSubject.get();
        } else {
            Optional<Subject> subjectWithSameName = subjects.stream()
                .filter(subject -> subject.getName().getValue().equals(name))
                .findAny();
            SubjectName subjectName = subjectWithSameName.map(Subject::getName)
                .orElseGet(() -> subjectNameRepository.save(new SubjectName(name)));
            Subject subject = new Subject(subjectName, grades.get(grade));;
            subject = subjectRepository.save(subject);
            subjects.add(subject);
            return subject;
        }
    }

    static class SchoolClassValue {
        int grade;
        int ordinal;
        public SchoolClassValue() {}
        public SchoolClassValue(int grade, int ordinal) {
            this.grade = grade;
            this.ordinal = ordinal;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            SchoolClassValue that = (SchoolClassValue) obj;
            return grade == that.grade && ordinal == that.ordinal;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(100 * grade + ordinal);
        }
    }
    static class CourseValue {
        SchoolClassValue schoolClass;
        String subject;
    }
}
