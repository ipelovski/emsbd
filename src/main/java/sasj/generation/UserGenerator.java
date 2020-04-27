package sasj.generation;

import sasj.data.user.PersonalInfo;
import sasj.data.user.User;
import sasj.data.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Component
class UserGenerator {
    // from https://sanovnik.at/българските-имена-през-вековете/
    private static final String[] maleFirstNames =
        ("Албен,Алекс,Александър,Ангел,Асен,Аспарух," +
            "Белислав,Билян,Бисер,Благой,Благомил,Благовест," +
            "Благослав,Богдан,Богомил,Богослав,Божидар,Бойко,Боян," +
            "Боримир,Борислав,Борис,Бранимир,Бранислав," +
            "Валентин,Велин,Величко,Велислав,Велко," +
            "Венелин,Венцислав,Веселин,Веско,Вихрен," +
            "Владимир,Владо,Владислав,Върбан," +
            "Гален,Галин,Георги,Герган,Горан," +
            "Данаил,Дарин,Дарослав,Даян,Диан,Деян," +
            "Делян,Десимир,Десислав,Детелин,Добри," +
            "Добрил,Добрислав,Добромир,Доброслав," +
            "Евгени,Евелин,Емил,Емо," +
            "Жан,Живко," +
            "Здравко,Злати,Златко,Златимир,Златислав," +
            "Ивайло,Иван,Иво,Искрен," +
            "Йоан,Йово,Йордан,Йото," +
            "Калин,Калоян,Камен,Кирил,Красен,Красимир," +
            "Крум,Кристин,Кристиян," +
            "Лилян,Лозан,Лъчезар,Любомил,Любомир,Любослав,Людмил," +
            "Малин,Маргарит,Марин,Марти,Мартин," +
            "Миглен,Милан,Милен,Милко,Милослав," +
            "Миро,Миролюб,Мирослав,Младен,Момчил,Михаил," +
            "Найден,Найо,Невен,Невян,Недялко,Никола,Николай," +
            "Огнян,Орлин," +
            "Павел,Петър,Петко,Петьо,Пламен,Пресиян,Преслав," +
            "Ради,Радил,Радин,Радо,Радомир,Радислав,Радмил," +
            "Радосвет,Радослав,Радостин,Райко,Райчо,Раян," +
            "Росен,Румен,Румян," +
            "Самуил,Симо,Сашо,Светозар,Светослав,Светлан,Светлин,Светлозар," +
            "Свилен,Слав,Слави,Спас,Спасимир,Стамен,Станимир,Станислав,Стоян," +
            "Стефан,Стилян,Страхил," +
            "Теодор,Тихомир,Тодор,Томислав,Траян,Трифон," +
            "Христо,Христин,Христофор," +
            "Цветан,Цветелин,Цветозар,Цветомир,Цветослав," +
            "Чавдар,Чудомир," +
            "Юлиян," +
            "Явор,Янислав,Ясен")
            .split(",");
    private static final String[] maleSecondNames =
        ("Албенов,Алексов,Александров,Ангелов,Асенов,Аспарухов," +
            "Белиславов,Билянов,Бисеров,Благоев,Благомилов,Благовестов," +
            "Благославов,Богданов,Богомилов,Богославов,Божидаров,Бойков,Боянов," +
            "Боримиров,Бориславов,Борисов,Бранимиров,Браниславов," +
            "Валентинов,Велинов,Величков,Велиславов,Велков," +
            "Венелинов,Венциславов,Веселинов,Весков,Вихренов," +
            "Владимиров,Владов,Владиславов,Върбанов," +
            "Галенов,Галинов,Георгиев,Герганон,Горанов," +
            "Данаилов,Даринов,Дарославов,Даянов,Дианов,Деянов," +
            "Делянов,Десимиров,Десиславов,Детелинов,Добрев," +
            "Добрилов,Добриславов,Добромиров,Доброславов," +
            "Евгениев,Евелинов,Емилов,Емов," +
            "Жанов,Живков," +
            "Здравков,Златев,Златков,Златимиров,Златиславов," +
            "Ивайлов,Иванов,Ивов,Искренов," +
            "Йоанов,Йовов,Йорданов,Йотов," +
            "Калинов,Калоянов,Каменов,Кирилов,Красенов,Красимиров," +
            "Крумов,Кристинов,Кристиянов," +
            "Лилянов,Лозанов,Лъчезаров,Любомилов,Любомиров,Любославов,Людмилов," +
            "Малинов,Маргаритов,Маринов,Мартев,Мартинов," +
            "Мигленов,Миланов,Миленов,Милков,Милославов," +
            "Миров,Миролюбов,Мирославов,Младенов,Момчилов,Михайлов," +
            "Найденов,Найов,Невенов,Невянов,Недялков,Николов,Николаев," +
            "Огнянов,Орлинов," +
            "Павелов,Петров,Петков,Петьов,Пламенов,Пресиянов,Преславов," +
            "Радев,Радилов,Радинов,Радов,Радомиров,Радиславов,Радмилов," +
            "Радосветов,Радославов,Радостинов,Райков,Райчов,Раянов," +
            "Росенов,Руменов,Румянов," +
            "Самуилов,Симов,Сашов,Светозаров,Светославов,Светланов,Светлинов,Светлозаров," +
            "Свиленов,Славов,Славев,Спасов,Спасимиров,Стаменов,Станимиров,Станиславов,Стоянов," +
            "Стефанов,Стилянов,Страхилов," +
            "Теодоров,Тихомиров,Тодоров,Томиславов,Траянов,Трифонов," +
            "Христов,Христинов,Христофоров," +
            "Цветанов,Цветелинов,Цветозаров,Цветомиров,Цветославов," +
            "Чавдаров,Чудомиров," +
            "Юлиянов," +
            "Яворов,Яниславов,Ясенов")
            .split(",");
    private static final String[] femaleFirstNames =
        ("Албена,Алена,Александра,Александрина,Ангелина,Ана,Анна,Ани," +
            "Асена,Асенка,Аспаруха,Ахинора," +
            "Балкана,Бела,Беляна,Белимира,Белослава," +
            "Биляна,Бисера,Бисерка,Бистра,Блага,Благовеста," +
            "Благомира,Благородна,Благослава," +
            "Богдана,Богомила,Богослава,Божана,Божина,Божидара," +
            "Бойка,Бояна,Боряна,Борислава,Бранимира,Бранислава," +
            "Валентина,Велина,Велия,Велислава," +
            "Венелина,Венцислава,Вера,Вяра,Верислава," +
            "Весела,Веселина,Веселка,Вихра," +
            "Влада,Владена,Владимира,Владислава,Върбана,Върбина," +
            "Галена,Галина,Галя,Гергана,Горана," +
            "Данаила,Дарина,Дарослава,Даяна,Диана,Деяна," +
            "Деляна,Деница,Десимира,Десислава,Детелина," +
            "Добрина,Добромила,Добромира,Доброслава," +
            "Ева,Евгения,Евдокия,Евелина,Екатерина," +
            "Елеонора,Елена,Елица,Елка,Емилия," +
            "Жана,Живка," +
            "Здравка,Златина,Златимира,Златислава,Зорница," +
            "Ивайла,Ивана,Иванка,Ива,Ивона,Ивелина," +
            "Иглика,Ина,Инна,Ирена,Ирен,Ирина,Искра," +
            "Йоана,Йована,Йордана," +
            "Калина,Калояна,Камена,Капка,Катерина,Катя," +
            "Кирила,Кирилка,Красена,Красимира," +
            "Кристина,Кристияна," +
            "Лидия,Лиляна,Лили,Лилия,Лозана," +
            "Лъчезара,Люба,Любов,Любомира,Любослава,Людмила," +
            "Малина,Марга,Маргарита,Маргарет,Маргита," +
            "Марина,Марта,Мартина,Магдалена," +
            "Миглена,Мила,Милена,Милина,Милица,Милка," +
            "Миляна,Милослава,Мими," +
            "Мира,Мирела,Мирена,Миролюба,Мирослава,Младена,Момчила,Михаела," +
            "Надя,Надежда,Невена,Невелина,Невяна,Неда,Недка,Недялка," +
            "Николина,Никол,Нина,Ния," +
            "Огняна,Орлина," +
            "Павлина,Пенка,Пепа,Петя,Полина,Поля," +
            "Пламена,Пламенка,Преслава," +
            "Рада,Радка,Радмила,Радомила,Радомира,Радосвета," +
            "Радослава,Радост,Радостина,Райка,Райна,Рая," +
            "Ралица,Роза,Розалина,Розана,Розалия," +
            "Роса,Росана,Роселина,Росена,Росица,Румена,Румяна," +
            "Симона,Саша,Светла,Светлана,Светлозара,Светломира," +
            "Свилена,Слава,Славена,Снежа,Снежана,Снежанка,Снежина," +
            "Спасена,Спаска,Стамена,Станимира,Станислава,Стояна,Стоянка," +
            "Стефана,Стефи,Стиляна,Стела,Страхила," +
            "Таня,Теменуга,Теменуждка,Теодора,Теди," +
            "Тиха,Тихомира,Томислава,Траяна,Трифонка," +
            "Хриса,Хриси,Христина," +
            "Цвета,Цветелина,Цветозара,Цветомира,Цветослава," +
            "Чавдара,Чудомира," +
            "Юлия,Юлияна," +
            "Явора,Яна,Яница,Янислава,Ясна")
            .split(",");
    private static final String[] femaleSecondNames = Arrays.stream(maleSecondNames)
        .map(maleSecondName -> maleSecondName + "а")
        .toArray(String[]::new);

//    @Autowired
    private PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
    @Autowired
    private UserRepository userRepository;

    private Random random = new Random(0);
    private Set<String> generatedNames = new HashSet<>();

    User createUser(User.Role role, int minAge, int maxAge) {
        PersonalInfo.Gender gender = PersonalInfo.Gender.values()[randomInt(0, 2)];
        return createUser(role, gender, minAge, maxAge + 1);
    }

    User createUser(User.Role role, PersonalInfo.Gender gender, int minAge, int maxAge) {
        User user = new User();
        setNamesAndGender(user, gender);
        user.setPassword(passwordEncoder.encode(user.getUsername()));
        user.setRole(role);
        // TODO
        user.getPersonalInfo().setBornAt(LocalDate.now().minusYears(randomInt(minAge, maxAge)));
        return userRepository.save(user);
    }

    private void setNamesAndGender(User user, PersonalInfo.Gender gender) {
        String[] firstNames = gender == PersonalInfo.Gender.male ?
            maleFirstNames : femaleFirstNames;
        String[] secondNames = gender == PersonalInfo.Gender.male ?
            maleSecondNames : femaleSecondNames;
        String firstName, middleName, lastName, fullName;
        do {
            firstName = firstNames[random.nextInt(firstNames.length)];
            middleName = secondNames[random.nextInt(secondNames.length)];
            lastName = secondNames[random.nextInt(secondNames.length)];
            fullName = firstName + "," + lastName;
        } while (generatedNames.contains(fullName));
        generatedNames.add(fullName);
        String username = firstName.toLowerCase() + "." + lastName.toLowerCase();
        user.setUsername(username);
        String email = username + "@моето-училище.бг";
        user.setEmail(email);
        user.getPersonalInfo()
            .setFirstName(firstName)
            .setMiddleName(middleName)
            .setLastName(lastName)
            .setGender(gender);
    }

    private int randomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }
}
