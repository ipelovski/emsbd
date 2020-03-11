package emsbj.generation;

import emsbj.Course;
import emsbj.Lesson;
import emsbj.Subject;
import emsbj.Teacher;
import emsbj.WeeklyLessons;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WeeklySlotRepository weeklySlotRepository;

    private List<WeeklySlot> weeklySlots;
    private int teacherSuffix = 1;
    private Random random = new Random(0);

    public void generate() {
        weeklySlots = StreamSupport
            .stream(weeklySlotRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());

        TeachingProgram program9 = TeachingProgram.create9();
        TeachingProgram program10 = TeachingProgram.create10();
        TeachingProgram program11 = TeachingProgram.create11();
        TeachingProgram program12 = TeachingProgram.create12();
        Map<Integer, TeachingProgram> gradePrograms = new LinkedHashMap<>(4);
        gradePrograms.put(9, program9);
        gradePrograms.put(10, program10);
        gradePrograms.put(11, program11);
        gradePrograms.put(12, program12);
        TeachingProgram programAll = mergePrograms(
            program9, program9, program9, program9,
            program10, program10, program10, program10,
            program11, program11, program11, program11,
            program12, program12, program12, program12);
        Map<String, List<Teacher>> teachers = createTeachers(programAll);
        Map<String, List<TeacherTimeTable>> teacherTimeTables = createTeacherTimeTables(teachers);
        Map<SchoolClassValue, TeachingProgram> programs =
            new LinkedHashMap<>();
        for (int grade = 9; grade <= 12; grade++) {
            for (int ordinal = 1; ordinal <= 4; ordinal++) {
                SchoolClassValue schoolClass = new SchoolClassValue(grade, ordinal);
                programs.put(schoolClass, gradePrograms.get(grade));
            }
        }
        Map<SchoolClassValue, Map<String, Teacher>> schoolClassTeachers =
            computeSchoolClassesTeachers(programs, teachers);

        SchoolClassTimeTable grade9schoolClass1TimeTable =
            createSchoolClassTimeTable(new SchoolClassValue(9, 1), program9);

        populateTeacherTimeTables(
            Collections.singletonList(grade9schoolClass1TimeTable),
            teacherTimeTables, schoolClassTeachers);

        fixTeacherTimeTables(teacherTimeTables);

        printAll();
    }

    private TeachingProgram mergePrograms(TeachingProgram... programs) {
        assert programs.length > 0;
        TeachingProgram programAll = new TeachingProgram(programs[0]);
        for (TeachingProgram program : programs) {
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
        return teacher;
    }

    private Collection<Subject> getSubjects(String subjectName) {
        return Collections.emptyList();
    }

    private User createTeacherUser() {
        String firstName = "Генади " + teacherSuffix;
        String middleName = "Захариев " + teacherSuffix;
        String lastName = "Хаджитошев " + teacherSuffix;
        teacherSuffix += 1;
        return createUser(User.Role.teacher, firstName, middleName, lastName);
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

    private SchoolClassTimeTable createSchoolClassTimeTable(
        SchoolClassValue schoolClass, TeachingProgram program
    ) {
        TeachingProgram programCopy = new TeachingProgram(program);
        SchoolClassTimeTable timeTable = new SchoolClassTimeTable(schoolClass, weeklySlots);
        for (DayOfWeek day : timeTable.getDays()) {
            List<WeeklySlot> dailySlots = timeTable.getDailySlots(day);
            Set<String> subjectsSet = new HashSet<>(dailySlots.size());
            for (WeeklySlot dailySlot : dailySlots) {
                if (programCopy.size() == 0) {
                    break;
                }
                String subject = pickSubject(programCopy, subjectsSet);
                timeTable.occupy(dailySlot, subject);
            }
        }
        return timeTable;
    }

    private WeeklyLessons createSchedule(
        TeachingProgram program, TeachingProgram programCopy,
        Map<String, List<Teacher>> teachersForSubjects,
        Map<String, Course> courses
    ) {
        List<Lesson> lessons = new ArrayList<>();
        Map<DayOfWeek, List<WeeklySlot>> schedule = new LinkedHashMap<>();
        for (DayOfWeek day : schedule.keySet()) {
            List<WeeklySlot> dailySlots = schedule.get(day);
            Set<String> subjectsSet = new HashSet<>(dailySlots.size());
            for (WeeklySlot dailySlot : dailySlots) {
                String subject = pickSubject(programCopy, subjectsSet);
                Course course = courses.get(subject);
                lessons.add(new Lesson(course, dailySlot));
                course.getWeeklySlots().add(dailySlot);
            }
        }
        return new WeeklyLessons(lessons);
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

    private Teacher pickTeacher(Map<Teacher, Integer> teachersAvailableLessons) {
        int index = randomInt(teachersAvailableLessons.size());
        Teacher teacher = getKey(teachersAvailableLessons, index);
        Integer count = teachersAvailableLessons.get(teacher);
        if (count == 1) {
            teachersAvailableLessons.remove(teacher);
        } else {
            teachersAvailableLessons.replace(teacher, count - 1);
        }
        return teacher;
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

    private String pickSubject(TeachingProgram programCopy, Set<String> subjectsSet) {
        String subject;
        int index;
        do {
            index = randomInt(programCopy.size());
            subject = getKey(programCopy, index);
        }
        while (subjectsSet.contains(subject));
        subjectsSet.add(subject);
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
