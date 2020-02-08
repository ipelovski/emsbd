package emsbj;

import emsbj.admin.AdminGradeController;
import emsbj.admin.AdminSchoolClassController;
import emsbj.admin.AdminStudentController;
import emsbj.admin.AdminTeacherController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.util.Locale;

@Service
public class Extensions {
    @Autowired
    private MessageSource messageSource;
    private AdminUrls adminUrls;

    protected Extensions() {
    }

    public String c(String label, String... args) {
        return capitalize(label, args);
    }

    public String capitalize(String label, String... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return StringUtils.capitalize(messageSource.getMessage(label, args, locale));
    }

    public AdminUrls au() {
        return getAdminUrls();
    }

    public AdminUrls getAdminUrls() {
        if(adminUrls == null) {
            adminUrls = new AdminUrls();
        }
        return adminUrls;
    }

    public static class AdminUrls {
        private static final EntitiesUrlBuilder<SchoolYear> schoolYearsUrlBuilder =
            new EntitiesUrlBuilder<>("/school-years");
        private static final EntitiesUrlBuilder<Term> termsUrlBuilder =
            new EntitiesUrlBuilder<>("/terms");
        private static final EntitiesUrlBuilder<Subject> subjectsUrlBuilder =
            new EntitiesUrlBuilder<>("/subjects");
        private static final EntitiesUrlBuilder<Grade> gradesUrlBuilder =
            new EntitiesUrlBuilder<>("/grades");
        private static final String addUrl = "/add";

        public String schoolYears() {
            return schoolYearsUrlBuilder.build();
        }

        public String schoolYear(SchoolYear schoolYear) {
            return schoolYearsUrlBuilder.entity(schoolYear).build();
        }

        public String term(Term term) {
            return termsUrlBuilder.entity(term).build();
        }

        public String addSubject(Term term) {
            assert !term.isNew();
            return subjectsUrlBuilder.build() + addUrl + "?term=" + term.getId();
        }

        public String addSubject() {
            return subjectsUrlBuilder.build() + addUrl;
        }

        public String grades() {
            UriComponentsBuilder uriComponentsBuilder =
                MvcUriComponentsBuilder.fromMethodName(
                    AdminGradeController.class, "list", Model.class);
            return uriComponentsBuilder.build().toUriString();
        }

        public String addGrade() {
            return gradesUrlBuilder.build() + addUrl;
        }

        public String schoolClasses() {
            UriComponentsBuilder uriComponentsBuilder =
                MvcUriComponentsBuilder.fromMethodName(
                    AdminSchoolClassController.class, "list", Model.class);
            return uriComponentsBuilder.build().toUriString();
        }

        public String addSchoolClass() {
            UriComponentsBuilder uriComponentsBuilder =
                MvcUriComponentsBuilder.fromMethodName(
                    AdminSchoolClassController.class, "add", Model.class);
            return uriComponentsBuilder.build().toUriString();
        }

        public String teachers() {
            UriComponentsBuilder uriComponentsBuilder =
                MvcUriComponentsBuilder.fromMethodName(
                    AdminTeacherController.class, "list", Model.class);
            return uriComponentsBuilder.build().toUriString();
        }

        public String students() {
            UriComponentsBuilder uriComponentsBuilder =
                MvcUriComponentsBuilder.fromMethodName(
                    AdminStudentController.class, "list", Model.class);
            return uriComponentsBuilder.build().toUriString();
        }

        private static class EntitiesUrlBuilder<T extends JournalPersistable> {
            private final String path;
            EntitiesUrlBuilder(String path) {
                this.path = path;
            }
            EntityUrlBuilder<T> entity(T entity) {
                return new EntityUrlBuilder<>(this, entity);
            }
            String build() {
                return "/admin" + path;
            }
        }
        private static class EntityUrlBuilder<T extends JournalPersistable> {
            private final EntitiesUrlBuilder<T> parent;
            private final T entity;
            EntityUrlBuilder(EntitiesUrlBuilder<T> parent, T entity) {
                assert !entity.isNew();
                this.parent = parent;
                this.entity = entity;
            }
            String build() {
                return parent.build() + "/" + entity.getId();
            }
        }
    }
}
